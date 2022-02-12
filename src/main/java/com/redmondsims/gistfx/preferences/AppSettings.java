package com.redmondsims.gistfx.preferences;

import javafx.scene.paint.Color;

import java.util.Random;
import java.util.prefs.Preferences;


public class AppSettings {

	enum SETTING {
		PASSWORD_HASH,
		TOKEN_HASH,
		THEME,
		PROGRESS_BAR_THEME,
		PROGRESS_BAR_COLOR,
		PROGRESS_COLOR_SOURCE,
		LOGIN_SCREEN,
		LOGIN_SCREEN_COLOR,
		LOAD_SOURCE,
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
		DISTRACTION_FREE_WARNING;

		public String Name(SETTING this) {
			return switch (this) {
				case PASSWORD_HASH -> "GFX_PasswordHash";
				case TOKEN_HASH -> "GFX_TokenHash";
				case THEME -> "GFX_Theme";
				case PROGRESS_BAR_THEME -> "GFX_ProgressBarTheme";
				case PROGRESS_BAR_COLOR -> "GFX_ProgressBarColor";
				case PROGRESS_COLOR_SOURCE -> "GFX_ProgressColorSource";
				case LOGIN_SCREEN -> "GFX_LoginScreen";
				case LOGIN_SCREEN_COLOR -> "GFX_LoginScreenColor";
				case LOAD_SOURCE -> "GFX_LoadSource";
				case FIRST_RUN -> "GFX_FirstRun";
				case BUTTON_BAR -> "GFX_ShowButtonBar";
				case SECURITY_OPTION -> "GFX_SecurityOption";
				case FLAG_DIRTY_FILES -> "GFX_FlagDirtyFile";
				case DIRTY_FILE_FLAG_COLOR -> "GFX_DirtyFileFlagColor";
				case JSON_GIST -> "GFX_StoreJsonDataInGist";
				case PROGRESS_BAR_STYLE -> "GFX_ProgressBarStyle";
				case WIDE_MODE -> "GFX_WideMode";
				case DIVIDER_POSITIONS -> "GFX_DividerPosition";
				case DISABLE_DIRTY_WARNING -> "GFX_DisableDirtyWarning";
				case DISTRACTION_FREE_WARNING -> "GFX_DistractionFreeWarning";
			};
		}
	}

