package com.redmondsims.gistfx.data.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.kohsuke.github.GHGist;

import java.sql.Timestamp;
import java.util.*;

import static com.redmondsims.gistfx.enums.Names.GITHUB_METADATA;
import static com.redmondsims.gistfx.preferences.UISettings.DataSource.GITHUB;

public class MetaManager {

	private              Categories       CATEGORIES   = new Categories();
	private              Names            NAMES        = new Names();
	private              FileDescriptions DESCRIPTIONS = new FileDescriptions();
	private              Hosts            HOSTS        = new Hosts();
	private              GHGist           ghGist;
	private              MetadataJson     metadataJson;
	private final        Gson             gson         = new GsonBuilder().setPrettyPrinting().create();
	private static final Gson             staticGson   = new GsonBuilder().setPrettyPrinting().create();
	private              boolean          makingNew    = false;

 	private void makeNewGist() {
		if (LiveSettings.isOffline()) return;
		makingNew = true;
		Action.updateProgress("Creating new Metadata Gist");
		if(Action.ghGistMapIsEmpty()){
			Action.getNewGHGistMap();
		}
		while (Action.ghGistMapIsEmpty()) {
			Action.sleep(150L);
		}
		Map<String, GHGist> ghGistMap = Action.getGHGistMap();
		if (!NAMES.hasData()) {
			for (String gistId : ghGistMap.keySet()) {
				GHGist ghGist          = ghGistMap.get(gistId);
				String gistDescription = ghGist.getDescription();
				NAMES.setName(gistId, gistDescription);
			}
		}
		metadataJson = new MetadataJson(NAMES.getMap(),
										CATEGORIES.getList(),
										CATEGORIES.getMap(),
										DESCRIPTIONS.getMap(),
										HOSTS.getList());
		metadataJson.setLastGistRecreate();
		String jsonMetadata = gson.toJson(metadataJson);
		ghGist = Action.getNewGist(MetadataJson.GistDescription, MetadataJson.FileName, jsonMetadata, false);
		saveData();
		makingNew = false;
	}

	private void reCreateGist() {
		if (LiveSettings.isOffline()) return;
		 if (metadataJson != null) {
			 Action.updateProgress("Refreshing GitHub Metadata");
			 Action.deleteGistByDescription(GITHUB_METADATA.Name());
			 metadataJson.setLastGistRecreate();
			 String jsonString = gson.toJson(metadataJson);
			 ghGist = Action.getNewGist(MetadataJson.GistDescription, MetadataJson.FileName, jsonString, false);
			 saveData();
		 }
	}

	/**
	 * Local and GitHub Data File Methods
	 */

	private String getGitHubMetadata() {
		String gitHubMetadata = "";
		ghGist       = Action.getGistByDescription(MetadataJson.GistDescription);
		if (ghGist != null) {
			gitHubMetadata = ghGist.getFile(MetadataJson.FileName).getContent();
		}
		return gitHubMetadata;
	}

