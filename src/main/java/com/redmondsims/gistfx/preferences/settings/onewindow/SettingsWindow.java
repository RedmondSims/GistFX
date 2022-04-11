package com.redmondsims.gistfx.preferences.settings.onewindow;

import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.settings.onewindow.screens.GuiElements;
import com.redmondsims.gistfx.preferences.settings.onewindow.screens.TreeTest;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.utils.Resources;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import static javafx.scene.control.PopupControl.USE_PREF_SIZE;
import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;

public class SettingsWindow {

	public SettingsWindow() {
		createForm();
		double height = 325;
		setGuiElements();
		SceneOne.set(tabPane, sceneId,SceneOne.getStage(Resources.getSceneIdGistWindow()))
				.size(width, height)
				.centered().title("Application Settings")
				.onCloseEvent(e->closing())
				.styleSheets(LiveSettings.getTheme().getStyleSheet())
				.build();
	}

	private final TreeTest treeSettingsCategory = new TreeTest();
	private final TreeTest treeSettingsGist     = new TreeTest();
	private final TreeTest treeSettingsFile     = new TreeTest();
	private final double   width                = 550;
	private final String   sceneId              = "SettingWindow";
	private final Tab      tabGUI               = new Tab("GUI");
	private final Tab      tabTree              = new Tab("Tree");
	private final Tab      tabWide              = new Tab("Wide Mode");
	private final Tab      tabReset             = new Tab("Reset");
	private final Tab      tabCategory          = treeSettingsCategory.contents(Type.CATEGORY);
	private final Tab      tabGist              = treeSettingsGist.contents(Type.GIST);
	private final Tab      tabFile              = treeSettingsFile.contents(Type.FILE);
	private final TabPane  tabPane              = new TabPane(tabGUI, tabTree, tabWide, tabReset);
	private final TabPane  tabPaneTree          = new TabPane(tabCategory, tabGist, tabFile);

	private void createForm() {
		tabGUI.setClosable(false);
		tabTree.setClosable(false);
		tabWide.setClosable(false);
		tabReset.setClosable(false);
		tabCategory.setClosable(false);
		tabGist.setClosable(false);
		tabFile.setClosable(false);
		tabPaneTree.setMaxHeight(USE_PREF_SIZE);
		tabPaneTree.setMaxWidth(USE_PREF_SIZE);
		tabPaneTree.setMinHeight(USE_PREF_SIZE);
		tabPaneTree.setMinWidth(USE_PREF_SIZE);
		tabPaneTree.setPrefHeight(325.0);
		tabPaneTree.setPrefWidth(550.0);
		tabPaneTree.setTabClosingPolicy(UNAVAILABLE);
		tabTree.setContent(tabPaneTree);
	}


	private GuiElements guiElements;
	private void setGuiElements() {
		guiElements = new GuiElements(width);
		VBox vbox = guiElements.controls();
		tabGUI.setContent(vbox);
	}

	private void setWideMode() {

	}

	private void setResetOptions() {

	}

	public void show() {
		SceneOne.showAndWait(sceneId);
	}

	private void closing() {

	}

}
