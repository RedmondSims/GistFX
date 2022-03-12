package com.redmondsims.gistfx.preferences.settings;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.preferences.UISettings;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Random;
import java.util.prefs.Preferences;

public class Get {

	private final Preferences prefs = PREFERENCE.prefs;

	public String hashedToken() {
		return prefs.get(PREFERENCE.TOKEN_HASH.Name(), "");
	}

	public String hashedPassword() {
		return prefs.get(PREFERENCE.PASSWORD_HASH.Name(), "");
	}

	public UISettings.DataSource dataSource() {
		String option = prefs.get(PREFERENCE.DATA_SOURCE.Name(), UISettings.DataSource.GITHUB.Name());
		return UISettings.DataSource.get(option);
	}

	public Color progressBarColor() {
		if (progressColorSource() == UISettings.ProgressColorSource.RANDOM) {
			return getRandomColor();
		}
		String color = prefs.get(PREFERENCE.PROGRESS_BAR_COLOR.Name(), "#FF0000");
		return Color.valueOf(color);
	}

	public UISettings.LoginScreen loginScreenChoice() {
		String option = prefs.get(PREFERENCE.LOGIN_SCREEN.Name(), UISettings.LoginScreen.GRAPHIC.Name());
		return UISettings.LoginScreen.get(option);
	}

	public UISettings.LoginScreenColor loginScreenColor() {
		String option = prefs.get(PREFERENCE.LOGIN_SCREEN_COLOR.Name(), UISettings.LoginScreenColor.GREEN.Name());
		return UISettings.LoginScreenColor.get(option);
	}

	public UISettings.ProgressColorSource progressColorSource() {
		String choice = prefs.get(PREFERENCE.PROGRESS_COLOR_SOURCE.Name(), UISettings.ProgressColorSource.RANDOM.getName());
		return UISettings.ProgressColorSource.get(choice);
	}

	public boolean firstRun() {
		String setting = prefs.get(PREFERENCE.FIRST_RUN.Name(), "true");
		return setting.equals("true");
	}

	public boolean showButtonBar() {
		String setting = prefs.get(PREFERENCE.BUTTON_BAR.Name(), "true");
		return setting.equals("true");
	}

	public UISettings.LoginScreen securityOption() {
		String option = prefs.get(PREFERENCE.SECURITY_OPTION.Name(), UISettings.LoginScreen.UNKNOWN.Name());
		return UISettings.LoginScreen.get(option);
	}

	public UISettings.Theme theme() {
		String option = prefs.get(PREFERENCE.THEME.Name(), UISettings.Theme.DARK.Name());
		return UISettings.Theme.get(option);
	}

	public Color dirtyFileFlagColor() {
		String setting = prefs.get(PREFERENCE.DIRTY_FILE_FLAG_COLOR.Name(), "#FF0000");
		return Color.valueOf(setting);
	}

	public Color categoryFolderIconColor() {
		String setting = PREFERENCE.prefs.get(PREFERENCE.CATEGORY_FOLDER_ICON_COLOR.Name(), "0xffa200ff");
		return Color.valueOf(setting);
	}

	public Color gistFolderIconColor() {
		String setting = prefs.get(PREFERENCE.GIST_FOLDER_ICON_COLOR.name(), "0xffff00ff");
		return Color.valueOf(setting);
	}

	public Color fileIconColor() {
		String setting = prefs.get("test", "0xffff00ff");
		return Color.valueOf(setting);
	}

	public boolean saveToGist() {
		String setting = prefs.get(PREFERENCE.JSON_GIST.Name(), "true");
		return setting.equals("true");
	}

	public String progressBarStyle() {
		String colorString  = "#" + progressBarColor().toString().replaceFirst("0x", "").substring(0, 6);
		String defaultStyle = "-fx-accent: " + colorString + ";";
		return prefs.get(PREFERENCE.PROGRESS_BAR_STYLE.Name(), defaultStyle);
	}

	public boolean disableDirtyWarning() {
		String setting = prefs.get(PREFERENCE.DISABLE_DIRTY_WARNING.Name(), "false");
		return setting.equals("true");
	}

	public boolean wideMode() {
		String setting = prefs.get(PREFERENCE.WIDE_MODE.Name(), "false");
		return setting.equals("true");
	}

	public String dividerPositions() {
		return prefs.get(PREFERENCE.DIVIDER_POSITIONS.Name(), "");
	}

	public String metadata() {
		return prefs.get(PREFERENCE.METADATA.Name(), "");
	}

	public boolean fileMoveWarning() {
		String setting = prefs.get(PREFERENCE.FILE_MOVE_WARNING.Name(), "true");
		return setting.equals("true");
	}

	private Color getRandomColor() {
		Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.HOTPINK, Color.LIGHTBLUE};
		Random  random = new Random();
		int     top    = colors.length - 1;
		return colors[random.nextInt(top)];
	}

	/**
	 * Tree Icon Settings
	 */

	public File userIconFileFolder() {
		String path = prefs.get(PREFERENCE.USER_ICON_FILE_FOLDER.Name(), System.getProperty("user.home"));
		File file = new File(path);
		if (!file.exists()) {
			file = new File(System.getProperty("user.home"));
		}
		return file;
	}

	public boolean useDefaultCategoryIcon() {
		String setting = prefs.get(PREFERENCE.USE_DEFAULT_CATEGORY_ICON.Name(), "true");
		return setting.equals("true");
	}

	public boolean useDefaultGistIcon() {
		String setting = prefs.get(PREFERENCE.USE_DEFAULT_GIST_ICON.Name(), "true");
		return setting.equals("true");
	}

	public boolean useDefaultFileIcon() {
		String setting = prefs.get(PREFERENCE.USE_DEFAULT_FILE_ICON.Name(), "true");
		return setting.equals("true");
	}

	public File userCategoryIcon() {
		String defaultPath = Main.class.getResource("Icons/Folder.png").toExternalForm().replaceFirst("file:","");
		String path = prefs.get(PREFERENCE.USER_CATEGORY_ICON_PATH.Name(), defaultPath);
		File file = null;
		if (!path.isEmpty()) {
			file = new File(path);
		}
		return file;
	}

	public String userCategoryIconName() {
		String path = userCategoryIcon().getAbsolutePath();
		String response = "";
		if (!path.isEmpty()) {
			File file = new File(path);
			response = file.getName();
		}
		return response;
	}

	public File userGistIcon() {
		String defaultPath = Main.class.getResource("Icons/Folder.png").toExternalForm().replaceFirst("file:","");
		String path = prefs.get(PREFERENCE.USER_GIST_ICON_PATH.Name(), defaultPath);
		File file = null;
		if (!path.isEmpty()) {
			file = new File(path);
		}
		return file;
	}

	public String userGistIconName() {
		String path = userGistIcon().getAbsolutePath();
		String response = "";
		if (!path.isEmpty()) {
			File file = new File(path);
			response = file.getName();
		}
		return response;
	}

	public File userFileIcon() {
		String defaultPath = Main.class.getResource("Icons/FileIcon.png").toExternalForm().replaceFirst("file:", "");
		String path = prefs.get(PREFERENCE.USER_FILE_ICON_PATH.Name(), defaultPath);
		File file = null;
		if (!path.isEmpty()) {
			file = new File(path);
		}
		return file;
	}

	public String userFileIconName() {
		String path = userFileIcon().getAbsolutePath();
		String response = "";
		if (!path.isEmpty()) {
			File file = new File(path);
			response = file.getName();
		}
		return response;
	}

}
