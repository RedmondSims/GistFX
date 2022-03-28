package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.utils.Resources;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.nio.file.Path;

public class Icons {


	private static String dirtyFlag;
	private static String conflictFlag;
	private static String fileIcon;
	private static String folderIcon;

	private final static String dirtyFlagName = "DirtyFlag.png";
	private final static String conflictName  = "ConflictFlag.png";
	private final static String file2Name     = "File2.png";
	private final static String folderName    = "Folder.png";
	private final static String file1Name     = "File1.png";
	private static       String iconPath;
	private static final String toolBar       = "Icons/ToolBar/";

	private static final ObjectProperty<Node> categoryGraphicNode = new SimpleObjectProperty<>();


	public static void init() {
		for(Path treeIconPath : Resources.treeIconPaths()) {
			String filename = treeIconPath.toFile().getName();
			switch(filename) {
				case dirtyFlagName -> {
					dirtyFlag = "file:" + treeIconPath;
				}
				case conflictName -> {
					conflictFlag = "file:" + treeIconPath;
				}
				case file1Name -> {
					fileIcon = "file:" + treeIconPath;
				}
				case folderName -> {
					folderIcon = "file:" + treeIconPath;
				}
			}
		}
	}

	public static InputStream getToolBarIcon(String iconName) {
		return Main.class.getResourceAsStream(toolBar + iconName);
	}

	public static ImageView getDirtyIcon() {
		System.out.println("Get Dirty Flag Icon");
		iconPath = dirtyFlag;
		return getFormattedImageView(Color.RED, 1,true);
	}

	public static ImageView getConflictIcon() {
		return new ImageView(new Image(conflictFlag));
	}

	public static ImageView getGistCategoryIcon() {
		Color color = AppSettings.get().categoryFolderIconColor();
		boolean useDefault = AppSettings.get().useDefaultCategoryIcon();
		String userIconPath = AppSettings.get().userCategoryIconPath();
		iconPath = (useDefault || userIconPath.isEmpty()) ? folderIcon : userIconPath;
		return getFormattedImageView(color, .2, useDefault);
	}

	public static ImageView getGistIcon() {
		Color color = AppSettings.get().gistFolderIconColor();
		boolean useDefault = AppSettings.get().useDefaultGistIcon();
		String userIconPath = AppSettings.get().userGistIconPath();
		iconPath = (useDefault || userIconPath.isEmpty()) ? folderIcon : userIconPath;
		return getFormattedImageView(color, .2, useDefault);
	}

	public static ImageView getFileIcon() {
		Color color =  AppSettings.get().fileIconColor();
		boolean useDefault = AppSettings.get().useDefaultFileIcon();
		String userIconPath = AppSettings.get().userFileIconPath();
		iconPath = (useDefault || userIconPath.isEmpty()) ? fileIcon : userIconPath;
		return getFormattedImageView(color, .2, useDefault);
	}

	private static ImageView getFormattedImageView(Color color, double brightness, boolean useDefault) {
		ImageView imageView = new ImageView(new Image(iconPath));
		if(useDefault) {
			ImageView ivClip = new ImageView(new Image(iconPath));
			ivClip.setPreserveRatio(true);
			ivClip.setFitWidth(15);
			imageView.setClip(ivClip);
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
		}
		imageView.setPreserveRatio(true);
		imageView.setFitWidth(15);
		return imageView;
	}
}
