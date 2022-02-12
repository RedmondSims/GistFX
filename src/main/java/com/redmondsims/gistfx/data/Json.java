package com.redmondsims.gistfx.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import org.kohsuke.github.GHGist;

import java.util.Date;
import java.util.*;

import static com.redmondsims.gistfx.enums.Names.*;
import static com.redmondsims.gistfx.preferences.UISettings.DataSource.*;

class Json {

	private enum Source {
		LOCAL_FILE,
		GIST_FILE,
		SQLITE
	}

	private       Categories       CATEGORIES      = new Categories();
	private       Names            NAMES           = new Names();
	private       GHGist           ghGist;
	private       CustomDataFile   customDataFile;
	private final String           gistDescription = GIST_DATA_DESCRIPTION.Name();
	private final Gson             gson            = new GsonBuilder().setPrettyPrinting().create();
	private final CBooleanProperty useGitHub       = new CBooleanProperty(LiveSettings.useJsonGist);
	private       Timer            saveTimer;

 	private void makeNewGist() {
		String customDataGitHubJson = gson.toJson(customDataFile);
		ghGist = Action.getNewGist(gistDescription, CustomDataFile.FileName, customDataGitHubJson, false);
	}

	protected void getData() {
		UISettings.DataSource dataSource           = LiveSettings.getDataSource();
		String                customDataGitHubJson = "";
		String                customDataLocalJson  = AppSettings.getFXData();
		String                customDataSQLJson    = Action.getSQLFXData();
		CustomDataFile        customGitHub;
		CustomDataFile        customSQL;
		CustomDataFile        customLocal;
		boolean               haveGitHubData       = false;
		boolean               haveLocalData        = !customDataLocalJson.equals("");
		boolean               haveSQLData          = !customDataSQLJson.equals("");

		Map<Integer, Date> dateMap = new HashMap<>();
		customDataFile = new CustomDataFile(NAMES.getMap(), CATEGORIES.getList(), CATEGORIES.getMap());
		if (useGitHub.isTrue()) {
			ghGist = Action.getGistByDescription("GistFX!Data!");
		}
		if (dataSource.equals(GITHUB) && useGitHub.isTrue()) {
			while (!LiveSettings.gitHubAuthenticated()) {
				Action.sleep(100);
			}
			if (ghGist != null) {
				customDataGitHubJson = ghGist.getFile(CustomDataFile.FileName).getContent();
				haveGitHubData       = true;
			}
		}
		if (haveGitHubData) {
			customGitHub = gson.fromJson(customDataGitHubJson, CustomDataFile.class);
			dateMap.put(1,customGitHub.getDate());
		}
		if (haveSQLData) {
			customSQL = gson.fromJson(customDataSQLJson, CustomDataFile.class);
			dateMap.put(2,customSQL.getDate());
		}
		if (haveLocalData) {
			customLocal = gson.fromJson(customDataLocalJson, CustomDataFile.class);
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
			customDataFile = gson.fromJson(finalJsonString, CustomDataFile.class);
			CATEGORIES = customDataFile.getCategories();
			NAMES      = customDataFile.getNames();
		}
		else { //We have nothing!
			Map<String,GHGist> ghGistMap = Action.getGhGistMap();
			for (String gistId : ghGistMap.keySet()) {
				String description = ghGistMap.get(gistId).getDescription();
				NAMES.setName(gistId,description);
			}
		}
		if ((!haveGitHubData && useGitHub.isTrue() && dataSource.equals(GITHUB)) || (useGitHub.isTrue() && ghGist == null)) {
			makeNewGist();
		}
		saveData();
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
		customDataFile = new CustomDataFile(NAMES.getMap(), CATEGORIES.getList(), CATEGORIES.getMap());
		String customDataFileJson = gson.toJson(customDataFile);
		AppSettings.setFXData(customDataFileJson);
		Action.saveFXData(customDataFileJson);
		/*
		 * We delay the committing of data to GitHub to minimize upload resource usage in case
		 * the user is making many multiple changes within a short period of time.
		 * In situations where a user might exit the app before this delay has come to pass,
		 * it won't be a problem, because the next data load will come from a local source
		 */
		if (useGitHub.isTrue()) {
			if (saveTimer != null) {
				saveTimer.cancel();
			}
			saveTimer = new Timer();
			saveTimer.schedule(saveToGitHub(customDataFileJson), 3000);
		}
	}

	private TimerTask saveToGitHub(String jsonString) {
		return new TimerTask() {
			@Override public void run() {
				customDataFile = new CustomDataFile(NAMES.getMap(), CATEGORIES.getList(), CATEGORIES.getMap());
				String gistId = ghGist.getGistId();
				String filename = CustomDataFile.FileName;
				String fileContent = gson.toJson(customDataFile);
				Action.updateGistFile(gistId,filename,fileContent);
			}
		};
	}

	protected void removeName(String gistId) {
		NAMES.deleteName(gistId);
		CATEGORIES.unMapCategory(gistId);
		saveData();
	}

	protected void setName(String gistId, String name) {
		NAMES.setName(gistId,name.trim());
		saveData();
	}

	protected String getName(String gistId) {
		return NAMES.getName(gistId).trim();
	}

	protected void addCategoryName(String categoryName) {
		CATEGORIES.addCategory(categoryName.trim());
		saveData();
	}

	protected void deleteCategoryName(String categoryName) {
		CATEGORIES.deleteCategory(categoryName);
		saveData();
	}

	protected void mapCategoryNameToGist(String gistId, String categoryName) {
		CATEGORIES.mapCategory(gistId,categoryName.trim());
		saveData();
	}

	protected void changeCategoryName(String oldName, String newName) {
		CATEGORIES.renameCategory(oldName,newName.trim());
		saveData();
	}

	protected String getGistCategoryName(String gistId) {
		return CATEGORIES.getMappedCategory(gistId);
	}

	protected List<String> getGistCategoryList() {
		return CATEGORIES.getList();
	}

	protected Map<String,String> getGistCategoryMap() {
		return CATEGORIES.getFormattedCategoryMap();
	}

	protected Map<String,String> getMappedCategories() {
		return CATEGORIES.getCategoryMap();
	}

	protected void accommodateUserSettingChange() {
		if(useGitHub.isTrue() && !LiveSettings.useJsonGist) {
			Action.deleteFullGist(ghGist.getGistId());
		}
		if (useGitHub.isFalse() && LiveSettings.useJsonGist) {
			customDataFile = new CustomDataFile(NAMES.getMap(), CATEGORIES.getList(), CATEGORIES.getMap());
			makeNewGist();
		}
		useGitHub.toggle();
	}

	protected void deleteGitHubCustomData() {
		Action.deleteFullGist(ghGist.getGistId());
	}

	protected void deleteLocalAppSettingsData() {
		AppSettings.clearFXData();
	}

	public void loadJsonData() {
		getData();
	}
}
