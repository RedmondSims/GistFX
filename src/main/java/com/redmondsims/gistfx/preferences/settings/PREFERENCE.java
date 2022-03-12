package com.redmondsims.gistfx.preferences.settings;

import java.util.prefs.Preferences;

enum PREFERENCE {

	PASSWORD_HASH,
	TOKEN_HASH,
	THEME,
	PROGRESS_BAR_THEME,
	PROGRESS_BAR_COLOR,
	PROGRESS_COLOR_SOURCE,
	LOGIN_SCREEN,
	LOGIN_SCREEN_COLOR,
	DATA_SOURCE,
	FIRST_RUN,
	BUTTON_BAR,
	SECURITY_OPTION,
	FLAG_DIRTY_FILES,
	DIRTY_FILE_FLAG_COLOR,
	JSON_GIST,
	PROGRESS_BAR_STYLE,
	DISABLE_DIRTY_WARNING,
	WIDE_MODE,
	DIVIDER_POSITIONS,
	FILE_MOVE_WARNING,
	METADATA,
	GIST_FOLDER_ICON_COLOR,
	CATEGORY_FOLDER_ICON_COLOR,
	FILE_ICON_COLOR,
	USE_DEFAULT_CATEGORY_ICON,
	USE_DEFAULT_GIST_ICON,
	USE_DEFAULT_FILE_ICON,
	USER_CATEGORY_ICON_PATH,
	USER_GIST_ICON_PATH,
	USER_FILE_ICON_PATH,
	USER_ICON_FILE_FOLDER
	;

	public String Name(PREFERENCE this) {
		return switch (this) {
			case PASSWORD_HASH -> "GFX_Password_Hash";
			case TOKEN_HASH -> "GFX_Token_Hash";
			case THEME -> "GFX_Theme";
			case PROGRESS_BAR_THEME -> "GFX_Progress_Bar_Theme";
			case PROGRESS_BAR_COLOR -> "GFX_Progress_Bar_Color";
			case PROGRESS_COLOR_SOURCE -> "GFX_Progress_Color_Source";
			case LOGIN_SCREEN -> "GFX_Login_Screen";
			case LOGIN_SCREEN_COLOR -> "GFX_Login_Screen_Color";
			case DATA_SOURCE -> "GFX_Data_Source";
			case FIRST_RUN -> "GFX_First_Run";
			case BUTTON_BAR -> "GFX_Button_Bar";
			case SECURITY_OPTION -> "GFX_Security_Option";
			case FLAG_DIRTY_FILES -> "GFX_Flag_Dirty_Files";
			case DIRTY_FILE_FLAG_COLOR -> "GFX_Dirty_File_Flag_Color";
			case JSON_GIST -> "GFX_Json_Gist";
			case PROGRESS_BAR_STYLE -> "GFX_Progress_Bar_Style";
			case WIDE_MODE -> "GFX_Wide_Mode";
			case DIVIDER_POSITIONS -> "GFX_Divider_Positions";
			case DISABLE_DIRTY_WARNING -> "GFX_Disable_Dirty_Warning";
			case FILE_MOVE_WARNING -> "GFX_File_Move_Warning";
			case METADATA -> "GFX_Metadata";
			case GIST_FOLDER_ICON_COLOR -> "GFX_Gist_Folder_Icon_Color";
			case CATEGORY_FOLDER_ICON_COLOR -> "GFX_Category_Folder_Icon_Color";
			case FILE_ICON_COLOR -> "GFX_File_Icon_Color";
			case USE_DEFAULT_CATEGORY_ICON -> "GFX_Use_Default_Tree_Category_Icon";
			case USE_DEFAULT_GIST_ICON -> "GFX_Use_Default_Tree_Gist_Icon";
			case USE_DEFAULT_FILE_ICON -> "GFX_Use_Default_Tree_File_Icon";
			case USER_CATEGORY_ICON_PATH -> "GFX_User_Tree_Category_Icon_Path";
			case USER_GIST_ICON_PATH -> "GFX_User_Tree_Gist_Icon_Path";
			case USER_FILE_ICON_PATH -> "GFX_User_Tree_File_Icon_Path";
			case USER_ICON_FILE_FOLDER -> "GFX_User_Icon_File_Folder";
		};
	}

	public static final Preferences prefs = Preferences.userNodeForPackage(PREFERENCE.class);

}
