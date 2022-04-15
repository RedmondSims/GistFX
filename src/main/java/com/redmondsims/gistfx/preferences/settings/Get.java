package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.enums.ColorOption;
import com.redmondsims.gistfx.enums.Colors;
import com.redmondsims.gistfx.enums.Theme;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import javafx.scene.paint.Color;

import java.util.prefs.Preferences;

public class Get {

	private final Preferences prefs = LABEL.prefs;



	public Color progressBarColor() {
		Color color;
		if (progressColorLogin()) {
			color = LiveSettings.getLoginScreenColor().getColor();
		}
		else if(progressColorRandom()) {
			color = Colors.random();
		}
		else {
			color = progressCustomColor();
		}
		return color;
	}

	public boolean progressColorRandom() {
		return prefs.getBoolean(LABEL.PROGRESS_COLOR_RANDOM.Name(), true);
	}

	public boolean progressColorLogin() {
		return prefs.getBoolean(LABEL.PROGRESS_COLOR_LOGIN.Name(), true);
	}

	public Color progressCustomColor() {
		return Color.valueOf(prefs.get(LABEL.PROGRESS_CUSTOM_COLOR.Name(), loginScreenColor().toString()));
	}

	public String hashedToken() {
		return prefs.get(LABEL.TOKEN_HASH.Name(), "");
	}

	public String hashedPassword() {
		return prefs.get(LABEL.PASSWORD_HASH.Name(), "");
	}

	public UISettings.DataSource dataSource() {
		String option = prefs.get(LABEL.DATA_SOURCE.Name(), UISettings.DataSource.GITHUB.Name());
		return UISettings.DataSource.get(option);
	}

	public Colors loginScreenColor() {
		if (loginScreenRandom()) {
			return LiveSettings.getLoginScreenColor();
		}
		return Colors.get(prefs.get(LABEL.LOGIN_SCREEN_COLOR.Name(), Colors.BLUE.Name()));
	}

	public boolean loginScreenRandom() {
		return prefs.getBoolean(LABEL.LOGIN_SCREEN_RANDOM.Name(), true);
	}

	public boolean firstRun() {
		return prefs.getBoolean(LABEL.FIRST_RUN.Name(), true);
	}

	public boolean showToolBar() {
		return prefs.getBoolean(LABEL.SHOW_TOOL_BAR.Name(), true);
	}

	public Theme theme() {
		String option = prefs.get(LABEL.THEME.Name(), UISettings.Theme.DARK.Name());
		return Theme.get(option);
	}

	public Color dirtyFileFlagColor() {
		String setting = prefs.get(LABEL.DIRTY_FILE_FLAG_COLOR.Name(), "#FF0000");
		return Color.valueOf(setting);
	}

	public Color categoryFolderIconColor() {
		String setting = LABEL.prefs.get(LABEL.CATEGORY_FOLDER_ICON_COLOR.Name(), "0xcc6633ff");
		return Color.valueOf(setting);
	}

	public Color gistFolderIconColor() {
		String setting = prefs.get(LABEL.GIST_FOLDER_ICON_COLOR.name(), "0xe6994dff");
		return Color.valueOf(setting);
	}

	public Color fileIconColor() {
		String setting = prefs.get(LABEL.FILE_ICON_COLOR.Name(), "0xffe6b3ff");
		return Color.valueOf(setting);
	}

	public boolean disableDirtyWarning() {
		return prefs.getBoolean(LABEL.DISABLE_DIRTY_WARNING.Name(), false);
	}

	public String metadata() {
		return prefs.get(LABEL.METADATA.Name(), "");
	}

	public boolean fileMoveWarning() {
		return prefs.getBoolean(LABEL.FILE_MOVE_WARNING.Name(), true);
	}

	public boolean useDefaultCategoryIcon() {
		return prefs.getBoolean(LABEL.USE_DEFAULT_CATEGORY_ICON.Name(), true);
	}

	public boolean useDefaultGistIcon() {
		return prefs.getBoolean(LABEL.USE_DEFAULT_GIST_ICON.Name(),true);
	}

	public boolean useDefaultFileIcon() {
		return prefs.getBoolean(LABEL.USE_DEFAULT_FILE_ICON.Name(),true);
	}

	public String userCategoryIcon() {
		return prefs.get(LABEL.USER_CATEGORY_ICON.Name(), "");
	}

	public String userGistIcon() {
		return prefs.get(LABEL.USER_GIST_ICON.Name(), "");
	}

	public String userFileIcon() {
		return prefs.get(LABEL.USER_FILE_ICON.Name(), "");
	}

	public String mailServer() {
		return prefs.get(LABEL.MAIL_SERVER.Name(), "");
	}

	public String lastTokenHash() {
		return prefs.get(LABEL.LAST_TOKEN_HASH.Name(), "");
	}

	public String lastGitHubUserId() {
		return prefs.get(LABEL.LAST_GITHUB_USER_ID.Name(), "");
	}

	public boolean runInSystemTray() {
	 	return prefs.getBoolean(LABEL.RUN_IN_SYSTRAY.Name(), false);
	}

	public Colors trayIconColor() {
		Colors color = null;
		if(trayIconColorOption().equals(ColorOption.FOLLOW_LOGIN))
			color = loginScreenColor();
		else if (trayIconColorOption().equals(ColorOption.DEFAULT))
			color = Colors.GREEN;
		else
			color = trayIconUserColor();
		return color;
	}

	public Colors trayIconUserColor() {
		return Colors.get(prefs.get(LABEL.TRAY_ICON_USER_COLOR.Name(), "Green"));
	}

	public boolean showAppIcon() {
		return prefs.getBoolean(LABEL.SHOW_APP_ICON.Name(), true);
	}

	public double dividerAtRest() {
		return prefs.getDouble(LABEL.DIVIDER_AT_REST.Name(), 0.0);
	}

	public double dividerExpanded() {
		return prefs.getDouble(LABEL.DIVIDER_EXPANDED.Name(), 0.0);
	}

	public double iconBaseSize() {
		return prefs.getDouble(LABEL.ICON_BASE_SIZE.Name(), 25.0);
	}

	public ColorOption trayIconColorOption() {
		ColorOption option = ColorOption.get(prefs.get(LABEL.TRAY_ICON_COLOR_OPTION.Name(), "Default"));
		if (option == null)
			return ColorOption.get("Default");
		return option;
	}

	public boolean searchFileContents() {
		return prefs.getBoolean(LABEL.SEARCH_FILE_CONTENTS.Name(), false);
	}

}
