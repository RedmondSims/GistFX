package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.javafx.CProgressBar;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.ui.LoginWindow;
import com.redmondsims.gistfx.data.metadata.Json;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.io.File;
import java.sql.Date;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
 * One class to rule them all - Action is the class that isolates the rest of the program from those classes that affect change on data.
 * Its intent is to ensure data integrity as well as simplify workflow throughout the program.
 */

public class Action {

	private static final GitHub GITHUB = new GitHub();
	private static final SQLite SQLITE = new SQLite();
	private static final Disk   DISK   = new Disk();
	private static final Json   JSON   = new Json();

	/**
	 * SQLite ONLY Methods
	 */

	public static void setDatabaseConnection() {
		SQLITE.setConnection();
	}

	public static void deleteDatabaseFile() {
		SQLITE.deleteDatabaseFile();
	}

	public static void addGistToSQL(Gist gist) {SQLITE.addGist(gist);}

	public static void cleanDatabase() {
		SQLITE.cleanDatabase();
	}

	public static void delete(GistFile file) {
		GITHUB.delete(file);
		SQLITE.deleteGistFile(file);
	}

	public static void addToNameMap(String gistId, String name) {
		JSON.setName(gistId, name);
	}

	public static int addFileToSQL(String gistId, String filename, String content, Date uploadDate) {
		return SQLITE.newSQLFile(gistId, filename, content, uploadDate);
	}

	public static void setDirtyFile(Integer fileId, boolean dirty) {
		SQLITE.setDirtyFile(fileId, dirty);
	}

	public static boolean fileIsDirty(Integer fileId) {
		return SQLITE.fileIsDirty(fileId);
	}

	public static Map<String, Gist> getGistMap() {
		return SQLITE.getGistMap();
	}

	public static String getGistName(GHGist ghGist) {
		return JSON.getName(ghGist.getGistId());
	}

	public static String getGistName(String gistId) {
		return JSON.getName(gistId);
	}

	public static void setGistName(String gistId, String newName) {
		JSON.setName(gistId, newName);
	}

	public static String getSQLMetadata() {
		return SQLITE.getMetadata();
	}

	public static void saveMetadata(String jsonString) {
		SQLITE.saveMetadata(jsonString);
	}

	/**
	 * GitHub ONLY Methods
	 */

	public static GHGist getNewGist(String description, String filename, String content, boolean isPublic) {
		return GITHUB.newGist(description,filename,content,isPublic);
	}

	public static String getName() {
		return GITHUB.getName();
	}

	public static GHGist addGistToGitHub(String description, String filename, String content, boolean isPublic) {
		return GITHUB.newGist(description, filename, content, isPublic);
	}

	public static GHGistFile addFileToGitHub(String gistId, String filename, String content) {
		return GITHUB.addFileToGist(gistId, filename, content);
	}

	public static Integer getForkCount(String gistId) {
		return GITHUB.getForkCount(gistId);
	}

	public static boolean tokenValid(String token) {
		return GITHUB.tokenValid(token);
	}

	public static void loadWindow() {
		UISettings.DataSource dataSource = LiveSettings.getDataSource();
		if (dataSource.equals(UISettings.DataSource.GITHUB)) {
			LoginWindow.updateProcess("Downloading Gist Objects");
			Map<String,GHGist> ghGistMap = GITHUB.getNewGHGistMap();
			JSON.getData();
			GistManager.startFromGit(ghGistMap, Source.GITHUB);
		}
		if (dataSource.equals(UISettings.DataSource.LOCAL)) {
			JSON.getData();
			GistManager.startFromDatabase();
		}
	}

	public static void refreshAllData() {
		GITHUB.refreshAllData();
	}

	public static GHGist getGHGist(String gistId) {
		return GITHUB.getLocalGist(gistId);
	}

	public static void updateGistFile(String gistId, String filename, String content) {
		GITHUB.updateFile(gistId, filename, content);
	}

	public static GHGist getGistByDescription(String description) {
		return GITHUB.getGitHubGistByDescription(description);
	}

	public static BooleanProperty getNotifyProperty() {
		return GITHUB.uploading;
	}

	public static Map<String, GHGist> getNewGhGistMap() {
		return GITHUB.getNewGHGistMap();
	}

	public static Date getGistUpdateDate(String gistId) {
		return GITHUB.getGistUpdateDate(gistId);
	}

	public static void deleteGistFile(String gistId, String filename) {
		GITHUB.deleteGistFile(gistId,filename);
	}

	public static void deleteFullGist(String gistId) {
		GITHUB.delete(gistId);
	}

