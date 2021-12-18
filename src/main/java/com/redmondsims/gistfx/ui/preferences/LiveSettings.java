package com.redmondsims.gistfx.ui.preferences;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.github.gist.GistManager;
import com.redmondsims.gistfx.ui.preferences.UISettings.DataSource;
import com.redmondsims.gistfx.ui.preferences.UISettings.ProgressColorSource;
import javafx.scene.CacheHint;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.Objects;

import static com.redmondsims.gistfx.ui.preferences.UISettings.Theme.DARK;

public class LiveSettings {


	private static DataSource             dataSource;
	private static UISettings.Theme       theme            = DARK;
	private static UISettings.LoginScreen loginScreen;
	public static  Color                  progressBarColor = AppSettings.getProgressBarColor();
	public static  ProgressColorSource    progressBarColorSource;
	public static  Boolean                useJsonGist;
	public static  boolean                flagDirtyFiles;
	public static  Color                  dirtyFileFlagColor;
	public static  boolean                doMasterReset    = false;

	public static void applyAppSettings() {
		progressBarColorSource = AppSettings.getProgressColorSource();
		useJsonGist            = AppSettings.getSaveToGist();
		theme                  = AppSettings.getTheme();
		flagDirtyFiles         = AppSettings.getFlagDirtyFile();
		dirtyFileFlagColor     = AppSettings.getDirtyFileFlagColor();
		setLoginScreen(AppSettings.getLoginScreenChoice());
		GistManager.refreshDirtyFileFlags();
	}

	public static void setDataSource(DataSource source) {
		dataSource = source;
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

	public static ImageView getDirtyFlag() {
		String    dirtyFlagPath = Objects.requireNonNull(Main.class.getResource("DirtyFlag.png")).toExternalForm();
		Image     image         = new Image(dirtyFlagPath);
		ImageView imageView     = new ImageView(image);
		imageView.setClip(new ImageView(image));
		ColorAdjust monochrome = new ColorAdjust();
		monochrome.setSaturation(-1.0);
		Blend brush = new Blend(BlendMode.MULTIPLY,
								monochrome,
								new ColorInput(0, 0,
											   imageView.getImage().getWidth(),
											   imageView.getImage().getHeight(),
											   AppSettings.getDirtyFileFlagColor()));
		imageView.setEffect(brush);

		imageView.setCache(true);
		imageView.setCacheHint(CacheHint.SPEED);
		return imageView;
	}

	public static ImageView getConflictFlag() {
		String    conflictFlagPath = Objects.requireNonNull(Main.class.getResource("ConflictFlag.png")).toExternalForm();
		Image     image         = new Image(conflictFlagPath);
		return new ImageView(image);
	}

	public static UISettings.LoginScreen getLoginScreen() {
		return loginScreen;
	}

	public static void setLoginScreen(UISettings.LoginScreen loginScreen) {
		LiveSettings.loginScreen = loginScreen;
	}
}
