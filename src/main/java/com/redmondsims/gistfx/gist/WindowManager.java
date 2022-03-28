package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.enums.FileState;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.networking.Payload;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.gist.GistWindow;
import com.redmondsims.gistfx.ui.gist.factory.TreeNode;
import com.redmondsims.gistfx.utils.Status;
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
				Status.setState(State.NORMAL);
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
		buttonTimer.schedule(buttonTask(), 500);
	}

	private static TimerTask buttonTask() {
		return new TimerTask() {
			@Override public void run() {
				if (gistWindow != null) {
					gistWindow.handleButtonBar();
				}
			}
		};
	}

	public static void deleteGist() {
		gistWindow.getActions().deleteGist(gistWindow.getGist());
	}

	public static void newGist() {
		gistWindow.getActions().newGist(gistWindow.getSelectedNode());
	}

	public static void deleteFile() {
		gistWindow.getActions().deleteFile(gistWindow.getFile());
	}

	public static void renameFile() {
		gistWindow.getActions().renameFile(gistWindow.getFile());
	}

	public static void newFile() {
		gistWindow.getActions().newFile(gistWindow.getGist());
	}

	public static void shareObject() {
		gistWindow.shareObject(gistWindow.getSelectedNode());
	}

	public static void receiveData(Payload payload) {
		gistWindow.getActions().receiveData(payload);
	}

	public static void deleteCategory() {
		gistWindow.getActions().deleteCategory(gistWindow.getSelectedNode());
	}

	public static void editCategories() {
		gistWindow.getActions().editCategories();
	}

	public static void renameGist() {
		gistWindow.getActions().renameGist(gistWindow.getGist());
	}

	public static void renameCategory() {
		gistWindow.getActions().renameCategory();
	}

	public static void setConflict(GistFile file, FileState conflict, boolean selected) {
		gistWindow.getTreeActions().setFileDirtyState(file, conflict, selected);
		gistWindow.handleButtonBar();
	}

	public static void setPBarStyle(String style) {
		gistWindow.setPBarStyle(style);
	}

	public static void updateFileContent(String content) {
		gistWindow.updateFileContent(content);
	}

	public static void handleTreeEvent(TreeItem<TreeNode> treeItem) {
		gistWindow.getTreeActions().handleTreeEvent(treeItem);
	}

	public static void refreshBranch(GistFile gistFile) {
		gistWindow.getTreeActions().refreshGistBranch(gistFile);
	}

	public static void refreshTree() {
		gistWindow.getTreeActions().refreshTree();
	}

	public static void fillTree() {
		gistWindow.fillTree();
	}

	private static Timer iconTimer;

	public static void refreshFileIcons() {
		//Use of Timer prevents multiple rapid calls from executing the method more than once
		if (iconTimer != null) iconTimer.cancel();
		iconTimer = new Timer();
		iconTimer.schedule(refreshIcons(), 500);
	}

	public static void updateGitHubLabel(String text, boolean show) {
		if(gistWindow != null)
			gistWindow.updateGitLabel(text,show);
	}

	private static TimerTask refreshIcons() {
		return new TimerTask() {
			@Override public void run() {
				gistWindow.getTreeActions().refreshIcons();
			}
		};
	}
}
