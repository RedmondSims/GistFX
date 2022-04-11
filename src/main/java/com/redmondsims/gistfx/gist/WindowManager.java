package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.networking.Payload;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.gist.GistWindow;
import com.redmondsims.gistfx.ui.gist.factory.TreeNode;
import com.redmondsims.gistfx.utils.Resources;
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

	public static void deleteGist() {
		if(gistWindow != null)
		gistWindow.getActions().deleteGist(gistWindow.getGist());
	}

	public static void newGist() {
		if(gistWindow != null)
			gistWindow.getActions().newGist(gistWindow.getSelectedNode());
	}

	public static void deleteFile() {
		if(gistWindow != null)
			gistWindow.getActions().deleteFile(gistWindow.getFile());
	}

	public static void renameFile() {
		if(gistWindow != null)
			gistWindow.getActions().renameFile(gistWindow.getFile());
	}

	public static void newFile() {
		if(gistWindow != null)
			gistWindow.getActions().newFile(gistWindow.getGist());
	}

	public static void shareObject() {
		if(gistWindow != null)
			gistWindow.shareObject(gistWindow.getSelectedNode());
	}

	public static void receiveData(Payload payload) {
		if(gistWindow != null)
			gistWindow.getActions().receiveData(payload);
	}

	public static void deleteCategory() {
		if(gistWindow != null)
			gistWindow.getActions().deleteCategory(gistWindow.getSelectedNode());
	}

	public static void editCategories() {
		if(gistWindow != null)
			gistWindow.getActions().editCategories();
	}

	public static void renameGist() {
		gistWindow.getActions().renameGist(gistWindow.getGist());
	}

	public static void renameCategory() {
		if(gistWindow != null)
		gistWindow.getActions().renameCategory();
	}

	public static void setPBarStyle(String style) {
		if(gistWindow != null)
		gistWindow.setPBarStyle(style);
	}

	public static void updateFileContent(String content) {
		if(gistWindow != null)
		gistWindow.updateFileContent(content);
	}

	public static void handleTreeEvent(TreeItem<TreeNode> treeItem) {
		if(gistWindow != null)
		gistWindow.getTreeActions().handleTreeEvent(treeItem);
	}

	public static void refreshLeaf(GistFile gistFile) {
		if(gistWindow != null)
		gistWindow.getTreeActions().refreshGistLeaf(gistFile);
	}

	public static void refreshTree() {
		if(gistWindow != null)
		gistWindow.getTreeActions().refreshTree();
	}

	public static void fillTree() {
		if(gistWindow != null)
		gistWindow.fillTree();
	}

	public static void refreshTreeIcons() {
		if(gistWindow != null) {
			gistWindow.getTreeActions().refreshTreeIcons();
		}
	}

	public static void showAppSettings() {
		if(gistWindow != null)
		gistWindow.showAppSettings();
	}

	public static void showTreeSettings() {
		if(gistWindow != null)
		gistWindow.showTreeSettings();
	}

	public static void updateGitHubLabel(String text, boolean show) {
		if(gistWindow != null)
			gistWindow.updateGitLabel(text,show);
	}

	public static void showGistWindow() {
		SceneOne.show(Resources.getSceneIdGistWindow());
	}

}
