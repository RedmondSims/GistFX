package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.preferences.UISettings;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.prefs.Preferences;

public class Get {

	private final Preferences prefs = LABEL.prefs;



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

	public Color progressBarColor() {
		if (progressColorSource() == UISettings.ProgressColorSource.RANDOM) {
			Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.HOTPINK, Color.LIGHTBLUE};
			Random  random = new Random();
			int     top    = colors.length - 1;
			return colors[random.nextInt(top)];
		}
		String color = prefs.get(LABEL.PROGRESS_BAR_COLOR.Name(), "#FF0000");
		return Color.valueOf(color);
	}

	public String progressBarStyle() {
		String colorString  = "#" + progressBarColor().toString().replaceFirst("0x", "").substring(0, 6);
		String defaultStyle = "-fx-accent: " + colorString + ";";
		return prefs.get(LABEL.PROGRESS_BAR_STYLE.Name(), defaultStyle);
	}

	public UISettings.ProgressColorSource progressColorSource() {
		String choice = prefs.get(LABEL.PROGRESS_COLOR_SOURCE.Name(), UISettings.ProgressColorSource.RANDOM.getName());
		return UISettings.ProgressColorSource.get(choice);
	}

	public boolean customProgressColor() {
		return prefs.getBoolean(LABEL.CUSTOM_PROGRESS_COLOR.Name(), false);
	}

	public UISettings.LoginScreen loginScreenChoice() {
		String option = prefs.get(LABEL.LOGIN_SCREEN.Name(), UISettings.LoginScreen.GRAPHIC.Name());
		return UISettings.LoginScreen.get(option);
	}

	public UISettings.LoginScreenColor loginScreenColor() {
		String option = prefs.get(LABEL.LOGIN_SCREEN_COLOR.Name(), UISettings.LoginScreenColor.BLUE.Name());
		return UISettings.LoginScreenColor.get(option);
	}

	public boolean firstRun() {
		return prefs.getBoolean(LABEL.FIRST_RUN.Name(), true);
	}

	public boolean showToolBar() {
		return prefs.getBoolean(LABEL.SHOW_TOOL_BAR.Name(), true);
	}

	public UISettings.Theme theme() {
		String option = prefs.get(LABEL.THEME.Name(), UISettings.Theme.DARK.Name());
		return UISettings.Theme.get(option);
	}

	public Color dirtyFileFlagColor() {
		String setting = prefs.get(LABEL.DIRTY_FILE_FLAG_COLOR.Name(), "#FF0000");
		return Color.valueOf(setting);
	}

	public Color categoryFolderIconColor() {
		String setting = LABEL.prefs.get(LABEL.CATEGORY_FOLDER_ICON_COLOR.Name(), "0xffa200ff");
		return Color.valueOf(setting);
	}

	public Color gistFolderIconColor() {
		String setting = prefs.get(LABEL.GIST_FOLDER_ICON_COLOR.name(), "0xffff00ff");
		return Color.valueOf(setting);
	}

	public Color fileIconColor() {
		String setting = prefs.get(LABEL.FILE_ICON_COLOR.Name(), "0xccffffff");
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

	public boolean runInSystray() {
	 	return prefs.getBoolean(LABEL.RUN_IN_SYSTRAY.Name(), false);
	}

	public String systrayColor() {
		return prefs.get(LABEL.SYSTRAY_COLOR.Name(), "White");
	}

	public boolean showAppIcon() {
		return prefs.getBoolean(LABEL.SHOW_APP_ICON.Name(), true);
	}
}
