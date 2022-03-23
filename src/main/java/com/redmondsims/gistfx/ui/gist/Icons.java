package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.preferences.AppSettings;
import javafx.scene.CacheHint;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URL;

public class Icons {


	private static final URL dirtyFlag    = Main.class.getResource("Icons/Flags/DirtyFlag.png");
	private static final URL conflictFlag = Main.class.getResource("Icons/Flags/ConflictFlag.png");
	private static final URL fileIcon     = Main.class.getResource("Icons/FileIcon.png");
	private static final URL folderIcon   = Main.class.getResource("Icons/Folder.png");

	public static ImageView getDirtyFlagIcon() {
		return coloredImage(dirtyFlag, AppSettings.get().dirtyFileFlagColor(), 1);
	}

	public static ImageView getCategoryFolderIcon() {
		ImageView imageView = null;
		boolean useDefault = AppSettings.get().useDefaultCategoryIcon();
		if (useDefault) {
			imageView = coloredImage(folderIcon, AppSettings.get().categoryFolderIconColor(), .2);
		}
		else {
			File iconFile = new File(AppSettings.get().userCategoryIconPath());
			if(iconFile != null) {
				if (iconFile.exists()) {
					String iconPath = "file:" + iconFile.getAbsolutePath();
					Image  image    = new Image(iconPath);
					imageView = new ImageView(image);
					imageView.setPreserveRatio(true);
					imageView.setFitWidth(15);
				}
			}
		}
		return imageView;
	}

	public static ImageView getGistFolderIcon() {
		ImageView imageView = null;
		boolean useDefault = AppSettings.get().useDefaultGistIcon();
		if (useDefault) {
			imageView = coloredImage(folderIcon, AppSettings.get().gistFolderIconColor(), .2);
		}
		else {
			File iconFile = new File(AppSettings.get().userGistIconPath());
			if(iconFile != null) {
				if (iconFile.exists()) {
					String iconPath = "file:" + iconFile.getAbsolutePath();
					Image  image    = new Image(iconPath);
					imageView = new ImageView(image);
					imageView.setPreserveRatio(true);
					imageView.setFitWidth(15);
				}
			}
		}
		return imageView;
	}

	public static ImageView getFileIcon() {
		ImageView imageView = null;
		boolean useDefault = AppSettings.get().useDefaultFileIcon();
		if(useDefault) {
			imageView = coloredImage(fileIcon, AppSettings.get().fileIconColor(), .1);
		}
		else {
			File iconFile = new File(AppSettings.get().userFileIconPath());
			if(iconFile != null) {
				if (iconFile.exists()) {
					String iconPath = "file:" + iconFile.getAbsolutePath();
					Image  image    = new Image(iconPath);
					imageView = new ImageView(image);
					imageView.setPreserveRatio(true);
					imageView.setFitWidth(15);
				}
			}
		}
		return imageView;
	}

	public static ImageView getConflictFlag() {
		return new ImageView(new Image(conflictFlag.toExternalForm()));
	}

	private static ImageView coloredImage(URL imagePath, Color color, double brightness) {
		Image     image     = new Image(imagePath.toExternalForm());
		ImageView imageView = new ImageView(image);
		imageView.setClip(new ImageView(image));
		ColorAdjust monochrome = new ColorAdjust();
		monochrome.setSaturation(-1);
		monochrome.setBrightness(brightness);
		Blend brush = new Blend(BlendMode.DARKEN,
								monochrome,
								new ColorInput(0, 0,
											   imageView.getImage().getWidth(),
											   imageView.getImage().getHeight(),
											   color));
		imageView.setEffect(brush);
		imageView.setCache(true);
		imageView.setCacheHint(CacheHint.SPEED);
		return imageView;
	}
}
