package com.redmondsims.gistfx.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.Languages;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.*;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.help.Help;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.PaneSplitSetting;
import com.redmondsims.gistfx.preferences.TreeSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.tree.DragFactory;
import com.redmondsims.gistfx.ui.tree.DragNode;
import com.redmondsims.gistfx.utils.Status;
import eu.mihosoft.monacofx.MonacoFX;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import javafx.stage.*;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.*;

import static com.redmondsims.gistfx.enums.PaneState.*;
import static com.redmondsims.gistfx.enums.Type.*;

public class GistWindow {

	private static final Response           MISTAKE               = Response.MISTAKE;
	private static final Response           YES                   = Response.YES;
	private static final Response           PROCEED               = Response.PROCEED;
	private final        Label              lblDescription        = new Label();
	private final        CheckBox           publicCheckBox        = new CheckBox("");
	private final        Label              lblCheckBox           = new Label("Public");
	private final        Label              lblDescriptionLabel   = new Label("Description:");
	private final        Label              lblFileDescription    = new Label("Description:");
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
	private final        ButtonBar          buttonBar             = new ButtonBar();
	private final        MyMenuBar          menuBar               = new MyMenuBar();
	private final        AnchorPane         ap                    = new AnchorPane();
	private final        AnchorPane         apPane                = new AnchorPane();
	private final        TextArea           taFileDescription     = new TextArea();
	private final        ProgressBar        pBar                  = Action.getProgressNode(10);
	private              Type               buttonBarType         = GIST;
	private final        CBooleanProperty   paneExpanded          = new CBooleanProperty(false);
	private final        CBooleanProperty   showButtonBar         = new CBooleanProperty(false);
	private final        CBooleanProperty   inWideMode            = new CBooleanProperty(false);
	private final        CBooleanProperty   savingData            = new CBooleanProperty(false);
	private final        CBooleanProperty   inFullScreen          = new CBooleanProperty(false);
	private final        CBooleanProperty   windowResizing        = new CBooleanProperty(false);
	private final        CBooleanProperty   recordExpanded        = new CBooleanProperty(false);
	private final        CBooleanProperty   recordSplit           = new CBooleanProperty(false);
	private              TreeItem<DragNode> treeRoot;
	private              GistFile           file;
	private              Gist               gist;
	private              SplitPane          splitPane;
	private              PaneSplitSetting   paneSplitSetting;
	private              String             gistURL               = "";
	private              TreeView<DragNode> treeView;
	private final        String             sceneId               = "GistWindow";
	private final        String             compareWarning        = "Please wait until I'm done comparing local Gists with GitHub.";
	private final        Gson               gson                  = new GsonBuilder().setPrettyPrinting().create();
	private              Timer       resizeTimer;
	private final KeyCodeCombination kcCodeMac = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);
	private final KeyCodeCombination kcCodeOther = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);


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
		createHappyTree();
		showButtonBar.setValue(com.redmondsims.gistfx.preferences.AppSettings.get().showButtonBar());
		placeControlsOnPane();
		setControlLayoutProperties();
		setControlActionProperties();
		addMenuBarItems();
		buttonBarType = CATEGORY;
		handleButtonBar();
		configurePaneSplitting();
		if (launchSource.equals(Source.LOCAL))
			checkFileConflicts();
		createScene();
		setPaneSplit();
	}

	private void createScene() {
		SceneOne.set(ap, sceneId).initStyle(StageStyle.DECORATED).newStage().onCloseEvent(this::closeWindowEvent).autoSize().title("GistFX - " + Action.getName()).show();
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
				System.out.println("Expanded");
				setPaneSplit();
			}
		});
		splitPane.getItems().get(0).setOnMouseExited(e -> {
			if (inWideMode.isTrue()) {
				paneExpanded.setFalse();
				System.out.println("Resting");
				setPaneSplit();
			}
		});
		splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
			if (inWideMode.isTrue()) {
				if (recordSplit.isTrue()) {
					if (recordTimer != null) recordTimer.cancel();
					recordTimer = new Timer();
					recordTimer.schedule(recordValue((double) newValue), 700);
				}
			}
		});
		//splitPane.getDividers().get(0).positionProperty().addListener((o,oldV,newV) -> LiveSettings.setLastPaneSplitValue((double)newV));
	}

	private Timer recordTimer = new Timer();

	private TimerTask recordValue(double value) {
		return new TimerTask() {
			@Override public void run() {
				if (recordExpanded.isTrue()) {
					paneSplitSetting.setPosition(EXPANDED, value);
				}
				else {
					paneSplitSetting.setPosition(REST, value);
				}
				System.out.println("Value Recorded");
				String jsonString = gson.toJson(paneSplitSetting);
				com.redmondsims.gistfx.preferences.AppSettings.set().dividerPositions(jsonString);
			}
		};
	}

	private void checkFileConflicts() {
		new Thread(() -> {
			showComparingWithGitHub("Downloading from GitHub", true);
			Map<String, GHGist> ghGistMap = Action.getNewGhGistMap();
			showComparingWithGitHub("Comparing local data with GitHub", true);
			for (GHGist ghGist : ghGistMap.values()) { //Add any Gists or files that do not currently exist locally
				String description = ghGist.getDescription();
				String gistId      = ghGist.getGistId();
				if (!description.equals(Names.GIST_DATA_DESCRIPTION.Name())) {
					if (!GistManager.hasGist(gistId)) {
						GistManager.addGistFromGitHub(ghGist);
						addBranch(gistId);
					}
					for (String filename : ghGist.getFiles().keySet()) {
						GHGistFile ghGistFile = Action.getGitHubFile(gistId, filename);
						String     gitContent = ghGistFile.getContent();
						if (!GistManager.gistHasFile(gistId, filename)) {
							GistManager.addFileToList(GistManager.getFileList(gistId), gistId, ghGistFile);
							addFileToBranch(gistId, filename);
						}
					}
				}
			}
			Status.setState(State.COMPARING);
			while (Status.filesComparing()) {
				Action.setProgress(Status.getRegisteredFileRatio());
			}
			Action.setProgress(0.0);
			if (filesAreDirty()) {
				Platform.runLater(() -> {
					Response response = CustomAlert.showSaveFilesConfirmationResponse("You have edited files that have not been uploaded to GitHub, how would you like to proceed?\n", false);
					if (response.equals(Response.SAVE)) {
						saveAllFiles();
					}
				});
			}
			if (filesInConflict()) {
				Platform.runLater(() -> CustomAlert.showWarning("Files In Conflict!", "You have one or more files in conflict with the GitHub version. Click on the file with the red X next to it, then click on the Resolve Conflict button and chose which version of the file should be kept."));
			}
			showComparingWithGitHub("", false);
			Status.setState(State.NORMAL);
		}).start();
	}

	private void closeWindowEvent(WindowEvent event) {
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
		addMainNode(lblGistNameLabel, 20, -1, 40, -1);
		addMainNode(lblGistName, 105, 250, 40, -1);
		addMainNode(lblCheckBox, -1, 15, 90, -1);
		addMainNode(lblGitUpdate, -1, 15, 70, -1);
		addMainNode(lblDescriptionLabel, 20, -1, 80, -1);
		addMainNode(lblDescription, 105, 20, 80, -1);
		addMainNode(pBar, 15, 15, 105, -1);

		addPaneNode(buttonBar, 0, -1, 5, -1);
		addPaneNode(taFileDescription,5,5,35, -1);
		addPaneNode(CodeEditor.get(), 0, 0, 55, 0);
		splitPane = new SplitPane(treeView, apPane);
		addMainNode(splitPane, -1, -1, -1, -1);
		setAnchors(splitPane, 10, 10, 120, 10);
		SplitPane.setResizableWithParent(apPane, true);
		buttonBar.setPrefHeight(25);
		buttonBar.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		lblDescriptionLabel.setPrefWidth(70);
	}

	private void labelsVisible(boolean visible) {
		lblDescription.setVisible(visible);
		lblDescriptionLabel.setVisible(visible);
		lblGistName.setVisible(visible);
		lblGistNameLabel.setVisible(visible);
		lblCheckBox.setVisible(visible);
		publicCheckBox.setVisible(visible);
	}

	private void setControlLayoutProperties() {
		lblGistName.setPrefWidth(200);
		lblGistName.setWrapText(false);
		lblGistName.setEllipsisString("...");
		lblGistNameLabel.setAlignment(Pos.CENTER_RIGHT);
		lblGistNameLabel.setPrefWidth(75);
		lblDescriptionLabel.setPrefWidth(75);
		lblDescriptionLabel.setAlignment(Pos.CENTER_RIGHT);
		lblDescription.setWrapText(false);
		lblDescription.setEllipsisString("...");
		buttonSaveFile.setMinWidth(85);
		buttonSaveFile.setMaxWidth(85);
		taFileDescription.setMaxHeight(50);
		taFileDescription.setId("filedesc");
		buttonUndo.setMaxWidth(80);
		Tooltip.install(buttonPasteFromClip, new Tooltip("Paste text contents from clipboard into selected document."));
		publicCheckBox.setDisable(true);
		lblCheckBox.setGraphic(publicCheckBox);
		lblCheckBox.setContentDisplay(ContentDisplay.RIGHT);
		pBar.setPrefHeight(10);
		pBar.setStyle(com.redmondsims.gistfx.preferences.AppSettings.get().progressBarStyle());
		pBar.setVisible(pBar.progressProperty().isBound());
		labelsVisible(false);
		lblGitUpdate.setId("notify");
		buttonCompare.setId("conflict");
		buttonBar.setVisible(com.redmondsims.gistfx.preferences.AppSettings.get().showButtonBar());
		buttonEditCategories.setPrefWidth(125);
		CodeEditor.get().setVisible(false);
	}

	private void setControlActionProperties() {
		lblGistName.setOnMouseClicked(e -> renameGist());
		lblDescription.setOnMouseClicked(e -> changeGistDescription());
		publicCheckBox.setOnAction(e -> changePublicState());
		lblGitUpdate.visibleProperty().bind(Action.getNotifyProperty());
		buttonCopyToClipboard.setOnAction(e -> copyFileToClipboard());
		buttonPasteFromClip.setOnAction(e -> pasteFromClipboard());
		buttonUndo.setOnAction(e -> undoFile());
		buttonSaveFile.setOnAction(e -> saveFile());
		buttonCompare.setOnAction(e -> openCompareWindow());
		buttonWideMode.setOnAction(e -> inWideMode.toggle());
		buttonFullScreen.setOnAction(e -> inFullScreen.toggle());
		buttonFullScreen.setMaxWidth(60);
		buttonDistraction.setOnAction(e -> distractionFree());
		buttonEditCategories.setOnAction(e -> editCategories());
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
		Tooltip.install(taFileDescription,new Tooltip("File Description"));
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
		Tooltip.install(button,new Tooltip(label));
	}

	private void distractionFree() {
		if (file != null) {
			new DistractionFree().start(file.getContent(), file.getLanguage());
		}
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
			buttonBar.getButtons().clear();
			buttonBar.getButtons().setAll(buttonEditCategories, buttonWideMode, buttonFullScreen);
			if (buttonBarType.equals(FILE)) {
				buttonBar.getButtons().clear();
				if (file != null) {
					boolean isDirty    = file.isDirty();
					boolean inConflict = file.isInConflict();
					if (isDirty) {
						buttonBar.getButtons().setAll(buttonCopyToClipboard, buttonSaveFile, buttonUndo);
						String colorString = formatColorString(LiveSettings.getDirtyFileFlagColor().toString());
						buttonSaveFile.setStyle("-fx-text-fill: #" + colorString);
					}
					else {
						buttonSaveFile.setStyle("");
						buttonBar.getButtons().setAll(buttonCopyToClipboard);
					}
					buttonBar.getButtons().add(buttonWideMode);
					buttonBar.getButtons().add(buttonFullScreen);
					buttonBar.getButtons().add(buttonDistraction);
					buttonBar.getButtons().add(buttonPasteFromClip);
					if (inConflict) {
						buttonBar.getButtons().add(buttonCompare);
					}
				}
			}
			if (buttonBarType.equals(GIST)) {
				buttonBar.getButtons().clear();
				buttonBar.getButtons().add(buttonWideMode);
				buttonBar.getButtons().add(buttonFullScreen);
			}
			double top = buttonBar.isVisible() ? 35 : 5;
			SplitPane.setResizableWithParent(buttonBar, true);
			buttonBar.setPadding(new Insets(0, 0, 0, 0));
			buttonBar.setVisible(showButtonBar.getValue());
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

	private void addNode(AnchorPane ap, Node node, double left, double right, double top, double bottom) {
		ap.getChildren().add(node);
		setAnchors(node, left, right, top, bottom);
	}

	private void openCompareWindow() {
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
		MonacoFX gitHubEditor = new MonacoFX();
		MonacoFX localEditor  = new MonacoFX();
		if (com.redmondsims.gistfx.preferences.AppSettings.get().theme().equals(Theme.DARK)) {
			gitHubEditor.getEditor().setCurrentTheme("vs-dark");
			localEditor.getEditor().setCurrentTheme("vs-dark");
		}
		gitHubEditor.getEditor().getDocument().setText(file.getGitHubVersion());
		localEditor.getEditor().getDocument().setText(file.getContent());
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

	private void changePublicState() {
		boolean lastState = !publicCheckBox.isSelected();
		if (checkSelectedGist("Change Public Source")) {
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
						 "<h2 style=\"text-align:centerOnScreen\">Change Gist Source</h2>\n" +
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

	private void saveFile() {
		if (checkSelectedGistFile("Save File")) {
			if (file.isInConflict()) {
				CustomAlert.showWarning("Cannot Save File!", "This file is in conflict with the GitHub version. Resolve conflict first.");
				openCompareWindow();
				return;
			}
			new Thread(() -> {
				Action.sleep(500);
				String gistId   = file.getGistId();
				String filename = file.getFilename();
				if (!file.flushDirtyData()) {
					Platform.runLater(() -> CustomAlert.showWarning("There was a problem committing the data."));
				}
				refreshGistBranch(file.getGistId());
				Platform.runLater(() -> {
					TreeItem<DragNode> leaf = getLeaf(gistId, filename);
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
					for (TreeItem<DragNode> branch : treeRoot.getChildren()) {
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
						CustomAlert.showWarning("There was a problem.");
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
		if (Status.isComparing()) {
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

	private void copyFileToClipboard() {
		if (checkSelectedGistFile("Copy To Clipboard")) {
			Clipboard        clipboard     = Clipboard.getSystemClipboard();
			ClipboardContent content       = new ClipboardContent();
			String           contentString = file.getContent();
			if (contentString.length() > 0) {
				content.putString(contentString);
				clipboard.setContent(content);
				Platform.runLater(() -> CustomAlert.showInfo("Clipboard", file.getFilename() + " copied to clipboard", SceneOne.getOwner(sceneId)));
			}
		}
	}

	private void pasteFromClipboard() {
		try {
			String data     = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			String document = CodeEditor.get().getEditor().getDocument().getText();
			String newText  = document + data;
			CodeEditor.get().getEditor().getDocument().setText(newText);
		}
		catch (IOException | UnsupportedFlavorException e) {
			e.printStackTrace();
		}
	}

	public void setPBarStyle(String style) {
		pBar.setStyle(style);
	}

	public void bindProgressBar(DoubleProperty doubleProperty) {
		pBar.progressProperty().bind(doubleProperty);
		pBar.setVisible(true);
		pBar.toFront();
	}

	private boolean filesInConflict() {
		boolean inConflict = false;
		for (TreeItem<DragNode> branch : treeRoot.getChildren()) {
			if (branch.getValue().getType().equals(CATEGORY)) {
				for (TreeItem<DragNode> branchInCategory : branch.getChildren()) {
					for (TreeItem<DragNode> leaf : branchInCategory.getChildren()) {
						if (leaf.getValue().getFile().isInConflict()) {
							branchInCategory.setExpanded(true);
							branch.setExpanded(true);
							inConflict = true;
						}
					}
				}
			}
			if (branch.getValue().getType().equals(GIST)) {
				for (TreeItem<DragNode> leaf : branch.getChildren()) {
					if (leaf.getValue().getFile().isInConflict()) {
						branch.setExpanded(true);
						inConflict = true;
					}
				}
			}
		}
		return inConflict;
	}

	private void expandBranch(TreeItem<DragNode> branch) {
		String gistId = branch.getValue().getGistId();
		for (TreeItem<DragNode> treeBranch : treeRoot.getChildren()) {
			if (treeBranch.getValue().getType().equals(CATEGORY)) {
				for (TreeItem<DragNode> gistNode : treeBranch.getChildren()) {
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

	private boolean filesAreDirty() {
		boolean isDirty = false;
		for (TreeItem<DragNode> branch : treeRoot.getChildren()) {
			if (branch.getValue().getType().equals(CATEGORY)) {
				for (TreeItem<DragNode> branchInCategory : branch.getChildren()) {
					for (TreeItem<DragNode> leaf : branchInCategory.getChildren()) {
						if (leaf.getValue().getFile().isDirty()) {
							branchInCategory.setExpanded(true);
							isDirty = true;
						}
					}
				}
			}
			if (branch.getValue().getType().equals(GIST)) {
				for (TreeItem<DragNode> leaf : branch.getChildren()) {
					if (leaf.getValue().getFile().isDirty()) {
						branch.setExpanded(true);
						isDirty = true;
					}
				}
			}
		}
		return isDirty;
	}

	/**
	 * Popup Windows and User Actions
	 */

	private void showUserSettings() {
		UISettings.showWindow(SceneOne.getStage(sceneId));
	}

	private void showTreeSettings() {
		TreeSettings.showWindow(SceneOne.getStage(sceneId));
	}

	public void newGist() {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		Platform.runLater(() -> {
			boolean categorySet = false;
			String selectedCategory = "";
			if (selectedNode != null) {
				if (selectedNode.getType().equals(CATEGORY)) {
					categorySet = true;
					selectedCategory = selectedNode.getCategory();
				}
			}
			String[] choices = CustomAlert.newGistAlert(getDefaultJavaText("File.java"), categorySet,selectedCategory);
			if (choices != null) {
				boolean isPublic    = choices[0].equals("Public");
				String  gistName    = choices[1];
				String  filename    = choices[2];
				String  description = choices[3];
				String  gistFile    = choices[4];
				String  category    = choices[5];
				String  newGistID   = GistManager.addNewGistToGitHub(gistName, description, filename, gistFile, isPublic);
				if (!newGistID.isEmpty()) {
					if (!category.trim().equals("!@#none#@!")) Action.mapCategoryNameToGist(newGistID, category);
					else if (categorySet) {
						Action.mapCategoryNameToGist(newGistID, selectedCategory);
					}
					fillTree();
				}
			}
		});
	}

	public void newFile() {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if (checkSelectedGist("New File")) {
			String                             gistId      = gist.getGistId();
			StringProperty                     filename    = new SimpleStringProperty();
			StringProperty                     contents    = new SimpleStringProperty();
			Map<Response, Map<String, String>> responseMap = CustomAlert.showNewFileAlert(gist.getName(), getDefaultJavaText(gist.getName()));
			new Thread(() -> {
				for (Response response : responseMap.keySet()) {
					if (response == PROCEED) {
						for (Map<String, String> fileMap : responseMap.values()) {
							for (String mapFilename : fileMap.keySet()) {
								filename.setValue(mapFilename);
								contents.setValue(fileMap.get(mapFilename));
							}
						}
						GistFile file = GistManager.addNewFileToGitHub(gistId, filename.getValue(), contents.getValue());
						if (file != null) {
							String newFilename = file.getFilename();
							addFileToBranch(gistId, newFilename);
							TreeItem<DragNode> branch = getBranch(gistId);
							branch.setExpanded(true);
							for (TreeItem<DragNode> leaf : branch.getChildren()) {
								if (leaf.getValue().toString().equals(filename.getValue())) {
									treeView.getSelectionModel().select(leaf);
									Platform.runLater(() -> setSelectedNode(leaf.getValue()));
								}
							}
						}
					}
				}
			}).start();
		}
	}

	public void deleteFile() {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if (checkSelectedGistFile("Delete File")) {
			Platform.runLater(() -> {
				if (CustomAlert.showConfirmation("Are you sure you want to delete the file\n\n" + file.getFilename() + "\n\nFrom Gist: " + lblGistName.getText() + "?")) {
					GistManager.deleteFile(file);
					removeLeaf(file);
				}
			});
		}
	}

	private void undoFile() {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if (checkSelectedGistFile("Undo Edit")) {
			Platform.runLater(() -> {
				if (CustomAlert.showConfirmation("This action will overwrite your local changes with the last version that was uploaded to your GitHub account.\n\nAre you sure?")) {
					file.undo();
				}
			});
		}
	}

	public void deleteGist() {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if (checkSelectedGist("Delete Gist")) {
			String   gistId   = gist.getGistId();
			Response response = deleteGistResponse(gistId);
			if (response == YES) {
				if (removeBranch(gistId)) {
					GistManager.deleteGist(gistId);
					Platform.runLater(() -> CustomAlert.showInfo("Gist deleted successfully.", SceneOne.getOwner(sceneId)));
				}
			}
			if (response == MISTAKE) deleteGist();
		}
	}

	public void renameGist() {
		if (checkSelectedGist("Rename Gist")) {
			Platform.runLater(() -> {
				String gistId  = gist.getGistId();
				String newName = CustomAlert.showChangeNameAlert(gist.getName(),"Gist").replaceAll("\\n", " ").trim();
				if (!newName.isEmpty()) {
					gist.setName(newName);
					fillTree();
					TreeItem<DragNode> branch = getBranch(gistId);
					treeView.getSelectionModel().select(branch);
					setSelectedNode(branch.getValue());
				}
			});
		}
	}

	public void renameCategory() {
		if(treeView.getSelectionModel().getSelectedItem().getValue().getType().equals(CATEGORY)) {
			String oldName = treeView.getSelectionModel().getSelectedItem().getValue().toString();
			Platform.runLater(() -> {
				String newName = CustomAlert.showChangeNameAlert(oldName,"Category").replaceAll("\\n", " ").trim();
				if (!newName.isEmpty()) {
					Action.changeCategoryName(oldName,newName);
					fillTree();
				}
			});
		}
	}

	public void renameFile() {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if (checkSelectedGistFile("Rename File")) {
			String newFileName = CustomAlert.showFileRenameAlert(file.getFilename());
			if (!newFileName.isEmpty()) {
				new Thread(() -> {
					file.setName(newFileName);
					refreshGistBranch(file.getGistId());
					Platform.runLater(() -> {
						TreeItem<DragNode> leaf = getLeaf(gist.getGistId(), newFileName);
						treeView.getSelectionModel().select(leaf);
						setSelectedNode(leaf.getValue());
					});
				}).start();
			}
		}
	}

	private void changeGistDescription() {
		if (Status.isComparing()) {
			CustomAlert.showWarning(compareWarning);
			return;
		}
		if (checkSelectedGist("Change Description")) {
			String newDescription = CustomAlert.showChangeGistDescriptionAlert(gist.getDescription());
			if (!newDescription.isEmpty()) {
				gist.setDescription(newDescription);
			}
		}
	}

	public void editCategories() {
		String    name                = "Categories";
		double    width               = 400;
		double    height              = 500;
		Label     lblNewCategory      = new Label("New Category");
		Label     lblSelectedCategory = new Label("Selected Category");
		Label     lblNewCategoryName  = new Label("New Name");
		TextField tfNewCategory       = new TextField();
		TextField tfSelectedCategory  = new TextField();
		TextField tfNewName           = new TextField();
		Tooltip.install(tfNewCategory, new Tooltip("Type in the category name and hit ENTER to save it"));
		Tooltip.install(tfSelectedCategory, new Tooltip("Click on a category from the list, then rename or delete it"));
		Tooltip.install(tfNewName, new Tooltip("Click on a category from the list, then Type in a new name here then press ENTER"));
		tfSelectedCategory.setEditable(false);
		ListView<String> lvCategories = new ListView<>();
		lvCategories.getItems().setAll(Action.getCategoryList());
		Button btnClose = new Button("Close");
		Button btnDelete = new Button("Delete Category");
		AnchorPane apCategories = new AnchorPane(lvCategories,
												 lblNewCategory,
												 lblSelectedCategory,
												 lblNewCategoryName,
												 tfNewCategory,
												 tfSelectedCategory,
												 tfNewName,
												 btnClose,
												 btnDelete);
		apCategories.setPrefSize(width, height);
		lblNewCategory.setMinWidth(85);
		lblSelectedCategory.setMinWidth(85);
		lblNewCategoryName.setMinWidth(55);
		btnClose.setMinWidth(55);
		btnClose.setMinHeight(35);
		btnDelete.setMinWidth(75);
		btnDelete.setMinHeight(35);
		setAnchors(lblNewCategory, 20, -1, 20, -1);
		setAnchors(tfNewCategory, 135, 20, 17.5, -1);
		setAnchors(lblSelectedCategory, 20, -1, 50, -1);
		setAnchors(tfSelectedCategory, 135, 20, 47.5, -1);
		setAnchors(lblNewCategoryName, 20, -1, 80, -1);
		setAnchors(tfNewName, 135, 20, 77.5, -1);
		setAnchors(lvCategories, 20, 20, 125, 65);
		setAnchors(btnClose, 80, -1, -1, 20);
		setAnchors(btnDelete,-1, 80, -1, 20);
		btnDelete.setDisable(true);
		tfSelectedCategory.textProperty().addListener((observable, oldValue, newValue) -> btnDelete.setDisable(newValue.length() == 0));
		tfNewCategory.setOnMouseClicked(e -> {
			tfSelectedCategory.clear();
			tfNewName.clear();
		});
		tfNewCategory.setOnAction(e -> {
			String categoryName = tfNewCategory.getText();
			Action.addCategoryName(categoryName);
			lvCategories.getItems().setAll(Action.getCategoryList());
			tfNewCategory.clear();
			tfNewCategory.requestFocus();
			fillTree();
		});
		tfNewName.setOnAction(e -> {
			Action.changeCategoryName(tfSelectedCategory.getText(), tfNewName.getText());
			tfSelectedCategory.clear();
			tfNewName.clear();
			lvCategories.getItems().setAll(Action.getCategoryList());
			tfNewName.clear();
			tfNewCategory.requestFocus();
			fillTree();
		});
		lvCategories.setOnMouseClicked(e -> {
			tfSelectedCategory.setText(lvCategories.getSelectionModel().getSelectedItem());
			tfNewName.requestFocus();
		});
		btnClose.setOnAction(e -> {
			if (!tfNewCategory.getText().isEmpty()) {
				String categoryName = tfNewCategory.getText();
				Action.addCategoryName(categoryName);
				lvCategories.getItems().setAll(Action.getCategoryList());
				tfNewCategory.clear();
				tfNewCategory.requestFocus();
				fillTree();
			}
			SceneOne.close(name);
		});
		btnDelete.setOnAction(e -> {
			Action.deleteCategoryName(tfSelectedCategory.getText());
			Platform.runLater(() -> tfSelectedCategory.setText(""));
			lvCategories.getItems().setAll(Action.getCategoryList());
			fillTree();
		});
		SceneOne.set(apCategories, name)
				.centered()
				.newStage()
				.title("Edit Categories")
				.show();
	}

	public void deleteCategory() {
		if (selectedNode != null) {
			if (selectedNode.getType().equals(CATEGORY)) {
				String category = selectedNode.getCategory();
				if (CustomAlert.showConfirmation("Are you sure you want to delete category: " + category + "?")) {
					Action.deleteCategoryName(category);
					fillTree();
				}
			}
		}
	}

	private void assignCategory(String gistId) {
		String name     = "Assign";
		double width    = 450;
		double height   = 175;
		String gistName = Action.getGistName(gistId);
		Text   text1    = new Text("Assign ");
		Text   text2    = new Text(gistName);
		Text   text3    = new Text(" to category:");
		Color  color1;
		Color  color2;
		if (LiveSettings.getTheme().equals(Theme.DARK)) {
			color1 = Color.rgb(144, 163, 127);
			//color2 = Color.rgb(185,55,0);
			color2 = Color.YELLOW;
		}
		else {
			color1 = Color.BLACK;
			color2 = Color.DARKRED;
		}
		text1.setFill(color1);
		text2.setFill(color2);
		text3.setFill(color1);
		text1.setFont(Font.font("Avenir", 15));
		text2.setFont(Font.font("Avenir", 15));
		text3.setFont(Font.font("Avenir", 15));
		HBox hbox = new HBox(text1, text2, text3);
		hbox.setSpacing(0);
		hbox.setPadding(new Insets(0, 0, 0, 0));
		hbox.setPrefWidth(width - 40);
		hbox.setAlignment(Pos.CENTER);
		ChoiceBox<String> cbCategories = new ChoiceBox<>(Action.getCategoryList());
		Button            btnClose     = new Button("Assign");
		cbCategories.setMinWidth(150);
		cbCategories.setMaxWidth(150);
		btnClose.setMinWidth(55);
		AnchorPane ap = new AnchorPane(hbox, cbCategories, btnClose);
		ap.setMinSize(width, height);
		setAnchors(hbox, 20, 20, 20, -1);
		btnClose.setOnAction(e -> {
			SceneOne.close(name);
			String category = cbCategories.getValue();
			if (category != null) {
				Action.mapCategoryNameToGist(gistId, category);
				fillTree();
				setSelectedNode(getBranchCategory(category).getValue());
				getBranchCategory(Action.getGistCategoryName(gistId)).setExpanded(true);
			}
		});
		SceneOne.set(ap, name).centered().size(width, height).newStage().show();
		double windowWidth = SceneOne.getWindow(name).getWidth();
		if (windowWidth > width) {
			setAnchors(cbCategories, (ap.getWidth() / 2) - 75, -1, 55, -1);
			setAnchors(btnClose, (ap.getWidth() / 2) - 25.0, -1, -1, 20);
		}
		else {
			setAnchors(cbCategories, (width / 2) - 75, -1, 55, -1);
			setAnchors(btnClose, (width / 2) - 25.0, -1, -1, 20);
		}
	}

	private Response deleteGistResponse(String gistId) {
		int     forkCount = gist.getForkCount();
		boolean isPublic  = gist.isPublic();
		String  forkText  = "";
		if (forkCount > 0) {
			forkText = "This Gist currently has " + forkCount + " fork(s). When you delete this Gist, each fork will be converted into a local Gist for those users who have a fork.\n\n";
		}
		StringBuilder sb        = new StringBuilder(forkText);
		List<String>  fileNames = GistManager.getFilenamesFor(gistId);
		sb.append("Are you sure you wish to delete this gist and these files?\n");
		if (fileNames.size() > 10) {
			sb.append(" (partial list)");
		}
		sb.append("\n");
		int max = Math.min(fileNames.size(), 10);
		for (int x = 0; x < max; x++) {
			sb.append("\t").append(fileNames.get(x)).append("\n");
		}
		if (fileNames.size() > 10) sb.append("...");
		sb.append("\n");
		return CustomAlert.showHardConfirmation("Delete Gist", sb.toString());
	}

	/**
	 * Tree Methods
	 */

	private void refreshGistBranch(String gistId) {
		TreeItem<DragNode> branch = getBranch(gistId);
		treeView.getSelectionModel().select(branch);
		setSelectedNode(branch.getValue());
		Gist selectedGist = branch.getValue().getGist();
		branch.getChildren().clear();
		addFilesToBranch(selectedGist, branch);
	}

	private void addFilesToBranch(Gist gist, TreeItem<DragNode> branch) {
		for (GistFile file : gist.getFiles()) {
			TreeItem<DragNode> leaf = getNewLeaf(file);
			file.addedToTree();
			branch.getChildren().add(leaf);
		}
	}

	public void refreshGistBranch(GistFile gistFile) {
		String gistId   = gistFile.getGistId();
		String filename = gistFile.getFilename();
		refreshGistBranch(gistId);
		TreeItem<DragNode> branch = getBranch(gistId);
		expandBranch(branch);
		TreeItem<DragNode> leaf = getLeaf(gistId, filename);
		treeView.getSelectionModel().select(leaf);
		setSelectedNode(leaf.getValue());
	}

	public void addFileToBranch(String gistId, String filename) {
		GistFile           file   = GistManager.getFile(gistId, filename);
		TreeItem<DragNode> branch = getBranch(gistId);
		TreeItem<DragNode> leaf   = new TreeItem<>(new DragNode(filename, file));
		leaf.setGraphic(leaf.getValue().getGraphic());
		branch.getChildren().add(leaf);
	}

	public void addBranch(String gistId) {
		Gist               newGist   = GistManager.getGist(gistId);
		TreeItem<DragNode> newBranch = getNewBranch(newGist);
		treeRoot.getChildren().add(newBranch);
	}

	private DragNode selectedNode;

	private void setSelectedNode(DragNode treeSelection) {
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
		lblDescription.textProperty().unbind();
		GistManager.unBindFileObjects();
		if (!treeSelection.getType().equals(CATEGORY)) {
			gist = treeSelection.getGist();
			lblDescription.textProperty().bind(gist.getDescriptionProperty());
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
				setAnchors(CodeEditor.get(), 0, 0, 90, 0);
				taFileDescription.setText(file.getDescription());
				taFileDescription.setDisable(false);
			}
			case GIST -> {
				file = null;
				CodeEditor.hide();
				labelsVisible(false);
				Platform.runLater(() -> {
					lblGistName.setVisible(true);
					lblGistNameLabel.setVisible(true);
					lblDescription.setVisible(true);
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
					lblDescription.setVisible(false);
					lblDescriptionLabel.setVisible(false);
					taFileDescription.setDisable(true);
					taFileDescription.setText("");
				});
			}
		}
		handleButtonBar();
	}

	private List<String> categoryList = new ArrayList<>();

	private void removeLeaf(GistFile file) {
		Objects.requireNonNull(getBranch(file.getGistId())).getChildren().removeIf(leaf -> leaf.getValue().toString().equals(file.getFilename()));
	}

	private boolean removeBranch(String gistId) {
		boolean success = false;
		for (TreeItem<DragNode> category : branchCategoryMap.values()) {
			for (TreeItem<DragNode> categoryBranch : category.getChildren()) {
				if (categoryBranch.getValue().getGistId().equals(gistId)) {
					category.getChildren().remove(categoryBranch);
					return true;
				}
			}
		}
		for (TreeItem<DragNode> branch : treeRoot.getChildren()) {
			if (branch.getValue().getCategory() == null) {
				if (branch.getValue().getGistId().equals(gistId)) {
					treeRoot.getChildren().remove(branch);
					return true;
				}
			}
		}
		return false;
	}

	private TreeItem<DragNode> getLeaf(String gistId, String filename) {
		TreeItem<DragNode> branch = getBranch(gistId);
		for (TreeItem<DragNode> leaf : branch.getChildren()) {
			if (leaf.getValue().getFile().getFilename().equals(filename)) return leaf;
		}
		return null;
	}

	private TreeItem<DragNode> getBranch(String gistId) {
		for (TreeItem<DragNode> branch : treeRoot.getChildren()) {
			if (branch.getValue().getType().equals(CATEGORY)) {
				for (TreeItem<DragNode> branchInCategory : branch.getChildren()) {
					if (branchInCategory.getValue().getType().equals(GIST) && branchInCategory.getValue().getGistId().equals(gistId)) {
						return branchInCategory;
					}
				}
			}
			if (branch.getValue().getType().equals(GIST) && branch.getValue().getGistId().equals(gistId)) {
				return branch;
			}
		}
		return null;
	}

	private TreeItem<DragNode> getNewBranch(Gist gist) {
		TreeItem<DragNode> branch;
		String gistId = gist.getGistId();
		String name = Action.getGistName(gistId);
		branch = new TreeItem<>(new DragNode(name, gist));
		branch.setGraphic(branch.getValue().getGraphic());
		branch.expandedProperty().addListener((observable, oldValue, newValue) -> branch.getValue().getGist().setExpanded(newValue));
		addFilesToBranch(gist, branch);
		branch.setExpanded(branch.getValue().getGist().isExpanded());
		return branch;
	}

	private TreeItem<DragNode> getNewLeaf(GistFile file) {
		TreeItem<DragNode> leaf =  new TreeItem<>(new DragNode(file.getFilename(),file));
		leaf.graphicProperty().bind(file.getGraphicNode());
		return leaf;
	}

	private List<TreeItem<DragNode>> getFileNodes() {
		List<TreeItem<DragNode>> list = new ArrayList<>();
		for(TreeItem<DragNode> branch : treeRoot.getChildren()) {
			if(branch.isExpanded()) list.add(branch);
			for(TreeItem<DragNode> twig : branch.getChildren()) {
				if(twig.isExpanded()) list.add(twig);
				if(twig.getChildren().size() > 0) {
					list.addAll(twig.getChildren());
				}
				else {
					list.add(twig);
				}
			}
		}
		return list;
	}

	private List<TreeItem<DragNode>> getAllNodes() {
		List<TreeItem<DragNode>> list = new ArrayList<>();
		for(TreeItem<DragNode> branch : treeRoot.getChildren()) {
			list.add(branch);
			list.addAll(branch.getChildren());
			for(TreeItem<DragNode> twig : branch.getChildren()) {
				list.addAll(twig.getChildren());
			}
		}
		return list;
	}

	Timer refreshTimer;

	private void resetNodes() {
		if (refreshTimer != null) refreshTimer.cancel();
		refreshTimer = new Timer();
		refreshTimer.schedule(resetTask(),500);
	}

	private TimerTask resetTask() {
		return new TimerTask() {
			@Override public void run() {
				boolean value = treeRoot.getChildren().get(0).isExpanded();
				treeRoot.getChildren().get(0).setExpanded(!value);
				treeRoot.getChildren().get(0).setExpanded(value);
			}
		};
	}

	public void refreshIcons() {
		Platform.runLater(() -> {
			List<TreeItem<DragNode>> list = getAllNodes();
			for(TreeItem<DragNode> treeItem : list) {
				if(!treeItem.getValue().getType().equals(FILE)) {
					treeItem.setGraphic(treeItem.getValue().getGraphic());
				}
				else {
					treeItem.getValue().getFile().refreshGraphicNode();
				}
			}
			resetNodes();
		});
	}

	public void setFileDirtyState(GistFile gistFile, Type state, boolean selected){
		String             gistId = gistFile.getGistId();
		TreeItem<DragNode> branch = getBranch(gistId);
		if (branch != null) {
			if (!state.equals(Type.OK)) {
				for (TreeItem<DragNode> leaf : branch.getChildren()) {
					if (leaf.getValue().getFile().equals(gistFile)) {
						treeView.getSelectionModel().select(branch);
					}
				}
			}
			if(state.equals(Type.CONFLICT) && selected) {
				Platform.runLater(() -> {
					CustomAlert.showWarning("This Gist file is in conflict with the version on GitHub. Perhaps it was edited in between GistFX sessions...\n\nThe next window will show you the GitHub version and the locally stored version so that you can decide which one to keep.\n\nYou will not be able to edit the file until you resolve the conflict.");
					openCompareWindow();
				});
			}
		}
	}

	private void createBranchCategories() {
		branchCategoryMap.clear();
		categoryList = Action.getCategoryList();
		treeRoot.getChildren().clear();
		Collections.sort(categoryList);
		for(String category : categoryList) {
			TreeItem<DragNode> categoryBranch = new TreeItem<>(new DragNode(category));
			categoryBranch.setGraphic(categoryBranch.getValue().getGraphic());
			branchCategoryMap.put(category, categoryBranch);
			treeRoot.getChildren().add(categoryBranch);
		}
	}

	private final Map<String,TreeItem<DragNode>> branchCategoryMap = new HashMap<>();

	public void fillTree() {
		createBranchCategories();
		List<Gist> gists = new ArrayList<>(GistManager.getGists());
		gists.sort(Comparator.comparing(Gist::getName));
		for (Gist gist : gists) {
			String             category = Action.getGistCategoryName(gist.getGistId());
			TreeItem<DragNode> branch = getNewBranch(gist);
			if (branchCategoryMap.containsKey(category)) {
				getBranchCategory(category).getChildren().add(branch);
			}
			else {
				treeRoot.getChildren().add(branch);
			}
		}
	}

	public void refreshTree() {
		fillTree();
	}

	private TreeItem<DragNode> getBranchCategory(String category) {
		return branchCategoryMap.getOrDefault(category, null);
	}

	private void createHappyTree() {
		treeView = new TreeView<>();
		treeView.setCellFactory(new DragFactory());
		treeRoot = new TreeItem<>(new DragNode());
		treeView.setRoot(treeRoot);
		treeView.setShowRoot(false);
		new Thread(this::fillTree).start();
		treeView.setOnMouseClicked(e -> {
			TreeItem<DragNode> item = treeView.getSelectionModel().getSelectedItem();
			if(item != null) {
				setSelectedNode(item.getValue());
			}
		});
	}

	public void handleTreeEvent(TreeItem<DragNode> selected) {
		if (selected != null) {
			System.out.println("handleTreeEvent - " + selected.getValue());
			setSelectedNode(selected.getValue());
		}
	}

	/**
	 * Misc. Methods
	 */

	private String getDefaultJavaText(String name) {
		return "public class " + name + " {\n" +
			   "\n" +
			   "\tpublic static void main(String[] args) {\n" +
			   "\t\tSystem.out.println(\"Hello, World!\");\n" +
			   "\t}\n" +
			   "}";
	}

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

	public void showComparingWithGitHub(String label,boolean show) {
		Platform.runLater(() -> {
			lblGitUpdate.setText(label);
			if(show) {
				lblGitUpdate.visibleProperty().unbind();
				lblGitUpdate.setVisible(show);
			}
			else {
				lblGitUpdate.setText("Updating GitHub");
				lblGitUpdate.visibleProperty().bind(Action.getNotifyProperty());
			}
		});
	}

	/**
	 * Menu Bar Methods
	 */

	private final MenuItem miToggleWideMode   = new MenuItem("Toggle Wide Mode");
	private final MenuItem miToggleFullScreen = new MenuItem("Enter Fullscreen");
	private final MenuItem miRecordSetExpanded = new MenuItem("Enable Record Expanded");
	private final MenuItem miRecordSplit = new MenuItem("Enable Record Split");

	private void addMenuBarItems() {
		miToggleFullScreen.setOnAction(e -> inFullScreen.toggle());
		miToggleWideMode.setOnAction(e -> {
			inWideMode.toggle();
			handleButtonBar();
		});

		menuBar.addToFileMenu("New File", e -> newFile(), false);
		menuBar.addToFileMenu("Save File", e -> saveFile(), false);
		menuBar.addToFileMenu("Delete File", e -> deleteFile(), false);
		menuBar.addToFileMenu("Save All Files", e -> saveAllFiles(), false);
		menuBar.addToFileMenu("Open In Browser", e -> openGistInWebBrowser(), true);
		menuBar.addToFileMenu("Exit GistFX", e -> closeApp(), false);

		menuBar.addToGistMenu("New Gist", e -> newGist(), false);
		menuBar.addToGistMenu("Delete Gist", e -> deleteGist(), false);
		menuBar.addToGistMenu("Download Gists", e -> reDownloadAllGists(), false);

		menuBar.addToEditMenu("Copy File To Clipboard", e -> copyFileToClipboard(), false);
		menuBar.addToEditMenu("Undo current edits", e -> undoFile(), false);
		menuBar.addToEditMenu("Save Uncommitted Data", e -> saveAllFiles(), false);
		menuBar.addToEditMenu("Refresh Tree", e -> fillTree(), false);
		menuBar.addToEditMenu("Edit Categories", e -> editCategories(), true);
		menuBar.addToEditMenu("App Settings", e -> showUserSettings(), false);
		menuBar.addToEditMenu("Tree Settings", e -> showTreeSettings(), true);
		menuBar.addToEditMenu(miRecordSetExpanded, false);
		menuBar.addToEditMenu(miRecordSplit, false);

		MenuItem miToggleButtonBar = new MenuItem(showButtonBar.isTrue() ? "Hide ButtonBar" : "Show ButtonBar");
		miToggleButtonBar.setOnAction(e -> {
			showButtonBar.toggle();
			handleButtonBar();
			if (showButtonBar.isTrue()) miToggleButtonBar.setText("Hide ButtonBar");
			else miToggleButtonBar.setText("Show ButtonBar");
			buttonBar.setVisible(showButtonBar.getValue());
		});
		menuBar.addToViewMenu(miToggleButtonBar, false);
		menuBar.addToViewMenu(miToggleFullScreen, false);
		menuBar.addToViewMenu(miToggleWideMode,false);

		menuBar.addToHelpMenu("GistFX Help", e -> Help.mainOverview(), false);
		menuBar.addToHelpMenu("General Help", e -> Help.generalHelp(), false);
		menuBar.addToHelpMenu("If Something Goes Wrong", e -> Help.somethingWrong(), false);
		menuBar.addToHelpMenu("Token Info", e -> Help.showCreateTokenHelp(), true);
		menuBar.addToHelpMenu("Code Languages", e -> Languages.showCodeInformation(), true);
		menuBar.addToHelpMenu("About this program", e -> {
			final int      year    = LocalDate.now().getYear();
			final String versionPath = Main.class.getResource("version.txt").toExternalForm().replaceFirst("file:","");
			final File versionFile = new File(versionPath);
			final String version = Action.loadTextFile(versionFile);
			final Label text    = new Label(Main.APP_TITLE + "\nVersion: " + version + "\n");
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
			SceneOne.set(vBox,"showLegal").title(Main.APP_TITLE).modality(Modality.APPLICATION_MODAL).centered().show();
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

		private void addToEditMenu(MenuItem menuItem, boolean separator) {
			menuEdit.getItems().add(menuItem);
			if (separator) {
				menuEdit.getItems().add(new SeparatorMenuItem());
			}
		}

	}
}
