package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.Help;
import com.redmondsims.gistfx.alerts.Languages;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Response;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.gist.GistType;
import com.redmondsims.gistfx.javafx.PaddedGridPane;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.utils.SceneOne;
import eu.mihosoft.monacofx.MonacoFX;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.redmondsims.gistfx.enums.State.RELOAD;
import static com.redmondsims.gistfx.utils.SceneOne.Choice.*;
import static javafx.scene.layout.AnchorPane.*;

public class GistWindow {

	private static final Response           MISTAKE               = Response.MISTAKE;
	private static final Response           YES                   = Response.YES;
	private static final Response           PROCEED               = Response.PROCEED;
	private final        Label              lblLanguageSetting    = new Label("Recognized Language");
	private final        Label              lblDescription        = new Label();
	private final        CheckBox           publicCheckBox        = new CheckBox("");
	private final        Label              checkBoxLabel         = new Label("Public");
	private final        Label              lblDescriptionLabel   = new Label("Description:");
	private final        Label              lblFileNameLabel      = new Label("Filename:");
	private final        Label              lblGitHubName         = new Label("");
	private final        Label              lblGistNameLabel      = new Label("  Gist Name:");
	private final        Label              lblGitUpdate          = new Label("Updating GitHub");
	private final        Label              lblFileName           = new Label();
	private final        Label              lblGistName           = new Label();
	private final        Button             buttonNewFile         = new Button("New File");
	private final        Button             buttonSaveFile        = new Button("Upload File");
	private final        Button             buttonDeleteFile      = new Button("Delete File");
	private final        Button             buttonNewGist         = new Button("New Gist");
	private final        Button             buttonCopyToClipboard = new Button("Copy File To Clipboard");
	private final        Button             buttonDeleteGist      = new Button("Delete Gist");
	private final        Button             buttonUndo            = new Button("Undo Edit");
	private final        Button             buttonCompare         = new Button("Resolve Conflict");
	private final        ButtonBar          buttonBar             = new ButtonBar();
	private final        MyMenuBar          menuBar               = new MyMenuBar();
	private final        AnchorPane         ap                    = new AnchorPane();
	private final        AnchorPane         apPane                = new AnchorPane();
	private final        ProgressBar        pBar                  = Action.getProgressNode(10);
	private              State              launchState;
	private              Type               buttonBarType         = Type.GIST;
	private              boolean            showButtonBar         = false;
	private              TreeView<GistType> masterTreeView;
	private              TreeItem<GistType> treeRoot;
	private              GistFile           file;
	private              Gist               gist;
	private              SplitPane          splitPane;
	private              String             gistURL               = "";
	private              boolean            savingData;
	private              TreeView<GistType> treeView;
	private              TreeItem<GistType> selectedTreeItemForGistName;
	private              TreeItem<GistType> selectedTreeItemForGistFileName;
	private final        String             sceneId               = "GistWindow";

	private static void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

	/**
	 * UI Related Methods
	 */

	public void showMainWindow(State launchState) {
		this.launchState = launchState;
		masterTreeView = getTreeView();
		showButtonBar  = AppSettings.getShowButtonBar();
		placeControlsOnPane();
		setControlVisualProperties();
		setControlActionProperties();
		splitPane.setDividerPosition(0, .2);
		addMenuBarItems();
		SceneOne.set(ap,sceneId).show(DECORATED,CENTERED);
		SceneOne.setOnKeyPressed(sceneId,e -> {
			if (e.getCode() == KeyCode.T && e.isMetaDown()) {
				treeView.requestFocus();
			}
			if (e.getCode() == KeyCode.E && e.isControlDown() && e.isAltDown()) {
				CodeEditor.requestFocus();
			}
		});
		SceneOne.setStageCloseEvent(sceneId, this::closeWindowEvent);
		if (this.launchState.equals(RELOAD)) CustomAlert.showInfo("All data re-downloaded successfully.", SceneOne.getOwner(sceneId));
		else checkUnsavedFiles();
		handleButtonBar();
	}

