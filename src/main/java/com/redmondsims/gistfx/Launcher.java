package com.redmondsims.gistfx;

import com.redmondsims.gistfx.preferences.AppSettings;

import java.awt.*;

public class Launcher {


	public static void main(String[] args) {
		if (AppSettings.get().runInSystray() && !AppSettings.get().showAppIcon()) {
			System.setProperty("apple.awt.UIElement", "true");
			Toolkit.getDefaultToolkit();
		}
		Main.main(args);
	}
}
