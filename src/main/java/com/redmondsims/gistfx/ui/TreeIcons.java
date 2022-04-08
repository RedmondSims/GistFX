package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.Launcher;
import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.utils.Resources;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TreeIcons {

	private final static Path   treeIconPath = Resources.getExternalTreeIconPath();
	private static       Path   dirtyFlag;
	private static       Path   conflictFlag;
	private static       Path   folderIcon;
	private static       Path   fileIcon;
	private final static String ToolBar      = "Icons/ToolBar/";

	public static void init() {
		dirtyFlag    = Paths.get("file:" + treeIconPath,"DirtyFlag.png");
		conflictFlag = Paths.get("file:" + treeIconPath,"Conflict.png");
		folderIcon   = Paths.get("file:" + treeIconPath,"Folder.png");
		fileIcon     = Paths.get("file:" + treeIconPath,"file.png");
	}

	public static InputStream getToolBarIcon(String iconName) {
		return Launcher.class.getResourceAsStream(ToolBar + iconName);
	}

	public static ImageView getDirtyIcon() {
		return newImageView(dirtyFlag, AppSettings.get().dirtyFileFlagColor());
	}

	public static ImageView getConflictIcon() {
		ImageView iv = newImageView(conflictFlag);
		iv.setPreserveRatio(true);
		iv.setFitWidth(17);
		return iv;
	}

	public static ImageView getGistCategoryIcon() {
		Color   color        = AppSettings.get().categoryFolderIconColor();
		boolean useDefault   = AppSettings.get().useDefaultCategoryIcon();
		Path    userIconPath = Paths.get(AppSettings.get().userCategoryIconPath());
		if (!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(folderIcon,AppSettings.get().categoryFolderIconColor()) : newImageView(userIconPath);
	}

	public static ImageView getGistIcon() {
		Color   color        = AppSettings.get().gistFolderIconColor();
		boolean useDefault   = AppSettings.get().useDefaultGistIcon();
		Path    userIconPath = Paths.get(AppSettings.get().userGistIconPath());
		if (!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(folderIcon,AppSettings.get().gistFolderIconColor()) : newImageView(userIconPath);
	}

	public static ImageView getFileIcon() {
		Color   color        = AppSettings.get().fileIconColor();
		boolean useDefault   = AppSettings.get().useDefaultFileIcon();
		Path    userIconPath = Paths.get(AppSettings.get().userFileIconPath());
		if (!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(fileIcon,AppSettings.get().fileIconColor()) : newImageView(userIconPath);
	}

	private static ImageView reColor(Image inputImage, Color finalColor) {
		Color sourceColor = Color.WHITE;
		int W = (int) inputImage.getWidth();
		int H = (int) inputImage.getHeight();
		WritableImage  outputImage = new WritableImage(W, H);
		PixelReader    reader      = inputImage.getPixelReader();
		PixelWriter    writer      = outputImage.getPixelWriter();
		float          ocR         = (float) sourceColor.getRed();
		float          ocG         = (float) sourceColor.getGreen();
		float          ocB         = (float) sourceColor.getBlue();
		float          ncR         = (float) finalColor.getRed();
		float          ncG         = (float) finalColor.getGreen();
		float          ncB         = (float) finalColor.getBlue();
		java.awt.Color oldColor    = new java.awt.Color(ocR, ocG, ocB);
		java.awt.Color newColor    = new java.awt.Color(ncR, ncG, ncB);
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				int            argb       = reader.getArgb(x, y);
				java.awt.Color pixelColor = new java.awt.Color(argb, true);
				writer.setArgb(x, y,
							   pixelColor.equals(oldColor) ?
							   newColor.getRGB() :
							   pixelColor.getRGB());
			}
		}
		ImageView ivOut = new ImageView(outputImage);
		ivOut.setPreserveRatio(true);
		ivOut.setFitWidth(15);
		return ivOut;
	}

	private static ImageView newImageView(Path imagePath, Color newColor) {
		return reColor(new Image(imagePath.toString()), newColor);
	}

	private static ImageView newImageView(Path imagePath) {
		return new ImageView(new Image(imagePath.toString()));
	}
}