	private void checkUnsavedFiles() {
		if (GistManager.isDirty()) {
			Response response = CustomAlert.showSaveFilesConfirmationResponse("You have edited files that have not been uploaded to GitHub, how would you like to proceed?\n",false);
			if (response.equals(Response.SAVE)) {
				saveAllFiles();
			}
		}
	}

	private void closeWindowEvent(WindowEvent event) {
		boolean saveDirtyData = false;
		if (LiveSettings.disableDirtyWarning() && GistManager.isDirty()) {
			saveDirtyData = true;
		}
		else if (GistManager.isDirty()) {
			Response response = CustomAlert.showSaveFilesConfirmationResponse("You have unsaved files, how would you like to proceed?\n",true);
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
			savingData = true;
			new Thread(() -> {
				while (savingData) sleep(100);
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
		addMainNode(lblGitHubName, -1, 25, 40, -1);
		addMainNode(checkBoxLabel, -1, 20, 90, -1);
		addMainNode(lblGitUpdate, -1, 40, 75, -1);
		addMainNode(lblDescriptionLabel, 20, -1, 80, -1);
		addMainNode(lblDescription, 105, 20, 80, -1);
		addMainNode(pBar, 15, 15, 100, -1);

		addPaneNode(buttonBar, 0, -1, 5, -1);
		addPaneNode(lblFileNameLabel, 0, -1, 35, -1);
		addPaneNode(lblFileName, 85, 20, 35, -1);
		addPaneNode(lblLanguageSetting, 20, -1, 60, -1);
		addPaneNode(CodeEditor.get(), 20, 20, 85, 20);
		setAnchors(CodeEditor.get(), 20, 20, 85, 20);
		splitPane = new SplitPane(masterTreeView, apPane);
		addMainNode(splitPane, -1, -1, -1, -1);
		setAnchors(splitPane, 10, 10, 120, 10);
		buttonBar.setPrefHeight(25);
		buttonBar.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		lblLanguageSetting.setPrefWidth(155);
		lblFileName.setPrefWidth(200);
		lblDescriptionLabel.setPrefWidth(70);
	}

	private void labelsVisible(boolean visible) {
		lblDescription.setVisible(visible);
		lblDescriptionLabel.setVisible(visible);
		lblFileName.setVisible(visible);
		lblGistName.setVisible(visible);
		lblLanguageSetting.setVisible(visible);
		lblFileNameLabel.setVisible(visible);
		lblGistNameLabel.setVisible(visible);
	}

	private void setControlVisualProperties() {
		lblGistName.setPrefWidth(200);
		lblGistName.setWrapText(false);
		lblGistName.setEllipsisString("...");
		lblGistNameLabel.setAlignment(Pos.CENTER_RIGHT);
		lblGistNameLabel.setPrefWidth(75);
		lblGitHubName.setAlignment(Pos.CENTER_RIGHT);
		lblGitHubName.setText(Action.getName());
		lblDescriptionLabel.setPrefWidth(75);
		lblDescriptionLabel.setAlignment(Pos.CENTER_RIGHT);
		lblFileNameLabel.setAlignment(Pos.CENTER_LEFT);
		lblFileNameLabel.setPrefWidth(60);
		lblDescription.setWrapText(false);
		lblDescription.setEllipsisString("...");
		buttonNewFile.setMaxWidth(55);
		buttonSaveFile.setMinWidth(85);
		buttonSaveFile.setMaxWidth(85);
		buttonNewGist.setMaxWidth(55);
		buttonDeleteGist.setMaxWidth(80);
		buttonUndo.setMaxWidth(80);
		buttonDeleteFile.setMaxWidth(80);
		lblFileName.setPrefWidth(125);
		lblFileName.setAlignment(Pos.CENTER_LEFT);
		lblFileNameLabel.setPrefWidth(75);
		lblFileNameLabel.setAlignment(Pos.CENTER_RIGHT);
		lblLanguageSetting.setVisible(false);
		lblFileNameLabel.setVisible(false);
		lblFileName.setVisible(false);
		publicCheckBox.setDisable(true);
		checkBoxLabel.setGraphic(publicCheckBox);
		checkBoxLabel.setContentDisplay(ContentDisplay.RIGHT);
		pBar.setPrefHeight(10);
		pBar.setStyle(AppSettings.getProgressBarStyle());
		pBar.setVisible(pBar.progressProperty().isBound());
		labelsVisible(false);
		lblGitUpdate.setId("notify");
		buttonCompare.setId("conflict");
		buttonBar.setVisible(AppSettings.getShowButtonBar());
		CodeEditor.get().setVisible(false);
	}

	private void setControlActionProperties() {
		buttonNewGist.setOnAction(e -> NewGist());
		buttonCopyToClipboard.setOnAction(e -> copyFileToClipboard());
		buttonDeleteGist.setOnAction(e -> deleteGist());
		buttonUndo.setOnAction(e -> undoFile());
		buttonNewFile.setOnAction(e -> newFile());
		buttonSaveFile.setOnAction(e -> saveFile());
		buttonDeleteFile.setOnAction(e -> deleteFile());
		lblFileName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				if (file != null) {
					if (selectedTreeItemForGistFileName != null) {
						selectedTreeItemForGistFileName.setValue(new GistType(file));
					}
				}
			}
		});
		lblGistName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				if (selectedTreeItemForGistName != null) {
					selectedTreeItemForGistName.setValue(new GistType(gist));
				}
			}
		});
		lblFileName.setOnMouseClicked(e -> renameFile());
		lblGistName.setOnMouseClicked(e -> renameGist());
		lblDescription.setOnMouseClicked(e -> changeGistDescription());
		publicCheckBox.setOnAction(e -> changePublicState());
		lblGitUpdate.visibleProperty().bind(Action.getNotifyProperty());
		buttonCompare.setOnAction(e->openCompareWindow());
	}

	private void showWorking() {
		Label lblWorking = new Label("Working");
		lblWorking.setMinWidth(100);
		lblWorking.setMinHeight(50);
		lblWorking.setAlignment(Pos.CENTER);
		Platform.runLater(() -> {
			VBox vbox = new VBox(lblWorking);
			SceneOne.set(vbox,"working").show();
			new Thread(() -> {
				String[] labels = new String[]{"Working", "Working.", "Working..", "Working..."};
				sleep(100);
				while (SceneOne.isShowing("working")) {
					for (int x = 1; x < 4; x++) {
						String text = labels[x];
						Platform.runLater(() -> lblWorking.setText(text));
						sleep(800);
					}
					for (int x = 2; x >= 0; x--) {
						String text = labels[x];
						Platform.runLater(() -> lblWorking.setText(text));
						sleep(800);
					}
				}
			}).start();
		});
	}

	public void handleButtonBar() {
		Platform.runLater(() -> {
			buttonBar.getButtons().clear();
			if (buttonBarType.equals(Type.FILE)) {
				if (file != null) {
					if (file.isDirty()) {
						buttonBar.getButtons().setAll(buttonNewFile, buttonNewGist, buttonCopyToClipboard, buttonSaveFile, buttonUndo, buttonDeleteFile);
					}
					else {
						buttonBar.getButtons().setAll(buttonNewFile, buttonNewGist, buttonCopyToClipboard, buttonDeleteFile);
					}
					if (file.isInConflict()) {
						buttonBar.getButtons().add(buttonCompare);
					}
				}
			}

			if (buttonBarType.equals(Type.GIST)) {
				if (gist != null) {
					buttonBar.getButtons().setAll(buttonNewGist, buttonNewFile, buttonDeleteGist);
				}
				else {
					buttonBar.getButtons().setAll(buttonNewGist);
				}
			}
			double top = buttonBar.isVisible() ? 35 : 5;
			setAnchors(lblFileNameLabel, 0, -1, top, -1);
			setAnchors(lblFileName, 85, 20, top, -1);
			setAnchors(lblLanguageSetting, 20, -1, top + 25, -1);
			setAnchors(CodeEditor.get(), 20, 20, top + 50, 20);
		});
	}

	private void addMainNode(Node node, double left, double right, double top, double bottom) {
		ap.getChildren().add(node);
		setNodePosition(node, left, right, top, bottom);
	}

	private void addPaneNode(Node node, double left, double right, double top, double bottom) {
		apPane.getChildren().add(node);
		setNodePosition(node, left, right, top, bottom);
	}

	private void addNode(AnchorPane ap, Node node, double left, double right, double top, double bottom) {
		ap.getChildren().add(node);
		setNodePosition(node, left, right, top, bottom);
	}

	private void setAnchors(Node node, double left, double right, double top, double bottom) {
		if (top != -1) AnchorPane.setTopAnchor(node, top);
		if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
		if (left != -1) AnchorPane.setLeftAnchor(node, left);
		if (right != -1) AnchorPane.setRightAnchor(node, right);
	}

	private void openCompareWindow() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		double width = screenBounds.getWidth() * .75;
		double height = screenBounds.getHeight() * .75;
		AnchorPane ap = new AnchorPane();
		Button buttonKeepGitHub = new Button("Keep GitHub Version");
		Button buttonKeepLocal = new Button("Keep Local Version");
		buttonKeepGitHub.setId("GitHubCompare");
		buttonKeepLocal.setId("GitHubCompare");
		buttonKeepGitHub.setOnAction(e -> {
			file.resolveConflict(Type.GITHUB);
			SceneOne.close("Compare");
		});
		buttonKeepLocal.setOnAction(e -> {
			file.resolveConflict(Type.LOCAL);
			SceneOne.close("Compare");
		});
		MonacoFX gitHubEditor = new MonacoFX();
		MonacoFX localEditor = new MonacoFX();
		if (AppSettings.getTheme().equals(Theme.DARK)) {
			gitHubEditor.getEditor().setCurrentTheme("vs-dark");
			localEditor.getEditor().setCurrentTheme("vs-dark");
		}
		gitHubEditor.getEditor().getDocument().setText(file.getGitHubVersion());
		localEditor.getEditor().getDocument().setText(file.getContent());
		double midPoint = width / 2;
		addNode(ap,buttonKeepGitHub,10,-1,10,-1);
		addNode(ap,buttonKeepLocal,-1,10,10,-1);
		addNode(ap,gitHubEditor,10,midPoint + 5,40,10);
		addNode(ap,localEditor,midPoint + 5,10,40,10);
		SceneOne.set(ap,"Compare").widthHeight(width,height).show(CENTERED,FLOATER);
	}

	/**
	 * Action Methods
	 */

	private void changePublicState() {
		boolean lastState = !publicCheckBox.isSelected();
		if (checkSelectedGist("Change Public State")) {
			if (confirmChangePublicState(gist.getGistId())) {
				new Thread(() -> {
					showWorking();
					TreeItem<GistType> oldBranch = getBranch(gist.getGistId());
					Gist               newGist   = GistManager.setPublicState(gist, publicCheckBox.isSelected());
					if (newGist == null) {
						publicCheckBox.setSelected(lastState);
					}
					else {
						TreeItem<GistType> newBranch = new TreeItem<>(new GistType(newGist));
						for (GistFile file : newGist.getFiles()) {
							TreeItem<GistType> leaf = new TreeItem<>(new GistType(file));
							newBranch.getChildren().add(leaf);
						}
						int index = treeRoot.getChildren().indexOf(oldBranch);
						treeRoot.getChildren().remove(oldBranch);
						treeRoot.getChildren().add(index, newBranch);
						treeView.getSelectionModel().select(newBranch);
						String newState = publicCheckBox.isSelected() ? "Public" : "Secret";
						Platform.runLater(SceneOne::close);
						CustomAlert.showInfo("This Gist has been converted to a " + newState + " Gist", SceneOne.getOwner(sceneId));
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
						 "<h2 style=\"text-align:centerOnScreen\">Change Gist State</h2>\n" +
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
		if (AppSettings.getTheme().equals(Theme.DARK)) {
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
			if(file.isInConflict()) {
				CustomAlert.showWarning("Cannot Save File!","This file is in conflict with the GitHub version. Resolve conflict first.");
				openCompareWindow();
				return;
			}
			new Thread(() -> {
				sleep(500);
				if(!file.flushDirtyData()) {
					Platform.runLater(() -> CustomAlert.showWarning("There was a problem committing the data."));
				}
			}).start();
		}
	}

	private void saveAllFiles() {
		if(filesInConflict()) {
			CustomAlert.showWarning("Cannot Save Files!","You have one or more files in conflict with the GitHub version. Click on the file with the red X next to it, then click on the Resolve Conflict button and chose which version of the file should be kept before saving any files.");
			return;
		}
		if (GistManager.isDirty()) {
			new Thread(() -> {
				savingData = true;
				Platform.runLater(() -> {
					CodeEditor.get().setVisible(false);
					bindProgressBar(Action.getGitHubDownloadProgress());
					Action.getGitHubDownloadProgress().setValue(0);
					for (TreeItem<GistType> branch : treeRoot.getChildren()) {
						branch.setExpanded(false);
					}
				});
				List<GistFile> unsavedFileList = GistManager.getUnsavedFiles();
				double         total           = unsavedFileList.size();
				double         count           = 1;
				for (GistFile file : unsavedFileList) {
					double newCount = count;
					Platform.runLater(() -> Action.getGitHubDownloadProgress().setValue(newCount / total));
					if (!file.flushDirtyData()) {
						CustomAlert.showWarning("There was a problem.");
						savingData = false;
						return;
					}
					count++;
				}
				Platform.runLater(() -> {
					clearProgressBar();
					Action.getGitHubDownloadProgress().setValue(0);
					if(!LiveSettings.disableDirtyWarning()) {
						CustomAlert.showInfo("All unsaved files were uploaded to GitHub successfully.", SceneOne.getOwner(sceneId));
					}
					savingData = false;
				});
			}).start();
		}
		else {CustomAlert.showInfo("No files need to be uploaded to GitHub.", SceneOne.getOwner(sceneId));}
	}

	private void NewGist() {
		Platform.runLater(() -> {
			String[] choices = CustomAlert.newGistAlert();
			if (choices != null) {
				boolean isPublic    = choices[0].equals("Public");
				String  gistName    = choices[1];
				String  filename    = choices[2];
				String  description = choices[3];
				String  newGistID   = GistManager.addNewGistToGitHub(gistName, description, filename, getDefaultJavaText(FilenameUtils.getBaseName(filename)), isPublic);
				if (!newGistID.isEmpty()) {
					addGistBranch(GistManager.getGist(newGistID),0);
				}
			}
		});
	}

	private void newFile() {
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
							TreeItem<GistType> leaf = new TreeItem<>(new GistType(file));
							TreeItem<GistType> branch = getBranch(gistId);
							addLeafToBranch(branch, leaf);
							branch.setExpanded(true);
						}
					}
				}
			}).start();
		}
	}

	private void deleteFile() {
		if (checkSelectedGistFile("Delete File")) {
			String gistId = file.getGistId();
			if (CustomAlert.showConfirmation("Are you sure you want to delete the file\n\n" + file.getFilename() + "\n\nFrom Gist: " + lblGistName.getText() + "?")) {
				if (GistManager.deleteFile(file)) {
					removeLeaf(file);
					setSelectedBranchOrLeaf(Objects.requireNonNull(getBranch(gistId)).getValue());
				}
			}
		}
	}

	private void undoFile() {
		if(checkSelectedGistFile("Undo Edit")) {
			if (CustomAlert.showConfirmation("This action will overwrite your local changes with the last version that was uploaded to your GitHub account.\n\nAre you sure?")) {
				file.undo();
			}
		}
	}

	private void deleteGist() {
		if (checkSelectedGist("Delete Gist")) {
			String   gistId   = gist.getGistId();
			Response response = deleteGistResponse(gistId);
			if (response == YES) {
				if (GistManager.deleteGist(gist)) {
					removeBranch(gist);
					CustomAlert.showInfo("Gist deleted successfully.", SceneOne.getOwner(sceneId));
				}
			}
			if (response == MISTAKE) deleteGist();
		}
	}

	private Response deleteGistResponse(String gistId) {
		int    forkCount = gist.getForkCount();
		String forkText  = "";
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

	private void renameGist() {
		if (checkSelectedGist("Rename Gist")) {
			String gistId  = gist.getGistId();
			String newName = CustomAlert.showChangeGistNameAlert(gist.getName());
			if (!newName.isEmpty()) {
				gist.setName(newName);
				Objects.requireNonNull(getBranch(gistId)).setValue(new GistType(gist));
				lblGistName.setText(newName.replaceAll("\\n", " "));
			}
		}
	}

	private void renameFile() {
		if (checkSelectedGistFile("Rename File")) {
			String newFileName = CustomAlert.showFileRenameAlert(file.getFilename());
			if (!newFileName.isEmpty()) {
				file.renameFile(newFileName);
			}
		}
	}

	private void changeGistDescription() {
		if (checkSelectedGist("Change Description")) {
			String newDescription = CustomAlert.showChangeGistDescriptionAlert(gist.getDescription());
			if (!newDescription.isEmpty()) {
				gist.newDescription(newDescription);
			}
		}
	}

	private void reDownloadAllGists() {
		if (GistManager.isDirty()) {
			Response response = CustomAlert.showSaveFilesConfirmationResponse("You have unsaved files. Proceeding without first saving your files will result in the total loss of the unsaved data.\n\nHow would you like to proceed?\n",false);
			if (response.equals(Response.SAVE)) saveAllFiles();
			if (response.equals(Response.CANCELED)) return;
		}
		file = null;
		gist = null;
		bindProgressBar(Action.getGitHubDownloadProgress());
		GistManager.unBindFileObjects();
		new Thread(() -> {
			Action.refreshAllData();
			Platform.runLater(SceneOne::close);
			clearProgressBar();
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
				CustomAlert.showInfo("Clipboard", file.getFilename() + " copied to clipboard", SceneOne.getOwner(sceneId));
			}
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

	public void clearProgressBar() {
		pBar.progressProperty().unbind();
		pBar.setVisible(false);
	}

	private boolean filesInConflict() {
		for (TreeItem<GistType> branch : treeRoot.getChildren()) {
			for (TreeItem<GistType> leaf : branch.getChildren()) {
				if (leaf.getValue().getFile().isInConflict()) return true;
			}
		}
		return false;
	}

	/**
	 * Tree Methods
	 */

	private void setSelectedBranchOrLeaf(GistType treeSelection) {
		lblFileName.textProperty().unbind();
		lblGistName.setText("");
		lblFileName.setText("");
		publicCheckBox.setDisable(false);
		buttonBarType = treeSelection.getType();
		CodeEditor.setEditorTheme();
		String dark = "-fx-text-fill: rgba(155,200,155,1)";
		String light = "-fx-text-fill: rgba(155,0,0,.5)";
		lblLanguageSetting.setStyle(AppSettings.getTheme().equals(Theme.DARK) ? dark : light);
		labelsVisible(false);
		switch (treeSelection.getType()) {
			case FILE -> {
				file = treeSelection.getFile();
				gist = treeSelection.getGist();
				lblLanguageSetting.setVisible(true);
				file.setActiveWith(lblFileName.textProperty(), lblLanguageSetting.textProperty());
				CodeEditor.show();
				labelsVisible(true);
				setAnchors(CodeEditor.get(), 20, 20, 120, 20);
			}
			case GIST -> {
				gist = treeSelection.getGist();
				file = null;
				CodeEditor.hide();
				lblGistName.setVisible(true);
				lblGistNameLabel.setVisible(true);
				lblDescriptionLabel.setVisible(true);
				lblDescription.setVisible(true);
				lblDescription.textProperty().unbind();
				lblDescription.textProperty().bind(gist.getDescriptionProperty());
				setAnchors(CodeEditor.get(), 20, 20, 20, 20);
			}
		}
		lblGistName.setText(gist.getName().replaceAll("\\n", " "));
		gistURL = gist.getURL();
		publicCheckBox.setSelected(gist.isPublic());
		handleButtonBar();
	}

	private void addLeafToBranch(TreeItem<GistType> branch, TreeItem<GistType> leaf) {
		GistFile gistFile = leaf.getValue().getFile();
		leaf.graphicProperty().bind(gistFile.getFlagNode());
		gistFile.addedToTree();
		branch.getChildren().add(leaf);
	}

	private void addGistBranch(Gist gist,int index) {
		TreeItem<GistType> branch = new TreeItem<>(new GistType(gist));
		for(GistFile file : gist.getFiles()) {
			TreeItem<GistType> leaf   = new TreeItem<>(new GistType(file));
			addLeafToBranch(branch,leaf);
			if (file.isDirty()) branch.setExpanded(true);
		}
		if (index == -1) treeRoot.getChildren().add(branch);
		else treeRoot.getChildren().add(index,branch);
	}

	private void removeLeaf(GistFile file) {
		Objects.requireNonNull(getBranch(file.getGistId())).getChildren().removeIf(leaf -> leaf.getValue().getFile().equals(file));
	}

	private void removeBranch(Gist gist) {
		treeRoot.getChildren().removeIf(branch -> branch.getValue().getGist().equals(gist));
	}

	public void setFileDirtyState(GistFile gistFile, Type state){
		if (!state.equals(Type.OK)) {
			for(TreeItem<GistType> branch : treeRoot.getChildren()) {
				for(TreeItem<GistType> leaf : branch.getChildren()) {
					if (leaf.getValue().getFile().equals(gistFile)) {
						branch.setExpanded(true);
					}
				}
			}
		}
		if(state.equals(Type.CONFLICT)) {
			Platform.runLater(() -> {
				CustomAlert.showWarning("This Gist file is in conflict with the version on GitHub. Perhaps it was edited in between GistFX sessions...\n\nThe next window will show you the GitHub version and the locally stored version so that you can decide which one to keep.\n\nYou will not be able to edit the file until you resolve the conflict.");
				openCompareWindow();
			});
		}
	}

	private TreeItem<GistType> getBranch(String gistId) {
		for (TreeItem<GistType> branch : treeRoot.getChildren()) {
			if (branch.getValue().getType().equals(Type.GIST) && branch.getValue().getGistID().equals(gistId)) {
				return branch;
			}
		}
		return null;
	}

	private TreeView<GistType> getTreeView() {
		treeView = new TreeView<>();
		treeRoot = new TreeItem<>(new GistType());
		treeView.setRoot(treeRoot);
		treeView.setShowRoot(false);
		for (Gist gist : GistManager.getGists()) {
			addGistBranch(gist,-1);
		}
		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getClickCount() == 1) {
				handleTreeEvent();
			}
		});
		treeView.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			boolean enter = e.getCode().getName().equals("Enter");
			if (enter) {
				handleTreeEvent();
			}
		});
		return treeView;
	}

	private void handleTreeEvent() {
		TreeItem<GistType> selected = treeView.getSelectionModel().getSelectedItem();
		if (selected != null) {
			if (selected.getValue().getType() == Type.GIST) {
				selectedTreeItemForGistName     = selected;
				selectedTreeItemForGistFileName = null;
			}
			else {
				selectedTreeItemForGistName     = null;
				selectedTreeItemForGistFileName = selected;
			}
			setSelectedBranchOrLeaf(selected.getValue());
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

	private void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Menu Bar Methods
	 */

	private void showSettings() {
		UISettings.showWindow(SceneOne.getScene(sceneId));
	}

	private void addMenuBarItems() {
		menuBar.addToFileMenu("New File", e -> newFile(), false);
		menuBar.addToFileMenu("Save File", e -> saveFile(), true);
		menuBar.addToFileMenu("Save All Files", e -> saveAllFiles(), false);
		menuBar.addToFileMenu("Open In Browser", e -> openGistInWebBrowser(), false);
		menuBar.addToFileMenu("Delete File", e -> deleteFile(), true);
		menuBar.addToFileMenu("Exit GistFX", e -> closeApp(), false);

		menuBar.addToGistMenu("New Gist", e -> NewGist(), false);
		menuBar.addToGistMenu("Delete Gist", e -> deleteGist(), false);
		menuBar.addToGistMenu("Download Gists", e -> reDownloadAllGists(), false);

		menuBar.addToEditMenu("Copy File To Clipboard", e -> copyFileToClipboard(), false);
		menuBar.addToEditMenu("Undo current edits", e -> undoFile(), false);
		menuBar.addToEditMenu("Save Uncommitted Data", e -> saveAllFiles(), true);
		menuBar.addToEditMenu("UserOptions", e -> showSettings(), false);

		MenuItem miToggleBB = new MenuItem(showButtonBar ? "Hide ButtonBar" : "Show ButtonBar");
		miToggleBB.setOnAction(e -> {
			showButtonBar = !showButtonBar;
			handleButtonBar();
			if (showButtonBar) miToggleBB.setText("Hide ButtonBar");
			else miToggleBB.setText("Show ButtonBar");
			buttonBar.setVisible(showButtonBar);
		});
		menuBar.addToViewMenu(miToggleBB, false);
		menuBar.addToViewMenu("Toggle Fullscreen", e -> {
			SceneOne.setFullScreen(sceneId,!SceneOne.isFullScreen());
			if (SceneOne.isFullScreen(sceneId)) {
				splitPane.setDividerPosition(0, .15);
			}
			else {
				splitPane.setDividerPosition(0, .2);
			}
		}, false);

		menuBar.addToHelpMenu("GistFX Help", e -> Help.mainOverview(), false);
		menuBar.addToHelpMenu("General Help", e -> Help.generalHelp(), false);
		menuBar.addToHelpMenu("If Something Goes Wrong", e -> Help.somethingWrong(), false);
		menuBar.addToHelpMenu("Token Info", e -> Help.showCreateTokenHelp(), true);
		menuBar.addToHelpMenu("Code Languages", e -> Languages.showCodeInformation(), true);
		menuBar.addToHelpMenu("About this program", e -> {
			PaddedGridPane grid    = new PaddedGridPane(5, 30);
			final int      year    = LocalDate.now().getYear();
			final String   version = getClass().getPackage().getImplementationVersion();
			final Label    text    = new Label(Main.APP_TITLE + "\nVersion: " + version + "\n");
			final TextArea taLicense = new TextArea(
					"Copyright \u00A9 " + year + "\n\tDustin K. Redmond <dredmond@gaports.com>\n\tMichael D. Sims <mike@simtechdata.com>\n\n" +
					"Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
					"of this software and associated documentation files (the \"Software\"), to deal\n" +
					"in the Software without restriction, including without limitation the rights\n" +
					"to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
					"copies of the Software, and to permit persons to whom the Software is\n" +
					"furnished to do so, subject to the following conditions:\n" +
					"\n" +
					"The above copyright notice and this permission notice shall be included in all\n" +
					"copies or substantial portions of the Software.\n" +
					"\n" +
					"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
					"IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
					"FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE\n" +
					"AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
					"LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
					"OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
					"SOFTWARE." +
					"\n" +
					"\n" +
					"Application icons provided by https://icons8.com");
			taLicense.setEditable(false);
			VBox vBox = new VBox(5, text, taLicense);
			grid.getChildren().add(vBox);
			SceneOne.set(vBox,"showLegal").title(Main.APP_TITLE).show(APP_MODAL, CENTERED);
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

		private void addToViewMenu(String menuName, EventHandler<ActionEvent> event, boolean separator) {
			MenuItem menuItem = new MenuItem(menuName);
			menuItem.setOnAction(event);
			menuView.getItems().add(menuItem);
			if (separator) {
				menuView.getItems().add(new SeparatorMenuItem());
			}
		}

		private void addToViewMenu(MenuItem menuItem, boolean separator) {
			menuView.getItems().add(menuItem);
			if (separator) {
				menuView.getItems().add(new SeparatorMenuItem());
			}
		}

	}
}
