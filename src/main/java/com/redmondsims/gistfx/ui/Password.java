package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.cryptology.Crypto;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Password {

	private static boolean oldPasswordOK = false;
	private static boolean passwordMatches = false;
	private static boolean passwordChanged = false;

	private static HBox newHBox(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(5,5,5,5));
		hbox.setAlignment(Pos.CENTER_LEFT);
		return hbox;
	}

	private static VBox newVBox(Node... nodes) {
		VBox vbox = new VBox(nodes);
		vbox.setSpacing(1);
		vbox.setPadding(new Insets(5,5,5,5));
		vbox.setAlignment(Pos.CENTER_LEFT);
		return vbox;
	}

	private static Label newLabel(String text) {
		double lblWidth = 115;
		Label label = new Label(text);
		label.setAlignment(Pos.CENTER_RIGHT);
		label.setPadding(new Insets(5,5,5,5));
		label.setMinWidth(lblWidth);
		label.setMaxWidth(lblWidth);
		label.setPrefWidth(lblWidth);
		return label;
	}

	private static Label newLabel(String text, double width) {
		Label label = new Label(text);
		label.setAlignment(Pos.CENTER_RIGHT);
		label.setPadding(new Insets(5,5,5,5));
		label.setMinWidth(width);
		label.setMaxWidth(width);
		label.setPrefWidth(width);
		return label;
	}

	private static PasswordField newPasswordField() {
		PasswordField passwordField = new PasswordField();
		passwordField.setMinWidth(175);
		passwordField.setAlignment(Pos.CENTER_LEFT);
		return passwordField;
	}


	private static PasswordField tfOldPassword;
	private static PasswordField tfNewPassword;
	private static PasswordField tfNewPasswordV;

	public static boolean change(Stage stage) {
		passwordChanged = false;
		oldPasswordOK = false;
		double width = 350;
		double height = 250;
		String        sceneId         = "ChangePassword";
		Label         lblOldPassword  = newLabel("Current Password");
		Label         lblNewPassword  = newLabel("New Password");
		Label         lblNewPasswordV = newLabel("New Password");
		tfOldPassword   = newPasswordField();
		tfNewPassword   = newPasswordField();
		tfNewPasswordV  = newPasswordField();
		Button btnSet = new Button("Set Password");
		HBox boxOld = newHBox(lblOldPassword,tfOldPassword);
		HBox boxNew = newHBox(lblNewPassword,tfNewPassword);
		HBox boxNewV = newHBox(lblNewPasswordV,tfNewPasswordV);
		VBox vbox = newVBox(boxOld,boxNew,boxNewV);
		btnSet.setOnAction(e-> {
			if(changePassword()) SceneOne.close(sceneId);
		});
		tfOldPassword.setOnAction(e-> {
			if(changePassword()) {
				passwordChanged = true;
				SceneOne.close(sceneId);
			}
		});
		tfNewPassword.setOnAction(e-> {
			if(changePassword()) {
				passwordChanged = true;
				SceneOne.close(sceneId);
			}
		});
		tfNewPasswordV.setOnAction(e-> {
			if(changePassword()) {
				passwordChanged = true;
				SceneOne.close(sceneId);
			}
		});
		ToolWindow toolWindow = new ToolWindow.Builder(vbox)
				.setSceneId(sceneId)
				.attachToStage(stage)
				.size(width,height)
				.title("change Password")
				.addButton(btnSet)
				.build();
		toolWindow.showAndWait();
		return passwordChanged;
	}

	private static boolean changePassword() {
		oldPasswordOK = false;
		String hashedPassword = AppSettings.get().hashedPassword();
		String oldPassword    = tfOldPassword.getText();
		String newPassword    = tfNewPassword.getText();
		String newPasswordV   = tfNewPasswordV.getText();
		if (!oldPasswordOK) {
			oldPasswordOK = Crypto.validatePassword(oldPassword, hashedPassword);
			if (!oldPasswordOK) {
				CustomAlert.showWarning("Current password is incorrect.");
				tfOldPassword.setText("");
				tfOldPassword.requestFocus();
				return false;
			}
		}
		if (newPassword.length() < 5) {
			CustomAlert.showWarning("New Password is too short.\nMust be at least 5 characters.");
			tfNewPassword.setText("");
			tfNewPasswordV.setText("");
			tfNewPassword.requestFocus();
			return false;
		}
		if (!newPassword.equals(newPasswordV)) {
			CustomAlert.showWarning("The New Passwords do not match.");
			tfNewPassword.setText("");
			tfNewPasswordV.setText("");
			tfNewPassword.requestFocus();
			return false;
		}
		else {
			if(Action.hasData()) {
				if (!Action.reEncryptData(oldPassword,newPassword))
					return false;
			}
			String passwordHash = Crypto.hashPassword(newPassword);
			AppSettings.set().hashedPassword(passwordHash);
			String token = Crypto.decryptWithPassword(AppSettings.get().hashedToken(),oldPassword);
			String tokenHash = Crypto.encryptWithPassword(token,newPassword);
			AppSettings.set().hashedToken(tokenHash);
			return true;
		}
	}

	public static boolean verify(String password, Stage parentStage) {
		passwordMatches = false;
		double width = 325;
		double height = 105;
		String sceneId = "MatchPassword";
		Label lblVerify = newLabel("Verify Password",100);
		PasswordField tfVerify = newPasswordField();
		tfVerify.getStylesheets().add(UISettings.Theme.TEXT_FIELD.getStyleSheet());
		Button btnVerify = new Button("Verify");
		HBox pad1 = newHBox(new Label("    "));
		HBox pad2 = newHBox(new Label(""));
		HBox boxVerify = newHBox(lblVerify,tfVerify);
		VBox vbox = newVBox(pad1,boxVerify);
		AnchorPane ap = new AnchorPane(vbox);
		ap.getStylesheets().add(UISettings.Theme.ANCHOR_PANE.getStyleSheet());
		switch(AppSettings.get().loginScreenColor()) {
			case RED -> {
				ap.setId("red");
				tfVerify.setId("red");
				lblVerify.setStyle("-fx-text-fill:rgb(255,95,95)");
			}
			case GREEN -> {
				ap.setId("green");
				tfVerify.setId("green");
				lblVerify.setStyle("-fx-text-fill:rgb(0,50,53)");
			}
			case BLUE -> {
				ap.setId("blue");
				tfVerify.setId("blue");
				lblVerify.setStyle("-fx-text-fill:rgb(190,190,255)");
			}
			case YELLOW -> {
				ap.setId("yellow");
				tfVerify.setId("yellow");
				lblVerify.setStyle("-fx-text-fill:black");
			}
			case HOTPINK -> {
				ap.setId("hotpink");
				tfVerify.setId("hotpink");
				lblVerify.setStyle("-fx-text-fill:rgb(255,155,255)");
			}
		}
		ap.setPrefSize(width,height);
		btnVerify.setOnAction(e->{
			checkForMatch(tfVerify.getText(),password);
			SceneOne.close(sceneId);
		});
		tfVerify.setOnAction(e->{
			checkForMatch(tfVerify.getText(),password);
			SceneOne.close(sceneId);
		});
		final CBooleanProperty responded = new CBooleanProperty(false);
		final ToolWindow toolWindow = new ToolWindow.Builder(ap)
				.attachToStage(parentStage)
				.setSceneId(sceneId)
				.transparentStyle()
				.noButtons()
				.size(width,height)
				.title("Verify Password")
				.build();
		Platform.runLater(() -> {
			toolWindow.showAndWait();
			responded.setTrue();
		});
		while (responded.isFalse()) {
			Action.sleep(100);
		}
		return passwordMatches;
	}

	private static void checkForMatch(String password1, String password2) {
		passwordMatches = password1.equals(password2);
		if(!passwordMatches) {
			CustomAlert.showWarning("Passwords Do Not Match");
		}
	}

}
