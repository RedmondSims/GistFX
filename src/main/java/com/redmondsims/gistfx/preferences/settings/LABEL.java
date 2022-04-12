package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.preferences.LiveSettings;

import java.util.prefs.Preferences;

public enum LABEL {

	PASSWORD_HASH,
	TOKEN_HASH,
	THEME,
	PROGRESS_COLOR_RANDOM,
	PROGRESS_COLOR_LOGIN,
	PROGRESS_CUSTOM_COLOR,
	LOGIN_SCREEN_COLOR,
	LOGIN_SCREEN_RANDOM,
	DATA_SOURCE,
	FIRST_RUN,
	SHOW_TOOL_BAR,
	FLAG_DIRTY_FILES,
	DIRTY_FILE_FLAG_COLOR,
	GIST_FOLDER_ICON_COLOR,
	FILE_ICON_COLOR,
	CATEGORY_FOLDER_ICON_COLOR,
	DISABLE_DIRTY_WARNING,
	FILE_MOVE_WARNING,
	METADATA,
	USE_DEFAULT_CATEGORY_ICON,
	USE_DEFAULT_GIST_ICON,
	USE_DEFAULT_FILE_ICON,
	USER_CATEGORY_ICON,
	USER_GIST_ICON,
	USER_FILE_ICON,
	MAIL_SERVER,
	LAST_TOKEN_HASH,
	LAST_GITHUB_USER_ID,
	RUN_IN_SYSTRAY,
	SYSTRAY_COLOR,
	SHOW_APP_ICON,
	DIVIDER_AT_REST,
	DIVIDER_EXPANDED
	;

