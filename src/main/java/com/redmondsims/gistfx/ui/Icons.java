package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.Launcher;
import java.nio.file.Path;

import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.utils.Resources;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.nio.file.Paths;

public class Icons {

	private static Path dirtyFlag;
	private static Path conflictFlag;
	private static Path folderIcon;
	private static Path fileIcon;

	private final static String DirtyFlagName = "DirtyFlagNew4.png";
	private final static String ConflictName  = "ConflictFlag2.png";
	private final static String FileName      = "File.png";
	private final static String FolderName    = "Folder.png";
	private final static String ToolBar       = "Icons/ToolBar/";

	public static void init() {
		for(Path treeIconPath : Resources.treeIconPaths()) {
			String filename = FilenameUtils.getName(treeIconPath.toString());
			switch(filename) {
				case DirtyFlagName -> dirtyFlag = treeIconPath;
				case ConflictName -> conflictFlag = treeIconPath;
				case FolderName -> folderIcon = treeIconPath;
				case FileName -> fileIcon = treeIconPath;
			}
		}
	}

	public static InputStream getToolBarIcon(String iconName) {
		return Launcher.class.getResourceAsStream(ToolBar + iconName);
	}

	public static ImageView getDirtyIcon() {
		return newImageView(dirtyFlag, Color.WHITE, AppSettings.get().dirtyFileFlagColor());
	}

	public static ImageView getConflictIcon() {
		ImageView iv = newImageView(conflictFlag);
		iv.setPreserveRatio(true);
		iv.setFitWidth(17);
		return iv;
	}

	public static ImageView getGistCategoryIcon() {
		Color color = AppSettings.get().categoryFolderIconColor();
		boolean useDefault   = AppSettings.get().useDefaultCategoryIcon();
		Path    userIconPath = (Path) Paths.get(AppSettings.get().userCategoryIconPath());
		if(!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(folderIcon, Color.WHITE, color) : newImageView(userIconPath);
	}

	public static ImageView getGistIcon() {
		Color color = AppSettings.get().gistFolderIconColor();
		boolean useDefault = AppSettings.get().useDefaultGistIcon();
		Path userIconPath = (Path) Paths.get(AppSettings.get().userGistIconPath());
		if(!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(folderIcon, Color.WHITE, color) : newImageView(userIconPath);
	}

	public static ImageView getFileIcon() {
		Color color =  AppSettings.get().fileIconColor();
		boolean useDefault = AppSettings.get().useDefaultFileIcon();
		Path userIconPath = (Path) Paths.get(AppSettings.get().userFileIconPath());
		if(!userIconPath.toFile().exists()) useDefault = true;
		return (useDefault) ? newImageView(fileIcon, Color.WHITE, color) : newImageView(userIconPath);
	}

	private static ImageView reColor(Image inputImage, Color sourceColor, Color finalColor) {
		int            W           = (int) inputImage.getWidth();
		int            H           = (int) inputImage.getHeight();
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

	private static ImageView newImageView(Path imagePath, Color oldColor, Color newColor) {
		return reColor(new Image(imagePath.toString()), oldColor, newColor);
	}

	private static ImageView newImageView(Path imagePath) {
		return new ImageView(new Image(imagePath.toString()));
	}
}
