package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.data.metadata.Json;
import com.redmondsims.gistfx.enums.LoginStates;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.javafx.CProgressBar;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.ui.LoginWindow;
import com.redmondsims.gistfx.utils.Resources;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
 * One class to rule them all - Action is the class that isolates the rest of the program from those classes that affect change on data.
 * Its intent is to ensure data integrity as well as simplify workflow throughout the program.
 */

public class Action {

	private static final BooleanProperty gitHubActivityProperty = new SimpleBooleanProperty(false);
	private static final DoubleProperty  progress               = new SimpleDoubleProperty(0);
	private static final GitHub          GITHUB                 = new GitHub();
	private static final SQLite          SQLITE                 = new SQLite();
	private static final Disk            DISK                   = new Disk();
	private static final Json            JSON                   = new Json();

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

	public static void wipeSQLAndMetaData() {
		SQLITE.cleanDatabase();
		AppSettings.clear().metadata();
	}

	public static void delete(GistFile file) {
		GITHUB.delete(file);
		SQLITE.deleteGistFile(file);
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

	public static String getMD2() {
		return SQLITE.getMD2();
	}

	public static boolean hasData() {
		return SQLITE.hasData();
	}

	public static boolean reEncryptData(String oldPassword, String newPassword) {
		return SQLITE.reEncryptData(oldPassword,newPassword);
	}

	public static void deleteAllLocalData() {
		SQLITE.deleteAllLocalData();
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

	public static LoginStates tokenValid(String token) {
		return GITHUB.tokenValid(token);
	}

	public static void loadWindow() {
		UISettings.DataSource dataSource = LiveSettings.getDataSource();
		if (dataSource.equals(UISettings.DataSource.GITHUB)) {
			if (GITHUB.noGists()) {
				GistManager.startEmpty();
			}
			else {
				LoginWindow.updateProgress("Downloading Gist Objects");
				Map<String,GHGist> ghGistMap = GITHUB.getNewGHGistMap();
				GistManager.startFromGit(ghGistMap, Source.GITHUB);
			}
		}
		else if (dataSource.equals(UISettings.DataSource.LOCAL)) {
			GistManager.startFromDatabase();
		}
	}

	public static void updateProgress(String text) {
		LoginWindow.updateProgress(text);
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

	public static boolean updateGistFile(int fileId, String gistId, String filename, String content, boolean isDirty) {
		boolean dirty = LiveSettings.isOffline() || isDirty;
		SQLITE.updateGitHubVersion(fileId, content);
		SQLITE.saveFile(fileId,content,dirty);
		return GITHUB.updateFile(gistId, filename, content);
	}

	public static GHGist getGistByDescription(String description) {
		return GITHUB.getGitHubGistByDescription(description);
	}

	public static Map<String, GHGist> getNewGhGistMap() {
		return GITHUB.getNewGHGistMap();
	}

	public static Map<String, GHGist> getGHGistMap() {
		return GITHUB.getGHGistMap();
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

	public static void deleteGistByDescription(String description) {
		GITHUB.deleteGistByDescription(description);
	}

	public static boolean ghGistMapIsEmpty() {
		return GITHUB.ghGistMapIsEmpty();
	}

	public static String getGitHubFileContent(String gistId, String filename) {
		return GITHUB.getGitHubFileContent(gistId, filename);
	}

	public static String getLocalGitHubVersion(int fileId) {
		return SQLITE.getLocalGitHubVersion(fileId);
	}

	/**
	 * Json Methods
	 */

	public static void deleteGitHubMetadata() {JSON.deleteGitHubMetadata();}

	public static void loadMetaData() {
		JSON.loadMetaData();
	}

	public static String getJSonGistName(String gistId) {
		return JSON.getName(gistId);
	}

	public static void deleteGistMetadata(String gistId) {
		JSON.deleteGistMetadata(gistId);
	}

	public static void setJsonName(String gistId, String name) {
		JSON.setName(gistId,name);
	}

	/**
	 * Json Category Methods
	 */

	public static ObservableList<String> getCategoryList() {
		return JSON.getCategoryList();
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

	public static List<Gist> getGistsInCategory(String category) {
		return JSON.getGistsInCategory(category);
	}

	/**
	 * Json File Description Methods
	 */

	public static void setFileDescription(GistFile gistFile, String description) {
		JSON.setFileDescription(gistFile,description);
	}

	public static void setFileDescription(String gistId, String filename, String description) {
		JSON.setFileDescription(gistId,filename,description);
	}

	public static String getFileDescription(GistFile gistFile) {
		return JSON.getFileDescription(gistFile);
	}

	public static void deleteFileDescription(String gistId, String filename) {
		JSON.deleteFileDescription(gistId,filename);
	}

	/**
	 * Json Hosts Methods
	 */

	public static void addHost(String host) {
		JSON.addHost(host);
	}

	public static Collection<String> getHostCollection() {
		return JSON.getHostCollection();
	}

	public static void removeHost(String host) {
		JSON.removeHost(host);
	}

	public static void renameHost(String oldName, String newName) {
		JSON.renameHost(oldName,newName);
	}

	/**
	 * GitHub AND SQLite Methods
	 */

	public static void delete(String gistId) {
		deleteGistMetadata(gistId);
		GITHUB.delete(gistId);
		SQLITE.deleteGist(gistId);
	}

	public static void updateLocalGistDescription(Gist gist) {
		SQLITE.updateGistDescription(gist);
	}

	public static void updateGitHubGistDescription(Gist gist) {
		GITHUB.updateDescription(gist);
	}

	public static void localFileSave(int fileId, String content, boolean dirty) {
		SQLITE.saveFile(fileId, content, dirty);
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
		String content = file.getLiveVersion();
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

	public static void deleteLocalMetaData(boolean doSQL) {
		AppSettings.clear().metadata();
		if(doSQL) {
			setDatabaseConnection();
			SQLITE.deleteMetadata();
		}
	}

	/**
	 * ProgressBar Methods
	 */

	public static CProgressBar getProgressNode(double height) {
		return new CProgressBar(progress, height);
	}

	public static CProgressBar getProgressNode(double height, Color color) {
		return new CProgressBar(progress, height, color);
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
		Platform.runLater(() -> {
			progress.setValue(value);
		});
	}

	/**
	 * Misc
	 */

	public static Tooltip newTooltip(String message) {
		Tooltip toolTip = new Tooltip(message);
		toolTip.setShowDuration(Duration.seconds(120));
		toolTip.setTextAlignment(TextAlignment.RIGHT);
		return toolTip;
	}

	public static void setGitHubUserId(Long gitHubUserId) {
		JSON.setGitHubUserId(gitHubUserId);
	}

	public static BooleanProperty getGitHubActivityProperty() {
		return gitHubActivityProperty;
	}

	public static void gitHubActivity (boolean active) {
		Platform.runLater(() -> {
			gitHubActivityProperty.setValue(active);
		});
	}

	public static boolean accessingGitHub() {
		return gitHubActivityProperty.getValue();
	}

	public static void deleteLocalGist(String gistId) {
		SQLITE.deleteGist(gistId);
		JSON.deleteGistMetadata(gistId);
	}

	public static void deleteLocalFiles() {
		try {
			FileUtils.deleteDirectory(Resources.getExternalRootPath().toFile());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void uploadGistToGitHub(String gistId) {
		Gist gist = GistManager.getGist(gistId);
		String description = gist.getDescription();
		boolean createGhGist = true;
		String newGistId = "";
		for(GistFile gistFile : gist.getFiles()) {
			String content = gistFile.getLiveVersion();
			String filename = gistFile.getFilename();
			if(createGhGist) {
				createGhGist = false;
				GHGist ghGist = GITHUB.newGist(description,filename,content,gist.isPublic());
				newGistId = ghGist.getGistId();
				continue;
			}
			if (!newGistId.isEmpty())
				GITHUB.addFileToGist(newGistId,filename,content);
		}
		if(!newGistId.isEmpty()) {
			JSON.changeGistId(gistId,newGistId);
		}
	}

	public static void error(Exception e) {
		System.err.println(e.getMessage());
		StackTraceElement[] elements = e.getStackTrace();
		for(StackTraceElement element : elements) {
			if(element.toString().contains("gistfx")) {
				System.out.println("\t" + element.toString().replaceFirst("com.redmondsims.gistfx/com.redmondsims.gistfx.",""));
			}
		}
		System.exit(0);
	}

}
