package com.redmondsims.gistfx.ui.gist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.Launcher;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.Languages;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.*;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.help.Help;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.networking.Transport;
import com.redmondsims.gistfx.preferences.*;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.Editors;
import com.redmondsims.gistfx.ui.gist.factory.TreeCellFactory;
import com.redmondsims.gistfx.ui.gist.factory.TreeNode;
import com.redmondsims.gistfx.ui.trayicon.TrayIcon;
import com.redmondsims.gistfx.utils.Resources;
import com.redmondsims.gistfx.utils.Status;
import com.simtechdata.waifupnp.UPnP;
import eu.mihosoft.monacofx.MonacoFX;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import javafx.stage.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.*;

import static com.redmondsims.gistfx.enums.PaneState.*;
import static com.redmondsims.gistfx.enums.TreeType.*;

public class GistWindow {

	private static final Response           PROCEED               = Response.PROCEED;
	private final        CheckBox           publicCheckBox        = new CheckBox("");
	private final        Label              lblCheckBox           = new Label("Public");
	private final        Label              lblGistDescription    = new Label();
	private final        Label              lblDescriptionLabel   = new Label("Description:");
	private final        Label              lblGistNameLabel      = new Label("  Gist Name:");
	private final        Label              lblGitUpdate          = new Label("Updating GitHub");
	private final        Label              lblGistName           = new Label();

