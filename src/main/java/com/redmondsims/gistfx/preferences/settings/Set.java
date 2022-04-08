package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.AppSettings;
import javafx.scene.paint.Color;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Set {

	private final Preferences prefs = LABEL.prefs;

	public void hashedToken(String tokenHash) {
		AppSettings.clear().tokenHash();
		prefs.put(LABEL.TOKEN_HASH.Name(), tokenHash);
	}

	public void hashedPassword(String passwordHash) {
		AppSettings.clear().passwordHash();
		prefs.put(LABEL.PASSWORD_HASH.Name(), passwordHash);
	}

	public void dataSource(UISettings.DataSource option) {
		AppSettings.clear().loadSource();
		prefs.put(LABEL.DATA_SOURCE.Name(), option.Name());
	}

	public void progressBarColor(Color color) {
		AppSettings.clear().progressBarColor();
		prefs.put(LABEL.PROGRESS_BAR_COLOR.Name(), String.valueOf(color));
	}

	public void progressBarStyle(String style) {
		AppSettings.clear().progressBarStyle();
		prefs.put(LABEL.PROGRESS_BAR_STYLE.Name(), style);
	}

	public void progressColorSource(UISettings.ProgressColorSource choice) {
		AppSettings.clear().progressColorSource();
		prefs.put(LABEL.PROGRESS_COLOR_SOURCE.Name(), String.valueOf(choice));
	}

	public void loginScreenChoice(UISettings.LoginScreen option) {
		AppSettings.clear().logonScreenChoice();
		prefs.put(LABEL.LOGIN_SCREEN.Name(), option.Name());
	}

	public void loginScreenColor(UISettings.LoginScreenColor option) {
		AppSettings.clear().logonScreenColor();
		prefs.put(LABEL.LOGIN_SCREEN_COLOR.Name(), option.Name());
	}

	public void firstRun(boolean setting) {
		AppSettings.clear().firstRun();
		prefs.put(LABEL.FIRST_RUN.Name(), String.valueOf(setting));
	}

	public void showToolBar(boolean setting) {
		AppSettings.clear().showToolBar();
		prefs.put(LABEL.TOOL_BAR.Name(), String.valueOf(setting));
	}

	public void securityOption(UISettings.LoginScreen option) {
		AppSettings.clear().securityOption();
		prefs.put(LABEL.SECURITY_OPTION.Name(), option.Name());
	}

	public void theme(UISettings.Theme theme) {
		AppSettings.clear().theme();
		prefs.put(LABEL.THEME.Name(), theme.Name());
	}

	public void dirtyFileFlagColor(Color color) {
		AppSettings.clear().dirtyFileFlagColor();
		String colorString = "#" + color.toString().replaceFirst("0x","").substring(0,6);
		prefs.put(LABEL.DIRTY_FILE_FLAG_COLOR.Name(), colorString);
	}

	public void categoryFolderIconColor(Color color) {
		AppSettings.clear().categoryFolderIconColor();
		prefs.put(LABEL.CATEGORY_FOLDER_ICON_COLOR.Name(), color.toString());
	}

	public void gistFolderIconColor(Color color) {
		AppSettings.clear().gistFolderIconColor();
		prefs.put(LABEL.GIST_FOLDER_ICON_COLOR.name(), color.toString());
	}

	public void fileIconColor(Color color) {
		AppSettings.clear().categoryFolderIconColor();
		prefs.put("test", color.toString());
	}

	public void disableDirtyWarning(boolean setting) {
		AppSettings.clear().disableDirtyWarning();
		prefs.put(LABEL.DISABLE_DIRTY_WARNING.Name(), String.valueOf(setting));
	}

	public void wideMode(boolean setting) {
		AppSettings.clear().wideMode();
		prefs.put(LABEL.WIDE_MODE.Name(), String.valueOf(setting));
	}

	public void dividerPositions(String positions) {
		AppSettings.clear().dividerPositions();
		prefs.put(LABEL.DIVIDER_POSITIONS.Name(), positions);
	}

	public void metadata(String value) {
		AppSettings.clear().metadata();
		prefs.put(LABEL.METADATA.Name(), value);
	}

	public void fileMoveWarning(boolean value) {
		AppSettings.clear().fileMoveWarning();
		prefs.put(LABEL.FILE_MOVE_WARNING.Name(), String.valueOf(value));
	}

	public void useDefaultCategoryIcon(boolean setting) {
		AppSettings.clear().useDefaultCategoryIcon();
		prefs.put(LABEL.USE_DEFAULT_CATEGORY_ICON.Name(), String.valueOf(setting));
	}

	public void useDefaultGistIcon(boolean setting) {
		AppSettings.clear().useDefaultGistIcon();
		prefs.put(LABEL.USE_DEFAULT_GIST_ICON.Name(), String.valueOf(setting));
	}

	public void useDefaultFileIcon(boolean setting) {
		AppSettings.clear().useDefaultFileIcon();
		prefs.put(LABEL.USE_DEFAULT_FILE_ICON.Name(), String.valueOf(setting));
	}

	public void userCategoryIconPath(String setting) {
		AppSettings.clear().userCategoryIconPath();
		prefs.put(LABEL.USER_CATEGORY_ICON_PATH.Name(), setting);
	}

	public void userGistIconPath(String setting) {
		AppSettings.clear().userGistIconPath();
		prefs.put(LABEL.USER_GIST_ICON_PATH.Name(), setting);
	}

	public void userFileIconPath(String setting) {
		AppSettings.clear().userFileIconPath();
		prefs.put(LABEL.USER_FILE_ICON_PATH.Name(), setting);
	}

	public void userIconFileFolder(String setting) {
		AppSettings.clear().userIconFileFolder();
		prefs.put(LABEL.USER_ICON_FILE_FOLDER.Name(), setting);
	}

	public void setDefaults() {
		try {prefs.clear();}catch (BackingStoreException e) {e.printStackTrace();}
	}

	public void mailServer(String setting) {
		AppSettings.clear().mailServer();
		prefs.put(LABEL.MAIL_SERVER.Name(), setting);
	}

	public void lastTokenHash(String setting) {
		AppSettings.clear().lastTokenHash();
		prefs.put(LABEL.LAST_TOKEN_HASH.name(), setting);
	}

	public void lastGitHubUserId(String setting) {
		AppSettings.clear().lastGitHubUserId();
		prefs.put((LABEL.LAST_GITHUB_USER_ID.Name()), setting);
	}

	public void runInSystray(boolean setting) {
		AppSettings.clear().runInSystray();
		prefs.put((LABEL.RUN_IN_SYSTRAY.Name()), String.valueOf(setting));
	}

	public void systrayColor(String setting) {
		AppSettings.clear().systrayColor();
		prefs.put((LABEL.SYSTRAY_COLOR.Name()), String.valueOf(setting));
	}

	public void showAppIcon(boolean setting) {
		AppSettings.clear().showAppIcon();
		prefs.put((LABEL.SHOW_APP_ICON.Name()), String.valueOf(setting));
	}

}
