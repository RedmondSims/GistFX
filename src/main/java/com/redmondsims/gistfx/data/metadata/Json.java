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
import com.redmondsims.gistfx.ui.gist.GistCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.kohsuke.github.GHGist;

import java.util.*;

import static com.redmondsims.gistfx.enums.Names.GITHUB_METADATA;
import static com.redmondsims.gistfx.preferences.UISettings.DataSource.GITHUB;

public class Json {

	private       Categories       CATEGORIES      = new Categories();
	private       Names            NAMES           = new Names();
	private       FileDescriptions DESCRIPTIONS    = new FileDescriptions();
	private       Hosts            HOSTS           = new Hosts();
	private       GHGist           ghGist;
	private       MetadataFile     metadataFile;
	private final Gson             gson            = new GsonBuilder().setPrettyPrinting().create();
	private       Timer            saveTimer;
	private boolean makingNew = false;
	private final Map<String, GistCategory> gistCategoryMap = new HashMap<>();

 	private void makeNewGist() {
		new Thread(() -> {
			makingNew = true;
			while(Action.ghGistMapIsEmpty() && LiveSettings.gitHubAuthenticated()) {
				Action.sleep(150L);
			}
			Map<String,GHGist> ghGistMap = Action.getGHGistMap();
			if(!NAMES.hasData()) {
				for (String gistId : ghGistMap.keySet()) {
					GHGist ghGist = ghGistMap.get(gistId);
					String gistDescription = ghGist.getDescription();
					NAMES.setName(gistId,gistDescription);
				}
			}
			metadataFile = new MetadataFile(NAMES.getMap(),
											CATEGORIES.getList(),
											CATEGORIES.getMap(),
											DESCRIPTIONS.getMap(),
											HOSTS.getList());
			String jsonMetadata = gson.toJson(metadataFile);
			ghGist = Action.getNewGist(MetadataFile.GistDescription, MetadataFile.FileName, jsonMetadata, false);
			saveData();
			makingNew = false;
		}).start();
	}

	private void reCreateGist() {
		 if (metadataFile != null) {
			 Action.updateProgress("Refreshing GitHub Metadata");
			 Action.deleteGistByDescription(GITHUB_METADATA.Name());
			 String jsonString = gson.toJson(metadataFile);
			 ghGist = Action.getNewGist(MetadataFile.GistDescription, MetadataFile.FileName,jsonString,false);
		 }
	}

	private void loadGistCategoryMap() {
		gistCategoryMap.clear();
		for (String category : CATEGORIES.getList()) {
			gistCategoryMap.put(category,new GistCategory(category));
		}
	}

	/**
	 * Local and GitHub Data File Methods
	 */

	private void getData() {
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
										HOSTS.getList());
		ghGist = Action.getGistByDescription(MetadataFile.GistDescription);
		if (ghGist != null) {
			customDataGitHubJson = ghGist.getFile(MetadataFile.FileName).getContent();
			haveGitHubData       = true;
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
			Date reWriteDate = metadataFile.getLastReWrite();
			if (reWriteDate == null) {
				metadataFile.setLastReWrite();
				saveData();
			}
			else {
				DateTime then        = new DateTime(reWriteDate);
				DateTime now         = new DateTime(new Date());
				Instant  thenInstant = then.toInstant();
				Instant  nowInstant  = now.toInstant();
				long     thenMillis  = thenInstant.getMillis();
				long     nowMillis   = nowInstant.getMillis();
				Duration duration    = new Duration(nowMillis - thenMillis);
				Days     days        = duration.toStandardDays();
				if (days.getDays() >= 10) {
					metadataFile.setLastReWrite();
					reCreateGist();
				}
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

	private Integer findNewestDate(Map<Integer,Date> dateMap) {
		SortedSet<Date> dates = new TreeSet<>(dateMap.values());
		Date finalDate = dates.first();
		DateTime finalDT = new DateTime(finalDate);
		int finalIndex = 1;
		for(Integer index : dateMap.keySet()) {
			DateTime date = new DateTime(dateMap.get(index));
			if (date.isBefore(finalDT)) {
				finalDT = date;
				finalIndex = index;
			}
		}
		for(Integer index : dateMap.keySet()) {
			DateTime date = new DateTime(dateMap.get(index));
			if (date.isBefore(finalDT)) {
				finalDT = date;
				finalIndex = index;
			}
		}
		return finalIndex;
	}

	private void saveData() {
		metadataFile = new MetadataFile(NAMES.getMap(),
										CATEGORIES.getList(),
										CATEGORIES.getMap(),
										DESCRIPTIONS.getMap(),
										HOSTS.getList());
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
												HOSTS.getList());
				String gistId = ghGist.getGistId();
				String filename = MetadataFile.FileName;
				String fileContent = gson.toJson(metadataFile);
				Action.updateGistFile(gistId,filename,fileContent);
			}
		};
	}

	public void deleteGitHubMetadata() {
		Action.deleteGistByDescription(MetadataFile.GistDescription);
	}

	public void loadJsonData() {
		getData();
	}

	private void loadGitHubData() {
		ghGist = Action.getGistByDescription(MetadataFile.GistDescription);
		if (ghGist != null) {
			String metadataJson = ghGist.getFile(MetadataFile.FileName).getContent();
			metadataFile = gson.fromJson(metadataJson,MetadataFile.class);
			CATEGORIES   = metadataFile.getCategories();
			NAMES        = metadataFile.getNames();
			DESCRIPTIONS = metadataFile.getFileDescriptions();
			HOSTS        = metadataFile.getHosts();
			if(metadataFile.getLastReWrite() == null) {
				metadataFile.setLastReWrite();
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
		gistCategoryMap.put(categoryName,new GistCategory(categoryName));
		saveData();
	}

	public void deleteCategoryName(String categoryName) {
		CATEGORIES.deleteCategory(categoryName);
		gistCategoryMap.remove(categoryName);
		saveData();
	}

	public void mapCategoryNameToGist(String gistId, String categoryName) {
		CATEGORIES.mapCategory(gistId,categoryName.trim());
		saveData();
	}

	public void changeCategoryName(String oldName, String newName) {
		CATEGORIES.renameCategory(oldName,newName.trim());
		gistCategoryMap.put(newName,gistCategoryMap.get(oldName));
		gistCategoryMap.get(newName).setCategoryName(newName);
		gistCategoryMap.remove(oldName);
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

	public Map<String,String> getGistCategoryMap() {
		return CATEGORIES.getFormattedCategoryMap();
	}

	public Map<String,String> getMappedCategories() {
		return CATEGORIES.getCategoryMap();
	}

	public List<GistCategory> getGistCategoryList() {
		return new ArrayList<>(gistCategoryMap.values());
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

	public GistCategory getGistCategory(String categoryName) {
		return gistCategoryMap.get(categoryName);
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
				Action.deleteLocalMetaData(false);
				Action.deleteAllLocalData();
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

}
