package com.redmondsims.gistfx.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class LoginFormGUI extends GridPane {

	protected final ColumnConstraints columnConstraints;
	protected final ColumnConstraints columnConstraints0;
	protected final ColumnConstraints columnConstraints1;
	protected final ColumnConstraints columnConstraints2;
	protected final RowConstraints rowConstraints;
	protected final RowConstraints rowConstraints0;
	protected final RowConstraints rowConstraints1;
	protected final RowConstraints rowConstraints2;
	protected final Label lblToken;
	protected final TextField tfToken;
	protected final Label lblPassword;
	protected final PasswordField tfPassword;
	protected final TextField tfInfo;
	protected final ProgressBar pBar;

	public LoginFormGUI() {

		columnConstraints = new ColumnConstraints();
		columnConstraints0 = new ColumnConstraints();
		columnConstraints1 = new ColumnConstraints();
		columnConstraints2 = new ColumnConstraints();
		rowConstraints = new RowConstraints();
		rowConstraints0 = new RowConstraints();
		rowConstraints1 = new RowConstraints();
		rowConstraints2 = new RowConstraints();
		lblToken = new Label();
		tfToken = new TextField();
		lblPassword = new Label();
		tfPassword = new PasswordField();
		tfInfo = new TextField();
		pBar = new ProgressBar();

		setMaxHeight(USE_PREF_SIZE);
		setMaxWidth(USE_PREF_SIZE);
		setMinHeight(USE_PREF_SIZE);
		setMinWidth(USE_PREF_SIZE);
		setPrefHeight(400.0);
		setPrefWidth(600.0);

		columnConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints.setMaxWidth(250.0);
		columnConstraints.setMinWidth(10.0);
		columnConstraints.setPrefWidth(189.0);

		columnConstraints0.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints0.setMaxWidth(146.0);
		columnConstraints0.setMinWidth(10.0);
		columnConstraints0.setPrefWidth(93.0);

		columnConstraints1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints1.setMaxWidth(262.0);
		columnConstraints1.setMinWidth(10.0);
		columnConstraints1.setPrefWidth(252.0);

		columnConstraints2.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints2.setMaxWidth(107.0);
		columnConstraints2.setMinWidth(10.0);
		columnConstraints2.setPrefWidth(64.0);

		rowConstraints.setMaxHeight(194.0);
		rowConstraints.setMinHeight(10.0);
		rowConstraints.setPrefHeight(114.0);
		rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints0.setMaxHeight(194.0);
		rowConstraints0.setMinHeight(10.0);
		rowConstraints0.setPrefHeight(92.0);
		rowConstraints0.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints1.setMaxHeight(239.0);
		rowConstraints1.setMinHeight(10.0);
		rowConstraints1.setPrefHeight(66.0);
		rowConstraints1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints2.setMaxHeight(169.0);
		rowConstraints2.setMinHeight(10.0);
		rowConstraints2.setPrefHeight(128.0);
		rowConstraints2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		GridPane.setColumnIndex(lblToken, 1);
		GridPane.setHalignment(lblToken, javafx.geometry.HPos.RIGHT);
		GridPane.setRowIndex(lblToken, 1);
		lblToken.setText("Access Token");
		GridPane.setMargin(lblToken, new Insets(0.0, 10.0, 0.0, 0.0));

		GridPane.setColumnIndex(tfToken, 2);
		GridPane.setRowIndex(tfToken, 1);

		GridPane.setColumnIndex(lblPassword, 1);
		GridPane.setHalignment(lblPassword, javafx.geometry.HPos.RIGHT);
		GridPane.setRowIndex(lblPassword, 2);
		lblPassword.setText("Password");
		GridPane.setMargin(lblPassword, new Insets(0.0, 10.0, 0.0, 0.0));

		GridPane.setColumnIndex(tfPassword, 2);
		GridPane.setRowIndex(tfPassword, 2);

		GridPane.setRowSpan(tfInfo, 3);
		tfInfo.setEditable(false);
		tfInfo.setId("InfoBox");
		tfInfo.setPrefHeight(281.0);
		tfInfo.setPrefWidth(215.0);

		GridPane.setColumnSpan(pBar, 4);
		GridPane.setRowIndex(pBar, 3);
		GridPane.setValignment(pBar, javafx.geometry.VPos.BOTTOM);
		pBar.setPrefHeight(20.0);
		pBar.setPrefWidth(602.0);
		pBar.setProgress(0.0);
		GridPane.setMargin(pBar, new Insets(0.0, 0.0, 20.0, 0.0));

		getColumnConstraints().add(columnConstraints);
		getColumnConstraints().add(columnConstraints0);
		getColumnConstraints().add(columnConstraints1);
		getColumnConstraints().add(columnConstraints2);
		getRowConstraints().add(rowConstraints);
		getRowConstraints().add(rowConstraints0);
		getRowConstraints().add(rowConstraints1);
		getRowConstraints().add(rowConstraints2);
		getChildren().add(lblToken);
		getChildren().add(tfToken);
		getChildren().add(lblPassword);
		getChildren().add(tfPassword);
		getChildren().add(tfInfo);
		getChildren().add(pBar);

	}
}
