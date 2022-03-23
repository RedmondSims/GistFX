package com.redmondsims.gistfx.data.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import org.kohsuke.github.GHGist;

import java.util.*;

import static com.redmondsims.gistfx.enums.Names.GIST_DATA_DESCRIPTION;
import static com.redmondsims.gistfx.preferences.UISettings.DataSource.GITHUB;

public class Json {

	private enum Source {
		LOCAL_FILE,
		GIST_FILE,
		SQLITE
	}

	private       Categories       CATEGORIES      = new Categories();
	private       Names            NAMES           = new Names();
	private       FileDescriptions DESCRIPTIONS    = new FileDescriptions();
	private       Hosts            HOSTS           = new Hosts();
	private       GHGist           ghGist;
	private       MetadataFile     metadataFile;
	private final String           gistDescription = GIST_DATA_DESCRIPTION.Name();
	private final Gson             gson            = new GsonBuilder().setPrettyPrinting().create();
	private       Timer            saveTimer;
	private       Long             gitHubUserId    = null;

 	private void makeNewGist() {
		new Thread(() -> {
			while(Action.ghGistMapIsEmpty() && LiveSettings.gitHubAuthenticated()) {
				Action.sleep(150L);
			}
			Map<String,GHGist> ghGistMap = Action.getGHGistMap();
			for (String gistId : ghGistMap.keySet()) {
				GHGist ghGist = ghGistMap.get(gistId);
				String gistDescription = ghGist.getDescription();
				NAMES.setName(gistId,gistDescription);
			}
			metadataFile = new MetadataFile(NAMES.getMap(),
											CATEGORIES.getList(),
											CATEGORIES.getMap(),
											DESCRIPTIONS.getMap(),
											HOSTS.getList(),
											gitHubUserId);
			String jsonMetadata = gson.toJson(metadataFile);
			ghGist = Action.getNewGist(gistDescription, MetadataFile.FileName, jsonMetadata, false);
			saveData();
		}).start();
	}

	/**
	 * Local and GitHub Data File Methods
	 */

	public void getData() {
		UISettings.DataSource dataSource           = LiveSettings.getDataSource();
		String                customDataGitHubJson = "";
		String                customDataLocalJson  = AppSettings.get().metadata();
		String                customDataSQLJson    = Action.getSQLMetadata();
		MetadataFile          customGitHub;
		MetadataFile          customSQL;
		MetadataFile          customLocal;
		boolean               haveGitHubData       = false;
		boolean               haveLocalData        = !customDataLocalJson.equals("");
		boolean               haveSQLData          = !customDataSQLJson.equals("");

		Map<Integer, Date> dateMap = new HashMap<>();
		metadataFile = new MetadataFile(NAMES.getMap(),
										CATEGORIES.getList(),
										CATEGORIES.getMap(),
										DESCRIPTIONS.getMap(),
										HOSTS.getList(),
										gitHubUserId);
		ghGist = Action.getGistByDescription(gistDescription);
		if (dataSource.equals(GITHUB)) {
			while (!LiveSettings.gitHubAuthenticated()) {
				Action.sleep(100);
			}
			if (ghGist != null) {
				customDataGitHubJson = ghGist.getFile(MetadataFile.FileName).getContent();
				haveGitHubData       = true;
			}
		}
		if (haveGitHubData) {
			customGitHub = gson.fromJson(customDataGitHubJson, MetadataFile.class);
			dateMap.put(1,customGitHub.getDate());
		}
		if (haveSQLData) {
			customSQL = gson.fromJson(customDataSQLJson, MetadataFile.class);
			dateMap.put(2,customSQL.getDate());
		}
		if (haveLocalData) {
			customLocal = gson.fromJson(customDataLocalJson, MetadataFile.class);
			dateMap.put(3,customLocal.getDate());
		}
		String finalJsonString = "";
		if (dateMap.size() > 1) {
			switch (findNewestDate(dateMap)) {
				case 1 -> finalJsonString = customDataGitHubJson;
				case 2 -> finalJsonString = customDataSQLJson;
				case 3 -> finalJsonString = customDataLocalJson;
			}
		}
		else if (dateMap.size() == 1) {
			for(Integer index : dateMap.keySet()) {
				switch (index) {
					case 1 -> finalJsonString = customDataGitHubJson;
					case 2 -> finalJsonString = customDataSQLJson;
					case 3 -> finalJsonString = customDataLocalJson;
				}
			}
		}
		if (!finalJsonString.equals("")) {
			metadataFile = gson.fromJson(finalJsonString, MetadataFile.class);
			CATEGORIES   = metadataFile.getCategories();
			NAMES        = metadataFile.getNames();
			DESCRIPTIONS = metadataFile.getFileDescriptions();
			HOSTS        = metadataFile.getHosts();
		}
		else { //We have nothing!
			Map<String,GHGist> ghGistMap = Action.getNewGhGistMap();
			for (String gistId : ghGistMap.keySet()) {
				String description = ghGistMap.get(gistId).getDescription();
				NAMES.setName(gistId,description);
			}
		}
		if ((!haveGitHubData && dataSource.equals(GITHUB)) || ghGist == null) {
			makeNewGist();
		}
		new Thread(() -> {
			Action.sleep(5000L);
			saveData();
		}).start();
	}

