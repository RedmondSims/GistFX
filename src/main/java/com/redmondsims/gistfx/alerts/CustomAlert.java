package com.redmondsims.gistfx.alerts;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Response;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.sceneone.SceneOne;
import eu.mihosoft.monacofx.Document;
import eu.mihosoft.monacofx.MonacoFX;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomAlert {

	private static       String iconPath;
	private static final String applicationTitle = "";
	private static       String response         = "";

	private static TextField newTextField(String text, String prompt) {
		TextField tf = new TextField(text);
		tf.setPromptText(prompt);
		tf.getStylesheets().add(Theme.TEXT_FIELD.getStyleSheet());
		return tf;
	}

	private static Alert getAlert(Alert.AlertType type, String header, Node content) {
		Alert alert = new Alert(type);
		alert.setTitle(applicationTitle);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setHeaderText(header);
		alert.getDialogPane().setContent(content);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		return alert;
	}

	private static Alert getAlert(Alert.AlertType type, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(applicationTitle);
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		return alert;
	}

	public static void showInfo(String headerText, String contentText, Window owner) {
		Alert alert = getAlert(Alert.AlertType.INFORMATION, headerText, contentText);
		alert.initOwner(owner);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		addDialogIconTo(alert, true);
		alert.showAndWait();
	}

	public static boolean showInfoResponse(String contentText, Window owner) {
		Alert alert = getAlert(Alert.AlertType.INFORMATION, "", contentText);
		alert.initOwner(owner);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		addDialogIconTo(alert, true);
		alert.showAndWait();
		return true;
	}

	public static void showInfo(String headerText, Node content, Window owner) {
		Alert alert = getAlert(Alert.AlertType.INFORMATION, headerText, content);
		alert.initOwner(owner);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		addDialogIconTo(alert, true);
		alert.showAndWait();
	}

	public static void showRequireOK(String headerText, String content, Window owner) {
		Alert alert = getAlert(Alert.AlertType.INFORMATION, headerText, content);
		alert.initOwner(owner);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		addDialogIconTo(alert, true);
		alert.showAndWait();
	}

	public static void showInfo(String contentText, Window owner) {
		showInfo("", contentText, owner);
	}

	public static void showWarning(String headerText, String contentText) {
		Alert alert = getAlert(Alert.AlertType.ERROR, headerText, contentText);
		addDialogIconTo(alert, false);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		alert.showAndWait();
	}

	public static void showWarning(String contentText) {
		showWarning("", contentText);
	}

	public static Response showConfirmationResponse(Node content) {
		return showConfirmationResponse("", content);
	}

	public static Response showConfirmationResponse(String headerText, Node content) {
		Alert      alert   = getAlert(Alert.AlertType.NONE, headerText, content);
		ButtonType proceed = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancel  = new ButtonType("Cancel", ButtonBar.ButtonData.OK_DONE);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().setAll(cancel, proceed);
		alert.getDialogPane().setPadding(new Insets(10, 20, 0, 10));
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		Button btnProceed = (Button) alert.getDialogPane().lookupButton(proceed);
		Button btnCancel  = (Button) alert.getDialogPane().lookupButton(cancel);
		btnProceed.setDefaultButton(false);
		btnCancel.setDefaultButton(true);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			if (result.get().equals(proceed)) {
				return Response.PROCEED;
			}
		}
		return Response.CANCELED;
	}

	public static Response showSaveFilesConfirmationResponse(String message, boolean exiting) {
		Alert      alert  = getAlert(Alert.AlertType.WARNING, "", message);
		ButtonType save   = new ButtonType("Save To GitHub", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancel = new ButtonType(exiting ? "Back To GistFX" : "Cancel", ButtonBar.ButtonData.OK_DONE);
		ButtonType exit   = new ButtonType(exiting ? "Exit Without Saving" : "Continue without saving", ButtonBar.ButtonData.OK_DONE);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().setAll(cancel, save, exit);

		alert.getDialogPane().setPadding(new Insets(10, 20, 0, 10));
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		Button btnSave = (Button) alert.getDialogPane().lookupButton(save);
		Button btnCancel  = (Button) alert.getDialogPane().lookupButton(cancel);
		Button btnExit = (Button) alert.getDialogPane().lookupButton(exit);
		btnExit.setDefaultButton(false);
		btnSave.setDefaultButton(true);
		btnCancel.setDefaultButton(false);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			if (result.get().equals(save)) return Response.SAVE;
			else if (result.get().equals(exit)) return Response.EXIT;
			else return Response.CANCELED;
		}
		return Response.CANCELED;
	}

	public static Boolean showConfirmation(String headerText, String contentText) {
		Alert alert = getAlert(Alert.AlertType.CONFIRMATION, headerText, contentText);
		addDialogIconTo(alert, false);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && result.get().equals(ButtonType.YES);
	}

	public static Response showHardConfirmation(String headerText, String contentText) {
		TextField tfYes      = newTextField("", "Type YES to confirm");
		Label     lblMessage = new Label(contentText);
		lblMessage.setWrapText(true);
		VBox  content = new VBox(lblMessage, tfYes);
		Alert alert   = getAlert(Alert.AlertType.CONFIRMATION, headerText, content);
		addDialogIconTo(alert, false);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get().equals(ButtonType.YES)) {
			if (tfYes.getText().equals("YES")) {
				return Response.YES;
			}
			else {
				Platform.runLater(() -> showInfo("You must type YES in the text field before clicking Yes.", null));
				return Response.MISTAKE;
			}
		}
		return Response.CANCELED;
	}

	public static String showChangeNameAlert(String currentName, String type) {
		double width = 275;
		double height = 175;
		Text txtName = newText(currentName);
		String name = "Change"+type+"Name";
		Label  lblMessage = newLabel("Renaming "+type+": ",85,35,false);
		Label  lblNewGistName = newLabel("New Name:",65,35,false);
		TextField tfGistName = newTextField(currentName, "");
		tfGistName.setPrefWidth(165);
		tfGistName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.trim().isEmpty()) {
				response = "";
			}
			else {
				response = newValue.trim();
			}
		});
		tfGistName.setOnAction(e -> SceneOne.close(name));
		Button btnProceed = new Button("Proceed");
		Button btnCancel = new Button("Cancel");
		btnProceed.setOnAction(e->{
			response = tfGistName.getText().trim();
			SceneOne.close(name);
		});
		btnCancel.setOnAction(e->{
			response = "";
			SceneOne.close(name);
		});
		HBox hbMessage = newHBox(width,35,Pos.CENTER_LEFT,lblMessage,txtName);
		HBox hbLabels = newHBox(width,35,Pos.CENTER_LEFT,lblNewGistName, tfGistName);
		HBox hbButtons = newHBox(width,55,Pos.CENTER,btnProceed,btnCancel);
		VBox vbox = new VBox(hbMessage, hbLabels, hbButtons);
		vbox.setSpacing(0);
		vbox.setPadding(new Insets(10,10, 10, 10));
		vbox.setAlignment(Pos.CENTER);
		AnchorPane ap = new AnchorPane(vbox);
		SceneOne.set(ap,name).centered().size(width,height).newStage().showAndWait();
		return response;
	}

	private static Text newText(String string) {
		Text          text = new Text(string);
		Color color;
		if (LiveSettings.getTheme().equals(Theme.DARK)) {
			color = Color.rgb(144,163,127);
		}
		else {
			color = Color.BLACK;
		}
		text.setFill(color);
		return text;
	}

	private static HBox newHBox(double width, double height, Pos alignment, Node...nodes) {
		HBox hbox = new HBox();
		for(Node node : nodes) {
			hbox.getChildren().add(node);
		}
		hbox.setSpacing(5);
		hbox.setPadding(new Insets(5, 5, 5, 5));
		hbox.setAlignment(alignment);
		hbox.setPrefWidth(width);
		hbox.setPrefHeight(height);
		return hbox;
	}

	private static Label newLabel(String text, double width, double height, boolean wrapText) {
		Label label = new Label(text);
		label.setPrefWidth(width);
		label.setPrefHeight(height);
		label.setWrapText(wrapText);
		return label;
	}

	public static String showChangeGistDescriptionAlert(String currentDescription) {
		String message    = "Gist Description";
		Label  lblMessage = new Label(message);
		lblMessage.setWrapText(true);
		lblMessage.setPrefWidth(100);
		lblMessage.setPrefHeight(35);
		Label    lblBlank          = new Label(" ");
		Label    lblNewGistName    = new Label("New getName:");
		TextArea taGistDescription = new TextArea(currentDescription);
		taGistDescription.setWrapText(true);
		taGistDescription.setPrefWidth(500);
		taGistDescription.setPrefHeight(300);
		VBox vbox = new VBox(lblMessage, lblBlank, taGistDescription);
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		Alert      alert   = getAlert(Alert.AlertType.NONE, "", vbox);
		ButtonType proceed = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(proceed, ButtonType.CANCEL);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		Optional<ButtonType>  option      = alert.showAndWait();
		Map<Response, String> responseMap = new HashMap<>();
		if (option.isPresent() && option.get().equals(ButtonType.CANCEL)) {
			return "";
		}
		return taGistDescription.getText();
	}

	private static String    result;
	public static String showFileRenameAlert(String currentFilename) {
		double width  = 350;
		double height = 150;
		result = "";
		String    name           = "showFileRenameAlert";
		String    title          = "Renaming file " + currentFilename;
		Label     lblBlank       = new Label(" ");
		Label     lblNewFilename = new Label("New Filename:");
		Button    btnProceed     = new Button("Proceed");
		Button    btnCancel      = new Button("Cancel");
		TextField tfFilename     = newTextField(currentFilename, "");
		tfFilename.setPrefWidth(165);
		tfFilename.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) tfFilename.setText(currentFilename);
			else result = newValue;
		});
		tfFilename.setOnAction(e -> {
			result = tfFilename.getText();
			SceneOne.close(name);
		});
		btnProceed.setOnAction(e->{
			result = tfFilename.getText();
			SceneOne.close(name);
		});
		btnCancel.setOnAction(e->{
			result = "";
			SceneOne.close(name);
		});
		HBox hbNewName = new HBox(lblNewFilename, tfFilename);
		HBox hbButtons = newHBox(width,35,Pos.CENTER,btnProceed,btnCancel);
		hbNewName.setSpacing(10);
		hbNewName.setPadding(new Insets(10, 10, 10, 10));
		VBox vbox = new VBox(hbNewName,hbButtons);
		vbox.setSpacing(10);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		SceneOne.set(vbox,name).newStage().size(width,height).centered().showAndWait();
		return result;
	}

	public static String[] newGistAlert(String fileText, boolean categorySet, String selectedCategory) {
		Response response   = Response.CANCELED;
		Alert    alert      = new Alert(Alert.AlertType.NONE);
		Label    lblMessage = new Label("To create a new Gist, Provide the following information, then click Create Gist");
		lblMessage.setWrapText(true);
		Label lblGistFile 		 = new Label("First File");
		Label lblGistName        = new Label("Gist Name:");
		Label lblFilename        = new Label("Filename:");
		Label lblCategory        = new Label("Category: " + selectedCategory);
		Button btnPaste = new Button("Paste");
		MonacoFX monacoFX = new MonacoFX();
		Tooltip.install(btnPaste, new Tooltip("Paste text contents from clipboard into document."));
		btnPaste.setOnAction(e -> {
			try {
				String data     = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				monacoFX.getEditor().getDocument().setText(data);
			}
			catch (IOException | UnsupportedFlavorException ex) {
				ex.printStackTrace();
			}
		});
		if (LiveSettings.getTheme().equals(Theme.DARK)) monacoFX.getEditor().setCurrentTheme("vs-dark"); else monacoFX.getEditor().setCurrentTheme("vs-light");
		lblGistFile.setAlignment(Pos.CENTER_LEFT);
		lblGistName.setMinWidth(60);
		lblFilename.setMinWidth(45);
		lblCategory.setMinWidth(45);
		lblGistFile.setMinWidth(75);
		lblGistName.setMinHeight(21);
		lblFilename.setMinHeight(21);
		lblCategory.setMinHeight(21);
		lblGistFile.setMinHeight(21);
		lblGistName.setAlignment(Pos.BOTTOM_RIGHT);
		lblFilename.setAlignment(Pos.BOTTOM_RIGHT);
		lblGistFile.setAlignment(Pos.BOTTOM_RIGHT);
		ChoiceBox<String> categoryBox = Action.getCategoryBox();
		categoryBox.setMinWidth(75);
		CheckBox cbPublic  = new CheckBox("Public");
		CheckBox cbPrivate = new CheckBox("Private");
		cbPublic.selectedProperty().addListener((observable, oldValue, newValue) -> {if (newValue) cbPrivate.setSelected(false);});
		cbPrivate.selectedProperty().addListener((observable, oldValue, newValue) -> {if (newValue) cbPublic.setSelected(false);});
		cbPrivate.setSelected(true);
		HBox stateBox = new HBox(cbPrivate, cbPublic);
		stateBox.setSpacing(20);
		TextField tfFilename = newTextField("File.java", "");
		tfFilename.setPromptText("New Filename");
		tfFilename.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) tfFilename.setText("File.java");
			String ext = FilenameUtils.getExtension(newValue);
			monacoFX.getEditor().setCurrentLanguage(ext);
		});
		tfFilename.setMinWidth(250);
		TextField tfGistName = newTextField("New Gist", "");
		tfGistName.setPromptText("Gist getName");
		tfGistName.textProperty().addListener((observable, oldValue, newValue) -> {if (newValue.isEmpty()) tfGistName.setText("New Gist");});
		tfGistName.setMinWidth(200);
		TextArea taGistDescription = new TextArea("Gist Description");
		taGistDescription.textProperty().addListener((observable, oldValue, newValue) -> {if (newValue.isEmpty()) taGistDescription.setText("Gist Description");});
		taGistDescription.setPrefWidth(550);
		taGistDescription.setPrefHeight(90);
		Document codeDocument = monacoFX.getEditor().getDocument();
		codeDocument.setText(fileText);
		codeDocument.textProperty().addListener((observable, oldValue, newValue) -> {if (newValue.isEmpty()) codeDocument.setText(fileText);});
		monacoFX.setPrefWidth(550);
		monacoFX.setPrefHeight(290);
		monacoFX.getEditor().setCurrentLanguage("java");
		HBox filenameBox = new HBox(lblFilename, tfFilename,btnPaste);
		filenameBox.setAlignment(Pos.CENTER_LEFT);
		HBox gistNameBox = categorySet ? new HBox(lblGistName, tfGistName, lblCategory) : new HBox(lblGistName, tfGistName,lblCategory,categoryBox);
		VBox content     = new VBox(lblMessage,stateBox,gistNameBox,taGistDescription,filenameBox,monacoFX);
		content.setMinWidth(580);
		filenameBox.setSpacing(8);
		gistNameBox.setSpacing(8);
		content.setSpacing(15);
		alert.getDialogPane().setContent(content);
		tfFilename.requestFocus();
		tfFilename.selectAll();
		ButtonType createGist = new ButtonType("Create Gist", ButtonBar.ButtonData.YES);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(createGist, ButtonType.CANCEL);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		Optional<ButtonType> option  = alert.showAndWait();
		String[]             choices = null;
		if (option.isPresent()) {
			if (option.get().equals(createGist)) {
				String category = categoryBox.getValue();
				if(category == null) category = "!@#none#@!";
				choices = new String[]{
						(cbPublic.isSelected() ? "Public" : "Private"),
						tfGistName.getText(),
						tfFilename.getText(),
						taGistDescription.getText(),
						codeDocument.getText(),
						category
				};
			}
		}
		return choices;
	}

	public static Map<Response, Map<String,String>> showNewFileAlert(String gistName, String fileContent) {
		Response response   = Response.CANCELED;
		Label    lblMessage = new Label("Creating a new file in Gist: " + gistName + "\n\nPlease enter a name for this new file then click on Create File\n");
		MonacoFX codeEditor = new MonacoFX();
		codeEditor.getEditor().setCurrentTheme(LiveSettings.getTheme().equals(UISettings.Theme.DARK) ? "vs-dark" : "vs-light");
		codeEditor.getEditor().getDocument().setText(fileContent);
		codeEditor.getEditor().setCurrentLanguage("java");
		lblMessage.setWrapText(true);
		Label     lblFilename = new Label("Filename:");
		lblFilename.setAlignment(Pos.BOTTOM_CENTER);
		TextField tfFilename  = newTextField("", "New Filename");
		tfFilename.setMinWidth(250);
		tfFilename.textProperty().addListener((observable, oldValue, newValue) -> codeEditor.getEditor().setCurrentLanguage(getFileExtension(newValue)));
		HBox      filenameBox = new HBox(lblFilename, tfFilename);
		filenameBox.setSpacing(10);
		filenameBox.setPadding(new Insets(5,5,5,5));
		VBox content = new VBox(lblMessage, filenameBox, codeEditor);
		content.setPadding(new Insets(5,5,5,5));
		content.setSpacing(10);
		Alert alert = getAlert(Alert.AlertType.NONE, "", content);
		alert.getDialogPane().setContent(content);
		tfFilename.requestFocus();
		ButtonType createFile = new ButtonType("Create File", ButtonBar.ButtonData.YES);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(createFile, ButtonType.CANCEL);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		Optional<ButtonType> option = alert.showAndWait();
		Map<String,String> fileMap = new HashMap<>();
		fileMap.put(tfFilename.getText(),codeEditor.getEditor().getDocument().getText());
		if (option.isPresent() && option.get().equals(createFile)) response = Response.PROCEED;
		Map<Response, Map<String,String>> responseMap = new HashMap<>();
		responseMap.put(response, fileMap);
		return responseMap;
	}

	public static Boolean showConfirmation(String contentText) {
		return showConfirmation("", contentText);
	}

	public static void showExceptionDialog(Exception e, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		addDialogIconTo(alert, false);
		alert.setTitle(applicationTitle);
		alert.setHeaderText("Exception Occurred");
		alert.setContentText(message.isEmpty() ? "An unknown error occurred.\n" + e.getMessage() : message);
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		String exceptionText = e.getLocalizedMessage();

		Label label = new Label("The exception message was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		alert.getDialogPane().setExpandableContent(expContent);
		alert.getDialogPane().setExpanded(true);
		alert.showAndWait();
	}

	private static void addDialogIconTo(Alert alert, boolean addCustomHeaderGraphic) {
		if (iconPath != null && !iconPath.isEmpty()) {
			final Image appIcon     = new Image(iconPath);
			Stage       dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
			dialogStage.getIcons().add(appIcon);

			if (addCustomHeaderGraphic) {
				final ImageView headerIcon = new ImageView(iconPath);
				headerIcon.setFitHeight(48); // Set size to JavaFX API recommendation.
				headerIcon.setFitWidth(48);
				alert.getDialogPane().setGraphic(headerIcon);
			}
		}
	}

	private static String getFileExtension(String filename) {
		if (!filename.contains(".")) {
			return "";
		}
		return FilenameUtils.getExtension(filename);
	}

}