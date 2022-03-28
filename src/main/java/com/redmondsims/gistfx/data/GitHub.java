package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.enums.Names;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import javafx.application.Platform;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

class GitHub {

	private       org.kohsuke.github.GitHub                 gitHub;
	private       ConcurrentHashMap<String, GHGist>         ghGistMap      = null;
	private       List<GHGist>                              ghGistList;
	private final Map<String, String>                       descriptionMap = new HashMap<>();
	private       ConcurrentHashMap<GistFileId, GHGistFile> ghGistFileMap  = new ConcurrentHashMap<>();

	private void accessingGitHub(boolean active) {
		Action.gitHubActivity(active);
	}

	public String getName() {
		try {
			return gitHub.getMyself().getName();
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return "";
	}

	private void notifyUser(String gistId) {
		new Thread(() -> {
			boolean proceed = gistId.isEmpty();
			if(!proceed) {
				if(descriptionMap.isEmpty()) {
					for(GHGist ghGist : ghGistList) {
						String ghGistId = ghGist.getGistId();
						String description = ghGist.getDescription();
						descriptionMap.put(ghGistId,description);
					}
				}
				String description = descriptionMap.get(gistId);
				if(!description.equals(Names.GITHUB_METADATA.Name())) proceed = true;
			}
			if(proceed) {
				WindowManager.updateGitHubLabel("Updating GitHub ", true);
				while(Action.accessingGitHub()) Action.sleep(100);
				Action.sleep(2000);
				WindowManager.updateGitHubLabel("",false);
			}
		}).start();
	}

	public boolean tokenValid(String token) {
		try {
			gitHub = new GitHubBuilder().withOAuthToken(token).build();
			boolean authenticated = gitHub.isCredentialValid();
			LiveSettings.setGitHubAuthenticated(authenticated);
			if(authenticated) {
				String lastUserId = AppSettings.get().lastGitHubUserId();
				String thisUserId = String.valueOf(gitHub.getMyself().getId());
				if (!lastUserId.equals(thisUserId)) AppSettings.set().dataSource(UISettings.DataSource.GITHUB);
				Action.loadJsonData();
				Action.setGitHubUserId(gitHub.getMyself().getId());
				ghGistList = gitHub.getMyself().listGists().toList();
			}
			return authenticated;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return false;
	}

	public boolean noGists() {
		try {
			return gitHub.getMyself().listGists().toList().size() == 0;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setProgress(double value) {
		Action.setProgress(value);
	}

	public boolean ghGistMapIsEmpty() {
		try {
			if (ghGistMap == null) return true;
			else return ghGistMap.size() < gitHub.getMyself().listGists().toList().size();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public String getGitHubFileContent(String gistId, String filename) {
		try {
			GHGist ghGist = gitHub.getGist(gistId);
			GHGistFile ghGistFile = ghGist.getFile(filename);
			return (ghGistFile == null) ? "" : ghGistFile.getContent();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getLocalGitHubFileContent(String gistId, String filename) {
		GHGistFile ghGistFile = getLocalGitHubFile(gistId, filename);
		return (ghGistFile == null) ? "" : ghGistFile.getContent();
	}

	public GHGistFile getLocalGitHubFile(String gistId, String filename) {
		for (GistFileId fileId : ghGistFileMap.keySet()) {
			String     thisGistId   = fileId.getGistId();
			String     thisFilename = fileId.getFileName();
			GHGistFile ghGistFile   = ghGistFileMap.get(fileId);
			if (thisGistId.equals(gistId) && thisFilename.equals(filename)) {
				return ghGistFile;
			}
		}
		return null;
	}

	public Map<String, GHGist> getNewGHGistMap() {
		try {
			List<GHGist> list = gitHub.getMyself().listGists().toList();
			double       size = list.size();
			ghGistMap     = new ConcurrentHashMap<>();
			ghGistFileMap = new ConcurrentHashMap<>();
			for (double x = 0; x < size; x++) {
				String gistId = list.get((int) x).getGistId();
				GHGist ghGist = gitHub.getGist(gistId);
				addGhGistToMap(ghGist);
				setProgress(x / (size - 2));
			}
			setProgress(0);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return ghGistMap;
	}

	private void addGhGistToMap(GHGist ghGist) {
		String gistId = ghGist.getGistId();
		if (ghGistMap != null) {
			ghGistMap.putIfAbsent(gistId, ghGist);
			for(String ghGistFilename : ghGist.getFiles().keySet()) {
				GHGistFile ghGistFile = ghGist.getFile(ghGistFilename);
				GistFileId fileId = new GistFileId(gistId,ghGistFilename);
				ghGistFileMap.put(fileId, ghGistFile);
			}
		}
	}

	private void removeGhGistFromMap(String gistId) {
		if(ghGistMap != null) {
			ghGistMap.remove(gistId);
			for (GistFileId gistFileId : ghGistFileMap.keySet()) {
				if (gistFileId.getGistId().equals(gistId)) {
					ghGistFileMap.remove(gistFileId);
				}
			}
		}
	}

	private void updateGHGistMap(String gistId) {
		new Thread(() -> {
			try {
				removeGhGistFromMap(gistId);
				GHGist ghGist = gitHub.getGist(gistId);
				addGhGistToMap(ghGist);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public Map<String, GHGist> getGHGistMap() {
		if (ghGistMap == null) getNewGHGistMap();
		return ghGistMap;
	}

	public void refreshAllData() {
		getNewGHGistMap();
		GistManager.startFromGit(ghGistMap, Source.RELOAD);
	}

	public GHGist getLocalGist(String gistId) {
		if (ghGistMap == null) {
			getNewGHGistMap();
		}
		return ghGistMap.getOrDefault(gistId,null);
	}

	public GHGist getGitHubGistByDescription(String description) {
		try {
			for (GHGist gist : gitHub.getMyself().listGists().toList()) {
				String gistId = gist.getGistId();
				GHGist ghGist = gitHub.getGist(gistId);
				if (ghGist.getDescription().equals(description)) {
					return ghGist;
				}
			}
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return null;
	}

	public Date getGistUpdateDate(String gistId) {
		try {
			java.util.Date date = gitHub.getGist(gistId).getUpdatedAt();
			return new Date(date.getTime());
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return null;
	}

	public void updateDescription(Gist gist) {
		String  gistId      = gist.getGistId();
		String  description = gist.getDescription();
		try {
			accessingGitHub(true);
			notifyUser(gistId);
			gitHub.getGist(gistId).update().description(description).update();
			updateGHGistMap(gistId);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
	}

	public void updateFile(GistFile file) {
		String  gistId   = file.getGistId();
		String  filename = file.getFilename();
		String  content  = file.getContent();
		updateFile(gistId,filename,content);
	}

	public void renameFile(String gistId, String oldFilename, String newFilename, String content) {
		try {
			accessingGitHub(true);
			notifyUser("");
			gitHub.getGist(gistId).update().updateFile(oldFilename, newFilename, content).update();
			updateGHGistMap(gistId);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
	}

	public void updateFile(String gistId, String filename, String content) {
		try {
			accessingGitHub(true);
			notifyUser(gistId);
			gitHub.getGist(gistId).update().updateFile(filename,content).update();
			updateGHGistMap(gistId);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
	}

	public void delete(Gist gist) {
		deleteGist(gist.getGistId());
	}

	public void delete(String gistId) {
		deleteGist(gistId);
	}

	private void deleteGist(String gistId) {
		try {
			accessingGitHub(true);
			notifyUser("");
			gitHub.getGist(gistId).delete();
			removeGhGistFromMap(gistId);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
	}

	public void deleteGistByDescription(String description) {
		try {
			accessingGitHub(true);
			notifyUser("");
			double total = gitHub.getMyself().listGists().toList().size();
			double count = 1;
			for(GHGist ghGist : gitHub.getMyself().listGists().toList()) {
				String gistId = ghGist.getGistId();
				GHGist gist = gitHub.getGist(gistId);
				String gistDescription = gist.getDescription();
				if(gistDescription.equals(description)) {
					deleteGist(gistId);
					Action.setProgress(0.0);
					return;
				}
				System.out.println(count);
				Action.setProgress(count/total);
				count++;
			}
			accessingGitHub(false);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void delete(GistFile file) {
		String  gistId   = file.getGistId();
		String  filename = file.getFilename();
		deleteGistFile(gistId,filename);
	}

	public void deleteGistFile(String gistId, String filename) {
		try {
			accessingGitHub(true);
			notifyUser(gistId);
			gitHub.getGist(gistId).update().deleteFile(filename).update();
			updateGHGistMap(gistId);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
	}

	private void checkMapForNull() {
		if(ghGistMap == null && LiveSettings.gitHubAuthenticated()) {
			getNewGHGistMap();
		}
	}

	public GHGistFile addFileToGist(String gistId, String filename, String content) {
		checkMapForNull();
		GHGistFile ghGistFile = null;
		try {
			accessingGitHub(true);
			notifyUser(gistId);
			gitHub.getGist(gistId).update().addFile(filename, content).update();
			ghGistFile = gitHub.getGist(gistId).getFile(filename);
			updateGHGistMap(gistId);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return ghGistFile;
	}

	public GHGist newGist(String description, String filename, String content, boolean isPublic) {
		GHGist ghGist = null;
		try {
			accessingGitHub(true);
			notifyUser("");
			ghGist = gitHub.createGist().public_(isPublic).description(description).file(filename, content).create();
			ghGist = gitHub.getGist(ghGist.getGistId());
			addGhGistToMap(ghGist);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return ghGist;
	}

	public Integer getForkCount(String gistId) {
		try {
			return gitHub.getGist(gistId).listForks().toList().size();
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return null;
	}

	private void throwAlert() {
		Platform.runLater(() -> CustomAlert.showWarning("There was a problem accessing GitHub. See help for more information."));
	}

}