	private Integer findNewestDate(Map<Integer,Date> dateMap) {
		SortedSet<Date> dates = new TreeSet<>(dateMap.values());
		Date finalDate = dates.first();
		for(Integer index : dateMap.keySet()) {
			Date date = dateMap.get(index);
			if (date.equals(finalDate)) {
				return index;
			}
		}
		return null;
	}

	private void saveData() {
		metadataFile = new MetadataFile(NAMES.getMap(),
										CATEGORIES.getList(),
										CATEGORIES.getMap(),
										DESCRIPTIONS.getMap(),
										HOSTS.getList(),
										gitHubUserId);
		String jsonMetadata = gson.toJson(metadataFile);
		AppSettings.set().metadata(jsonMetadata);
		Action.saveMetadata(jsonMetadata);
		/*
		 * We delay the committing of data to GitHub to minimize upload resource usage in case
		 * the user is making many multiple changes within a short period of time.
		 * In situations where a user might exit the app before this delay has come to pass,
		 * it won't be a problem, because the next data load will come from a local source
		 */
		if (saveTimer != null) saveTimer.cancel();
		saveTimer = new Timer();
		saveTimer.schedule(saveToGitHub(jsonMetadata), 3000);
	}

	private TimerTask saveToGitHub(String jsonString) {
		return new TimerTask() {
			@Override public void run() {
				metadataFile = new MetadataFile(NAMES.getMap(),
												CATEGORIES.getList(),
												CATEGORIES.getMap(),
												DESCRIPTIONS.getMap(),
												HOSTS.getList(),
												gitHubUserId);
				String gistId = ghGist.getGistId();
				String filename = MetadataFile.FileName;
				String fileContent = gson.toJson(metadataFile);
				Action.updateGistFile(gistId,filename,fileContent);
			}
		};
	}

	public void deleteGitHubCustomData() {
		Action.deleteGistByDescription(gistDescription);
	}

	public void loadJsonData() {
		getData();
	}

	private void loadGitHubData() {
		ghGist = Action.getGistByDescription(gistDescription);
		if (ghGist != null) {
			String customDataGitHubJson = ghGist.getFile(MetadataFile.FileName).getContent();
			metadataFile = gson.fromJson(customDataGitHubJson,MetadataFile.class);
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
		 return new ChoiceBox<>(getGistCategoryList());
	}

	public ObservableList<String> getGistCategoryList() {
		ObservableList<String> oList = FXCollections.observableArrayList(CATEGORIES.getList());
		oList.sort(Comparator.comparing(String::toString));
		return oList;
	}

	public Map<String,String> getGistCategoryMap() {
		return CATEGORIES.getFormattedCategoryMap();
	}

	public Map<String,String> getMappedCategories() {
		return CATEGORIES.getCategoryMap();
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

	public Long getGitHubUserId() {
		return metadataFile.getGitHubUserId();
	}

	public void setGitHubUserId(Long gitHubUserId) {
		this.gitHubUserId = gitHubUserId;
		if (metadataFile != null) {
			if (metadataFile.getGitHubUserId() == null) {
				metadataFile.setGitHubUserId(gitHubUserId);
			}
			else if (!metadataFile.getGitHubUserId().equals(gitHubUserId)) {
				Action.deleteAllMetadata();
				loadGitHubData();
			}
		}
	}
}
