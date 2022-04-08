package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.enums.LoginStates;
import com.redmondsims.gistfx.enums.Names;
import com.redmondsims.gistfx.enums.OS;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.LoginWindow;
import javafx.application.Platform;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class GitHub {

	private       org.kohsuke.github.GitHub             gitHub;
	private       ConcurrentHashMap<String, GHGist>     ghGistMap            = null;
	private       List<GHGist>                          ghGistList;
	private final Map<String, String>                   descriptionMap       = new HashMap<>();
	private       ConcurrentHashMap<GistFileId, String> ghGistFileContentMap = new ConcurrentHashMap<>();
	private       boolean                               checkingInternet     = false;


	private void accessingGitHub(boolean active) {
		if(offLine()) return;
		Action.gitHubActivity(active);
	}

	private boolean offLine() {
		return LiveSettings.isOffline();
	}

	public String getName() {
		if(offLine()) return "Off Line Mode";
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
			if (ghGistList != null) {
				if(!proceed) {
					for (GHGist ghGist : ghGistList) {
						String ghGistId    = ghGist.getGistId();
						String description = ghGist.getDescription();
						descriptionMap.putIfAbsent(ghGistId, description);
					}
					String description = descriptionMap.get(gistId);
					if (!description.equals(Names.GITHUB_METADATA.Name()))
						proceed = true;
				}
				if(proceed) {
					WindowManager.updateGitHubLabel("Updating GitHub ", true);
					while(Action.accessingGitHub()) Action.sleep(100);
					Action.sleep(2000);
					WindowManager.updateGitHubLabel("",false);
				}
			}
		}).start();
	}

	private boolean internetPings() {
		boolean response = false;
		int success = 0;
		try {
			InetAddress internet = InetAddress.getByAddress(new byte[]{1,1,1,1});
			LoginWindow.updateProgress("Checking Internet ",true);
			checkingInternet = true;
			new Thread(() -> {
				while(checkingInternet) {
					LoginWindow.updateProgress(".",true);
					Action.sleep(3000);
				}
			}).start();
			for (int x = 0; x < 5; x++) {
				if(ping("1.1.1.1",2)) {
					success++;
				}
			}
			response = success >= 4;
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		checkingInternet = false;
		return response;
	}

	private boolean ping(String host, int pingCount) throws IOException, InterruptedException {
		String countSwitch = LiveSettings.getOS().equals(OS.WINDOWS) ? "-n" : "-c";
		String count = String.valueOf(pingCount);
		ProcessBuilder processBuilder = new ProcessBuilder("ping", countSwitch, count, host);
		Process proc = processBuilder.start();
		int returnVal = proc.waitFor();
		return returnVal == 0;
	}

	public LoginStates tokenValid(String token) {
		LoginStates response = LoginStates.TOKEN_FAILURE;
		try {
			gitHub = new GitHubBuilder().withOAuthToken(token).build();
			boolean authenticated = gitHub.isCredentialValid();
			if(!authenticated) {
				System.out.println("Failed: " + token);
			}
			LiveSettings.setGitHubAuthenticated(authenticated);
			if(authenticated) {
				response = LoginStates.TOKEN_VALID;
				Action.loadMetaData();
				Action.setGitHubUserId(gitHub.getMyself().getId());
				ghGistList = gitHub.getMyself().listGists().toList();
			}
			else {
				LoginWindow.updateProgress("clearToken failed ");
				if (!internetPings()) {
					response = LoginStates.INTERNET_DOWN;
				}
			}
		}
		catch (SocketException se) {
			response = LoginStates.INTERNET_DOWN;
			System.out.println(se.getMessage());
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return response;
	}

	public boolean noGists() {
		if(offLine()) return true;
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
		if(offLine()) return true;
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
		if(offLine()) return "";
		GHGist ghGist = ghGistMap.get(gistId);
		return ghGist.getFiles().get(filename).getContent();
	}

	public Map<String, GHGist> getNewGHGistMap() {
		try {
			List<GHGist> ghGists = gitHub.getMyself().listGists().toList();
			double       size = ghGists.size();
			ghGistMap            = new ConcurrentHashMap<>();
			ghGistFileContentMap = new ConcurrentHashMap<>();
			int x=0;
			for(GHGist ghGist : ghGists) {
				String gistId = ghGist.getGistId();
				GHGist realGHGist = gitHub.getGist(gistId);
				mapGHGist(gistId, realGHGist);
				x++;
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

	private void mapGHGist(String gistId, GHGist ghGist) {
		if(offLine()) return;
		if (ghGistMap != null) {
			if (!ghGistMap.containsKey(gistId)) {
				ghGistMap.put(gistId, ghGist);
				for (String ghGistFilename : ghGist.getFiles().keySet()) {
					GHGistFile ghGistFile = ghGist.getFile(ghGistFilename);
					GistFileId fileId     = new GistFileId(gistId, ghGistFilename);
					ghGistFileContentMap.put(fileId, ghGistFile.getContent());
				}
			}
		}
	}

	private void removeGhGistFromMap(String gistId) {
		if(offLine()) return;
		if(ghGistMap != null) {
			ghGistMap.remove(gistId);
			for (GistFileId gistFileId : ghGistFileContentMap.keySet()) {
				if (gistFileId.getGistId().equals(gistId)) {
					ghGistFileContentMap.remove(gistFileId);
				}
			}
		}
	}

	private void updateGHGistMap(String gistId) {
		if(offLine()) return;
		new Thread(() -> {
			try {
				removeGhGistFromMap(gistId);
				GHGist ghGist = gitHub.getGist(gistId);
				mapGHGist(gistId, ghGist);
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
		if(offLine()) return;
		getNewGHGistMap();
		GistManager.startFromGit(ghGistMap, Source.RELOAD);
	}

	public GHGist getLocalGist(String gistId) {
		if(offLine()) return null;
		if (ghGistMap == null) {
			getNewGHGistMap();
		}
		return ghGistMap.getOrDefault(gistId,null);
	}

	public GHGist getGitHubGistByDescription(String description) {
		if(offLine()) return null;
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
		if(offLine()) return null;
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
		if(offLine()) return;
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

	public void renameFile(String gistId, String oldFilename, String newFilename, String content) {
		if(offLine()) return;
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

	public boolean updateFile(String gistId, String filename, String content) {
		if(offLine()) return true;
		try {
			accessingGitHub(true);
			notifyUser(gistId);
			gitHub.getGist(gistId).update().updateFile(filename,content).update();
			updateGHGistMap(gistId);
			accessingGitHub(false);
			return true;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return false;
	}

	public void delete(Gist gist) {
		deleteGist(gist.getGistId());
	}

	public void delete(String gistId) {
		deleteGist(gistId);
	}

	private void deleteGist(String gistId) {
		if(offLine()) return;
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
		if(offLine()) return;
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
		if(offLine()) return;
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
		if(offLine()) return;
		if(ghGistMap == null && LiveSettings.gitHubAuthenticated()) {
			getNewGHGistMap();
		}
	}

	public GHGistFile addFileToGist(String gistId, String filename, String content) {
		if(offLine()) return null;
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
		if(offLine()) return null;
		GHGist ghGist = null;
		try {
			accessingGitHub(true);
			notifyUser("");
			ghGist = gitHub.createGist().public_(isPublic).description(description).file(filename, content).create();
			String gistId = ghGist.getGistId();
			mapGHGist(gistId, ghGist);
			accessingGitHub(false);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return ghGist;
	}

	public Integer getForkCount(String gistId) {
		if(offLine()) return -1;
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
