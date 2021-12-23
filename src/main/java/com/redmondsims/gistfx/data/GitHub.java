package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings.DataSource;
import com.redmondsims.gistfx.ui.LoginWindow;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class GitHub {

	public final  DoubleProperty            progress  = new SimpleDoubleProperty(0);
	public final  BooleanProperty           uploading = new SimpleBooleanProperty(false);
	private final DataSource                LOCAL     = DataSource.LOCAL;
	private final DataSource                GITHUB    = DataSource.GITHUB;
	private       org.kohsuke.github.GitHub gitHub;
	private       Map<String, GHGist>       ghGistMap = null;

	private void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void notifyUser() {
		new Thread(() -> {
			Platform.runLater(() -> uploading.setValue(true));
			sleep(2500);
			Platform.runLater(() -> uploading.setValue(false));
		}).start();
	}

	public String getName() {
		try {
			return gitHub.getMyself().getName();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public boolean tokenValid(String token) {
		try {
			gitHub = new GitHubBuilder().withOAuthToken(token).build();
			return gitHub.isCredentialValid();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void loadData() {
		DataSource dataSource = LiveSettings.getDataSource();
		if (dataSource.equals(LOCAL)) {
			GistManager.startFromDatabase();
		}
		if (dataSource.equals(GITHUB)) {
			LoginWindow.updateProcess("Downloading Gist Objects");
			getNewGHGistMap();
			GistManager.startFromGit(ghGistMap, State.GITHUB);
		}
	}

	private void setProgress(double value) {
		Platform.runLater(() -> progress.setValue(value));
	}

	public Map<String, GHGist> getNewGHGistMap() {
		try {
			List<GHGist> list = gitHub.getMyself().listGists().toList();
			double       size = list.size();
			ghGistMap = new HashMap<>();
			for (double x = 0; x < size; x++) {
				String gistId = list.get((int) x).getGistId();
				GHGist ghGist = gitHub.getGist(gistId);
				ghGistMap.put(gistId, ghGist);
				setProgress(x / (size - 2));
			}
			setProgress(0);
		}
		catch (IOException ignored) {
			//    e.printStackTrace();
		}
		return ghGistMap;
	}

	public void refreshAllData() {
		getNewGHGistMap();
		GistManager.startFromGit(ghGistMap,State.RELOAD);
	}

	public GHGist getGist(String gistId) {
		try {
			return gitHub.getGist(gistId);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public GHGist getGistByDescription(String description) {
		try {
			for (GHGist gist : gitHub.getMyself().listGists().toList()) {
				String gistId = gist.getGistId();
				GHGist finalGist = gitHub.getGist(gistId);
				if (finalGist.getDescription().equals(description)) {
					return finalGist;
				}
			}
		}
		catch (IOException e) {
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
			e.printStackTrace();
		}
		return null;
	}

	public GHGistFile getGistFile(String gistId, String filename) {
		try {
			return gitHub.getGist(gistId).getFile(filename);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean update(Gist gist) {
		String  gistId      = gist.getGistId();
		String  description = gist.getDescription();
		boolean success     = false;
		try {
			notifyUser();
			gitHub.getGist(gistId).update().description(description).update();
			success = true;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return success;
	}

	public boolean update(GistFile file) {
		boolean success  = false;
		String  gistId   = file.getGistId();
		String  filename = file.getFilename();
		String  content  = file.getContent();
		try {
			notifyUser();
			gitHub.getGist(gistId).update().updateFile(filename, content).update();
			success = true;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return success;
	}

	public boolean renameFile(GistFile file) {
		boolean success     = false;
		String  gistId      = file.getGistId();
		String  oldFilename = file.getOldFilename();
		String  newFilename = file.getNewFilename();
		String  content     = file.getContent();
		try {
			notifyUser();
			gitHub.getGist(gistId).update().addFile(newFilename, content).update();
			gitHub.getGist(gistId).update().deleteFile(oldFilename).update();
			success = true;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return success;
	}

	public boolean updateFile(String gistId, String filename, String content) {
		boolean success     = false;
		try {
			notifyUser();
			gitHub.getGist(gistId).update().updateFile(filename,content).update();

			success = true;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return success;
	}

	public boolean delete(Gist gist) {
		return deleteGist(gist.getGistId());
	}

	public boolean delete(String gistId) {
		return deleteGist(gistId);
	}

	public boolean delete(GistFile file) {
		boolean success  = false;
		String  gistId   = file.getGistId();
		String  filename = file.getFilename();
		try {
			notifyUser();
			gitHub.getGist(gistId).update().deleteFile(filename).update();
			success = true;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return success;
	}

	private boolean deleteGist(String gistId) {
		boolean success = false;
		try {
			notifyUser();
			gitHub.getGist(gistId).delete();
			if (ghGistMap != null) ghGistMap.remove(gistId);
			success = true;
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return success;
	}

	public GHGist newGist(String description, String filename, String content, boolean isPublic) {
		GHGist ghGist = null;
		try {
			notifyUser();
			ghGist = gitHub.createGist().public_(isPublic).description(description).file(filename, content).create();
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return ghGist;
	}

	public GHGistFile addFileToGist(String gistId, String filename, String content) {
		GHGistFile ghGistFile = null;
		try {
			notifyUser();
			gitHub.getGist(gistId).update().addFile(filename, content).update();
			ghGistFile = gitHub.getGist(gistId).getFile(filename);
		}
		catch (IOException e) {
			throwAlert();
			e.printStackTrace();
		}
		return ghGistFile;
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
		CustomAlert.showWarning("There was a problem accessing GitHub. See help for more information.");
	}

}
