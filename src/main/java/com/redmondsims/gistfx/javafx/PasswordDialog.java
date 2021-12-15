package com.redmondsims.gistfx.javafx;

import com.redmondsims.gistfx.ui.enums.Response;
import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.redmondsims.gistfx.ui.enums.Response.*;

public class PasswordDialog {

	Response thisResponse;

	private void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Response ConfirmPasswordYesNoCancel(String passwordToConfirm, Window owner) {
		thisResponse = WAITING;
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.NONE);
			alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.theme.getStyleSheet());
			PasswordField tfPassword = new PasswordField();
			alert.getDialogPane().getButtonTypes().clear();
			alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			Label    label1 = new Label("Re-type your password to confirm");
			Label    lblPW1 = new Label("Password");
			GridPane layout = new GridPane();
			layout.setPadding(new Insets(10, 10, 10, 10));
			layout.setVgap(5);
			layout.setHgap(5);
			layout.add(label1, 1, 0);
			layout.add(lblPW1, 0, 1);
			layout.add(tfPassword, 1, 1, 2, 1);
			alert.getDialogPane().setContent(layout);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent()) {
				if (result.get().equals(ButtonType.OK)) {
					if (tfPassword.getText().equals(passwordToConfirm)) {thisResponse = YES;}
					else {thisResponse = NO;}
				}
				if (result.get().equals(ButtonType.CANCEL)) {
					thisResponse = CANCELED;
				}
			}
		});
		while (thisResponse == WAITING) {
			sleep(100);
		}
		return thisResponse;
	}
}
