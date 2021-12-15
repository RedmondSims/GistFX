package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.github.gist.Gist;
import com.redmondsims.gistfx.ui.alerts.CustomAlert;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Json {

	private enum Source {
		LOCAL_FILE,
		GIST_FILE,
		SQLITE
	}

	private final String              gistDescription = "GistFX!Data!";
	private final Timer               syncGitHubTimer = new Timer();
	private final String              gistFilename    = "GistFXData.json";
	private final Gson                gson            = new GsonBuilder().setPrettyPrinting().create();
	private       Map<String, String> nameMap         = new HashMap<>();
	private       JsonTemplate        jsonTemplate    = new JsonTemplate();
	private       long                lastChangeTime  = System.currentTimeMillis();
	private       File                jsonLocalFile;
	private       GHGist              gist;

	public void initPath(String path) {
		File jsonPath = new File(path, "Json");
		jsonLocalFile = new File(jsonPath, "NameMap.json");
		try {
			if (!jsonPath.exists()) FileUtils.createParentDirectories(jsonLocalFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		syncGitHubTimer.scheduleAtFixedRate(syncWithGitHub(), 10000, 10000);
	}

	public void loadJsonIntoDatabase() {
		getBestNameMap();
		for (String gistId : nameMap.keySet()) {
			String name = nameMap.get(gistId);
			Action.addToSQLNameMap(gistId, name);
		}
	}

	public void loadBestNameMap() {
		getBestNameMap();
	}

	public void setName(String gistId, String newName) {
		if (nameMap.containsKey(gistId)) {
			String currentName = nameMap.get(gistId);
			if (currentName.equals(newName)) return;
			else nameMap.replace(gistId, newName);
		}
		else {
			nameMap.put(gistId, newName);
		}
		saveToLocalFile();
		lastChangeTime = System.currentTimeMillis();
	}

	public void removeName(String gistId) {
		nameMap.remove(gistId);
		saveToLocalFile();
		lastChangeTime = System.currentTimeMillis();
	}

	public void deleteLocalJsonFile() {
		if (jsonLocalFile != null) {
			if (jsonLocalFile.exists()) {
				try {
					FileUtils.forceDelete(jsonLocalFile);
				}
				catch (IOException e) {
					e.printStackTrace();
					CustomAlert.showExceptionDialog(e,"Problem deleting local json file.");
				}
			}
		}
	}

	public void deleteGistFile() {
		if(gistExists()) {
			if (!Action.delete(gist.getGistId())) {
				CustomAlert.showWarning("There was a problem deleting the Gist that contains your Gist Names. See help for more information.");
			}
		}
	}

	public String getGistName() {
		return gistDescription;
	}

	public void accommodateUserSettingChange() {
		if (LiveSettings.useJsonGist) createGist();
		else deleteGist();
	}

	private void getBestNameMap() {
		GHGistFile gistFile = null;
		if(LiveSettings.useJsonGist) {
			if (gistExists()) {
				gistFile = gist.getFile(gistFilename);
			}
		}

		boolean localFileExists = jsonLocalFile.exists();
		boolean gistFileExists = gistFile != null;

		Source dataSource = localFileExists ? Source.LOCAL_FILE : gistFileExists ? Source.GIST_FILE : Source.SQLITE;

		if (localFileExists && gistFileExists) {
			try {
				BasicFileAttributes attr = Files.readAttributes(jsonLocalFile.toPath(), BasicFileAttributes.class);
				Date gistUpDate = gist.getUpdatedAt();
				Date fileUpDate = new Date(attr.lastModifiedTime().toMillis());
				if(gistUpDate.after(fileUpDate)) {
					dataSource = Source.GIST_FILE;
				}
			}
			catch (IOException e) {e.printStackTrace();}
		}

		nameMap = null;

		switch(dataSource) {
			case GIST_FILE -> {
				String jsonString = gistFile.getContent();
				this.jsonTemplate = gson.fromJson(jsonString, JsonTemplate.class);
				nameMap = this.jsonTemplate.getNameMap();
			}

			case LOCAL_FILE -> {
				String jsonString = Action.loadTextFile(jsonLocalFile);
				this.jsonTemplate = gson.fromJson(jsonString, JsonTemplate.class);
				nameMap = this.jsonTemplate.getNameMap();
			}

			case SQLITE -> nameMap = Action.getNameMapFromSQL();

		}
		if (nameMap == null) {
			nameMap = new HashMap<>();
		}
		validateGistState();
	}

	private void validateGistState() {
		if (LiveSettings.useJsonGist) {
			if (!gistExists()) {
				new Thread(() -> {
					try {
						TimeUnit.MILLISECONDS.sleep(10000);
						createGist();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}).start();
			}
		}
	}

	private TimerTask syncWithGitHub() {
		/*
		 * The reason why this method exists, is because when I first tested the use of the Gist as a location to store the custom names that the user assigns,
		 * When I had the Gist update after every time the name was modified, simply loading the names from the local file or the database created 20 versions
		 * of the GistFile in the GitHub account. So I felt it would be more prudent to have a method that only commits changes to the GistFile after some time
		 * has elapsed once a change has occurred, and 45 seconds seemed as good as any number and was chosen under the scenario when the user might be making
		 * name changes to their gists serially, since giving them 45 seconds between their next name change seemed reasonable and will reset the clock so that
		 * the Gist commit won't happen until 45 seconds after their last change (unless they make yet another change obviously).
		 *
		 * The whole point of this method is to reduce the number of times we commit changes to the GitHub Gist so that our Gist versions don't get quickly
		 * out of control
		 */
		return new TimerTask() {
			@Override public void run() {
				long now = System.currentTimeMillis();
				boolean update = (now - lastChangeTime) > 45000;
				if (LiveSettings.useJsonGist && update) {
					lastChangeTime = now;
					boolean updateGitHub = false;
					GHGistFile ghGistFile;
					if(gistExists()) {
						ghGistFile = gist.getFile(gistFilename);
					}
					else return;
					String gistJsonString = ghGistFile.getContent();
					JsonTemplate tempJson = gson.fromJson(gistJsonString, JsonTemplate.class);
					Map<String,String> gistNameMap = tempJson.getNameMap();
					for (String gistId : nameMap.keySet()) {
						if (!gistNameMap.containsKey(gistId)) {
							updateGitHub = true;
							break;
						}
						String thisName = nameMap.get(gistId);
						String gistName = gistNameMap.get(gistId);
						if (!thisName.equals(gistName)) {
							updateGitHub = true;
							break;
						}
					}
					if (updateGitHub) {
						saveToGitHub();
					}
				}
			}
		};
	}

	private void saveToLocalFile() {
		jsonTemplate.setNameMap(nameMap);
		String jsonText = gson.toJson(this.jsonTemplate);
		if (jsonLocalFile != null) {
			Action.writeToTextFile(jsonLocalFile,jsonText);
		}
	}

 	private void saveToGitHub() {
		this.jsonTemplate.setNameMap(nameMap);
		String jsonString = gson.toJson(this.jsonTemplate);
		if (!Action.updateGistFile(gist.getGistId(),gistFilename,jsonString)) {
			CustomAlert.showWarning("Failed saving NameMap to GitHub. See Help for more info.");
		}
	}

	private boolean gistExists( ) {
		if (gist == null) {
			gist = Action.getGistByDescription(gistDescription);
		}
		return gist != null;
	}

	private void createGist() {
		if (LiveSettings.useJsonGist) {
			String jsonString = gson.toJson(this.jsonTemplate);
			gist = Action.addGistToGitHub(gistDescription, gistFilename, jsonString, false);
		}
	}

	private void deleteGist() {
		if(!LiveSettings.useJsonGist){
			if (!Action.delete(gist.getGistId())) {
				CustomAlert.showWarning("There was a problem deleting the Gist that contains your Gist Names. See help for more information.");
				return;
			}
			gist = null;
		}
	}
}
