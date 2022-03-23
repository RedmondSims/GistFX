package com.redmondsims.gistfx.preferences.mail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.utils.Util;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class ServerSettings {

	private enum BOX {
		CATEGORY,
		GIST,
		FILE;

		private static final Map<BOX,Integer> boxItems = new HashMap<>();

		public static void set(BOX box, Integer items) {
			boxItems.remove(box);
			boxItems.put(box,items);
		}

		public static Integer getTotalItems() {
			Integer total = 0;
			for (Integer count : boxItems.values()) {
				total += count;
			}
			return total;
		}

	}

	private static final String     name               = "MailServerSettings";
	private static final Gson       gson               = new GsonBuilder().setPrettyPrinting().create();
	private static final double     nodeHeight         = 30;
	private static       double     sceneHeight;
	private static final double     choseButtonSpacing = 190;
	private static final double     sceneWidth         = 400;
	private static final VBox       formContent        = newVBox(20);
	private static       ToolWindow toolWindow;

	private static Label newLabelTypeOne(String text) {
		return newLabel(text, "SettingsOne");
	}

	private static Label newLabelTypeTwo(String text) {
		return newLabel(text, "SettingsTwo");
	}

	private static Label newLabel(String text, String labelId) {
		Label label = new Label(text);
		label.setMinWidth(155);
		label.setAlignment(Pos.CENTER_LEFT);
		label.setId(labelId);
		label.setPrefHeight(nodeHeight);
		return label;
	}

	private static VBox newVBox(double spacing, Node... nodes) {
		VBox vbox = new VBox(nodes);
		vbox.setSpacing(spacing);
		vbox.setPrefHeight(nodeHeight * nodes.length);
		return vbox;
	}

	private static HBox newHBox(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(20);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static HBox newHBox(double spacing, Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(spacing);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static Label newBlank() {
		return new Label("       ");
	}

	private static HBox newHBox(Pos position, double spacing, Node... nodes) {
		HBox hbox = newHBox(spacing, nodes);
		hbox.setAlignment(position);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static HBox getSpacedHBoxRight(Node node, double space) {
		Label dummy = new Label(" ");
		dummy.setMinWidth(space);
		HBox hbox = new HBox(dummy,node);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static Node theme(Scene callingScene) {
		return UISettings.Theme.getNode(callingScene);
	}

	private static VBox getSettingsVBox() {
		ChoiceBox<String> cbServerType      = new ChoiceBox<>(FXCollections.observableArrayList("GMail", "Yahoo", "Outlook.com", "Other"));
		Label             lblChoice         = newLabelTypeOne("Server Type");
		Label             lblSMTPServer     = newLabelTypeOne("SMTP Server");
		Label             lblName           = newLabelTypeOne("Display Name");
		Label             lblUserName       = newLabelTypeOne("User Name");
		Label             lblPassword       = newLabelTypeOne("Password");
		Label             lblConfirm        = newLabelTypeOne("Confirm");
		Label             lblTLSPort        = newLabelTypeOne("TLS Port");
		Label             lblSSLPort        = newLabelTypeOne("SSL Port");
		Label             lblRequireTLS     = newLabelTypeOne("Requires TLS");
		Label             lblRequireSSL     = newLabelTypeOne("Requires SSL");
		Label             lblRequireAuth    = newLabelTypeOne("Requires Authentication");
		Label             lblRequireSecure  = newLabelTypeOne("Requires Secure Connection");
		Label blank1 = newBlank();
		Label blank2 = newBlank();
		Label blank3 = newBlank();
		Label blank4 = newBlank();
		lblChoice.setAlignment(Pos.CENTER_RIGHT);
		lblSMTPServer.setAlignment(Pos.CENTER_RIGHT);
		lblName.setAlignment(Pos.CENTER_RIGHT);
		lblUserName.setAlignment(Pos.CENTER_RIGHT);
		lblPassword.setAlignment(Pos.CENTER_RIGHT);
		lblConfirm.setAlignment(Pos.CENTER_RIGHT);
		lblTLSPort.setAlignment(Pos.CENTER_RIGHT);
		lblSSLPort.setAlignment(Pos.CENTER_RIGHT);
		lblRequireTLS.setAlignment(Pos.CENTER_LEFT);
		lblRequireSSL.setAlignment(Pos.CENTER_LEFT);
		lblRequireAuth.setAlignment(Pos.CENTER_LEFT);
		lblRequireSecure.setAlignment(Pos.CENTER_LEFT);
		lblChoice.setMinWidth(95);
		lblSMTPServer.setMinWidth(95);
		lblName.setMinWidth(95);
		lblUserName.setMinWidth(95);
		lblPassword.setMinWidth(95);
		blank1.setMinWidth(115);
		blank2.setMinWidth(115);
		blank3.setMinWidth(115);
		blank4.setMinWidth(115);
		lblConfirm.setMinWidth(95);
		lblTLSPort.setMinWidth(95);
		lblSSLPort.setMinWidth(95);
		TextField         tfSMTPServer      = new TextField();
		TextField         tfName            = new TextField();
		TextField         tfUserName        = new TextField();
		PasswordField     tfPassword        = new PasswordField();
		PasswordField     tfConfirm         = new PasswordField();
		TextField         tfTLSPort         = new TextField();
		TextField         tfSSLPort         = new TextField();
		CheckBox          cbRequiresTLS     = new CheckBox();
		CheckBox          cbRequiresSSL     = new CheckBox();
		CheckBox          cbRequiresAuth    = new CheckBox();
		CheckBox          cbRequiresSecure  = new CheckBox();
		Button            btnSet            = new Button("Set");
		tfSMTPServer.setMinWidth(sceneWidth-115);
		tfName.setMinWidth(sceneWidth-115);
		tfUserName.setMinWidth(sceneWidth-115);
		tfPassword.setMinWidth(sceneWidth-115);
		tfConfirm.setMinWidth(sceneWidth-115);
		tfTLSPort.setMaxWidth(55);
		tfSSLPort.setMaxWidth(55);
		HBox              boxServerType     = newHBox(10, lblChoice, cbServerType);
		HBox              boxSMTPServer     = newHBox(Pos.CENTER_LEFT,10, lblSMTPServer, tfSMTPServer);
		HBox              boxName           = newHBox(10, lblName, tfName);
		HBox              boxUserName       = newHBox(10, lblUserName, tfUserName);
		HBox              boxPassword       = newHBox(10, lblPassword, tfPassword);
		HBox              boxConfirm        = newHBox(10, lblConfirm, tfConfirm);
		HBox              boxTLSPort        = newHBox(10, lblTLSPort, tfTLSPort);
		HBox              boxSSLPort        = newHBox(10, lblSSLPort, tfSSLPort);
		HBox              boxRequiresTLS    = newHBox(10, blank1, cbRequiresTLS, lblRequireTLS);
		HBox              boxRequiresSSL    = newHBox(10, blank2, cbRequiresSSL, lblRequireSSL);
		HBox              boxRequiresAuth   = newHBox(10, blank3, cbRequiresAuth, lblRequireAuth);
		HBox              boxRequiresSecure = newHBox(10, blank4, cbRequiresSecure, lblRequireSecure);
		HBox              boxButtons        = newHBox(Pos.CENTER,20, btnSet);
		VBox settingsVBox = newVBox(12,
									boxServerType,
									boxSMTPServer,
									boxName,
									boxUserName,
									boxPassword,
									boxConfirm,
									boxTLSPort,
									boxSSLPort,
									boxRequiresTLS,
									boxRequiresSSL,
									boxRequiresAuth,
									boxRequiresSecure,
									boxButtons
		);
		Tooltip.install(tfSMTPServer, Action.newTooltip("ex: smtp.gmail.com"));
		Tooltip.install(tfName, Action.newTooltip("Name to be displayed, not your network address"));
		Tooltip.install(tfUserName, Action.newTooltip("Usually your network address on the server"));
		Tooltip.install(tfPassword, Action.newTooltip("Your account password"));
		Tooltip.install(tfConfirm, Action.newTooltip("re-type password for verification"));
		Tooltip.install(tfTLSPort, Action.newTooltip("a number, ex: 587"));
		Tooltip.install(tfSSLPort, Action.newTooltip("a number, ex: 443"));
		btnSet.setOnAction(e -> {
			String  smtpServer     = tfSMTPServer.getText();
			String  name           = tfName.getText();
			String  userName       = tfUserName.getText();
			String  password       = tfPassword.getText();
			String  confirm        = tfConfirm.getText();
			Integer tlsPort        = Integer.parseInt(tfTLSPort.getText());
			Integer sslPort        = Integer.parseInt(tfSSLPort.getText());
			boolean requiresTLS    = cbRequiresTLS.isSelected();
			boolean requiresSSL    = cbRequiresSSL.isSelected();
			boolean requiresAuth   = cbRequiresAuth.isSelected();
			boolean requiresSecure = cbRequiresSecure.isSelected();
			if (password.equals(confirm)) {
				SMTPServerSettingsa smtpServerSettings = new SMTPServerSettingsa(
						smtpServer,
						name,
						userName,
						password,
						tlsPort,
						sslPort,
						requiresTLS,
						requiresSSL,
						requiresAuth,
						requiresSecure);
				String jsonString = gson.toJson(smtpServerSettings);
				AppSettings.set().mailServer(jsonString);
			}
			else {
				CustomAlert.showWarning("Passwords do not match.");
				tfPassword.requestFocus();
				tfPassword.selectAll();
			}
		});
		cbServerType.setOnAction(e -> {
			String option = cbServerType.getValue();
			switch (option) {
				case "GMail" -> {
					tfSMTPServer.setText("smtp.gmail.com");
					tfName.setText("");
					tfPassword.setText("");
					tfConfirm.setText("");
					tfTLSPort.setText("587");
					tfSSLPort.setText("465");
					cbRequiresTLS.setSelected(true);
					cbRequiresSSL.setSelected(true);
					cbRequiresAuth.setSelected(true);
					cbRequiresSecure.setSelected(true);
					tfName.requestFocus();
					tfName.selectAll();
				}

				case "Yahoo" -> {
					tfSMTPServer.setText("smtp.mail.yahoo.com");
					tfName.setText("");
					tfPassword.setText("");
					tfConfirm.setText("");
					tfTLSPort.setText("587");
					tfSSLPort.setText("465");
					cbRequiresTLS.setSelected(true);
					cbRequiresSSL.setSelected(true);
					cbRequiresAuth.setSelected(true);
					cbRequiresSecure.setSelected(true);
					tfName.requestFocus();
					tfName.selectAll();
				}
				case "Outlook.com" -> {
					tfSMTPServer.setText("smtp.office365.com");
					tfName.setText("");
					tfPassword.setText("");
					tfConfirm.setText("");
					tfTLSPort.setText("587");
					tfSSLPort.setText("465");
					cbRequiresTLS.setSelected(true);
					cbRequiresSSL.setSelected(true);
					cbRequiresAuth.setSelected(true);
					cbRequiresSecure.setSelected(true);
					tfName.requestFocus();
					tfName.selectAll();
				}
				default -> {
					tfSMTPServer.setText("");
					tfName.setText("");
					tfPassword.setText("");
					tfConfirm.setText("");
					tfTLSPort.setText("");
					tfSSLPort.setText("");
					cbRequiresTLS.setSelected(false);
					cbRequiresSSL.setSelected(false);
					cbRequiresAuth.setSelected(false);
					cbRequiresSecure.setSelected(false);
					tfSMTPServer.requestFocus();
					tfSMTPServer.selectAll();
				}
			}
		});
		setBox(BOX.CATEGORY, 15);
		return settingsVBox;
	}

	private static void setBox(BOX box, int count) {
		int sizePer = 35;
		BOX.set(box,count);
		double newHeight = BOX.getTotalItems() * sizePer;
		double offset = Util.reMap(newHeight, 210, 315, 70, 50, 2);
		sceneHeight = newHeight + offset;
		if(toolWindow != null) toolWindow.resizeHeight(sceneHeight);
	}

	public static void showWindow(Stage callingStage) {
		formContent.getChildren().setAll(getSettingsVBox());
		formContent.setPadding(new Insets(2, 2, 2, 2));
		formContent.setAlignment(Pos.CENTER_LEFT);
		toolWindow = new ToolWindow.Builder(formContent).size(sceneWidth, sceneHeight).attachToStage(callingStage).title("Mail Server SMTPServerSettingsa").build();
		toolWindow.showAndWait();
	}
}
