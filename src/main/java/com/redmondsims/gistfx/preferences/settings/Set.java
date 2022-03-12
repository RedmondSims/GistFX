package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.AppSettings;
import javafx.scene.paint.Color;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Set {

	private final Preferences prefs = PREFERENCE.prefs;

	public void hashedToken(String tokenHash) {
		AppSettings.clear().tokenHash();
		prefs.put(PREFERENCE.TOKEN_HASH.Name(), tokenHash);
	}

	public void hashedPassword(String passwordHash) {
		AppSettings.clear().passwordHash();
		prefs.put(PREFERENCE.PASSWORD_HASH.Name(), passwordHash);
	}

	public void dataSource(UISettings.DataSource option) {
		AppSettings.clear().loadSource();
		prefs.put(PREFERENCE.DATA_SOURCE.Name(), option.Name());
	}

	public void progressBarColor(Color color) {
		AppSettings.clear().progressBarColor();
		prefs.put(PREFERENCE.PROGRESS_BAR_COLOR.Name(), String.valueOf(color));
	}

	public void progressBarStyle(String style) {
		AppSettings.clear().progressBarStyle();
		prefs.put(PREFERENCE.PROGRESS_BAR_STYLE.Name(), style);
	}

	public void progressColorSource(UISettings.ProgressColorSource choice) {
		AppSettings.clear().progressColorSource();
		prefs.put(PREFERENCE.PROGRESS_COLOR_SOURCE.Name(), String.valueOf(choice));
	}

	public void loginScreenChoice(UISettings.LoginScreen option) {
		AppSettings.clear().logonScreenChoice();
		prefs.put(PREFERENCE.LOGIN_SCREEN.Name(), option.Name());
	}

	public void loginScreenColor(UISettings.LoginScreenColor option) {
		AppSettings.clear().logonScreenColor();
		prefs.put(PREFERENCE.LOGIN_SCREEN_COLOR.Name(), option.Name());
	}

	public void firstRun(boolean setting) {
		AppSettings.clear().firstRun();
		prefs.put(PREFERENCE.FIRST_RUN.Name(), String.valueOf(setting));
	}

	public void showButtonBar(boolean setting) {
		AppSettings.clear().buttonBar();
		prefs.put(PREFERENCE.BUTTON_BAR.Name(), String.valueOf(setting));
	}

	public void securityOption(UISettings.LoginScreen option) {
		AppSettings.clear().securityOption();
		prefs.put(PREFERENCE.SECURITY_OPTION.Name(), option.Name());
	}

	public void theme(UISettings.Theme theme) {
		AppSettings.clear().theme();
		prefs.put(PREFERENCE.THEME.Name(), theme.Name());
	}

	public void dirtyFileFlagColor(Color color) {
		AppSettings.clear().dirtyFileFlagColor();
		String colorString = "#" + color.toString().replaceFirst("0x","").substring(0,6);
		prefs.put(PREFERENCE.DIRTY_FILE_FLAG_COLOR.Name(), colorString);
	}

	public void categoryFolderIconColor(Color color) {
		AppSettings.clear().categoryFolderIconColor();
		prefs.put(PREFERENCE.CATEGORY_FOLDER_ICON_COLOR.Name(), color.toString());
	}

	public void gistFolderIconColor(Color color) {
		AppSettings.clear().gistFolderIconColor();
		prefs.put(PREFERENCE.GIST_FOLDER_ICON_COLOR.name(), color.toString());
	}

	public void fileIconColor(Color color) {
		AppSettings.clear().categoryFolderIconColor();
		prefs.put("test", color.toString());
	}

	public void saveToGist(boolean setting) {
		AppSettings.clear().jsonGist();
		prefs.put(PREFERENCE.JSON_GIST.Name(), String.valueOf(setting));
	}

	public void disableDirtyWarning(boolean setting) {
		AppSettings.clear().disableDirtyWarning();
		prefs.put(PREFERENCE.DISABLE_DIRTY_WARNING.Name(), String.valueOf(setting));
	}

	public void wideMode(boolean setting) {
		AppSettings.clear().wideMode();
		prefs.put(PREFERENCE.WIDE_MODE.Name(), String.valueOf(setting));
	}

	public void dividerPositions(String positions) {
		AppSettings.clear().dividerPositions();
		prefs.put(PREFERENCE.DIVIDER_POSITIONS.Name(), positions);
	}

	public void metadata(String value) {
		AppSettings.clear().metadata();
		prefs.put(PREFERENCE.METADATA.Name(), value);
	}

	public void fileMoveWarning(boolean value) {
		AppSettings.clear().fileMoveWarning();
		prefs.put(PREFERENCE.FILE_MOVE_WARNING.Name(), String.valueOf(value));
	}

	public void useDefaultCategoryIcon(boolean setting) {
		AppSettings.clear().useDefaultCategoryIcon();
		prefs.put(PREFERENCE.USE_DEFAULT_CATEGORY_ICON.Name(), String.valueOf(setting));
	}

	public void useDefaultGistIcon(boolean setting) {
		AppSettings.clear().useDefaultGistIcon();
		prefs.put(PREFERENCE.USE_DEFAULT_GIST_ICON.Name(), String.valueOf(setting));
	}

	public void useDefaultFileIcon(boolean setting) {
		AppSettings.clear().useDefaultFileIcon();
		prefs.put(PREFERENCE.USE_DEFAULT_FILE_ICON.Name(), String.valueOf(setting));
	}

	public void userTreeCategoryIconPath(String setting) {
		AppSettings.clear().userCategoryIconPath();
		prefs.put(PREFERENCE.USER_CATEGORY_ICON_PATH.Name(), setting);
	}

	public void userTreeGistIconPath(String setting) {
		AppSettings.clear().userGistIconPath();
		prefs.put(PREFERENCE.USER_GIST_ICON_PATH.Name(), setting);
	}

	public void userTreeFileIconPath(String setting) {
		AppSettings.clear().userFileIconPath();
		prefs.put(PREFERENCE.USER_FILE_ICON_PATH.Name(), setting);
	}

	public void userIconFileFolder(String setting) {
		AppSettings.clear().userIconFileFolder();
		prefs.put(PREFERENCE.USER_ICON_FILE_FOLDER.Name(), setting);
	}

	public void setDefaults() {
		try {prefs.clear();}catch (BackingStoreException e) {e.printStackTrace();}
	}
}
