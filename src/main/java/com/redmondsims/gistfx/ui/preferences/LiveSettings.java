package com.redmondsims.gistfx.ui.preferences;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.github.gist.GistManager;
import javafx.scene.CacheHint;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import com.redmondsims.gistfx.ui.preferences.UISettings.ProgressColorSource;
import java.util.Objects;

public class LiveSettings {


	public static  UISettings.DataSource  dataSource;
	public static  UISettings.Theme       theme            = UISettings.Theme.DARK;
	public static  Color                  progressBarColor = AppSettings.getProgressBarColor();
	public static  ProgressColorSource    progressBarColorSource;
	public static  Boolean                useJsonGist;
	private static UISettings.LoginScreen loginScreen;
	public static  boolean                flagDirtyFiles;
	public static  Color                  dirtyFileFlagColor;
	public static  boolean                doMasterReset    = false;

	public static void applyUserPreferences() {
		progressBarColorSource = AppSettings.getProgressColorSource();
		useJsonGist            = AppSettings.getSaveToGist();
		theme                  = AppSettings.getTheme();
		flagDirtyFiles         = AppSettings.getFlagDirtyFile();
		dirtyFileFlagColor     = AppSettings.getDirtyFileFlagColor();
		setLoginScreen(AppSettings.getLoginScreenChoice());
		GistManager.refreshDirtyFileFlags();
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

	public static UISettings.LoginScreen getLoginScreen() {
		return loginScreen;
	}

	public static void setLoginScreen(UISettings.LoginScreen loginScreen) {
		LiveSettings.loginScreen = loginScreen;
	}
}