	private void getNewestMetadataData() {
		if (LiveSettings.isOffline()) {
			String json = AppSettings.get().metadata();
			if(json.isEmpty()) {
				json = Action.getSQLMetadata();
			}
			if (!json.isEmpty()) {
				metadataJson = gson.fromJson(json, MetadataJson.class);
				if(metadataJson == null) {
					System.out.println("Metadata null");
					System.exit(0);
				}
				CATEGORIES   = metadataJson.getCategories();
				NAMES        = metadataJson.getNames();
				DESCRIPTIONS = metadataJson.getFileDescriptions();
				HOSTS        = metadataJson.getHosts();
			}
			return;
		}
		String       gitHubJsonData   = getGitHubMetadata();
		String       settingsJsonData = AppSettings.get().metadata();
		String       SQLJsonData      = Action.getSQLMetadata();
		MetadataJson gitHubMetadata;
		MetadataJson SQLMetadata;
		MetadataJson prefsMetadata;
		boolean      haveGitHubJson   = !gitHubJsonData.equals("");
		boolean      haveLocalJson    = !settingsJsonData.equals("");
		boolean      haveSQLJson      = !SQLJsonData.equals("");
		Map<Integer, Long> timeMap = new HashMap<>();
		metadataJson = new MetadataJson(NAMES.getMap(),
										CATEGORIES.getList(),
										CATEGORIES.getMap(),
										DESCRIPTIONS.getMap(),
										HOSTS.getList());
		if (haveGitHubJson) {
			gitHubMetadata = gson.fromJson(gitHubJsonData, MetadataJson.class);
			timeMap.put(1,gitHubMetadata.getTimestamp());
		}
		if (haveSQLJson) {
			SQLMetadata = gson.fromJson(SQLJsonData, MetadataJson.class);
			timeMap.put(2,SQLMetadata.getTimestamp());
		}
		if (haveLocalJson) {
			prefsMetadata = gson.fromJson(settingsJsonData, MetadataJson.class);
			timeMap.put(3,prefsMetadata.getTimestamp());
		}
		String finalJsonString = "";
		if (timeMap.size() > 0) {
			switch (findNewestTime(timeMap)) {
				case 1 -> finalJsonString = gitHubJsonData;
				case 2 -> finalJsonString = SQLJsonData;
				case 3 -> finalJsonString = settingsJsonData;
			}
		}
		if (!finalJsonString.equals("")) {
			metadataJson = gson.fromJson(finalJsonString, MetadataJson.class);
			CATEGORIES   = metadataJson.getCategories();
			NAMES        = metadataJson.getNames();
			DESCRIPTIONS = metadataJson.getFileDescriptions();
			HOSTS        = metadataJson.getHosts();
			Date     reWriteDate = metadataJson.getLastGistRecreate();
			DateTime then        = new DateTime(reWriteDate);
			DateTime now         = new DateTime(new Date());
			Instant  thenInstant = then.toInstant();
			Instant  nowInstant  = now.toInstant();
			long     thenMillis  = thenInstant.getMillis();
			long     nowMillis   = nowInstant.getMillis();
			Duration duration    = new Duration(nowMillis - thenMillis);
			Days     days        = duration.toStandardDays();
			if (days.getDays() >= 7) {
				reCreateGist();
			}
		}
		else { //We have nothing!
			makingNew = true;
			makeNewGist();
		}
		if(!makingNew) {
			new Thread(this::saveData).start();
		}
	}

	private Integer findNewestTime(Map<Integer,Long> timeMap) {
		Long finalTime = null;
		Integer finalIndex = null;
		for(Integer index : timeMap.keySet()) {
			Long time = timeMap.get(index);
			if(finalTime == null && finalIndex == null) {
				finalTime = time;
				finalIndex = index;
				continue;
			}
			if (new Timestamp(time).toInstant().isAfter(new Timestamp(finalTime).toInstant())) {
				finalTime = time;
				finalIndex = index;
			}
		}
		return finalIndex;
	}

	private void saveData() {
		metadataJson = new MetadataJson(NAMES.getMap(),
										CATEGORIES.getList(),
										CATEGORIES.getMap(),
										DESCRIPTIONS.getMap(),
										HOSTS.getList());
		metadataJson.saveToSQL();
		metadataJson.saveToGitHub(ghGist.getGistId());
		metadataJson.saveToSettings();
	}

	public void deleteGitHubMetadata() {
		Action.deleteGistByDescription(MetadataJson.GistDescription);
	}

	public void loadMetaData() {
		getNewestMetadataData();
	}

	private void loadGitHubData() {
		if (LiveSettings.isOffline()) {
			String json = Action.getSQLMetadata();
			metadataJson = gson.fromJson(json, MetadataJson.class);
			CATEGORIES   = metadataJson.getCategories();
			NAMES        = metadataJson.getNames();
			DESCRIPTIONS = metadataJson.getFileDescriptions();
			HOSTS        = metadataJson.getHosts();
			return;
		}
		ghGist = Action.getGistByDescription(MetadataJson.GistDescription);
		if (ghGist != null) {
			String metadataJson = ghGist.getFile(MetadataJson.FileName).getContent();
			this.metadataJson = gson.fromJson(metadataJson, MetadataJson.class);
			CATEGORIES        = this.metadataJson.getCategories();
			NAMES             = this.metadataJson.getNames();
			DESCRIPTIONS      = this.metadataJson.getFileDescriptions();
			HOSTS             = this.metadataJson.getHosts();
			if(this.metadataJson.getLastGistRecreate() == null) {
				reCreateGist();
			}
			saveData();
		}
		else {
			makeNewGist();
		}
	}

	/**
	 * Name Methods
	 */