	public String Name(LABEL this) {
		if (LiveSettings.getDevMode()) {
			return switch (this) {
				case PASSWORD_HASH -> "GFX_DEV_Password_Hash";
				case TOKEN_HASH -> "GFX_DEV_Token_Hash";
				case THEME -> "GFX_DEV_Theme";
				case PROGRESS_COLOR_RANDOM -> "GFX_DEV_Progress_Color_Random";
				case PROGRESS_COLOR_LOGIN -> "GFX_DEV_Progress_Color_Login";
				case PROGRESS_CUSTOM_COLOR -> "GFX_DEV_Progress_Custom_Color";
				case LOGIN_SCREEN_COLOR -> "GFX_DEV_Login_Screen_Color";
				case LOGIN_SCREEN_RANDOM -> "GFX_DEV_Login_Screen_Random";
				case DATA_SOURCE -> "GFX_DEV_Data_Source";
				case FIRST_RUN -> "GFX_DEV_First_Run";
				case SHOW_TOOL_BAR -> "GFX_DEV_Show_Tool_Bar";
				case FLAG_DIRTY_FILES -> "GFX_DEV_Flag_Dirty_Files";
				case DIRTY_FILE_FLAG_COLOR -> "GFX_DEV_Dirty_File_Flag_Color";
				case DISABLE_DIRTY_WARNING -> "GFX_DEV_Disable_Dirty_Warning";
				case FILE_MOVE_WARNING -> "GFX_DEV_File_Move_Warning";
				case METADATA -> "GFX_DEV_Metadata";
				case GIST_FOLDER_ICON_COLOR -> "GFX_DEV_Gist_Folder_Icon_Color";
				case CATEGORY_FOLDER_ICON_COLOR -> "GFX_DEV_Category_Folder_Icon_Color";
				case FILE_ICON_COLOR -> "GFX_DEV_File_Icon_Color";
				case USE_DEFAULT_CATEGORY_ICON -> "GFX_DEV_Use_Default_Tree_Category_Icon";
				case USE_DEFAULT_GIST_ICON -> "GFX_DEV_Use_Default_Tree_Gist_Icon";
				case USE_DEFAULT_FILE_ICON -> "GFX_DEV_Use_Default_Tree_File_Icon";
				case USER_CATEGORY_ICON -> "GFX_DEV_User_Tree_Category_Icon";
				case USER_GIST_ICON -> "GFX_DEV_User_Tree_Gist_Icon";
				case USER_FILE_ICON -> "GFX_DEV_User_Tree_File_Icon";
				case MAIL_SERVER -> "GFX_DEV_Mail_Server";
				case LAST_TOKEN_HASH -> "GFX_DEV_Last_Token_Hash";
				case LAST_GITHUB_USER_ID -> "GFX_DEV_Last_GitHub_User_Id";
				case RUN_IN_SYSTRAY -> "GFX_DEV_Run_In_Systray";
				case SYSTRAY_COLOR -> "GFX_DEV_Systray_Color";
				case SHOW_APP_ICON -> "GFX_DEV_Show_App_Icon";
				case DIVIDER_AT_REST -> "GFX_DEV_Divider_At_Rest";
				case DIVIDER_EXPANDED -> "GFX_DEV_Divider_Expanded";
			};
		}
		else {
			return switch (this) {
				case PASSWORD_HASH -> "GFX_Password_Hash";
				case TOKEN_HASH -> "GFX_Token_Hash";
				case THEME -> "GFX_Theme";
				case PROGRESS_COLOR_RANDOM -> "GFX_Progress_Color_Random";
				case PROGRESS_COLOR_LOGIN -> "GFX_Progress_Color_Login";
				case PROGRESS_CUSTOM_COLOR -> "GFX_Progress_Custom_Color";
				case LOGIN_SCREEN_COLOR -> "GFX_Login_Screen_Color";
				case LOGIN_SCREEN_RANDOM -> "GFX_Login_Screen_Random";
				case DATA_SOURCE -> "GFX_Data_Source";
				case FIRST_RUN -> "GFX_First_Run";
				case SHOW_TOOL_BAR -> "GFX_Show_Tool_Bar";
				case FLAG_DIRTY_FILES -> "GFX_Flag_Dirty_Files";
				case DIRTY_FILE_FLAG_COLOR -> "GFX_Dirty_File_Flag_Color";
				case DISABLE_DIRTY_WARNING -> "GFX_Disable_Dirty_Warning";
				case FILE_MOVE_WARNING -> "GFX_File_Move_Warning";
				case METADATA -> "GFX_Metadata";
				case GIST_FOLDER_ICON_COLOR -> "GFX_Gist_Folder_Icon_Color";
				case CATEGORY_FOLDER_ICON_COLOR -> "GFX_Category_Folder_Icon_Color";
				case FILE_ICON_COLOR -> "GFX_File_Icon_Color";
				case USE_DEFAULT_CATEGORY_ICON -> "GFX_Use_Default_Tree_Category_Icon";
				case USE_DEFAULT_GIST_ICON -> "GFX_Use_Default_Tree_Gist_Icon";
				case USE_DEFAULT_FILE_ICON -> "GFX_Use_Default_Tree_File_Icon";
				case USER_CATEGORY_ICON -> "GFX_User_Tree_Category_Icon";
				case USER_GIST_ICON -> "GFX_User_Tree_Gist_Icon";
				case USER_FILE_ICON -> "GFX_User_Tree_File_Icon";
				case MAIL_SERVER -> "GFX_Mail_Server";
				case LAST_TOKEN_HASH -> "GFX_Last_Token_Hash";
				case LAST_GITHUB_USER_ID -> "GFX_Last_GitHub_User_Id";
				case RUN_IN_SYSTRAY -> "GFX_Run_In_Systray";
				case SYSTRAY_COLOR -> "GFX_Systray_Color";
				case SHOW_APP_ICON -> "GFX_Show_App_Icon";
				case DIVIDER_AT_REST -> "GFX_Divider_At_Rest";
				case DIVIDER_EXPANDED -> "GFX_Divider_Expanded";
			};
		}
	}

	public static final Preferences prefs = Preferences.userNodeForPackage(LABEL.class);

}