	private static final Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);

	//GETTERS

	public static String getHashedToken() {
		return prefs.get(SETTING.TOKEN_HASH.Name(), "");
	}

	public static String getHashedPassword() {
		return prefs.get(SETTING.PASSWORD_HASH.Name(), "");
	}

	public static UISettings.DataSource getLoadSource() {
		String option = prefs.get(SETTING.LOAD_SOURCE.Name(), UISettings.DataSource.GITHUB.Name());
		return UISettings.DataSource.get(option);
	}

	public static Color getProgressBarColor() {
		if (getProgressColorSource() == UISettings.ProgressColorSource.RANDOM) {
			return getRandomColor();
		}
		String color = prefs.get(SETTING.PROGRESS_BAR_COLOR.Name(), "#FF0000");
		return Color.valueOf(color);
	}

	public static UISettings.LoginScreen getLoginScreenChoice() {
		String option = prefs.get(SETTING.LOGIN_SCREEN.Name(), UISettings.LoginScreen.GRAPHIC.Name());
		return UISettings.LoginScreen.get(option);
	}

	public static UISettings.LoginScreenColor getLoginScreenColor() {
		String option = prefs.get(SETTING.LOGIN_SCREEN_COLOR.Name(), UISettings.LoginScreenColor.GREEN.Name());
		return UISettings.LoginScreenColor.get(option);
	}

	public static UISettings.ProgressColorSource getProgressColorSource() {
		String choice = prefs.get(SETTING.PROGRESS_COLOR_SOURCE.Name(), UISettings.ProgressColorSource.RANDOM.getName());
		return UISettings.ProgressColorSource.get(choice);
	}

	public static boolean getFirstRun() {
		String setting = prefs.get(SETTING.FIRST_RUN.Name(), "true");
		return setting.equals("true");
	}

	public static boolean getShowButtonBar() {
		String setting = prefs.get(SETTING.BUTTON_BAR.Name(), "false");
		return setting.equals("true");
	}

	public static UISettings.LoginScreen getSecurityOption() {
		String option = prefs.get(SETTING.SECURITY_OPTION.Name(), UISettings.LoginScreen.UNKNOWN.Name());
		return UISettings.LoginScreen.get(option);
	}

	public static UISettings.Theme getTheme() {
		String option = prefs.get(SETTING.THEME.Name(), UISettings.Theme.DARK.Name());
		return UISettings.Theme.get(option);
	}

	public static boolean getFlagDirtyFile() {
		String setting = prefs.get(SETTING.FLAG_DIRTY_FILES.Name(), "true");
		return setting.equals("true");
	}

	public static Color getDirtyFileFlagColor() {
		String setting = prefs.get(SETTING.DIRTY_FILE_FLAG_COLOR.Name(), "#FF0000");
		return Color.valueOf(setting);
	}

	public static boolean getSaveToGist() {
		String setting = prefs.get(SETTING.JSON_GIST.Name(), "true");
		return setting.equals("true");
	}

	public static String getProgressBarStyle() {
		String colorString = "#" + getProgressBarColor().toString().replaceFirst("0x","").substring(0,6);
		String defaultStyle ="-fx-accent: " + colorString + ";";
		return prefs.get(SETTING.PROGRESS_BAR_STYLE.Name(), defaultStyle);
	}

	public static boolean getDisableDirtyWarning() {
		String setting = prefs.get(SETTING.DISABLE_DIRTY_WARNING.Name(), "false");
		return setting.equals("true");
	}

	public static boolean getWideMode() {
		String setting = prefs.get(SETTING.WIDE_MODE.Name(), "false");
		return setting.equals("true");
	}

	public static String getDividerPositions() {
		return prefs.get(SETTING.DIVIDER_POSITIONS.Name(), "");
	}

	public static boolean getDistractionFreeWarning() {
		String setting = prefs.get(SETTING.DISTRACTION_FREE_WARNING.Name(), "true");
		return setting.equals("true");
	}

	//SETTERS - Always remove before setting

	public static void setHashedToken(String tokenHash) {
		clearTokenHash();
		prefs.put(SETTING.TOKEN_HASH.Name(), tokenHash);
	}

	public static void setHashedPassword(String passwordHash) {
		clearPasswordHash();
		prefs.put(SETTING.PASSWORD_HASH.Name(), passwordHash);
	}

	public static void setDataSource(UISettings.DataSource option) {
		clearLoadSource();
		prefs.put(SETTING.LOAD_SOURCE.Name(), option.Name());
	}

	public static void setProgressBarColor(Color color) {
		clearProgressBarColor();
		prefs.put(SETTING.PROGRESS_BAR_COLOR.Name(), String.valueOf(color));
	}

	public static void setLoginScreenChoice(UISettings.LoginScreen option) {
		clearLogonScreenChoice();
		prefs.put(SETTING.LOGIN_SCREEN.Name(), option.Name());
	}

	public static void setLoginScreenColor(UISettings.LoginScreenColor option) {
		clearLogonScreenChoice();
		prefs.put(SETTING.LOGIN_SCREEN_COLOR.Name(), option.Name());
	}

	public static void setProgressColorSource(UISettings.ProgressColorSource choice) {
		clearProgressColorSource();
		prefs.put(SETTING.PROGRESS_COLOR_SOURCE.Name(), String.valueOf(choice));
	}

	public static void setFirstRun(boolean setting) {
		clearFirstRun();
		prefs.put(SETTING.FIRST_RUN.Name(), String.valueOf(setting));
	}

	public static void setShowButtonBar(boolean setting) {
		clearButtonBar();
		prefs.put(SETTING.BUTTON_BAR.Name(), String.valueOf(setting));
	}

	public static void setSecurityOption(UISettings.LoginScreen option) {
		clearSecurityOption();
		prefs.put(SETTING.SECURITY_OPTION.Name(), option.Name());
	}

	public static void setTheme(UISettings.Theme theme, String source) {
		clearTheme();
		prefs.put(SETTING.THEME.Name(), theme.Name());
	}

	public static void setFlagDirtyFile(boolean setting) {
		clearFlagDirtyFile();
		prefs.put(SETTING.FLAG_DIRTY_FILES.Name(), String.valueOf(setting));
	}

	public static void setDirtyFileFlagColor(Color color) {
		clearDirtyFileFlagColor();
		prefs.put(SETTING.DIRTY_FILE_FLAG_COLOR.Name(),color.toString());
	}

	public static void setSaveToGist(boolean setting) {
		clearJsonGist();
		prefs.put(SETTING.JSON_GIST.Name(), String.valueOf(setting));
	}

	public static void setProgressBarStyle(String style) {
		clearProgressBarStyle();
		prefs.put(SETTING.PROGRESS_BAR_STYLE.Name(), style);
	}

	public static void setDisableDirtyWarning(boolean setting) {
		clearDisableDirtyWarning();
		prefs.put(SETTING.DISABLE_DIRTY_WARNING.Name(), String.valueOf(setting));
	}

	public static void setWideMode(boolean setting) {
		clearWideMode();
		prefs.put(SETTING.WIDE_MODE.Name(), String.valueOf(setting));
	}

	public static void setDividerPositions(String positions) {
		clearDividerPositions();
		prefs.put(SETTING.DIVIDER_POSITIONS.Name(), positions);
	}

	public static void setDistractionFreeWarning(boolean setting) {
		clearDistractionFreeWarning();
		prefs.put(SETTING.DISTRACTION_FREE_WARNING.Name(), String.valueOf(setting));
	}

	//REMOVERS

	private static void clearLoadSource()                 {prefs.remove(SETTING.LOAD_SOURCE.Name());}

	private static void clearLogonScreenChoice() {prefs.remove(SETTING.LOGIN_SCREEN.Name());}

	private static void clearLogonScreenColor() {prefs.remove(SETTING.LOGIN_SCREEN_COLOR.Name());}

	private static void clearProgressColorSource() {prefs.remove(SETTING.PROGRESS_COLOR_SOURCE.Name());}

	private static void clearProgressBarColor()           {prefs.remove(SETTING.PROGRESS_BAR_COLOR.Name());}

	public static void clearPasswordHash()                {prefs.remove(SETTING.PASSWORD_HASH.Name());}

	public static void clearTokenHash()                   {prefs.remove(SETTING.TOKEN_HASH.Name());}

	private static void clearTheme()                      {prefs.remove(SETTING.THEME.Name());}

	private static void clearFirstRun() {
		prefs.remove(SETTING.FIRST_RUN.Name());
	}

	private static void clearButtonBar() {
		prefs.remove(SETTING.BUTTON_BAR.Name());
	}

	private static void clearSecurityOption() {prefs.remove(SETTING.SECURITY_OPTION.Name());}

	private static void clearFlagDirtyFile() {prefs.remove(SETTING.FLAG_DIRTY_FILES.Name());}

	private static void clearDirtyFileFlagColor() {prefs.remove(SETTING.DIRTY_FILE_FLAG_COLOR.name());}

	public static void clearJsonGist() { prefs.remove(SETTING.JSON_GIST.Name());}

	public static void clearProgressBarStyle() {prefs.remove(SETTING.PROGRESS_BAR_STYLE.Name());}

	public static void clearDisableDirtyWarning() {prefs.remove(SETTING.DISABLE_DIRTY_WARNING.Name());}

	public static void clearWideMode() {prefs.remove(SETTING.WIDE_MODE.Name());}

	public static void clearDividerPositions() {
		prefs.remove(SETTING.DIVIDER_POSITIONS.Name());
	}

	public static void clearDistractionFreeWarning() {
		prefs.remove(SETTING.DISTRACTION_FREE_WARNING.Name());
	}


	public static void resetPreferences() {
		clearLoadSource();
		clearLogonScreenChoice();
		clearProgressColorSource();
		clearProgressBarColor();
		clearTheme();
		clearFirstRun();
		clearButtonBar();
		clearSecurityOption();
		clearJsonGist();
		clearProgressBarStyle();
		clearDisableDirtyWarning();

		setDataSource(getLoadSource());
		setLoginScreenChoice(getLoginScreenChoice());
		setProgressColorSource(getProgressColorSource());
		setProgressBarColor(getProgressBarColor());
		setTheme(getTheme(),"resetPreferences");
		setFirstRun(true);
		setShowButtonBar(getShowButtonBar());
		setSaveToGist(getSaveToGist());
		setProgressBarStyle(getProgressBarStyle());
		setDisableDirtyWarning(getDisableDirtyWarning());
	}

	public static void resetCredentials() {
		clearPasswordHash();
		clearTokenHash();
	}

	private static Color getRandomColor() {
		Color[] colors = new Color[]{Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW,Color.ORANGE,Color.HOTPINK,Color.LIGHTBLUE};
		Random random = new Random();
		int top = colors.length -1;
		return colors[random.nextInt(top)];
	}

}
