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
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.nio.file.Path;

public class Icons {


	private static String dirtyFlag;
	private static String conflictFlag;
	private static String fileIcon;
	private static String folderIcon;
	private static String base;

	private final static String dirtyFlagName = "DirtyFlag.png";
	private final static String conflictName  = "ConflictFlag.png";
	private final static String file2Name     = "File2.png";
	private final static String folderName    = "Folder.png";
	private final static String file1Name     = "File1.png";
	private final static String baseName      = "Base.png";
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
				case baseName -> {
					base = "file:" + treeIconPath;
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
		Image image;
		image = useDefault ? reColor(new Image(iconPath),Color.WHITE,color) : new Image(iconPath);
		ImageView imageView = new ImageView(image);
/*
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
*/
		imageView.setPreserveRatio(true);
		imageView.setFitWidth(15);
		return imageView;
	}

	public static Image reColor(Image inputImage, Color oldColor, Color newColor) {
		int W = (int) inputImage.getWidth();
		int H = (int) inputImage.getHeight();
		WritableImage outputImage = new WritableImage(W, H);
		PixelReader   reader      = inputImage.getPixelReader();
		PixelWriter   writer      = outputImage.getPixelWriter();
		int         ob     =(int) oldColor.getBlue()*255;
		int or=(int) oldColor.getRed()*255;
		int og=(int) oldColor.getGreen()*255;
		int nb=(int) newColor.getBlue()*255;
		int nr=(int) newColor.getRed()*255;
		int ng=(int) newColor.getGreen()*255;
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				int argb = reader.getArgb(x, y);
				int a = (argb >> 24) & 0xFF;
				int r = (argb >> 16) & 0xFF;
				int g = (argb >>  8) & 0xFF;
				int b =  argb        & 0xFF;
				if (g==og && r==or && b==ob) {
					r=nr;
					g=ng;
					b=nb;
				}

				argb = (a << 24) | (r << 16) | (g << 8) | b;
				writer.setArgb(x, y, argb);
			}
		}
		return outputImage;
	}
}
