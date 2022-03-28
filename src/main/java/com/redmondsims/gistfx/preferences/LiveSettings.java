package com.redmondsims.gistfx.preferences;

import com.redmondsims.gistfx.enums.OS;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.preferences.UISettings.DataSource;
import com.redmondsims.gistfx.preferences.UISettings.ProgressColorSource;
import javafx.scene.paint.Color;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.redmondsims.gistfx.enums.OS.*;
import static com.redmondsims.gistfx.preferences.UISettings.Theme.DARK;

public class LiveSettings {

	private static       DataSource                  dataSource;
	private static       UISettings.Theme            theme                 = DARK;
	private static       UISettings.LoginScreen      loginScreen;
	private static       UISettings.LoginScreenColor loginScreenColor;
	public static        Color                       progressBarColor      = AppSettings.get().progressBarColor();
	public static        ProgressColorSource         progressBarColorSource;
	private static       boolean                     disableDirtyWarning;
	private static       Color                       dirtyFileFlagColor;
	public static        boolean                     doMasterReset         = false;
	private static       Double                      lastPaneSplitValue    = 0.0;
	private static final String                      OSystem               = System.getProperty("os.name").toLowerCase();
	private static       String                      password              = "";
	private static       boolean                     authenticatedToGitHub = false;
	private static       Taskbar                     taskbar;
	private final static Integer tcpPortNumber = 59383;

	public static Path getFilePath() {
		Path finalPath;
		if(getOS().equals(OS.MAC)) {
			finalPath = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "GistFX");
		}
		else if(getOS().equals(OS.WINDOWS)) {
			finalPath = Paths.get(System.getenv("APPDATA"),"GistFX");
		}
		else {
			finalPath = Paths.get(System.getProperty("user.home"),".gistfx");
		}
		if (!finalPath.toFile().exists()) {
			finalPath.toFile().mkdir();
		}
		return finalPath;
	}

	public static Integer getTcpPortNumber() {
		return tcpPortNumber;
	}

	public static void applyAppSettings() {
		dataSource             = AppSettings.get().dataSource();
		progressBarColorSource = AppSettings.get().progressColorSource();
		theme                  = AppSettings.get().theme();
		dirtyFileFlagColor     = AppSettings.get().dirtyFileFlagColor();
		disableDirtyWarning    = AppSettings.get().disableDirtyWarning();
		loginScreenColor       = AppSettings.get().loginScreenColor();
		setLoginScreen(AppSettings.get().loginScreenChoice());
		GistManager.refreshDirtyFileFlags();
	}

	public static void setTaskbar(Taskbar taskbar) {
		LiveSettings.taskbar = taskbar;
	}

	public static Taskbar getTaskbar() {
		return taskbar;
	}

	public static void setDataSource(DataSource source) {
		AppSettings.set().dataSource(source);
		applyAppSettings();
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

	public static UISettings.Theme getTheme() {
		return theme;
	}

	public static void setTheme(UISettings.Theme theme) {
		LiveSettings.theme = theme;
	}

	public static UISettings.LoginScreen getLoginScreen() {
		return loginScreen;
	}

	public static UISettings.LoginScreenColor getLoginScreenColor() {return loginScreenColor;}

	public static String getLoginColor() {
		return loginScreenColor.folderName(loginScreenColor);
	}

	public static void setLoginScreen(UISettings.LoginScreen loginScreen) {
		LiveSettings.loginScreen = loginScreen;
	}

	public static boolean disableDirtyWarning() {return disableDirtyWarning;}

	public static void setDisableDirtyWarning(boolean setting) {
		AppSettings.set().disableDirtyWarning(setting);
		applyAppSettings();
	}

	public static Color getDirtyFileFlagColor() {return dirtyFileFlagColor;}

	public static void setDirtyFileFlagColor(Color color) {
		AppSettings.set().dirtyFileFlagColor(color);
		applyAppSettings();
	}

	public static void setLastPaneSplitValue(Double lastPaneSplitValue) {
		LiveSettings.lastPaneSplitValue = lastPaneSplitValue;
	}

	public static Double getLastPaneSplitValue() {
		return lastPaneSplitValue;
	}

	public static OS getOS() {
		if (OSystem.toLowerCase().contains("win")) return WINDOWS;
		else if (OSystem.toLowerCase().contains("mac")) return MAC;
		else return LINUX;
	}

	public static void setGitHubAuthenticated(boolean authenticated) {
		authenticatedToGitHub = authenticated;
	}

	public static boolean gitHubAuthenticated() {
		return authenticatedToGitHub;
	}

	public static void setPassword(String password) {
		LiveSettings.password = password;
	}

	public static String getPassword() {
		return password;
	}
}