	public void deleteGistMetadata(String gistId) {
		NAMES.deleteName(gistId);
		CATEGORIES.unMapCategory(gistId);
		DESCRIPTIONS.removeAllGistFiles(gistId);
		saveData();
	}

	public void setName(String gistId, String name) {
		NAMES.setName(gistId,name.trim());
		saveData();
	}

	public String getName(String gistId) {
		return NAMES.getName(gistId).trim();
	}

	public String getGistIdByName (String name) {
		 return NAMES.getGistId(name);
	}

	public Collection<String> getNameList () {
		 return NAMES.getList();
	}

	/**
	 * Category Methods
	 */

	public void addCategoryName(String categoryName) {
		CATEGORIES.addCategory(categoryName.trim());
		saveData();
	}

	public void deleteCategoryName(String categoryName) {
		CATEGORIES.deleteCategory(categoryName);
		saveData();
	}

	public void mapCategoryNameToGist(String gistId, String categoryName) {
		CATEGORIES.mapCategory(gistId,categoryName.trim());
		saveData();
	}

	public void changeCategoryName(String oldName, String newName) {
		CATEGORIES.renameCategory(oldName,newName.trim());
		saveData();
	}

	public String getGistCategoryName(String gistId) {
		return CATEGORIES.getGistCategoryName(gistId);
	}

	public ChoiceBox<String> getGistCategoryBox() {
		 return new ChoiceBox<>(getCategoryList());
	}

	public ObservableList<String> getCategoryList() {
		return FXCollections.observableArrayList(CATEGORIES.getList());
	}

	public List<Gist> getGistsInCategory(String category) {
		List<String> gistIdList = CATEGORIES.getGistIdsInCategory(category);
		List<Gist> gistList = new ArrayList<>();
		for(String gistId : gistIdList) {
			gistList.add(GistManager.getGist(gistId));
		}
		return gistList;
	}

	/**
	 * File Description Methods
	 */

	public void setFileDescription(GistFile gistFile, String description) {
		DESCRIPTIONS.setDescription(gistFile,description);
		saveData();
	}

	public void setFileDescription(String gistId, String filename, String description) {
		System.out.println("filename: " + filename + "\nDescription: " + description);
		DESCRIPTIONS.setDescription(gistId,filename,description);
		saveData();
	}

	public String getFileDescription(GistFile gistFile) {
		return DESCRIPTIONS.getDescription(gistFile);
	}

	public void deleteFileDescription(String gistId, String filename) {
		DESCRIPTIONS.deleteDescription(gistId,filename);
	}

	/**
	 * Host Methods
	 */

	public void addHost(String host) {
		HOSTS.addHost(host);
		saveData();
	}

	public Collection<String> getHostCollection() {
		return HOSTS.getCollection();
	}

	public void removeHost(String host) {
		HOSTS.removeHost(host);
		saveData();
	}

	public void renameHost(String oldName, String newName) {
		HOSTS.renameHost(oldName,newName);
		saveData();
	}

	public void setGitHubUserId(Long gitHubUserId) {
		new Thread(() -> {
			while(makingNew) Action.sleep(100);
			String thisUserId = String.valueOf(gitHubUserId);
			String lastUserId = AppSettings.get().lastGitHubUserId();
			if(lastUserId.isEmpty()) {
				AppSettings.set().lastGitHubUserId(thisUserId);
			}
			else if (!thisUserId.equals(lastUserId)) {
				Action.wipeSQLAndLocalData();
				loadGitHubData();
				AppSettings.set().dataSource(GITHUB);
				LiveSettings.applyAppSettings();
				AppSettings.set().lastGitHubUserId(thisUserId);
			}
		}).start();
	}

	/**
	 * Global Methods
	 */

	public void changeGistId(String oldGistId, String newGistId) {
		CATEGORIES.changeGistId(oldGistId,newGistId);
		DESCRIPTIONS.changeGistId(oldGistId,newGistId);
		NAMES.changeGistId(oldGistId,newGistId);
		saveData();
	}

	public static void getSampleJson() {
		MetadataJson metadataJson = new MetadataJson(new Names().getMap(), new Categories().getList(), new Categories().getMap(), new FileDescriptions().getMap(), new Hosts().getList());
		String       jsonString   = staticGson.toJson(metadataJson);
		System.out.println(jsonString);
	}

}
