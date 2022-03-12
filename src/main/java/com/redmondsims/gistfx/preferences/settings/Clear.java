package com.redmondsims.gistfx.preferences.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Clear {

	private final Preferences prefs = PREFERENCE.prefs;

	public void loadSource()              {prefs.remove(PREFERENCE.DATA_SOURCE.Name());}

	public void logonScreenChoice()       {prefs.remove(PREFERENCE.LOGIN_SCREEN.Name());}

	public void logonScreenColor()        {prefs.remove(PREFERENCE.LOGIN_SCREEN_COLOR.Name());}

	public void progressColorSource()     {prefs.remove(PREFERENCE.PROGRESS_COLOR_SOURCE.Name());}

	public void progressBarColor()        {prefs.remove(PREFERENCE.PROGRESS_BAR_COLOR.Name());}

	public void progressBarStyle()        {prefs.remove(PREFERENCE.PROGRESS_BAR_STYLE.Name());}

	public void passwordHash()            {prefs.remove(PREFERENCE.PASSWORD_HASH.Name());}

	public void tokenHash()               {prefs.remove(PREFERENCE.TOKEN_HASH.Name());}

	public void theme()                   {prefs.remove(PREFERENCE.THEME.Name());}

	public void firstRun()                {prefs.remove(PREFERENCE.FIRST_RUN.Name());}

	public void buttonBar()               {prefs.remove(PREFERENCE.BUTTON_BAR.Name());}

	public void securityOption()          {prefs.remove(PREFERENCE.SECURITY_OPTION.Name());}

	public void dirtyFileFlagColor()      {prefs.remove(PREFERENCE.DIRTY_FILE_FLAG_COLOR.name());}

	public void gistFolderIconColor()     {prefs.remove(PREFERENCE.GIST_FOLDER_ICON_COLOR.name());}

	public void categoryFolderIconColor() {prefs.remove(PREFERENCE.CATEGORY_FOLDER_ICON_COLOR.Name());}

	public void fileIconColor()           {prefs.remove("test");}

	public void jsonGist()                {prefs.remove(PREFERENCE.JSON_GIST.Name());}

	public void disableDirtyWarning()     {prefs.remove(PREFERENCE.DISABLE_DIRTY_WARNING.Name());}

	public void wideMode()                {prefs.remove(PREFERENCE.WIDE_MODE.Name());}

	public void dividerPositions()        {prefs.remove(PREFERENCE.DIVIDER_POSITIONS.Name());}

	public void metadata()                {prefs.remove(PREFERENCE.METADATA.Name());}

	public void fileMoveWarning()         {prefs.remove(PREFERENCE.FILE_MOVE_WARNING.Name());}

	public void clearAll() {
		try {prefs.clear();}catch (BackingStoreException e) {e.printStackTrace();}
	}

	/**
	 * Tree Icon Settings
	 */

	public void useDefaultCategoryIcon() {prefs.remove(PREFERENCE.USE_DEFAULT_CATEGORY_ICON.Name());}

	public void useDefaultGistIcon()   {prefs.remove(PREFERENCE.USE_DEFAULT_GIST_ICON.Name());}

	public void useDefaultFileIcon()   {prefs.remove(PREFERENCE.USE_DEFAULT_FILE_ICON.Name());}

	public void userCategoryIconPath() {prefs.remove(PREFERENCE.USER_CATEGORY_ICON_PATH.name());}

	public void userGistIconPath()     {prefs.remove(PREFERENCE.USER_GIST_ICON_PATH.name());}

	public void userFileIconPath()     {prefs.remove(PREFERENCE.USER_FILE_ICON_PATH.name());}

	public void userIconFileFolder()   {prefs.remove(PREFERENCE.USER_ICON_FILE_FOLDER.name());}
}