	public static boolean ghGistMapIsEmpty() {
		return GITHUB.ghGistMapIsEmpty();
	}

	public static String getGitHubFileContent(String gistId, String filename) {
		return GITHUB.getLocalGitHubFileContent(gistId, filename);
	}

	public static GHGistFile getGitHubFile(String gistId, String filename) {
		return GITHUB.getLocalGitHubFile(gistId, filename);
	}

	/**
	 * Json Methods
	 */

	public static void deleteJsonGistFile() {JSON.deleteGitHubCustomData();}

	public static void loadJsonData() {
		JSON.loadJsonData();
	}

	public static void accommodateUserSettingChange() {
		JSON.accommodateUserSettingChange();
	}

	public static String getJSonGistName(String gistId) {
		return JSON.getName(gistId);
	}

	public static void removeJsonName(String gistId) {
		JSON.removeName(gistId);
	}

	public static void setJsonName(String gistId, String name) {
		JSON.setName(gistId,name);
	}

	/**
	 * Json Category Methods
	 */

	public static ObservableList<String> getCategoryList() {
		return JSON.getGistCategoryList();
	}

	public static void changeCategoryName(String oldName, String newName) {
		JSON.changeCategoryName(oldName,newName);
	}

	public static void mapCategoryNameToGist(String gistId, String categoryName) {
		JSON.mapCategoryNameToGist(gistId,categoryName);
	}

	public static void deleteCategoryName(String categoryName) {
		JSON.deleteCategoryName(categoryName);
	}

	public static void addCategoryName(String categoryName) {
		JSON.addCategoryName(categoryName);
	}

	public static Collection<String> getNameList() {
		return JSON.getNameList();
	}

	public static String getGistIdByName (String name) {
		return JSON.getGistIdByName(name);
	}

	public static String getGistCategoryName(String gistId) {
		return JSON.getGistCategoryName(gistId);
	}

	public static ChoiceBox<String> getCategoryBox() {
		return JSON.getGistCategoryBox();
	}

	/**
	 * Json File Description Methods
	 */

	public static void setFileDescription(GistFile gistFile, String description) {
		JSON.setFileDescription(gistFile,description);
	}

	public static String getFileDescription(GistFile gistFile) {
		return JSON.getFileDescription(gistFile);
	}

	/**
	 * GitHub AND SQLite Methods
	 */

	public static void delete(String gistId) {
		GITHUB.delete(gistId);
		SQLITE.deleteGist(gistId);
	}

	public static void updateLocalGistDescription(Gist gist) {
		SQLITE.updateGistDescription(gist);
	}

	public static void updateGitHubGistDescription(Gist gist) {
		GITHUB.updateDescription(gist);
	}

	public static void updateGistFile(GistFile file) {
		SQLITE.saveFile(file);
		GITHUB.updateFile(file);
	}

	public static void localFileSave(GistFile file) {
		SQLITE.saveFile(file);
	}

	public static void renameFile(String gistId, Integer fileId, String oldFilename, String newFilename, String content) {
		GITHUB.renameFile(gistId,oldFilename,newFilename,content);
		SQLITE.renameFile(fileId,newFilename);
	}

	/**
	 * Methods that require multiple disciplines
	 */

	public static void moveFile(Gist oldGist, Gist newGist, GistFile file) {
		String oldGistId = oldGist.getGistId();
		String newGistId = newGist.getGistId();
		String filename = file.getFilename();
		String content = file.getContent();
		GITHUB.addFileToGist(newGistId,filename,content);
		GITHUB.deleteGistFile(oldGistId,filename);
		SQLITE.changeGistId(file,newGistId);
	}

	/**
	 * Disk Methods
	 */

	public static String loadTextFile(File file) {
		return DISK.loadTextFile(file);
	}

	public static void writeToTextFile(File file, String content) {
		DISK.writeToTextFile(file,content);
	}

	public static void deleteJsonLocalFile() {
		JSON.deleteLocalAppSettingsData();
	}

	/**
	 * ProgressBar Methods
	 */

	public static CProgressBar getProgressNode(double height) {
		return new CProgressBar(GITHUB.progress, height);
	}

	public static CProgressBar getProgressNode(double height, Color color) {
		return new CProgressBar(GITHUB.progress, height, color);
	}

	public static void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Double round(double number) {
		double finalNumber = number * 100;
		finalNumber = Math.round(finalNumber);
		finalNumber = finalNumber / 100;
		return finalNumber;
	}

	public static void setProgress(double value) {
		GITHUB.progress.setValue(value);
	}

}
