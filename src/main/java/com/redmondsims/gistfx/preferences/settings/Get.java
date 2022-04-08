package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.preferences.UISettings;
import javafx.scene.paint.Color;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
			return getRandomColor();
		}
		String color = prefs.get(LABEL.PROGRESS_BAR_COLOR.Name(), "#FF0000");
		return Color.valueOf(color);
	}

	public UISettings.LoginScreen loginScreenChoice() {
		String option = prefs.get(LABEL.LOGIN_SCREEN.Name(), UISettings.LoginScreen.GRAPHIC.Name());
		return UISettings.LoginScreen.get(option);
	}

	public UISettings.LoginScreenColor loginScreenColor() {
		String option = prefs.get(LABEL.LOGIN_SCREEN_COLOR.Name(), UISettings.LoginScreenColor.BLUE.Name());
		return UISettings.LoginScreenColor.get(option);
	}

	public UISettings.ProgressColorSource progressColorSource() {
		String choice = prefs.get(LABEL.PROGRESS_COLOR_SOURCE.Name(), UISettings.ProgressColorSource.RANDOM.getName());
		return UISettings.ProgressColorSource.get(choice);
	}

	public boolean firstRun() {
		String setting = prefs.get(LABEL.FIRST_RUN.Name(), "true");
		return setting.equals("true");
	}

	public boolean showToolBar() {
		String setting = prefs.get(LABEL.TOOL_BAR.Name(), "true");
		return setting.equals("true");
	}

	public UISettings.LoginScreen securityOption() {
		String option = prefs.get(LABEL.SECURITY_OPTION.Name(), UISettings.LoginScreen.UNKNOWN.Name());
		return UISettings.LoginScreen.get(option);
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
		String setting = prefs.get("test", "0xffff00ff");
		return Color.valueOf(setting);
	}

	public String progressBarStyle() {
		String colorString  = "#" + progressBarColor().toString().replaceFirst("0x", "").substring(0, 6);
		String defaultStyle = "-fx-accent: " + colorString + ";";
		return prefs.get(LABEL.PROGRESS_BAR_STYLE.Name(), defaultStyle);
	}

	public boolean disableDirtyWarning() {
		String setting = prefs.get(LABEL.DISABLE_DIRTY_WARNING.Name(), "false");
		return setting.equals("true");
	}

	public boolean wideMode() {
		String setting = prefs.get(LABEL.WIDE_MODE.Name(), "false");
		return setting.equals("true");
	}

	public String dividerPositions() {
		return prefs.get(LABEL.DIVIDER_POSITIONS.Name(), "");
	}

	public String metadata() {
		return prefs.get(LABEL.METADATA.Name(), "");
	}

	public boolean fileMoveWarning() {
		String setting = prefs.get(LABEL.FILE_MOVE_WARNING.Name(), "true");
		return setting.equals("true");
	}

	private Color getRandomColor() {
		Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.HOTPINK, Color.LIGHTBLUE};
		Random  random = new Random();
		int     top    = colors.length - 1;
		return colors[random.nextInt(top)];
	}

	/**
	 * Tree Icon SMTPServerSettingsa
	 */

	public File userIconFileFolder() {
		String path = prefs.get(LABEL.USER_ICON_FILE_FOLDER.Name(), System.getProperty("user.home"));
		File file = new File(path);
		if (!file.exists()) {
			file = new File(System.getProperty("user.home"));
		}
		return file;
	}

	public boolean useDefaultCategoryIcon() {
		return prefs.get(LABEL.USE_DEFAULT_CATEGORY_ICON.Name(), "true").equals("true");
	}

	public boolean useDefaultGistIcon() {
		return prefs.get(LABEL.USE_DEFAULT_GIST_ICON.Name(), "true").equals("true");
	}

	public boolean useDefaultFileIcon() {
		return prefs.get(LABEL.USE_DEFAULT_FILE_ICON.Name(), "true").equals("true");
	}

	public String userCategoryIconPath() {
		String path = prefs.get(LABEL.USER_CATEGORY_ICON_PATH.Name(), "");
		if(!path.isEmpty()) return "file:" + path;
		return "";
	}

	public String userGistIconPath() {
		String path = prefs.get(LABEL.USER_GIST_ICON_PATH.Name(), "");
		if(!path.isEmpty()) return "file:" + path;
		return "";
	}

	public String userFileIconPath() {
		String path = prefs.get(LABEL.USER_FILE_ICON_PATH.Name(), "");
		if(!path.isEmpty()) return "file:" + path;
		return "";
	}

	public String userCategoryIconName() {
		Path path = Paths.get(userCategoryIconPath());
		return path.toFile().getName();
	}

	public String userGistIconName() {
		Path path = Paths.get(userGistIconPath());
		return path.toFile().getName();
	}

	public String userFileIconName() {
		Path path = Paths.get(userFileIconPath());
		return path.toFile().getName();
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
		String value = prefs.get(LABEL.RUN_IN_SYSTRAY.Name(), "false");
		return value.toLowerCase().equals("true");
	}

	public String systrayColor() {
		return prefs.get(LABEL.SYSTRAY_COLOR.Name(), "White");
	}

	public boolean showAppIcon() {
		String value = prefs.get(LABEL.SHOW_APP_ICON.Name(), "true");
		return value.toLowerCase().equals("true");
	}
}
