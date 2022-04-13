package com.redmondsims.gistfx.preferences.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Clear {

	private final Preferences prefs = LABEL.prefs;

	public void progressColorRandom() {
		prefs.remove(LABEL.PROGRESS_COLOR_RANDOM.Name());
	}

	public void progressColorLogin() {
		prefs.remove(LABEL.PROGRESS_COLOR_LOGIN.Name());
	}

	public void progressCustomColor() {
		prefs.remove(LABEL.PROGRESS_CUSTOM_COLOR.Name());
	}

	public void dataSource() {prefs.remove(LABEL.DATA_SOURCE.Name());}

	public void logonScreenColor()        {prefs.remove(LABEL.LOGIN_SCREEN_COLOR.Name());}

	public void logonScreenRandom()        {prefs.remove(LABEL.LOGIN_SCREEN_RANDOM.Name());}

	public void hashedPassword() {prefs.remove(LABEL.PASSWORD_HASH.Name());}

	public void hashedToken() {prefs.remove(LABEL.TOKEN_HASH.Name());}

	public void theme()                   {prefs.remove(LABEL.THEME.Name());}

	public void firstRun()                {prefs.remove(LABEL.FIRST_RUN.Name());}

	public void showToolBar() {prefs.remove(LABEL.SHOW_TOOL_BAR.Name());}

	public void dirtyFileFlagColor()      {prefs.remove(LABEL.DIRTY_FILE_FLAG_COLOR.name());}

	public void categoryFolderIconColor() {prefs.remove(LABEL.CATEGORY_FOLDER_ICON_COLOR.Name());}

	public void gistFolderIconColor()     {prefs.remove(LABEL.GIST_FOLDER_ICON_COLOR.name());}

	public void fileIconColor()           {prefs.remove(LABEL.FILE_ICON_COLOR.Name());}

	public void disableDirtyWarning()     {prefs.remove(LABEL.DISABLE_DIRTY_WARNING.Name());}

	public void metadata()                {prefs.remove(LABEL.METADATA.Name());}

	public void fileMoveWarning()         {prefs.remove(LABEL.FILE_MOVE_WARNING.Name());}

	public void useDefaultCategoryIcon() {prefs.remove(LABEL.USE_DEFAULT_CATEGORY_ICON.Name());}

	public void useDefaultGistIcon()   {prefs.remove(LABEL.USE_DEFAULT_GIST_ICON.Name());}

	public void useDefaultFileIcon()   {prefs.remove(LABEL.USE_DEFAULT_FILE_ICON.Name());}

	public void userCategoryIcon() {
		prefs.remove(LABEL.USER_CATEGORY_ICON.name());
	}

	public void userGistIcon() {prefs.remove(LABEL.USER_GIST_ICON.name());}

	public void userFileIcon() {prefs.remove(LABEL.USER_FILE_ICON.name());}

	public void mailServer()           {prefs.remove(LABEL.MAIL_SERVER.Name());}

	public void lastTokenHash() {
		prefs.remove(LABEL.LAST_TOKEN_HASH.Name());
	}

	public void lastGitHubUserId() {
		prefs.remove(LABEL.LAST_GITHUB_USER_ID.Name());
	}

	public void runInSystemTray() {prefs.remove(LABEL.RUN_IN_SYSTRAY.name());}

	public void systrayColor()   {prefs.remove(LABEL.SYSTRAY_COLOR.name());}

	public void showAppIcon()   {prefs.remove(LABEL.SHOW_APP_ICON.name());}

	public void dividerAtRest()   {prefs.remove(LABEL.DIVIDER_AT_REST.name());}

	public void dividerExpanded()   {prefs.remove(LABEL.DIVIDER_EXPANDED.name());}

	public void iconBaseSize()   {prefs.remove(LABEL.ICON_BASE_SIZE.name());}


	/**
	 * Tree Icon SMTPServerSettings
	 */


	public void clearAll() {
		try {prefs.clear();}catch (BackingStoreException e) {e.printStackTrace();}
	}

}
