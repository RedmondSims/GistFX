package com.redmondsims.gistfx.ui.gist;

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
import com.redmondsims.gistfx.networking.Transport;
import com.redmondsims.gistfx.preferences.*;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.gist.factory.DragFactory;
import com.redmondsims.gistfx.ui.gist.factory.DragNode;
import com.redmondsims.gistfx.utils.Status;
import com.simtechdata.waifupnp.UPnP;
import eu.mihosoft.monacofx.MonacoFX;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
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
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import javafx.stage.*;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

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
import static com.redmondsims.gistfx.enums.Type.*;

public class GistWindow {

	private static final Response           MISTAKE               = Response.MISTAKE;
	private static final Response           YES                   = Response.YES;
	private static final Response           PROCEED               = Response.PROCEED;
	private final        CheckBox           publicCheckBox        = new CheckBox("");
	private final        Label              lblCheckBox           = new Label("Public");
	private final        Label              lblGistDescription    = new Label();
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
	private final        Transport          transport             = new Transport();
	private              TreeItem<DragNode> treeRoot;
	private              activities         pop;
	private              Tree               tree;
	private              GistFile           file;
	private              Gist               gist;
	private              SplitPane          splitPane;
	private              PaneSplitSetting   paneSplitSetting;
	private              String             gistURL               = "";
	private              TreeView<DragNode> treeView;
	private final        String             sceneId               = "GistWindow";
	private final        Gson               gson                  = new GsonBuilder().setPrettyPrinting().create();
	private              Timer              resizeTimer;
	private final        KeyCodeCombination kcCodeMac             = new KeyCodeCombination(KeyCode.C, KeyCombination.META_DOWN);
	private final        KeyCodeCombination kcCodeOther           = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
	private final        String             compareWarning        = "Please wait until I'm done comparing local Gists with GitHub.";


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
		pop = new activities(SceneOne.getStage(sceneId), SceneOne.getWindow(sceneId), this);
		tree = new Tree(this);
	}

	public TreeView<DragNode> getTreeView() {
		return treeView;
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
	}

	private void checkFileConflicts() {
		new Thread(() -> {
			showComparingWithGitHub("Comparing with GitHub", true);
			Map<String, GHGist> ghGistMap = Action.getNewGhGistMap();
			showComparingWithGitHub("Comparing local data with GitHub", true);
			for (GHGist ghGist : ghGistMap.values()) { //Add any Gists or files that do not currently exist locally
				String description = ghGist.getDescription();
				String gistId      = ghGist.getGistId();
				if (!description.equals(Names.GIST_DATA_DESCRIPTION.Name())) {
					if (!GistManager.hasGist(gistId)) {
						GistManager.addGistFromGitHub(ghGist);
						tree.addBranch(gistId);
					}
					for (String filename : ghGist.getFiles().keySet()) {
						if (!GistManager.gistHasFile(gistId, filename)) {
							GHGistFile ghGistFile = Action.getLocalGitHubFile(gistId, filename);
							GistManager.addFileToGist(gistId, ghGistFile);
							tree.addFileToBranch(gistId, filename);
						}
					}
				}
			}
			Status.setGistWindowState(State.COMPARING);
			while (Status.filesComparing()) {
				Action.setProgress(Status.getRegisteredFileRatio());
			}
			Status.setGistWindowState(State.NORMAL);
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
		addMainNode(lblGistDescription, 105, 20, 80, -1);
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
		lblGistDescription.setVisible(visible);
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
		lblGistDescription.setWrapText(false);
		lblGistDescription.setEllipsisString("...");
		lblGistDescription.setMaxHeight(30);
		buttonSaveFile.setMinWidth(85);
		buttonSaveFile.setMaxWidth(85);
		taFileDescription.setMaxHeight(50);
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
		buttonBar.setVisible(com.redmondsims.gistfx.preferences.AppSettings.get().showButtonBar());
		buttonEditCategories.setPrefWidth(125);
		CodeEditor.get().setVisible(false);
	}

	private void setControlActionProperties() {
		lblGistName.setOnMouseClicked(e -> pop.renameGist(gist));
		lblGistDescription.setOnMouseClicked(e -> pop.changeGistDescription(gist));
		publicCheckBox.setOnAction(e -> changePublicState());
		lblGitUpdate.visibleProperty().bind(Action.getGitHubActivityProperty());
		buttonCopyToClipboard.setOnAction(e -> copyFileToClipboard());
		buttonPasteFromClip.setOnAction(e -> pasteFromClipboard());
		buttonUndo.setOnAction(e -> pop.undoFile(file));
		buttonSaveFile.setOnAction(e -> saveFile());
		buttonCompare.setOnAction(e -> openCompareWindow());
		buttonWideMode.setOnAction(e -> inWideMode.toggle());
		buttonFullScreen.setOnAction(e -> inFullScreen.toggle());
		buttonFullScreen.setMaxWidth(60);
		buttonDistraction.setOnAction(e -> distractionFree());
		buttonEditCategories.setOnAction(e -> pop.editCategories());
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

	public void openCompareWindow() {
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

	public void closeShareWindow() {
		transport.closeReceiveWindow();
	}

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
				tree.refreshGistBranch(file.getGistId());
				Platform.runLater(() -> {
					TreeItem<DragNode> leaf = tree.getLeaf(gistId, filename);
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

	public void expandBranch(TreeItem<DragNode> branch) {
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

	public activities getPop() {
		return pop;
	}

	public Tree getTree() {
		return tree;
	}

	public Gist getGist() {
		return gist;
	}

	public GistFile getFile() {
		return file;
	}

	public DragNode getSelectedNode() {
		return selectedNode;
	}

	public void shareObject(DragNode selectedNode) {
		if(selectedNode != null) {
			switch(selectedNode.getType()) {
				case CATEGORY -> {
					String     category = selectedNode.getCategory();
					List<Gist> gistList = Action.getGistsInCategory(category);
					transport.shareCategory(gistList, category);
				}
				case GIST -> transport.shareGist(selectedNode.getGist());

				case FILE -> transport.shareGistFile(selectedNode.getFile());
			}
			transport.show(SceneOne.getStage(sceneId));
		}
	}

	public void setSelectedNode(DragNode treeSelection) {
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
		handleButtonBar();
	}

	private DragNode selectedNode;

	public TreeItem<DragNode> getTreeRoot() {
		return treeRoot;
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

	public void fillTree() {
		if(treeRoot == null)
			createHappyTree();
		if(tree == null) tree = new Tree(this);
		tree.createBranchCategories();
		List<Gist> gists = new ArrayList<>(GistManager.getGists());
		gists.sort(Comparator.comparing(Gist::getName));
		for (Gist gist : gists) {
			String             category = Action.getGistCategoryName(gist.getGistId());
			TreeItem<DragNode> branch = tree.getNewBranch(gist);
			if (tree.branchCategoryMap.containsKey(category)) {
				tree.getBranchCategory(category).getChildren().add(branch);
			}
			else {
				treeRoot.getChildren().add(branch);
			}
		}
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

	public void showComparingWithGitHub(String label,boolean show) {
		Platform.runLater(() -> {
			lblGitUpdate.setText(label);
			if(show) {
				lblGitUpdate.visibleProperty().unbind();
				lblGitUpdate.setVisible(show);
			}
			else {
				lblGitUpdate.setText("Updating GitHub");
				lblGitUpdate.visibleProperty().bind(Action.getGitHubActivityProperty());
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

	private void showIPAddress() {
		new Thread(() -> {
			String ipAddress = UPnP.getExternalIP();
			String portMapped = UPnP.isMappedTCP(LiveSettings.getTcpPortNumber()) ? "YES" : "NO";
			String message = "Your EXTERNAL IP Address is: " + ipAddress + "\n" +
							 "Port Mapped: " + portMapped;
			Platform.runLater(() -> CustomAlert.showInfo(message, SceneOne.getWindow(sceneId)));
		}).start();
	}

	private void addMenuBarItems() {
		miToggleFullScreen.setOnAction(e -> inFullScreen.toggle());
		miToggleWideMode.setOnAction(e -> {
			inWideMode.toggle();
			handleButtonBar();
		});

		menuBar.addToFileMenu("New File", e -> pop.newFile(gist), false);
		menuBar.addToFileMenu("Save File", e -> saveFile(), false);
		menuBar.addToFileMenu("Delete File", e -> pop.deleteFile(file), false);
		menuBar.addToFileMenu("Save All Files", e -> saveAllFiles(), false);
		menuBar.addToFileMenu("Open In Browser", e -> openGistInWebBrowser(), true);
		menuBar.addToFileMenu("Receive Shared Gist", e -> transport.waitForTransport(), true);
		menuBar.addToFileMenu("Exit GistFX", e -> closeApp(), false);

		menuBar.addToGistMenu("New Gist", e -> pop.newGist(selectedNode), false);
		menuBar.addToGistMenu("Delete Gist", e -> pop.deleteGist(gist), false);
		menuBar.addToGistMenu("Download Gists", e -> reDownloadAllGists(), false);

		menuBar.addToEditMenu("Copy File To Clipboard", e -> copyFileToClipboard(), false);
		menuBar.addToEditMenu("Undo current edits", e -> pop.undoFile(file), false);
		menuBar.addToEditMenu("Save Uncommitted Data", e -> saveAllFiles(), false);
		menuBar.addToEditMenu("Refresh Tree", e -> fillTree(), false);
		menuBar.addToEditMenu("Edit Categories", e -> pop.editCategories(), true);
		menuBar.addToEditMenu("App Settings", e -> showUserSettings(), false);
		menuBar.addToEditMenu("Tree Settings", e -> showTreeSettings(), true);
		menuBar.addToEditMenu(miRecordSetExpanded, false );
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
		menuBar.addToViewMenu("This IP Address",e -> showIPAddress(),false);

		menuBar.addToHelpMenu("GistFX Help", e -> Help.mainOverview(), false);
		menuBar.addToHelpMenu("General Help", e -> Help.generalHelp(), false);
		menuBar.addToHelpMenu("If Something Goes Wrong", e -> Help.somethingWrong(), false);
		menuBar.addToHelpMenu("Token Info", e -> Help.showCreateTokenHelp(), true);
		menuBar.addToHelpMenu("Code Languages", e -> Languages.showCodeInformation(), true);
		menuBar.addToHelpMenu("About this program", e -> {
			try {
				final int         year        = LocalDate.now().getYear();
				final InputStream versionTextStream = Main.class.getResourceAsStream("version.txt");
				final String version = IOUtils.toString(versionTextStream, StandardCharsets.UTF_8);
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
