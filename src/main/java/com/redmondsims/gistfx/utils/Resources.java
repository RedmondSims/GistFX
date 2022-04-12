package com.redmondsims.gistfx.utils;

import com.redmondsims.gistfx.Launcher;
import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Resources {

	private final static String       helpFiles            = "HelpFiles";
	private final static String       icons                = "Icons";
	private final static String       tree                 = "Tree";
	private final static String       tray                 = "Tray";
	private final static String       sqlIte               = "SQLite";
	private final static String       user                 = "User";
	private final static String       dev                  = "Dev";
	private final static String       root                 = LiveSettings.getDevMode() ? Paths.get(LiveSettings.getFilePath().toString(), dev).toString() : Paths.get(LiveSettings.getFilePath().toString()).toString();
	private final static Path         externalRootPath     = Paths.get(root);
	private final static Path         externalIconPath     = Paths.get(root, icons);
	private final static Path         externalTreeIconPath = Paths.get(root, icons, tree);
	private final static Path         externalUserIconPath = Paths.get(root, icons, tree, user);
	private final static Path         externalSQLPath      = Paths.get(root, sqlIte);
	private final static Path         externalHelpFilePath = Paths.get(root, helpFiles);
	private final static Path         externalTrayIconPath = Paths.get(root, icons, tray);
	private final static String       conflictName         = "Conflict.png";
	private final static String       dirtyFlagName        = "DirtyFlag.png";
	private final static String       fileName             = "File.png";
	private final static String       folderName           = "Folder.png";
	private final static List<String> sourceFiles          = new ArrayList<>(Arrays.asList(conflictName, dirtyFlagName, folderName, fileName));
	private final static String       trayIconWhite        = "White.png";
	private final static String       trayIconBlack        = "Black.png";
	private final static String       trayIconBlue         = "Blue.png";
	private final static String       trayIconGreen        = "Green.png";
	private final static String       trayIconHotPink      = "HotPink.png";
	private final static String       trayIconRed          = "Red.png";
	private final static String       trayIconYellow       = "Yellow.png";
	private final static List<String> trayIconSourceFiles = new ArrayList<>(Arrays.asList(trayIconWhite,trayIconBlack,trayIconBlue,trayIconGreen,trayIconHotPink,trayIconRed,trayIconYellow));
	private final static String       sceneIdGistWindow    = "GistWindow";

	public static void init() {
		checkResources();
	}

	public static Path getExternalRootPath() {
		return externalRootPath;
	}

	public static Path getExternalUserIconPath() {
		return externalUserIconPath;
	}

	public static Path getExternalTreeIconPath() {
		return externalTreeIconPath;
	}

	public static File getTrayIconFile() {
		return switch (AppSettings.get().systrayColor()) {
			case WHITE -> Paths.get(externalTrayIconPath.toString(),trayIconWhite).toFile();
			case BLACK -> Paths.get(externalTrayIconPath.toString(),trayIconBlack).toFile();
			case BLUE -> Paths.get(externalTrayIconPath.toString(),trayIconBlue).toFile();
			case GREEN -> Paths.get(externalTrayIconPath.toString(),trayIconGreen).toFile();
			case HOTPINK -> Paths.get(externalTrayIconPath.toString(),trayIconHotPink).toFile();
			case RED -> Paths.get(externalTrayIconPath.toString(),trayIconRed).toFile();
			case YELLOW -> Paths.get(externalTrayIconPath.toString(),trayIconYellow).toFile();
		};
	}

	public static List<Path> treeIconPaths() {
		List<Path> list = new ArrayList<>();
		for (String filename : sourceFiles) {
			list.add(Paths.get("file:" + Paths.get(root, icons, tree, filename)));
		}
		return list;
	}

	public static String getHelpRoot() {
		return externalHelpFilePath.toString();
	}

	public static File getSQLiteFile() {
		String databaseFilename;
		if (LiveSettings.getDevMode()) {
			databaseFilename = "DatabaseDev.sqlite";
		}
		else {
			databaseFilename = "Database.sqlite";
		}
		if (!externalSQLPath.toFile().exists()) externalSQLPath.toFile().mkdir();
		return new File(externalSQLPath.toFile(), databaseFilename);
	}

	private static void checkResources() {
		if (!externalRootPath.toFile().exists()) externalRootPath.toFile().mkdir();
		if (!externalIconPath.toFile().exists()) externalIconPath.toFile().mkdir();
		if (!externalTreeIconPath.toFile().exists()) externalTreeIconPath.toFile().mkdir();
		if (!externalSQLPath.toFile().exists()) externalSQLPath.toFile().mkdir();
		if (!externalHelpFilePath.toFile().exists()) externalHelpFilePath.toFile().mkdir();
		if (!externalUserIconPath.toFile().exists()) externalUserIconPath.toFile().mkdir();
		if (!externalTrayIconPath.toFile().exists()) externalTrayIconPath.toFile().mkdir();
		copyHelpFiles();
		copyIcons();
	}

	private static void copyHelpFiles() {
		Path helpRoot        = Paths.get(root, helpFiles);
		Path helpToken       = Paths.get(root, helpFiles, "HowToToken");
		Path helpGeneral     = Paths.get(root, helpFiles, "General");
		File helpFileRoot    = helpRoot.toFile();
		File helpFileToken   = helpToken.toFile();
		File helpFileGeneral = helpGeneral.toFile();
		if (!helpFileRoot.exists()) helpFileRoot.mkdir();
		if (!helpFileToken.exists()) helpFileToken.mkdir();
		if (!helpFileGeneral.exists()) helpFileGeneral.mkdir();
		try {
			String      filename = "Logo.png";
			File        outFile  = new File(helpRoot.toFile(), filename);
			InputStream is       = Launcher.class.getResourceAsStream(helpFiles + "/" + filename);
			if (is != null && !outFile.exists()) {
				FileUtils.copyInputStreamToFile(is, outFile);
			}
			for (int x = 1; x <= 7; x++) {
				filename = x + ".png";
				is       = Launcher.class.getResourceAsStream(helpFiles + "/HowToToken/" + filename);
				outFile  = new File(helpToken.toFile(), filename);
				if (is != null && !outFile.exists()) {
					FileUtils.copyInputStreamToFile(is, outFile);
				}
			}

			for (int x = 1; x <= 2; x++) {
				filename = x + ".png";
				is       = Launcher.class.getResourceAsStream(helpFiles + "/General/" + filename);
				outFile  = new File(helpGeneral.toFile(), filename);
				if (is != null && !outFile.exists()) {
					FileUtils.copyInputStreamToFile(is, outFile);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copyIcons() {
		try {
			for (String sourceFile : sourceFiles) {
				InputStream is      = Main.class.getResourceAsStream(icons + "/" + tree + "/" + sourceFile);
				File        outFile = new File(externalTreeIconPath.toFile(), sourceFile);
				if (is != null && !outFile.exists()) {
					FileUtils.copyInputStreamToFile(is, outFile);
				}
				else if (!outFile.exists()){
					System.err.println("Something is wrong with the internal resource (Resources class, copyIcons(): " + sourceFile);
					System.exit(0);
				}
			}
			for (String trayIconFile : trayIconSourceFiles) {
				InputStream is      = Main.class.getResourceAsStream(icons + "/" + tray + "/" + trayIconFile);
				File        outFile = new File(externalTrayIconPath.toFile(), trayIconFile);
				if (is != null && !outFile.exists()) {
					FileUtils.copyInputStreamToFile(is, outFile);
				}
				else if (!outFile.exists()){
					System.err.println("Something is wrong with the internal resource (Resources class, copyIcons(): " + trayIconFile);
					System.exit(0);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyUserIcon(String userIconPath) {
		try {
			Path sourcePath = Paths.get(userIconPath);
			if(sourcePath.toFile().exists()) {
				String fileName = FilenameUtils.getName(sourcePath.toString());
				File outFile = new File(externalUserIconPath.toFile(),fileName);
				if(!outFile.exists()) {
					FileUtils.copyFile(sourcePath.toFile(),outFile);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getSceneIdGistWindow() {
		return sceneIdGistWindow;
	}
}
