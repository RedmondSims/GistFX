package com.redmondsims.gistfx;

import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;

import java.awt.*;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;

public class Launcher {


	public static void main(String[] args) throws BackingStoreException {
		LiveSettings.setDevMode(Arrays.stream(args).toList().contains("devmode"));
/*
		DevLABEL.prefs.clear();
		System.exit(0);
*/

		if (AppSettings.get().runInSystray() && !AppSettings.get().showAppIcon()) {
			System.setProperty("apple.awt.UIElement", "true");
			Toolkit.getDefaultToolkit();
		}
		Main.main(args);
	}
}
