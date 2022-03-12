package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.GistWindow;
import com.redmondsims.gistfx.ui.tree.DragNode;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;

import java.util.Timer;
import java.util.TimerTask;

public class WindowManager {

	private static GistWindow gistWindow;

	public static void newGistWindow(Source launchSource) {
		Platform.runLater(() -> {
			if (!launchSource.equals(Source.RELOAD)) {
				gistWindow = new GistWindow();
				gistWindow.showMainWindow(launchSource);
			}
			if (launchSource.equals(Source.RELOAD)) {
				gistWindow.fillTree();
				CustomAlert.showInfo("All data re-downloaded successfully.", SceneOne.getOwner("GistWindow"));
			}
		});
	}

	private static Timer buttonTimer;

	public static void handleButtons() {
		//Use of Timer prevents multiple rapid calls from executing the method more than once
		if (buttonTimer != null) buttonTimer.cancel();
		buttonTimer = new Timer();
		buttonTimer.schedule(buttonTesk(),500);
	}

	private static TimerTask buttonTesk() {
		return new TimerTask() {
			@Override public void run() {
				if (gistWindow != null) {
					gistWindow.handleButtonBar();
				}
			}
		};
	}

	public static void deleteGist() {
		gistWindow.deleteGist();
	}

	public static void newGist() {
		gistWindow.newGist();
	}

	public static void deleteFile() {
		gistWindow.deleteFile();
	}

	public static void renameFile() {
		gistWindow.renameFile();
	}

	public static void newFile() {
		gistWindow.newFile();
	}

	public static void deleteCategory() {
		gistWindow.deleteCategory();
	}

	public static void editCategories() {
		gistWindow.editCategories();
	}

	public static void renameGist() {
		gistWindow.renameGist();
	}

	public static void renameCategory() {
		gistWindow.renameCategory();
	}

	public static void setConflict(GistFile file, Type conflict, boolean selected) {
		gistWindow.setFileDirtyState(file,conflict,selected);
		gistWindow.handleButtonBar();
	}

	public static void setPBarStyle(String style) {
		gistWindow.setPBarStyle(style);
	}

	public static void updateFileContent(String content) {
		gistWindow.updateFileContent(content);
	}

	public static void handleTreeEvent(TreeItem<DragNode> treeItem) {
		gistWindow.handleTreeEvent(treeItem);
	}

	public static void refreshBranch(GistFile gistFile) {
		gistWindow.refreshGistBranch(gistFile);
	}

	public static void refreshTree() {
		gistWindow.refreshTree();
	}

	private static Timer iconTimer;

	public static void refreshFileIcons() {
		//Use of Timer prevents multiple rapid calls from executing the method more than once
		if (iconTimer != null) iconTimer.cancel();
		iconTimer = new Timer();
		iconTimer.schedule(refreshIcons(), 500);
	}

	private static TimerTask refreshIcons() {
		return new TimerTask() {
			@Override public void run() {
				gistWindow.refreshIcons();
			}
		};
	}
}
