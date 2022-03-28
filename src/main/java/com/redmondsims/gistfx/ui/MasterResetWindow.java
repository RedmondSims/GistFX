package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class MasterResetWindow {

	/*
	 * Call this window by passing masterReset into the launch argument
	 */

	private final String sceneId = "MasterReset";

	public MasterResetWindow () {
		startWindow();
	}

	private void startWindow() {
		System.out.println("Showing MasterReset");
		double width = 400;
		double height = 385;
		Label    lblTitle   = new Label("Master Reset");
		lblTitle.setId("masterReset");
		Label    lblMessage = new Label(getMessage());
		lblMessage.setPadding(new Insets(10,10,10,10));
		CheckBox cbDatabase = new CheckBox("Reset Database (wipe out and create new)");
		CheckBox cbSettings = new CheckBox("Reset all app settings to default");
		CheckBox cbCredentials = new CheckBox("Reset Login Credentials");
		CheckBox cbLocalMetadata = new CheckBox("Wipe out Local Metadata");
		CheckBox cbGitHubMetadata = new CheckBox("Wipe out GitHub Metadata");
		Button button = new Button("GO");
		VBox vbox = new VBox(centeredHBox(lblTitle),lblMessage,cbDatabase, cbSettings,cbCredentials,cbLocalMetadata,cbGitHubMetadata,centeredHBox(button));
		vbox.setPadding(new Insets(15,15,15,20));
		vbox.setSpacing(15);
		cbSettings.selectedProperty().addListener((observable,oldValue,newValue) -> {
			if (newValue) {
				cbLocalMetadata.setSelected(false);
				cbCredentials.setSelected(false);
			}
		});
		cbLocalMetadata.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue) {
				if(cbGitHubMetadata.isSelected()) {
					metadataWarning();
				}
			}
		});
		cbGitHubMetadata.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue) {
				if(cbLocalMetadata.isSelected()) {
					metadataWarning();
				}
			}
		});
		cbLocalMetadata.disableProperty().bind(cbSettings.selectedProperty());
		cbCredentials.disableProperty().bind(cbSettings.selectedProperty());
		button.setOnAction(e -> performReset(cbDatabase.isSelected(), cbSettings.isSelected(), cbCredentials.isSelected(), cbLocalMetadata.isSelected(), cbGitHubMetadata.isSelected()));
		ToolWindow toolWindow = new ToolWindow.Builder(vbox).title("Master Reset").size(width,height).addButton(button).setSceneId(sceneId).build();
		toolWindow.showAndWait();
	}

	private void metadataWarning() {
		String message = """
				You have chosen to wipe out both the local metadata
				AND the GitHub metadata. Doing this will DELETE the
				following:
				
				   - ALL of your categories.
				   - All of your Gist custom names
				   - All of your file descriptions 
				""";
		CustomAlert.showInfo(message,SceneOne.getOwner(sceneId));
	}

	private HBox centeredHBox(Node node) {
		HBox hbox = new HBox(node);
		hbox.setPadding(new Insets(10,10,10,10));
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	private void performReset(boolean database, boolean appSettings, boolean credentials, boolean localMetadata, boolean gitHubMetadata) {
		if (!database && !appSettings && !credentials && !localMetadata && !gitHubMetadata) {
			CustomAlert.showInfo("No changes will be made.", SceneOne.getOwner(sceneId));
			SceneOne.close(sceneId);
			System.exit(11);
		}
		StringBuilder options = new StringBuilder("You have chosen to perform the following actions:\n\n");
		if (database) options.append("- Delete and re-create database\n\n");
		if (appSettings) options.append("- Reset all app settings to default\n\n");
		if (credentials) options.append("- Wipe out local credentials\n\n");
		if (localMetadata && gitHubMetadata) options.append("- Wipe out ALL Metadata, which will DELETE:\n\t- ALL Categories\n\t- ALL Gist Names\n\t- ALL File Descriptions\n");
		else {
			if (localMetadata) options.append("- Wipe out the LOCAL copy of your metadata\n\twhich will DELETE the local version of:\n\t- Categories\n\t- Gist Names\n\t- File Descriptions\n");
			if (gitHubMetadata) options.append("- Wipe out the GitHub stored copy of your metadata\n\twhich will DELETE GitHub version of:\n\t- Categories\n\t- Gist Names\n\t- File Descriptions\n");
		}
		options.append("\nAre you sure this is what you want to do?");
		if (CustomAlert.showConfirmation(options.toString())) {
			Label label = new Label("Working ...");
			label.setMinWidth(300);
			label.setMinHeight(200);
			label.setAlignment(Pos.CENTER);
			label.setId("masterReset");
			VBox vbox = new VBox(label);
			SceneOne.close();
			SceneOne.set(vbox).centered().modality(Modality.APPLICATION_MODAL).show();
			new Thread(() -> {
				if (database) Action.deleteDatabaseFile();
				if (localMetadata) Action.deleteLocalMetaData(!database);
				if (appSettings) AppSettings.clear().clearAll();
				if (credentials) AppSettings.resetCredentials();
				if (gitHubMetadata) Action.deleteGitHubMetadata();
				Platform.runLater(() -> {
					CustomAlert.showInfo("It is done!",SceneOne.getOwner());
					SceneOne.close(sceneId);
					System.exit(11);
				});
			}).start();
		}
		else {
			CustomAlert.showInfo("No changes will be made.", SceneOne.getOwner(sceneId));
			System.exit(11);
		}
	}

	private String getMessage() {
		return "Check the options that you would like to reset, then press GO!";
	}
}
