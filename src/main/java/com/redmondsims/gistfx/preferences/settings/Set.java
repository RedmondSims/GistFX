package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.enums.ColorOption;
import com.redmondsims.gistfx.enums.Colors;
import com.redmondsims.gistfx.enums.Theme;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.AppSettings;
import javafx.scene.paint.Color;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Set {

	private final Preferences prefs = LABEL.prefs;


	public void progressColorRandom(boolean value) {
		AppSettings.clear().progressColorRandom();
		prefs.putBoolean(LABEL.PROGRESS_COLOR_RANDOM.Name(), value);
	}

	public void progressColorLogin(boolean value) {
		AppSettings.clear().progressColorLogin();
		prefs.putBoolean(LABEL.PROGRESS_COLOR_LOGIN.Name(), value);
	}

	public void progressCustomColor(Color color) {
		AppSettings.clear().progressCustomColor();
		prefs.put(LABEL.PROGRESS_CUSTOM_COLOR.Name(), String.valueOf(color));
	}

	public void hashedToken(String tokenHash) {
		AppSettings.clear().hashedToken();
		prefs.put(LABEL.TOKEN_HASH.Name(), tokenHash);
	}

	public void hashedPassword(String passwordHash) {
		AppSettings.clear().hashedPassword();
		prefs.put(LABEL.PASSWORD_HASH.Name(), passwordHash);
	}

	public void dataSource(UISettings.DataSource option) {
		AppSettings.clear().dataSource();
		prefs.put(LABEL.DATA_SOURCE.Name(), option.Name());
	}

	public void loginScreenColor(Colors option) {
		AppSettings.clear().logonScreenColor();
		prefs.put(LABEL.LOGIN_SCREEN_COLOR.Name(), option.Name());
	}

	public void loginScreenRandom(boolean value) {
		AppSettings.clear().logonScreenRandom();
		prefs.putBoolean(LABEL.LOGIN_SCREEN_RANDOM.Name(), value);
	}

	public void firstRun(boolean value) {
		prefs.putBoolean(LABEL.FIRST_RUN.Name(), value);
	}

	public void showToolBar(boolean value) {
		prefs.putBoolean(LABEL.SHOW_TOOL_BAR.Name(), value);
	}

	public void theme(Theme theme) {
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
		if(color == null)
			prefs.remove(LABEL.CATEGORY_FOLDER_ICON_COLOR.Name());
		else
			prefs.put(LABEL.CATEGORY_FOLDER_ICON_COLOR.Name(), color.toString());
	}

	public void gistFolderIconColor(Color color) {
		AppSettings.clear().gistFolderIconColor();
		if(color == null)
			prefs.remove(LABEL.GIST_FOLDER_ICON_COLOR.Name());
		else
			prefs.put(LABEL.GIST_FOLDER_ICON_COLOR.name(), color.toString());
	}

	public void fileIconColor(Color color) {
		AppSettings.clear().fileIconColor();
		if(color == null)
			prefs.remove(LABEL.FILE_ICON_COLOR.Name());
		else
			prefs.put(LABEL.FILE_ICON_COLOR.Name(), color.toString());
	}

	public void disableDirtyWarning(boolean value) {
		prefs.putBoolean(LABEL.DISABLE_DIRTY_WARNING.Name(), value);
	}

	public void metadata(String value) {
		AppSettings.clear().metadata();
		prefs.put(LABEL.METADATA.Name(), value);
	}

	public void fileMoveWarning(boolean value) {
		prefs.putBoolean(LABEL.FILE_MOVE_WARNING.Name(), value);
	}

	public void useDefaultCategoryIcon(boolean value) {
		prefs.putBoolean(LABEL.USE_DEFAULT_CATEGORY_ICON.Name(), value);
	}

	public void useDefaultGistIcon(boolean value) {
		prefs.putBoolean(LABEL.USE_DEFAULT_GIST_ICON.Name(), value);
	}

	public void useDefaultFileIcon(boolean value) {
		prefs.putBoolean(LABEL.USE_DEFAULT_FILE_ICON.Name(), value);
	}

	public void userCategoryIcon(String setting) {
		AppSettings.clear().userCategoryIcon();
		AppSettings.set().useDefaultCategoryIcon(setting.isEmpty());
		prefs.put(LABEL.USER_CATEGORY_ICON.Name(), setting);
	}

	public void userGistIcon(String setting) {
		AppSettings.clear().userGistIcon();
		AppSettings.set().useDefaultGistIcon(setting.isEmpty());
		prefs.put(LABEL.USER_GIST_ICON.Name(), setting);
	}

	public void userFileIcon(String setting) {
		AppSettings.clear().userFileIcon();
		AppSettings.set().useDefaultFileIcon(setting.isEmpty());
		prefs.put(LABEL.USER_FILE_ICON.Name(), setting);
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

	public void runInSystemTray(boolean value) {
		AppSettings.clear().runInSystemTray();
		prefs.putBoolean((LABEL.RUN_IN_SYSTRAY.Name()), value);
	}

	public void trayIconColor(Colors setting) {
		AppSettings.clear().trayIconColor();
		prefs.put((LABEL.TRAY_ICON_COLOR.Name()), setting.Name());
	}

	public void trayIconUserColor(Colors value) {
		AppSettings.clear().trayIconUserColor();
		prefs.put((LABEL.TRAY_ICON_USER_COLOR.Name()),value.Name());
	}

	public void showAppIcon(boolean value) {
		prefs.putBoolean((LABEL.SHOW_APP_ICON.Name()), value);
	}

	public void dividerAtRest(double value) {
		AppSettings.clear().dividerAtRest();
		prefs.putDouble(LABEL.DIVIDER_AT_REST.Name(), value);
	}

	public void dividerExpanded(double value) {
		AppSettings.clear().dividerAtRest();
		prefs.putDouble(LABEL.DIVIDER_EXPANDED.Name(), value);
	}

	public void iconBaseSize(Integer value) {
		AppSettings.clear().iconBaseSize();
		prefs.putInt(LABEL.ICON_BASE_SIZE.Name(), value);
	}

	public void trayIconColorOption(ColorOption option) {
		AppSettings.clear().trayIconColorOption();
		prefs.put(LABEL.TRAY_ICON_COLOR_OPTION.Name(), option.Name());
	}

	public void searchFileContents(boolean value) {
		AppSettings.clear().searchFileContents();
		prefs.putBoolean(LABEL.SEARCH_FILE_CONTENTS.Name(), value);
	}

	public void setDefaults() {
		try {prefs.clear();}catch (BackingStoreException e) {e.printStackTrace();}
	}
}
