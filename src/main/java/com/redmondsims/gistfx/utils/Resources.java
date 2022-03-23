package com.redmondsims.gistfx.utils;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.preferences.LiveSettings;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Resources {

	private final static String helpFiles = "HelpFiles";

	public static void copyHelpFiles() {
		String root            = LiveSettings.getFilePath().toString();
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
			assert is != null;
			FileUtils.copyInputStreamToFile(is, outFile);
			for (int x = 1; x <= 7; x++) {
				filename = x + ".png";
				is       = Main.class.getResourceAsStream(helpFiles + "/HowToToken/" + filename);
				outFile  = new File(helpToken.toFile(), filename);
				assert is != null;
				FileUtils.copyInputStreamToFile(is, outFile);
			}

			for (int x = 1; x <= 2; x++) {
				filename = x + ".png";
				is       = Main.class.getResourceAsStream(helpFiles + "/General/" + filename);
				outFile  = new File(helpGeneral.toFile(), filename);
				assert is != null;
				FileUtils.copyInputStreamToFile(is, outFile);
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

	public static void deleteAllResources() {

	}

}
