package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.utils.Resources;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TreeIcons {

	private final static Path   treeIconPath = Resources.getExternalTreeIconPath();
	private static       String dirtyFlag;
	private static       String conflictFlag;
	private static       String folderIcon;
	private static       String fileIcon;
	private final static String ToolBar      = "Icons/ToolBar/";

	public static void init() {
		dirtyFlag    = Paths.get(treeIconPath.toString(),"DirtyFlag.png").toString();
		conflictFlag = Paths.get(treeIconPath.toString(),"Conflict.png").toString();
		folderIcon   = Paths.get(treeIconPath.toString(),"Folder.png").toString();
		fileIcon     = Paths.get(treeIconPath.toString(),"file.png").toString();
	}

	public static InputStream getToolBarIcon(String iconName) {
		return Main.class.getResourceAsStream(ToolBar + iconName);
	}

	public static ImageView getDirtyIcon() {
		return newImageView(dirtyFlag, AppSettings.get().dirtyFileFlagColor());
	}

	public static ImageView getConflictIcon() {
		return newImageView(conflictFlag);
	}

	public static ImageView getGistCategoryIcon() {
		Color   color        = AppSettings.get().categoryFolderIconColor();
		boolean useDefault   = AppSettings.get().useDefaultCategoryIcon();
		Path    userIconPath = Paths.get(Resources.getExternalUserIconPath().toString(), AppSettings.get().userCategoryIcon());
		if (!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(folderIcon,AppSettings.get().categoryFolderIconColor()) : newImageView(userIconPath.toString());
	}

	public static ImageView getGistIcon() {
		Color   color        = AppSettings.get().gistFolderIconColor();
		boolean useDefault   = AppSettings.get().useDefaultGistIcon();
		Path    userIconPath = Paths.get(Resources.getExternalUserIconPath().toString(), AppSettings.get().userGistIcon());
		if (!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(folderIcon,AppSettings.get().gistFolderIconColor()) : newImageView(userIconPath.toString());
	}

	public static ImageView getFileIcon() {
		Color   color        = AppSettings.get().fileIconColor();
		boolean useDefault   = AppSettings.get().useDefaultFileIcon();
		Path    userIconPath = Paths.get(Resources.getExternalUserIconPath().toString(), AppSettings.get().userFileIcon());
		if (!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(fileIcon,AppSettings.get().fileIconColor()) : newImageView(userIconPath.toString());
	}

	private static ImageView reColor(Image inputImage, Color finalColor) {
		boolean file = inputImage.getUrl().toLowerCase().contains("file.png");
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
		if(file)
			ivOut.setFitWidth(12.5);
		else
			ivOut.setFitWidth(14);
		return ivOut;
	}

	private static ImageView newImageView(String imagePath, Color newColor) {
		return reColor(new Image("file:" + imagePath), newColor);
	}

	private static ImageView newImageView(String imagePath) {
		ImageView iv = new ImageView(new Image("file:" + imagePath));
		iv.setPreserveRatio(true);
		iv.setFitWidth(14);
		return iv;
	}
}
