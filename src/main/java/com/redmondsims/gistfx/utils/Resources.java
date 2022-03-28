package com.redmondsims.gistfx.utils;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.gist.Icons;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Resources {

	private final static String helpFiles = "HelpFiles";
	private final static String icons = "Icons";
	private final static String tree = "Tree";
	private final static String root = LiveSettings.getFilePath().toString();
	private final static File rootFile = new File(root);
	private final static File iconRootFile = new File(rootFile,icons);
	private final static File treeIconRootFile = new File(iconRootFile,"Tree");
	private final static String conflictName = "ConflictFlag.png";
	private final static String dirtyFlagName = "DirtyFlag.png";
	private final static String file1Name = "File1.png";
	private final static String file2Name = "File2.png";
	private final static String folderName  = "Folder.png";
	private final static List<String> sourceFiles = new ArrayList<>(Arrays.asList(conflictName, dirtyFlagName, file1Name, file2Name, folderName));


	public static List<Path> treeIconPaths () {
		List<Path> list = new ArrayList<>();
		for(String filename : sourceFiles) {
			list.add(Paths.get(root,icons,tree,filename));
		}
		return list;
	}

	public static void init() {
		checkResources();
		Icons.init();
	}

	private static void copyIcons() {
		try {
			for(String sourceFile : sourceFiles) {
				InputStream is = Main.class.getResourceAsStream(icons + "/" + tree + "/" + sourceFile);
				File outFile = new File(treeIconRootFile,sourceFile);
				if (is != null && !outFile.exists()) {
					FileUtils.copyInputStreamToFile(is, outFile);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copyHelpFiles() {
		Path   helpRoot        = Paths.get(root, helpFiles);
		Path   helpToken       = Paths.get(root, helpFiles, "HowToToken");
		Path   helpGeneral     = Paths.get(root, helpFiles, "General");
		File   helpFileRoot    = helpRoot.toFile();
		File   helpFileToken   = helpToken.toFile();
		File   helpFileGeneral = helpGeneral.toFile();
		if (!helpFileRoot.exists()) helpFileRoot.mkdir();
		if (!helpFileToken.exists()) helpFileToken.mkdir();
		if (!helpFileGeneral.exists()) helpFileGeneral.mkdir();
		try {
			String      filename = "Logo.png";
			File        outFile  = new File(helpRoot.toFile(), filename);
			InputStream is       = Main.class.getResourceAsStream(helpFiles + "/" + filename);
			if (is != null && !outFile.exists()) {
				FileUtils.copyInputStreamToFile(is, outFile);
			}
			for (int x = 1; x <= 7; x++) {
				filename = x + ".png";
				is       = Main.class.getResourceAsStream(helpFiles + "/HowToToken/" + filename);
				outFile  = new File(helpToken.toFile(), filename);
				if (is != null && !outFile.exists()) {
					FileUtils.copyInputStreamToFile(is, outFile);
				}
			}

			for (int x = 1; x <= 2; x++) {
				filename = x + ".png";
				is       = Main.class.getResourceAsStream(helpFiles + "/General/" + filename);
				outFile  = new File(helpGeneral.toFile(), filename);
				if (is != null && !outFile.exists()) {
					FileUtils.copyInputStreamToFile(is, outFile);
				}
			}
		}
		catch (IOException e) {e.printStackTrace();
		}
	}

	public static String getHelpRoot() {
		return Paths.get(LiveSettings.getFilePath().toString(),helpFiles).toString();
	}

	private static String getSQLitePath() {
		Path sqlitePath = Paths.get(LiveSettings.getFilePath().toString(),"SQLite");
		if (!sqlitePath.toFile().exists()) sqlitePath.toFile().mkdir();
		return Paths.get(LiveSettings.getFilePath().toString(),"SQLite").toString();
	}

	public static File getSQLiteFile() {
		return new File(getSQLitePath(),"Database.sqlite");
	}

	private static void checkResources() {
		if (!rootFile.exists()) rootFile.mkdir();
		if (!iconRootFile.exists()) iconRootFile.mkdir();
		if (!treeIconRootFile.exists()) treeIconRootFile.mkdir();
		copyHelpFiles();
		copyIcons();
	}

}
