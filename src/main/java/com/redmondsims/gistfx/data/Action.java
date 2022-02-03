package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.javafx.CProgressBar;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.io.File;
import java.sql.Date;
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

	public static boolean delete(GistFile file) {
		if (!GITHUB.delete(file)) return false;
		SQLITE.deleteGistFile(file);
		return true;
	}

	public static boolean delete(String gistId) {
		return GITHUB.delete(gistId);
	}

	public static void addToNameMap(String gistId, String name) {
		SQLITE.addToNameMap(gistId, name);
	}

	public static int addFileToSQL(String gistId, String filename, String content) {
		return SQLITE.newSQLFile(gistId, filename, content);
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
		return SQLITE.getGistName(ghGist);
	}

	public static void setGistName(String gistId, String newName) {
		SQLITE.changeGistName(gistId, newName);
	}

	public static Map<String, String> getNameMapFromSQL() {
		return SQLITE.getNameMap();
	}

	/**
	 * GitHub ONLY Methods
	 */

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

	public static void loadData() {
		GITHUB.loadData();
	}

	public static void refreshAllData() {
		GITHUB.refreshAllData();
	}

	public static GHGist getGHGist(String gistId) {
		return GITHUB.getGist(gistId);
	}

	public static boolean updateGistFile(String gistId, String filename, String content) {
		return GITHUB.updateFile(gistId,filename,content);
	}

	public static GHGist getGistByDescription(String description) {
		return GITHUB.getGistByDescription(description);
	}

	public static BooleanProperty getNotifyProperty() {
		return GITHUB.uploading;
	}

	public static String getGitHubFileContent(String gistId, String filename) {
		GHGistFile file = GITHUB.getGistFile(gistId,filename);
		return file.getContent();
	}

	public static Map<String, GHGist> getNewGhGistMap() {
		return GITHUB.getNewGHGistMap();
	}

	public static Date getGistUpdateDate(String gistId) {
		return GITHUB.getGistUpdateDate(gistId);
	}

	/**
	 * Json Methods
	 */

	public static void initJson() {JSON.initPath(SQLITE.getCorePath());}

	public static void deleteJsonLocalFile() {JSON.deleteLocalJsonFile();}

	public static void deleteJsonGistFile() {JSON.deleteGistFile();}

	public static void loadNameMapIntoDatabase() {
		JSON.loadJsonIntoDatabase();
	}

	public static void loadBestNameMap() {
		JSON.loadBestNameMap();
	}

	public static void accommodateUserSettingChange() {
		JSON.accommodateUserSettingChange();
	}

	public static String getJSonGistName(String gistId) {
		return JSON.getGistName(gistId);
	}

	public static void removeJsonName(String gistId) {
		JSON.removeName(gistId);
	}

	public static void setJsonName(String gistId, String name) {
		JSON.setName(gistId,name);
	}

	/**
	 * GitHub AND SQLite Methods
	 */

	public static boolean delete(Gist gist) {
		if (!GITHUB.delete(gist)) return false;
		SQLITE.deleteGist(gist);
		return true;
	}

	public static void updateLocalGistDescription(Gist gist) {
		SQLITE.updateGistDescription(gist);
	}

	public static void updateGitHubGistDescription(Gist gist) {
		GITHUB.update(gist);
	}

	public static boolean updateGistFile(GistFile file) {
		SQLITE.saveFile(file);
		return GITHUB.update(file);
	}

	public static void localFileSave(GistFile file) {
		SQLITE.saveFile(file);
	}

	public static void renameFile(GistFile file) {
		if (!GITHUB.renameFile(file)) return;
		SQLITE.renameFile(file);
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

	/**
	 * ProgressBar Methods
	 */

	public static CProgressBar getProgressNode(double height) {
		return new CProgressBar(GITHUB.progress, height);
	}

	public static CProgressBar getProgressNode(double height, Color color) {
		return new CProgressBar(GITHUB.progress, height, color);
	}

	public static DoubleProperty getGitHubDownloadProgress() {
		return GITHUB.progress;
	}

	public static void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
