package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.alerts.CustomAlert;
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

	public MasterResetWindow () {
		startWindow();
	}

	private void startWindow() {
		Label    lblTitle   = new Label("Master Reset");
		lblTitle.setId("masterReset");
		Label    lblMessage = new Label(getMessage());
		CheckBox cbDatabase = new CheckBox("Reset Database (wipe out and create new)");
		CheckBox cbSettings = new CheckBox("Reset all app settings to default");
		CheckBox cbCredentials = new CheckBox("Reset Login Credentials");
		CheckBox cbLocalMetadata = new CheckBox("Wipe out Local Metadata");
		CheckBox cbGitHubCustomNames = new CheckBox("Wipe out GitHub Custom Names");
		Button button = new Button("GO");
		VBox vbox = new VBox(centeredHBox(lblTitle),lblMessage,cbDatabase, cbSettings,cbCredentials,cbLocalMetadata,cbGitHubCustomNames,centeredHBox(button));
		vbox.setPadding(new Insets(5,5,5,5));
		vbox.setSpacing(8);
		SceneOne.set(vbox).centered().modality(Modality.APPLICATION_MODAL).show();
		button.setOnAction(e -> performReset(cbDatabase.isSelected(), cbSettings.isSelected(), cbCredentials.isSelected(), cbLocalMetadata.isSelected(), cbGitHubCustomNames.isSelected()));
	}

	private HBox centeredHBox(Node node) {
		HBox hbox = new HBox(node);
		hbox.setPadding(new Insets(10,10,10,10));
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	private void performReset(boolean database, boolean appSettings, boolean credentials, boolean localMetadata, boolean gitHubNames) {
		if (!database && !appSettings && !credentials && !localMetadata && !gitHubNames) {
			Platform.runLater(() -> {
				CustomAlert.showInfo("No changes will be made.",SceneOne.getOwner());
				System.exit(11);
			});
		}
		StringBuilder options = new StringBuilder("You have chosen to perform the following actions:\n\n");
		if (database) options.append("- Delete and re-create database\n");
		if (appSettings) options.append("- Reset all app settings to default\n");
		if (credentials) options.append("- Wipe out local credentials\n");
		if (localMetadata) options.append("- Wipe out the locally stored copy of your metadata (custom names, categories and file descriptions)\n");
		if (gitHubNames) options.append("- Wipe out the GitHub stored copy of your metadata (custom names, categories and file descriptions)\n");
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
				if (gitHubNames) Action.deleteJsonGistFile();
				Platform.runLater(() -> {
					CustomAlert.showInfo("It is done!",SceneOne.getOwner());
					System.exit(11);
				});
			}).start();
		}
		else {
			Platform.runLater(() -> CustomAlert.showInfo("No changes will be made.", SceneOne.getOwner()));
			System.exit(11);
		}
	}

	private String getMessage() {
		return "Check the options that you would like to reset, then press GO!";
	}
}
