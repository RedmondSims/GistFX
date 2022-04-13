package com.redmondsims.gistfx.ui.trayicon;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.redmondsims.gistfx.enums.OS;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.utils.Resources;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.net.MalformedURLException;


public class TrayIcon {

	private static FXTrayIcon trayIcon;
	private static MenuItem   menuNewGist;
	private static MenuItem menuEditCategories;
	private static MenuItem menuManageApplication;
	private static MenuItem menuAppSettings;
	private static MenuItem menuTreeSettings;
	private static MenuItem menuExit;
	private static MenuItem menuShowGistFX;


	public static void start(Stage primaryStage, boolean show) {
		try {
			if(FXTrayIcon.isSupported()) {
				assignMenuActions();
				int dimension = LiveSettings.getOS().equals(OS.WINDOWS) ? 19 : 26;
				trayIcon = new FXTrayIcon
						.Builder(primaryStage, Resources.getTrayIconFile().toURL(), dimension, dimension)
						.menuItem(menuExit)
						.build();
				if(show) show();
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void show() {
		if(AppSettings.get().runInSystemTray()) {
			if (trayIcon != null)
				trayIcon.show();
		}
	}

	public static void hide() {
		if (trayIcon != null)
			trayIcon.hide();
	}

	public static void loggedIn() {
		if(trayIcon != null)
			addMenusToTrayIcon();
	}

	private static void close() {
		SceneOne.closeAll();
		System.exit(0);
	}

	private static MenuItem newMenuItem(String label, EventHandler<ActionEvent> eventHandler) {
		MenuItem menuItem = new MenuItem(label);
		menuItem.setOnAction(eventHandler);
		return menuItem;
	}

	private static void assignMenuActions() {
		menuShowGistFX = newMenuItem("Open GistFX", e->WindowManager.showGistWindow());
		menuNewGist    = newMenuItem("New Gist",e-> WindowManager.newGist());
		menuEditCategories = newMenuItem("Edit Categories", e->WindowManager.editCategories());
		menuAppSettings = newMenuItem("Application Settings", e->WindowManager.showAppSettings());
		menuTreeSettings = newMenuItem("Tree Settings", e->WindowManager.showTreeSettings());
		menuExit = newMenuItem("Exit",e->close());
	}

	private static void addMenusToTrayIcon() {
		trayIcon.clear();
		trayIcon.addMenuItems(menuShowGistFX);
		trayIcon.addSeparator();
		trayIcon.addMenuItems(menuNewGist,menuEditCategories,menuAppSettings,menuTreeSettings);
		trayIcon.addSeparator();
		trayIcon.addMenuItem(menuExit);
	}

	public static void setGraphic() {
		if(LiveSettings.getOS().equals(OS.WINDOWS)) {
			trayIcon.setGraphic(Resources.getTrayIconFile(),17,17);
		}
		else {
			trayIcon.setGraphic(Resources.getTrayIconFile(),26,26);
		}
	}
}
