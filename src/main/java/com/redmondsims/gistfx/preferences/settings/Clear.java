package com.redmondsims.gistfx.preferences.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Clear {

	private final Preferences prefs = LABEL.prefs;

	public void loadSource()              {prefs.remove(LABEL.DATA_SOURCE.Name());}

	public void logonScreenChoice()       {prefs.remove(LABEL.LOGIN_SCREEN.Name());}

	public void logonScreenColor()        {prefs.remove(LABEL.LOGIN_SCREEN_COLOR.Name());}

	public void progressColorSource()     {prefs.remove(LABEL.PROGRESS_COLOR_SOURCE.Name());}

	public void progressBarColor()        {prefs.remove(LABEL.PROGRESS_BAR_COLOR.Name());}

	public void progressBarStyle()        {prefs.remove(LABEL.PROGRESS_BAR_STYLE.Name());}

	public void passwordHash()            {prefs.remove(LABEL.PASSWORD_HASH.Name());}

	public void tokenHash()               {prefs.remove(LABEL.TOKEN_HASH.Name());}

	public void theme()                   {prefs.remove(LABEL.THEME.Name());}

	public void firstRun()                {prefs.remove(LABEL.FIRST_RUN.Name());}

	public void showToolBar() {prefs.remove(LABEL.TOOL_BAR.Name());}

	public void securityOption()          {prefs.remove(LABEL.SECURITY_OPTION.Name());}

	public void dirtyFileFlagColor()      {prefs.remove(LABEL.DIRTY_FILE_FLAG_COLOR.name());}

	public void gistFolderIconColor()     {prefs.remove(LABEL.GIST_FOLDER_ICON_COLOR.name());}

	public void categoryFolderIconColor() {prefs.remove(LABEL.CATEGORY_FOLDER_ICON_COLOR.Name());}

	public void fileIconColor()           {prefs.remove("test");}

	public void disableDirtyWarning()     {prefs.remove(LABEL.DISABLE_DIRTY_WARNING.Name());}

	public void wideMode()                {prefs.remove(LABEL.WIDE_MODE.Name());}

	public void dividerPositions()        {prefs.remove(LABEL.DIVIDER_POSITIONS.Name());}

	public void metadata()                {prefs.remove(LABEL.METADATA.Name());}

	public void fileMoveWarning()         {prefs.remove(LABEL.FILE_MOVE_WARNING.Name());}

	public void clearAll() {
		try {prefs.clear();}catch (BackingStoreException e) {e.printStackTrace();}
	}

	/**
	 * Tree Icon SMTPServerSettingsa
	 */

	public void useDefaultCategoryIcon() {prefs.remove(LABEL.USE_DEFAULT_CATEGORY_ICON.Name());}

	public void useDefaultGistIcon()   {prefs.remove(LABEL.USE_DEFAULT_GIST_ICON.Name());}

	public void useDefaultFileIcon()   {prefs.remove(LABEL.USE_DEFAULT_FILE_ICON.Name());}

	public void userCategoryIconPath() {prefs.remove(LABEL.USER_CATEGORY_ICON_PATH.name());}

	public void userGistIconPath()     {prefs.remove(LABEL.USER_GIST_ICON_PATH.name());}

	public void userFileIconPath()     {prefs.remove(LABEL.USER_FILE_ICON_PATH.name());}

	public void userIconFileFolder()   {prefs.remove(LABEL.USER_ICON_FILE_FOLDER.name());}

	public void mailServer()           {prefs.remove(LABEL.MAIL_SERVER.Name());}

	public void lastTokenHash() {
		prefs.remove(LABEL.LAST_TOKEN_HASH.Name());
	}

	public void lastGitHubUserId() {
		prefs.remove(LABEL.LAST_GITHUB_USER_ID.Name());
	}
}