	private final        Button             buttonSaveFile        = new Button("Upload File");
	private final        Button             buttonCopyToClipboard = new Button("Clipboard");
	private final        Button             buttonPasteFromClip   = new Button("Paste");
	private final        Button             buttonUndo            = new Button("Undo");
	private final        Button             buttonCompare         = new Button("Resolve Conflict");
	private final        Button             buttonWideMode        = new Button("Wide Mode");
	private final        Button             buttonFullScreen      = new Button("Full Screen");
	private final        Button             buttonDistraction     = new Button("Distraction Free");
	private final        Button             buttonEditCategories  = new Button("Edit Categories");
	//private final        ButtonBar          showToolBar             = new ButtonBar();
	private final        MyMenuBar          menuBar               = new MyMenuBar();
	private final        AnchorPane         ap                    = new AnchorPane();
	private final        AnchorPane         apPane                = new AnchorPane();
	private final        TextArea           taFileDescription     = new TextArea();
	private final        ProgressBar        pBar                  = Action.getProgressNode(10);
	private              TreeType           buttonBarType         = GIST;
	private final        CBooleanProperty   paneExpanded          = new CBooleanProperty(false);
	private final        CBooleanProperty   showToolBar           = new CBooleanProperty(false);
	public final         CBooleanProperty   inWideMode            = new CBooleanProperty(false);
	private final        CBooleanProperty   savingData            = new CBooleanProperty(false);
	public final         CBooleanProperty   inFullScreen          = new CBooleanProperty(false);
	private final        CBooleanProperty   windowResizing        = new CBooleanProperty(false);
	private final        CBooleanProperty   recordExpanded        = new CBooleanProperty(false);
	private final        CBooleanProperty   recordSplit           = new CBooleanProperty(false);
	private final        CBooleanProperty   detachToolbar         = new CBooleanProperty(false);
	private final        Transport          transport             = new Transport();
	private              TreeItem<TreeNode> treeRoot;
	private              gistWindowActions  actions;
	private              TreeActions        treeActions;
	private              GistFile           file;
	private              Gist               gist;
	private              SplitPane          splitPane;
	private              PaneSplitSetting   paneSplitSetting;
	private              String             gistURL               = "";
	private              TreeView<TreeNode> treeView;
	private final        String             sceneId               = Resources.getSceneIdGistWindow();
	private final        Gson               gson                  = new GsonBuilder().setPrettyPrinting().create();
	private              Timer              resizeTimer;
	private final        KeyCodeCombination kcCodeMac             = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);
	private final        KeyCodeCombination kcCodeOther           = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
	private final        String             compareWarning        = "Please wait until I'm done comparing local Gists with GitHub.";
	private final        ToolBar toolBars        = new ToolBar(this);
	private       HBox           toolBarAnchored = toolBars.nothingSelected();
	private       HBox           toolBarDetached;


	private void setAnchors(Node node, double left, double right, double top, double bottom) {
		if (top != -1) AnchorPane.setTopAnchor(node, top);
		if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
		if (left != -1) AnchorPane.setLeftAnchor(node, left);
		if (right != -1) AnchorPane.setRightAnchor(node, right);
	}

	/**
	 * UI Related Methods
	 */

	public void showMainWindow(Source launchSource) {
		if (!launchSource.equals(Source.BLANK))
			createTree();
		showToolBar.setValue(com.redmondsims.gistfx.preferences.AppSettings.get().showToolBar());
		placeControlsOnPane();
		setControlLayoutProperties();
		setControlActionProperties();
		addMenuBarItems();
		buttonBarType = CATEGORY;
		handleButtonBar();
		configurePaneSplitting();
		createScene();
		setPaneSplit();
		actions     = new gistWindowActions(SceneOne.getStage(sceneId), SceneOne.getWindow(sceneId), this);
		treeActions = new TreeActions(this);
		if (launchSource.equals(Source.LOCAL)) {
			if(!LiveSettings.isOffline()) {
				new Thread(() -> actions.compareLocalWithGitHub()).start();
			}
		}
		fillTree();
		TrayIcon.loggedIn();
	}

	public TreeView<TreeNode> getTreeView() {
		return treeView;
	}

	private void createScene() {
		SceneOne.set(ap, sceneId)
				.initStyle(StageStyle.DECORATED)
				.newStage()
				.onCloseEvent(this::closeWindowEvent)
				.autoSize()
				.title("GistFX - " + Action.getName())
				.show();
		SceneOne.setOnKeyPressed(sceneId, e -> {
			if (e.getCode() == KeyCode.T && e.isMetaDown()) {
				treeView.requestFocus();
			}
			if (e.getCode() == KeyCode.E && e.isControlDown() && e.isAltDown()) {
				CodeEditor.requestFocus();
			}
		});
	}

	private void startResize() {
		windowResizing.setTrue();
		if (resizeTimer != null) resizeTimer.cancel();
		resizeTimer = new Timer();
		resizeTimer.schedule(resizeDone(), 600); //This allows for the time it takes for the window to resize when either fullscreen or wide mode is toggled so that the setPaneSplit() won't try to position the divider while the window is resizing.
	}

	private TimerTask resizeDone() {
		return new TimerTask() {
			@Override public void run() {
				windowResizing.setFalse();
			}
		};
	}

	private Timer recordSplitTimer;

	private TimerTask stopRecordSplit() {
		return new TimerTask() {
			@Override public void run() {
				recordSplit.setFalse();
			}
		};
	}

	private void configurePaneSplitting() {
		paneSplitSetting = new PaneSplitSetting();
		splitPane.getItems().get(0).setOnMouseEntered(e -> {
			if (inWideMode.isTrue()) {
				paneExpanded.setTrue();
				setPaneSplit();
			}
		});
		splitPane.getItems().get(0).setOnMouseExited(e -> {
			if (inWideMode.isTrue()) {
				paneExpanded.setFalse();
				setPaneSplit();
			}
		});
	}

	private void closeWindowEvent(WindowEvent event) {
		if(LiveSettings.isOffline())
			System.exit(0);
		boolean saveDirtyData = false;
		if (LiveSettings.disableDirtyWarning() && GistManager.isDirty()) {
			saveDirtyData = true;
		}
		else if (GistManager.isDirty()) {
			Response response = CustomAlert.showSaveFilesConfirmationResponse("You have unsaved files, how would you like to proceed?\n", true);
			switch (response) {
				case SAVE -> saveDirtyData = true;
				case CANCELED -> event.consume();
				case EXIT -> SceneOne.exit();
			}
		}
		else {
			SceneOne.exit();
		}

		if (saveDirtyData) {
			event.consume();
			saveAllFiles();
			savingData.setTrue();
			new Thread(() -> {
				while (savingData.isTrue()) Action.sleep(100);
				SceneOne.exit();
			}).start();
		}
	}

	private void closeApp() {
		Window window = SceneOne.getWindow(sceneId);
		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	private void placeControlsOnPane() {
		menuBar.addMenuBar();
		addMainNode(menuBar, 0, 0, 0, -1);
		setAnchors(menuBar, 0, 0, 0, -1);
		addMainNode(lblGistNameLabel, 20, -1, 35, -1);
		addMainNode(lblGistName, 105, 250, 35, -1);
		addMainNode(lblCheckBox, -1, 15, 77, -1);
		addMainNode(lblGitUpdate, -1, 15, 55, -1);
		addMainNode(lblDescriptionLabel, 20, -1, 55, -1);
		addMainNode(lblGistDescription, 105, 20, 55, -1);
		addMainNode(pBar, 15, 15, 90, -1);
		addPaneNode(toolBarAnchored, 5, 5, 5, -1);
		addPaneNode(taFileDescription,5,5,65, -1);
		addPaneNode(CodeEditor.get(), 0, 0, 90, 0);
		splitPane = new SplitPane(treeView, apPane);
		addMainNode(splitPane, -1, -1, -1, -1);
		setAnchors(splitPane, 10, 10, 100, 10);
		SplitPane.setResizableWithParent(apPane, true);
/*
		showToolBar.setPrefHeight(25);
		showToolBar.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
*/
		lblDescriptionLabel.setPrefWidth(70);
	}

	private void setControlLayoutProperties() {
		lblGistName.setPrefWidth(200);
		lblGistName.setWrapText(false);
		lblGistName.setEllipsisString("...");
		lblGistNameLabel.setAlignment(Pos.CENTER_RIGHT);
		lblGistNameLabel.setPrefWidth(75);
		lblDescriptionLabel.setPrefWidth(75);
		lblDescriptionLabel.setAlignment(Pos.CENTER_RIGHT);
		lblGistDescription.setWrapText(false);
		lblGistDescription.setEllipsisString("...");
		lblGistDescription.setMaxHeight(30);
		buttonSaveFile.setMinWidth(85);
		buttonSaveFile.setMaxWidth(85);
		taFileDescription.setMaxHeight(20);
		taFileDescription.setId("filedesc");
		buttonUndo.setMaxWidth(80);
		Tooltip.install(buttonPasteFromClip, Action.newTooltip("Paste text contents from clipboard into selected document."));
		publicCheckBox.setDisable(true);
		lblCheckBox.setGraphic(publicCheckBox);
		lblCheckBox.setContentDisplay(ContentDisplay.RIGHT);
		pBar.setPrefHeight(10);
		pBar.setStyle(com.redmondsims.gistfx.preferences.AppSettings.get().progressBarStyle());
		pBar.setVisible(pBar.progressProperty().isBound());
		labelsVisible(false);
		lblGitUpdate.setId("notify");
		buttonCompare.setId("conflict");
		//showToolBar.setVisible(com.redmondsims.gistfx.preferences.AppSettings.get().showToolBar());
		buttonEditCategories.setPrefWidth(125);
		CodeEditor.get().setVisible(false);
	}

	private void setControlActionProperties() {
		lblGistName.setOnMouseClicked(e -> actions.renameGist(gist));
		lblGistDescription.setOnMouseClicked(e -> actions.changeGistDescription(gist));
		publicCheckBox.setOnAction(e -> changePublicState());
		lblGitUpdate.visibleProperty().bind(Action.getGitHubActivityProperty());
		buttonCopyToClipboard.setOnAction(e -> copyToClipboard());
		buttonPasteFromClip.setOnAction(e -> pasteFromClipboard());
		buttonUndo.setOnAction(e -> actions.undoFile());
		buttonSaveFile.setOnAction(e -> saveFile());
		buttonCompare.setOnAction(e -> openCompareWindow());
		buttonWideMode.setOnAction(e -> inWideMode.toggle());
		buttonFullScreen.setOnAction(e -> inFullScreen.toggle());
		buttonFullScreen.setMaxWidth(60);
		buttonDistraction.setOnAction(e -> distractionFree());
		buttonEditCategories.setOnAction(e -> actions.editCategories());
		setButton(buttonCopyToClipboard, 75);
		setButton(buttonPasteFromClip, 75);
		setButton(buttonUndo, 85);
		setButton(buttonSaveFile, 85);
		setButton(buttonCompare, 115);
		setButton(buttonWideMode, 100);
		setButton(buttonFullScreen, 100);
		setButton(buttonDistraction, 105);
		setButton(buttonEditCategories, 125);
		taFileDescription.setOnMouseClicked(e -> taFileDescription.setDisable(false));
		taFileDescription.setDisable(true);
		inWideMode.addListener((o, oldV, newV) -> {
			if (oldV != null) {
				if (!oldV.equals(newV)) {
					new Thread(() -> {
						startResize();
						Platform.runLater(() -> SceneOne.toggleWideMode(sceneId, newV));
						setPaneSplit();
					}).start();
				}
			}
		});
		inFullScreen.addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				if (!oldValue.equals(newValue)) {
					boolean leavingFullscreen = !newValue;
					new Thread(() -> {
						startResize();
						Platform.runLater(() -> SceneOne.setFullScreen(sceneId, newValue));
						miToggleFullScreen.setText(inFullScreen.isTrue() ? "Exit Fullscreen" : "Enter Fullscreen");
						handleButtonBar();
						setPaneSplit();
						while (windowResizing.isTrue()) Action.sleep(50);
						if (leavingFullscreen) {
							Platform.runLater(() -> SceneOne.toggleWideMode(sceneId, inWideMode.getValue()));
						}
					}).start();
				}
			}
		});
		recordExpanded.addListener(((observable, oldValue, newValue) -> {
			if (!oldValue.equals(newValue)) {
				if (newValue) miRecordSetExpanded.setText("Disable Record Expanded");
				else miRecordSetExpanded.setText("Enable Record Expanded");
			}
		}));
		recordSplit.addListener(((observable, oldValue, newValue) -> {
			if (newValue) miRecordSplit.setText("Disable Record Split");
			else miRecordSplit.setText("Enable Record Split");
		}));
		miRecordSetExpanded.setOnAction(e -> {
			recordExpanded.toggle();
			setupRecord();
		});
		miRecordSplit.setOnAction(e -> recordSplit.toggle());
		taFileDescription.textProperty().addListener((observable, oldValue, newValue) ->{
			if (!taFileDescription.isDisabled()) {
				if (selectedNode != null) {
					if (selectedNode.getType().equals(FILE)) {
						selectedNode.getFile().setDescription(newValue);
					}
				}
			}
		});
		taFileDescription.setWrapText(true);
		Tooltip.install(taFileDescription,Action.newTooltip("File Description"));
		CodeEditor.get().setOnKeyReleased(e -> {
			if (kcCodeMac.match(e)) {
				Object obj = CodeEditor.get().getWebEngine().executeScript("editorView.getModel().getValueInRange(editorView.getSelection())");
				Platform.runLater(()->{
					java.awt.datatransfer.Clipboard clipboardAwt = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboardAwt.setContents (new StringSelection(String.valueOf(obj)), null);
				});
			}
			else if (kcCodeOther.match(e)) {
				Object obj = CodeEditor.get().getWebEngine().executeScript("editorView.getModel().getValueInRange(editorView.getSelection())");
				Platform.runLater(()->{
					java.awt.datatransfer.Clipboard clipboardAwt = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboardAwt.setContents (new StringSelection(String.valueOf(obj)), null);
				});
			}
		});
		detachToolbar.addListener((observable,wasTrue,isTrue) -> {
			if (!wasTrue.equals(isTrue)) {
				if (isTrue) {
					detachToolBar();
				}
				else {
					restoreToolBar();
				}
			}
		});
		showToolBar.addListener((observable, isFalse, isTrue) -> {
			if (!isFalse.equals(isTrue)) {
				if (isTrue) {
					if (detachToolbar.isTrue()) {
						toolbarWindow.show();
						setToolbar();
					}
					else {
						toolBarAnchored.setVisible(true);
					}
				}
				else {
					if (detachToolbar.isTrue()) {
						toolbarWindow.hide();
					}
					else {
						toolBarAnchored.setVisible(false);
					}
				}
				reAnchorObjects();
			}
		});
	}

	private void labelsVisible(boolean visible) {
		lblGistDescription.setVisible(visible);
		lblDescriptionLabel.setVisible(visible);
		lblGistName.setVisible(visible);
		lblGistNameLabel.setVisible(visible);
		lblCheckBox.setVisible(visible);
		publicCheckBox.setVisible(visible);
	}

	private ToolWindow toolbarWindow;
	private void detachToolBar() {
		if(toolBarAnchored == null) return;
		String toolBarSceneId = "ToolBar";
		toolBarDetached = getToolBar();
		double width = 600;
		double height = 95;
		if(SceneOne.sceneExists(toolBarSceneId)) {
			toolbarWindow.setContent(toolBarDetached);
		}
		toolBarAnchored.setVisible(false);
		reAnchorObjects();
		if(toolbarWindow == null) {
			toolbarWindow = new ToolWindow.Builder(toolBarDetached)
					.noButtons()
					.setSceneId(toolBarSceneId)
					.size(width, height)
					.title("Tool Bar")
					.onCloseEvent(e -> detachToolbar.setFalse())
					.attachToStage(SceneOne.getStage(sceneId))
					.build();
		}
		toolbarWindow.showAndWait();
	}

	private void restoreToolBar() {
		removePaneNode(toolBarAnchored);
		toolBarAnchored = getToolBar();
		addPaneNode(toolBarAnchored, 5, 5, 5, -1);
		reAnchorObjects();
		toolBarDetached = null;
	}

	private void reAnchorObjects() {
		if (showToolBar.isTrue()) {
			if (detachToolbar.isTrue()) {
				setAnchors(taFileDescription,5,5,5, -1);
				setAnchors(CodeEditor.get(), 0, 0, 50, 0);
			}
			else {
				setAnchors(taFileDescription, 5, 5, 65, -1);
				setAnchors(CodeEditor.get(), 0, 0, 112.5, 0);
			}
		}
		else {
			setAnchors(taFileDescription, 5, 5, 5, -1);
			setAnchors(CodeEditor.get(), 0, 0, 50, 0);
		}
	}

	private void setToolbar() {
		if(showToolBar.isTrue()) {
			if(detachToolbar.isTrue()){
				detachToolBar();
			}
			else {
				restoreToolBar();
			}
		}
	}

	private HBox getToolBar() {
		if(selectedNode != null) {
			return switch(selectedNode.getType()) {
				case GIST -> toolBars.gistSelected();
				case CATEGORY -> toolBars.categorySelected();
				case FILE -> toolBars.fileSelected();
			};
		}
		return toolBars.nothingSelected();
	}

	private void setupRecord() {
		String name = "splitTest";
		Label label = new Label("Testing");
		Button button = new Button("OK");
		VBox vbox = new VBox(label,button);
		button.setOnAction(e->SceneOne.close(name));
		com.redmondsims.gistfx.sceneone.SceneOne.set(vbox,name, SceneOne.getStage(sceneId)).size(200,200).show();
	}

	private void setButton(Button button, double minMax) {
		button.setMinWidth(minMax);
		button.setMaxWidth(minMax);
		String label = button.getText();
		Tooltip.install(button,Action.newTooltip(label));
	}

	public void distractionFree() {
		if (file != null) {
			new DistractionFree().start(file.getLiveVersion(), file.getLanguage());
		}
	}

	public void newGist() {
		actions.newGist(selectedNode);
	}

	public void updateFileContent(String content) {
		CodeEditor.setContent(content);
		new Thread(() -> {
			Action.sleep(1400);
			setPaneSplit();
		}).start();
	}

	private void setPaneSplit() {
		new Thread(() -> {
			String jsonString = com.redmondsims.gistfx.preferences.AppSettings.get().dividerPositions();
			if (!jsonString.equals("")) {
				paneSplitSetting = gson.fromJson(jsonString, PaneSplitSetting.class);
			}
			while (windowResizing.isTrue()) Action.sleep(50);
			if (inWideMode.isTrue()) {
				if (paneExpanded.isTrue()) splitPane.getDividers().get(0).setPosition(paneSplitSetting.getPosition(EXPANDED));
				else splitPane.getDividers().get(0).setPosition(paneSplitSetting.getPosition(REST));
			}
			else {
				if (inFullScreen.isTrue()) splitPane.getDividers().get(0).setPosition(paneSplitSetting.getPosition(DEFAULT_FULL));
				else splitPane.getDividers().get(0).setPosition(paneSplitSetting.getPosition(DEFAULT));
			}
		}).start();
	}

	private void showWorking() {
		Label lblWorking = new Label("Working");
		lblWorking.setMinWidth(100);
		lblWorking.setMinHeight(50);
		lblWorking.setAlignment(Pos.CENTER);
		Platform.runLater(() -> {
			VBox vbox = new VBox(lblWorking);
			SceneOne.set(vbox, "working").show();
			new Thread(() -> {
				String[] labels = new String[]{"Working", "Working.", "Working..", "Working..."};
				Action.sleep(100);
				while (SceneOne.isShowing("working")) {
					for (int x = 1; x < 4; x++) {
						String text = labels[x];
						Platform.runLater(() -> lblWorking.setText(text));
						Action.sleep(800);
					}
					for (int x = 2; x >= 0; x--) {
						String text = labels[x];
						Platform.runLater(() -> lblWorking.setText(text));
						Action.sleep(800);
					}
				}
			}).start();
		});
	}

	private String formatColorString(String color) {
		String response = color.replaceFirst("0x", "");
		response = response.substring(0, 6);
		return response;
	}

	public void handleButtonBar() {
		Platform.runLater(() -> {
/*
			showToolBar.getButtons().clear();
			showToolBar.getButtons().setAll(buttonEditCategories, buttonWideMode, buttonFullScreen);
			if (buttonBarType.equals(FILE)) {
				showToolBar.getButtons().clear();
				if (file != null) {
					boolean isDirty    = file.isDirty();
					boolean inConflict = file.isInConflict();
					if (isDirty) {
						showToolBar.getButtons().setAll(buttonCopyToClipboard, buttonSaveFile, buttonUndo);
						String colorString = formatColorString(LiveSettings.getDirtyFileFlagColor().toString());
						buttonSaveFile.setStyle("-fx-text-fill: #" + colorString);
					}
					else {
						buttonSaveFile.setStyle("");
						showToolBar.getButtons().setAll(buttonCopyToClipboard);
					}
					showToolBar.getButtons().add(buttonWideMode);
					showToolBar.getButtons().add(buttonFullScreen);
					showToolBar.getButtons().add(buttonDistraction);
					showToolBar.getButtons().add(buttonPasteFromClip);
					if (inConflict) {
						showToolBar.getButtons().add(buttonCompare);
					}
				}
			}
			if (buttonBarType.equals(GIST)) {
				showToolBar.getButtons().clear();
				showToolBar.getButtons().add(buttonWideMode);
				showToolBar.getButtons().add(buttonFullScreen);
			}
			SplitPane.setResizableWithParent(showToolBar, true);
			showToolBar.setPadding(new Insets(0, 0, 0, 0));
			showToolBar.setVisible(showToolBar.getValue());
*/
		});
	}

	private void addMainNode(Node node, double left, double right, double top, double bottom) {
		ap.getChildren().add(node);
		setAnchors(node, left, right, top, bottom);
	}

	private void addPaneNode(Node node, double left, double right, double top, double bottom) {
		apPane.getChildren().add(node);
		setAnchors(node, left, right, top, bottom);
	}

	private void removePaneNode(Node node) {
		apPane.getChildren().remove(node);
	}

	private void addNode(AnchorPane ap, Node node, double left, double right, double top, double bottom) {
		ap.getChildren().add(node);
		setAnchors(node, left, right, top, bottom);
	}

	public void openCompareWindow() {
		String message = """
				This file is in conflict with the GitHub version.
				
				This means that this file has been altered on GitHub since the last time it was uploaded by GistFX.
				
				The next window will show you both versions of the file and give you an option of which version you'd like to keep.
				
				YOU WILL NOT BE ABLE TO EDIT THE FILE UNTIL YOU RESOLVE THE CONFLICT!
				""";
		CustomAlert.showWarning("Conflict Warning!", message);
		String      name             = "compare";
		Rectangle2D screenBounds     = Screen.getPrimary().getBounds();
		double      width            = screenBounds.getWidth() * .75;
		double      height           = screenBounds.getHeight() * .75;
		AnchorPane  ap               = new AnchorPane();
		Button      buttonKeepGitHub = new Button("Keep GitHub Version");
		Button      buttonKeepLocal  = new Button("Keep Local Version");
		buttonKeepGitHub.setId("GitHubCompare");
		buttonKeepLocal.setId("GitHubCompare");
		buttonKeepGitHub.setOnAction(e -> {
			file.resolveConflict(Type.GITHUB);
			SceneOne.close(name);
		});
		buttonKeepLocal.setOnAction(e -> {
			file.resolveConflict(Type.LOCAL);
			SceneOne.close(name);
		});
		String ext = FilenameUtils.getExtension(file.getFilename());
		MonacoFX gitHubEditor = Editors.getMonacoOne();
		MonacoFX localEditor = Editors.getMonacoTwo();
		gitHubEditor.getEditor().getDocument().setText(file.getGitHubVersion());
		localEditor.getEditor().getDocument().setText(file.getLiveVersion());
		gitHubEditor.getEditor().setCurrentLanguage(ext);
		localEditor.getEditor().setCurrentLanguage(ext);
		double midPoint = width / 2;
		addNode(ap, buttonKeepGitHub, 10, -1, 10, -1);
		addNode(ap, buttonKeepLocal, -1, 10, 10, -1);
		addNode(ap, gitHubEditor, 10, midPoint + 5, 40, 10);
		addNode(ap, localEditor, midPoint + 5, 10, 40, 10);
		SceneOne.set(ap, name).size(width, height).centered().newStage().show();
	}

	/**
	 * Action Methods
	 */

	public void closeShareWindow() {
		transport.closeReceiveWindow();
	}

	private void changePublicState() {
		boolean lastState = !publicCheckBox.isSelected();
		if (checkSelectedGist("change Public Source")) {
			if (confirmChangePublicState(gist.getGistId())) {
				new Thread(() -> {
					showWorking();
					Gist newGist = GistManager.setPublicState(gist, publicCheckBox.isSelected());
					if (newGist == null) {
						publicCheckBox.setSelected(lastState);
					}
					else {
						fillTree();
						Platform.runLater(() -> {
							String newState = publicCheckBox.isSelected() ? "Public" : "Secret";
							CustomAlert.showInfo("This Gist has been converted to a " + newState + " Gist", SceneOne.getOwner(sceneId));
						});
					}
				}).start();
			}
			else {
				publicCheckBox.setSelected(lastState);
			}
		}
	}

	private boolean confirmChangePublicState(String gistId) {
		int forkCount = Action.getForkCount(gistId);
		String style = """
				<style>
				.myDiv {
				  background-color: transparent;   \s
				}
				</style>""";
		String message = style + "<div class=\"myDiv\">" +
						 "<h2 style=\"text-align:centerOnScreen\">change Gist Source</h2>\n" +
						 "<body style=\"~background~;~color~\">" +
						 "\n" +
						 "<p>Changing the state of a Gist is a somewhat complex process...</p>\n" +
						 "\n" +
						 "<p>The process that GistFX takes when changing the public state is as follows:</p>\n" +
						 "\n" +
						 "<ol>\n" +
						 "\t<li>A new Gist is created in GitHub.\n" +
						 "\t<ul>\n" +
						 "\t\t<li>The description is set.</li>\n" +
						 "\t\t<li>The new public state is set.</li>\n" +
						 "\t</ul>\n" +
						 "\t</li>\n" +
						 "\t<li>Your Gist files are added to the new Gist.</li>\n" +
						 "\t<li>This GitHub Gist is deleted.</li>\n" +
						 "</ol>\n" +
						 "\n" +
						 "<p>This process might appear to be excessive, but it is necessary because a Gist might have forks, which would create a dichotomous situation if the public state could simply be toggled from public to private. Therefore, deleting the Gist is necessary, which will convert any forks into local copies for those users who have a fork.</p>\n" +
						 "\n" +
						 "<p><strong><span style=\"font-size:18px\">This gist currently has <span style=\"color:#e74c3c\">" + forkCount + "</span> fork(s).</span></strong></p>\n" +
						 "\n" +
						 "<p>Do you wish to proceed?</p>" +
						 "</div>";
		if (com.redmondsims.gistfx.preferences.AppSettings.get().theme().equals(Theme.DARK)) {
			message = message
					.replaceAll("~background~", "background-color:#373e43")
					.replaceAll("~color~", "color:lightgrey");
		}
		else {
			message = message
					.replaceAll("~background~", "background-color:#e6e6e6")
					.replaceAll("~color~", "color:black");
		}

		WebView webView = new WebView();
		webView.getEngine().loadContent(message);
		webView.setPrefSize(500, 450);
		return CustomAlert.showConfirmationResponse(webView) == PROCEED;
	}

	public void saveFile() {
		if (checkSelectedGistFile("Save File")) {
			if (file.isInConflict()) {
				openCompareWindow();
				return;
			}
			if(LiveSettings.isOffline()) {
				CustomAlert.showInfo("We are in Offline Mode - cannot upload to GitHub",SceneOne.getWindow(sceneId));
				return;
			}
			new Thread(() -> {
				String gistId   = file.getGistId();
				String filename = file.getFilename();
				if (!file.flushDirtyData()) {
					Platform.runLater(() -> CustomAlert.showWarning("There was a problem committing the data."));
				}
				treeActions.refreshGistBranch(file.getGistId());
				Platform.runLater(() -> {
					TreeItem<TreeNode> leaf = treeActions.getLeaf(gistId, filename);
					treeView.getSelectionModel().select(leaf);
					setSelectedNode(leaf.getValue());
				});
			}).start();
		}
	}

	private void saveAllFiles() {
		if (filesInConflict()) {
			CustomAlert.showWarning("Cannot Save Files!", "You have one or more files in conflict with the GitHub version. Click on the file with the red X next to it, then click on the Resolve Conflict button and chose which version of the file should be kept before saving any files.");
			return;
		}
		if (GistManager.isDirty()) {
			new Thread(() -> {
				savingData.setTrue();
				Platform.runLater(() -> {
					CodeEditor.get().setVisible(false);
					Action.setProgress(0.0);
					for (TreeItem<TreeNode> branch : treeRoot.getChildren()) {
						branch.setExpanded(false);
					}
				});
				List<GistFile> unsavedFileList = GistManager.getUnsavedFiles();
				double         total           = unsavedFileList.size();
				double         count           = 1;
				for (GistFile file : unsavedFileList) {
					double newCount = count;
					Platform.runLater(() -> Action.setProgress(newCount / total));
					if (!file.flushDirtyData()) {
						CustomAlert.showWarning("There was a problem saving the file.");
						savingData.setFalse();
						return;
					}
					count++;
				}
				Platform.runLater(() -> {
					Action.setProgress(0.0);
					if (!LiveSettings.disableDirtyWarning()) {
						Platform.runLater(() -> CustomAlert.showInfo("All unsaved files were uploaded to GitHub successfully.", SceneOne.getOwner(sceneId)));
					}
					savingData.setFalse();
				});
			}).start();
		}
		else {
			Platform.runLater(() -> CustomAlert.showInfo("No files need to be uploaded to GitHub.", SceneOne.getOwner(sceneId)));
		}
	}

	private void reDownloadAllGists() {
		if (Status.comparingLocalDataWithGitHub()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if (GistManager.isDirty()) {
			Response response = CustomAlert.showSaveFilesConfirmationResponse("You have unsaved files. Proceeding without first saving your files will result in the total loss of the unsaved data.\n\nHow would you like to proceed?\n", false);
			if (response.equals(Response.SAVE)) saveAllFiles();
			if (response.equals(Response.CANCELED)) return;
		}
		file = null;
		gist = null;
		GistManager.unBindFileObjects();
		new Thread(() -> {
			Action.refreshAllData();
			Action.setProgress(0.0);
		}).start();
	}

	private boolean checkSelectedGist(String action) {
		if (gist == null) {
			CustomAlert.showWarning(action, "No Gist is currently selected.");
			return false;
		}
		return true;
	}

	private boolean checkSelectedGistFile(String action) {
		if (file == null) {
			CustomAlert.showWarning(action, "No file is currently selected.");
			return false;
		}
		return true;
	}

	public void copyToClipboard() {
		if (checkSelectedGistFile("Copy To Clipboard")) {
			Clipboard        clipboard     = Clipboard.getSystemClipboard();
			ClipboardContent content       = new ClipboardContent();
			String           contentString = file.getLiveVersion();
			if (contentString.length() > 0) {
				content.putString(contentString);
				clipboard.setContent(content);
				Platform.runLater(() -> CustomAlert.showInfo("Clipboard", file.getFilename() + " copied to clipboard", SceneOne.getOwner(sceneId)));
			}
		}
	}

	public void pasteFromClipboard() {
		Label lblMsg = new Label("Append or Overwrite?");
		Button btnCancel = new Button("Cancel");
		Button btnAppend = new Button("Append");
		Button btnOverwrite = new Button("Overwrite");
		ToolWindow toolWindow = new ToolWindow.Builder(lblMsg)
				.addButton(btnCancel)
				.addButton(btnAppend)
				.addButton(btnOverwrite)
				.attachToStage(SceneOne.getStage(sceneId))
				.size(300,200)
				.build();
		btnCancel.setOnAction(e -> {
			toolWindow.setResponse("cancel");
			toolWindow.close();
		});
		btnAppend.setOnAction(e -> {
			toolWindow.setResponse("append");
			toolWindow.close();
		});
		btnOverwrite.setOnAction(e -> {
			toolWindow.setResponse("overwrite");
			toolWindow.close();
		});
		String response = toolWindow.showAndWaitResponse();
		if (response.equals("cancel")) return;
		String newText = "";
		try {
			newText = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		}
		catch (IOException | UnsupportedFlavorException e) {
			e.printStackTrace();
		}
		if(response.equals("append")) {
			String document = CodeEditor.get().getEditor().getDocument().getText();
			newText  = document + newText;
		}
		CodeEditor.get().getEditor().getDocument().setText(newText);
	}

	public void setPBarStyle(String style) {
		pBar.setStyle(style);
	}

	private boolean filesInConflict() {
		boolean inConflict = false;
		for (TreeItem<TreeNode> branch : treeRoot.getChildren()) {
			if (branch.getValue().getType().equals(CATEGORY)) {
				for (TreeItem<TreeNode> branchInCategory : branch.getChildren()) {
					for (TreeItem<TreeNode> leaf : branchInCategory.getChildren()) {
						if (leaf.getValue().getFile().isInConflict()) {
							branchInCategory.setExpanded(true);
							branch.setExpanded(true);
							inConflict = true;
						}
					}
				}
			}
			if (branch.getValue().getType().equals(GIST)) {
				for (TreeItem<TreeNode> leaf : branch.getChildren()) {
					if (leaf.getValue().getFile().isInConflict()) {
						branch.setExpanded(true);
						inConflict = true;
					}
				}
			}
		}
		return inConflict;
	}

	public void expandBranch(TreeItem<TreeNode> branch) {
		if(branch != null) {
			String gistId = branch.getValue().getGistId();
			for (TreeItem<TreeNode> treeBranch : treeRoot.getChildren()) {
				if (treeBranch.getValue().getType().equals(CATEGORY)) {
					for (TreeItem<TreeNode> gistNode : treeBranch.getChildren()) {
						if (gistNode.getValue().getGistId().equals(gistId)) {
							gistNode.setExpanded(true);
							treeBranch.setExpanded(true);
						}
					}
				}
				else {
					if (treeBranch.getValue().getGistId().equals(gistId)) {
						treeBranch.setExpanded(true);
					}
				}
			}
		}
	}

	/**
	 * Popup Windows and User Actions
	 */

	public void showAppSettings() {
		UISettings.showWindow(SceneOne.getStage(sceneId));
	}

	public void showTreeSettings() {
		TreeSettings.showWindow(SceneOne.getStage(sceneId));
	}

	public gistWindowActions getActions() {
		return actions;
	}

	public TreeActions getTreeActions() {
		return treeActions;
	}

	public Gist getGist() {
		return gist;
	}

	public void deleteGist() {
		actions.deleteGist(gist);
	}

	public GistFile getFile() {
		return file;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void shareObject(TreeNode selectedNode) {
		if(selectedNode != null) {
			switch(selectedNode.getType()) {
				case CATEGORY -> {
					String     category = selectedNode.getCategoryName();
					List<Gist> gistList = Action.getGistsInCategory(category);
					transport.shareCategory(gistList, category);
				}
				case GIST -> transport.shareGist(selectedNode.getGist());

				case FILE -> transport.shareGistFile(selectedNode.getFile());
			}
			transport.show(SceneOne.getStage(sceneId));
		}
	}

	public void setSelectedNode(TreeNode treeSelection) {
		selectedNode = treeSelection;
		Platform.runLater(() -> {
			lblGistName.textProperty().unbind();
			lblGistName.setText("");
			publicCheckBox.setDisable(false);
		});
		buttonBarType = treeSelection.getType();
		CodeEditor.setEditorTheme();
		String dark  = "-fx-text-fill: rgba(155,200,155,1)";
		String light = "-fx-text-fill: rgba(155,0,0,.5)";
		lblGistDescription.textProperty().unbind();
		GistManager.unBindFileObjects();
		if (!treeSelection.getType().equals(CATEGORY)) {
			gist = treeSelection.getGist();
			lblGistDescription.textProperty().bind(gist.getDescriptionProperty());
			gistURL = gist.getURL();
			Platform.runLater(() -> {
				publicCheckBox.setSelected(gist.isPublic());
				lblCheckBox.setVisible(true);
				publicCheckBox.setVisible(true);
				lblGistName.textProperty().bind(gist.getNameProperty());
			});
		}
		switch (treeSelection.getType()) {
			case FILE -> {
				file = treeSelection.getFile();
				CodeEditor.show();
				labelsVisible(true);
				file.setActive();
				SplitPane.setResizableWithParent(CodeEditor.get(), true);
				taFileDescription.setText(file.getDescription());
				taFileDescription.setDisable(false);
				if(file.isInConflict()) openCompareWindow();
			}
			case GIST -> {
				file = null;
				CodeEditor.hide();
				labelsVisible(false);
				Platform.runLater(() -> {
					lblGistName.setVisible(true);
					lblGistNameLabel.setVisible(true);
					lblGistDescription.setVisible(true);
					lblDescriptionLabel.setVisible(true);
					lblCheckBox.setVisible(true);
					publicCheckBox.setVisible(true);
					taFileDescription.setDisable(true);
					taFileDescription.setText("");
				});
			}
			case CATEGORY -> {
				file = null;
				gist = null;
				CodeEditor.hide();
				Platform.runLater(() -> {
					lblCheckBox.setVisible(false);
					publicCheckBox.setVisible(false);
					lblGistName.setVisible(false);
					lblGistNameLabel.setVisible(false);
					lblGistDescription.setVisible(false);
					lblDescriptionLabel.setVisible(false);
					taFileDescription.setDisable(true);
					taFileDescription.setText("");
				});
			}
		}
		setToolbar();
		handleButtonBar();
	}

	private TreeNode selectedNode;

	public TreeItem<TreeNode> getTreeRoot() {
		return treeRoot;
	}

	private void createTree() {
		treeView = new TreeView<>();
		treeView.setCellFactory(new TreeCellFactory());
		treeRoot = new TreeItem<>(new TreeNode());
		treeView.setRoot(treeRoot);
		treeView.setShowRoot(false);
		new Thread(this::fillTree).start();
		treeView.setOnMouseClicked(e -> {
			TreeItem<TreeNode> item = treeView.getSelectionModel().getSelectedItem();
			if(item != null) {
				setSelectedNode(item.getValue());
			}
		});
	}

	public void fillTree() {
		if(treeActions == null) return;
		if(treeRoot == null)
			createTree();
		treeActions.createBranchCategories();
		List<Gist> gists = new ArrayList<>(GistManager.getGists());
		gists.sort(Comparator.comparing(Gist::getName));
		for (Gist gist : gists) {
			TreeItem<TreeNode> branch   = treeActions.getNewBranch(gist);
			if(!treeActions.gistBranchInCategory(branch)) {
				treeRoot.getChildren().add(branch);
			}
		}
	}

	private void clearCredentials() {
		if(CustomAlert.showConfirmation("Are you sure you want to clear your credentials?\n\nThe next time you load GistFX, you\nwill be required to put in a valid GitHub token.")) {
			AppSettings.clear().tokenHash();
			AppSettings.clear().passwordHash();
			CustomAlert.showInfo("Credentials Cleared",SceneOne.getOwner(sceneId));
		}
		else {
			CustomAlert.showInfo("Credentials NOT Cleared",SceneOne.getOwner(sceneId));
		}
	}

	public void updateGitLabel(String label, boolean show) {
		Platform.runLater(() -> {
			lblGitUpdate.setText(label);
			if(show) {
				lblGitUpdate.visibleProperty().unbind();
				lblGitUpdate.setVisible(show);
			}
			else {
				lblGitUpdate.visibleProperty().bind(Action.getGitHubActivityProperty());
			}
		});
	}

	/**
	 * Misc. Methods
	 */

	private void openGistInWebBrowser() {
		if (checkSelectedGist("Open Browser")) {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				try {
					Desktop.getDesktop().browse(new URI(gistURL));
				}
				catch (IOException | URISyntaxException ioException) {ioException.printStackTrace();}
			}
		}
	}

	/**
	 * Menu Bar Methods
	 */

	private final MenuItem miToggleWideMode    = new MenuItem("Toggle Wide Mode");
	private final MenuItem miToggleFullScreen  = new MenuItem("Enter Fullscreen");
	private final MenuItem miRecordSetExpanded = new MenuItem("Enable Record Expanded");
	private final MenuItem miRecordSplit       = new MenuItem("Enable Record Split");
	private final MenuItem miToggleToolBar = new MenuItem(showToolBar.isTrue() ? "Hide Tool Bar" : "Show Tool Bar");
	private final MenuItem miDetachToolBar = new MenuItem("Toggle Detached Toolbar");

	private void showIPAddress() {
		new Thread(() -> {
			String ipAddress = UPnP.getExternalIP();
			String portMapped = UPnP.isMappedTCP(LiveSettings.getTcpPortNumber()) ? "YES" : "NO";
			String message = "Your EXTERNAL IP Address is: " + ipAddress + "\n" +
							 "Port Mapped: " + portMapped;
			Platform.runLater(() -> CustomAlert.showInfo(message, SceneOne.getWindow(sceneId)));
		}).start();
	}


	private MenuItem newMenuItem(String label, EventHandler<ActionEvent> eventHandler) {
		MenuItem menuItem = new MenuItem(label);
		menuItem.setOnAction(eventHandler);
		return menuItem;
	}

	private void addMenuBarItems() {
		String APP_TITLE    = "GistFX";

		miDetachToolBar.setOnAction(e->{
			Platform.runLater(() -> {
				if(showToolBar.isTrue()) {
					detachToolbar.toggle();
					if(detachToolbar.isFalse()) {
						SceneOne.close("ToolBar");
					}
				}
			});
		});

		miToggleFullScreen.setOnAction(e -> inFullScreen.toggle());
		miToggleWideMode.setOnAction(e -> {
			inWideMode.toggle();
			handleButtonBar();
		});

		menuBar.addToFileMenu("New File", e -> actions.newFile(gist), false);
		menuBar.addToFileMenu("Save File", e -> saveFile(), false);
		menuBar.addToFileMenu("Delete File", e -> actions.deleteFile(file), false);
		menuBar.addToFileMenu("Save All Files", e -> saveAllFiles(), false);
		menuBar.addToFileMenu("Open In Browser", e -> openGistInWebBrowser(), true);
		menuBar.addToFileMenu("Receive Shared Gist", e -> transport.waitForTransport(), true);
		menuBar.addToFileMenu("Exit GistFX", e -> closeApp(), false);

		menuBar.addToGistMenu("New Gist", e -> actions.newGist(selectedNode), false);
		menuBar.addToGistMenu("Delete Gist", e -> actions.deleteGist(gist), false);
		menuBar.addToGistMenu("Download Gists", e -> reDownloadAllGists(), false);

		menuBar.addToEditMenu("Copy File To Clipboard", e -> copyToClipboard(), false);
		menuBar.addToEditMenu("Undo current edits", e -> actions.undoFile(), false);
		menuBar.addToEditMenu("Save Uncommitted Data", e -> saveAllFiles(), false);
		menuBar.addToEditMenu("Clear Credentials", e -> clearCredentials(), false);
		menuBar.addToEditMenu("Refresh Tree", e -> fillTree(), false);
		menuBar.addToEditMenu("Edit Categories", e -> actions.editCategories(), true);
		menuBar.addToEditMenu("App Settings", e -> showAppSettings(), false);
		menuBar.addToEditMenu("Tree Settings", e -> showTreeSettings(), true);
		menuBar.addToEditMenu(miRecordSetExpanded, false );
		menuBar.addToEditMenu(miRecordSplit, false);

		miToggleToolBar.setOnAction(e -> {
			showToolBar.toggle();
			if (showToolBar.isTrue()) miToggleToolBar.setText("Hide Tool Bar");
			else miToggleToolBar.setText("Show Tool Bar");
			setToolbar();
		});

		menuBar.addToViewMenu(miToggleToolBar, false);
		menuBar.addToViewMenu(miDetachToolBar, false);
		menuBar.addToViewMenu(miToggleFullScreen, false);
		menuBar.addToViewMenu(miToggleWideMode,false);
		menuBar.addToViewMenu("This IP Address",e -> showIPAddress(),false);

		menuBar.addToHelpMenu("GistFX Help", e -> Help.mainOverview(), false);
		menuBar.addToHelpMenu("General Help", e -> Help.generalHelp(), false);
		menuBar.addToHelpMenu("If Something Goes Wrong", e -> Help.somethingWrong(), false);
		menuBar.addToHelpMenu("Token Info", e -> Help.showCreateTokenHelp(), true);
		menuBar.addToHelpMenu("Code Languages", e -> Languages.showCodeInformation(), true);
		menuBar.addToHelpMenu("About this program", e -> {
			try {
				final int         year        = LocalDate.now().getYear();
				final InputStream versionTextStream = Launcher.class.getResourceAsStream("version.txt");
				final String version = IOUtils.toString(versionTextStream, StandardCharsets.UTF_8);
				final Label text    = new Label(APP_TITLE + "\nVersion: " + version + "\n");
				final String license = """
				Copyright © %s
					Dustin K. Redmond <dredmond@gaports.com>
					Michael D. Sims <mike@simtechdata.com>
				
				Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the **Software**), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
				
				The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
				
				THE SOFTWARE IS PROVIDED **AS IS**, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
				""".formatted(year);
				final TextArea taLicense = new TextArea(license);
				taLicense.setWrapText(true);
				taLicense.setEditable(false);
				VBox vBox = new VBox(5, text, taLicense);
				vBox.setPadding(new Insets(20,20,20,20));
				//grid.getChildren().add(vBox);
				SceneOne.set(vBox,"showLegal").title(APP_TITLE).modality(Modality.APPLICATION_MODAL).centered().show();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}, false);
	}

	private static class MyMenuBar extends MenuBar {

		private final Menu menuFile;
		private final Menu menuGist;
		private final Menu menuEdit;
		private final Menu menuView;
		private final Menu menuHelp;

		public MyMenuBar() {
			menuFile = new Menu("File");
			menuGist = new Menu("Gist");
			menuEdit = new Menu("Edit");
			menuView = new Menu("View");
			menuHelp = new Menu("Help");
		}

		private void addMenuBar() {
			this.getMenus().add(menuFile);
			this.getMenus().add(menuGist);
			this.getMenus().add(menuEdit);
			this.getMenus().add(menuView);
			this.getMenus().add(menuHelp);
		}

		private void addToFileMenu(String menuName, EventHandler<ActionEvent> event, boolean separator) {
			MenuItem menuItem = new MenuItem(menuName);
			menuItem.setOnAction(event);
			menuFile.getItems().add(menuItem);
			if (separator) {
				menuFile.getItems().add(new SeparatorMenuItem());
			}
		}

		private void addToEditMenu(String menuName, EventHandler<ActionEvent> event, boolean separator) {
			MenuItem menuItem = new MenuItem(menuName);
			menuItem.setOnAction(event);
			menuEdit.getItems().add(menuItem);
			if (separator) {
				menuEdit.getItems().add(new SeparatorMenuItem());
			}
		}

		private void addToGistMenu(String menuName, EventHandler<ActionEvent> event, boolean separator) {
			MenuItem menuItem = new MenuItem(menuName);
			menuItem.setOnAction(event);
			menuGist.getItems().add(menuItem);
			if (separator) {
				menuGist.getItems().add(new SeparatorMenuItem());
			}
		}

		private void addToHelpMenu(String menuName, EventHandler<ActionEvent> event, boolean separator) {
			MenuItem menuItem = new MenuItem(menuName);
			menuItem.setOnAction(event);
			menuHelp.getItems().add(menuItem);
			if (separator) {
				menuHelp.getItems().add(new SeparatorMenuItem());
			}
		}

		private void addToViewMenu(MenuItem menuItem, boolean separator) {
			menuView.getItems().add(menuItem);
			if (separator) {
				menuView.getItems().add(new SeparatorMenuItem());
			}
		}

		private void addToViewMenu(String menuName, EventHandler<ActionEvent> event, boolean separator) {
			MenuItem menuItem = new MenuItem(menuName);
			menuItem.setOnAction(event);
			menuView.getItems().add(menuItem);
			if (separator) {
				menuView.getItems().add(new SeparatorMenuItem());
			}
		}

		private void addToEditMenu(MenuItem menuItem, boolean separator) {
			menuEdit.getItems().add(menuItem);
			if (separator) {
				menuEdit.getItems().add(new SeparatorMenuItem());
			}
		}

	}
}
