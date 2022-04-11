package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;


public class TrippleTabs extends TabPane {

	protected final Tab tab;
	protected final GridPane gridPane;
	protected final ColumnConstraints columnConstraints;
	protected final ColumnConstraints columnConstraints0;
	protected final ColumnConstraints columnConstraints1;
	protected final RowConstraints rowConstraints;
	protected final RowConstraints rowConstraints0;
	protected final RowConstraints rowConstraints1;
	protected final RowConstraints rowConstraints2;
	protected final CheckBox checkBox;
	protected final Button button;
	protected final ImageView imageView;
	protected final Label label;
	protected final ColorPicker colorPicker;
	protected final Button button0;
	protected final Button button1;
	protected final Tab tab0;
	protected final GridPane gridPane0;
	protected final ColumnConstraints columnConstraints2;
	protected final ColumnConstraints columnConstraints3;
	protected final ColumnConstraints columnConstraints4;
	protected final RowConstraints rowConstraints3;
	protected final RowConstraints rowConstraints4;
	protected final RowConstraints rowConstraints5;
	protected final RowConstraints rowConstraints6;
	protected final CheckBox checkBox0;
	protected final Button button2;
	protected final ImageView imageView0;
	protected final Label label0;
	protected final ColorPicker colorPicker0;
	protected final Button button3;
	protected final Button button4;
	protected final Tab tab1;
	protected final GridPane gridPane1;
	protected final ColumnConstraints columnConstraints5;
	protected final ColumnConstraints columnConstraints6;
	protected final ColumnConstraints columnConstraints7;
	protected final RowConstraints rowConstraints7;
	protected final RowConstraints rowConstraints8;
	protected final RowConstraints rowConstraints9;
	protected final RowConstraints rowConstraints10;
	protected final CheckBox checkBox1;
	protected final Button button5;
	protected final ImageView imageView1;
	protected final Label label1;
	protected final ColorPicker colorPicker1;
	protected final Button button6;
	protected final Button button7;
	protected final Label label2;
	protected final ColorPicker colorPicker2;

	public TrippleTabs() {

		tab = new Tab();
		gridPane = new GridPane();
		columnConstraints = new ColumnConstraints();
		columnConstraints0 = new ColumnConstraints();
		columnConstraints1 = new ColumnConstraints();
		rowConstraints = new RowConstraints();
		rowConstraints0 = new RowConstraints();
		rowConstraints1 = new RowConstraints();
		rowConstraints2 = new RowConstraints();
		checkBox = new CheckBox();
		button = new Button();
		imageView = new ImageView();
		label = new Label();
		colorPicker = new ColorPicker();
		button0 = new Button();
		button1 = new Button();
		tab0 = new Tab();
		gridPane0 = new GridPane();
		columnConstraints2 = new ColumnConstraints();
		columnConstraints3 = new ColumnConstraints();
		columnConstraints4 = new ColumnConstraints();
		rowConstraints3 = new RowConstraints();
		rowConstraints4 = new RowConstraints();
		rowConstraints5 = new RowConstraints();
		rowConstraints6 = new RowConstraints();
		checkBox0 = new CheckBox();
		button2 = new Button();
		imageView0 = new ImageView();
		label0 = new Label();
		colorPicker0 = new ColorPicker();
		button3 = new Button();
		button4 = new Button();
		tab1 = new Tab();
		gridPane1 = new GridPane();
		columnConstraints5 = new ColumnConstraints();
		columnConstraints6 = new ColumnConstraints();
		columnConstraints7 = new ColumnConstraints();
		rowConstraints7 = new RowConstraints();
		rowConstraints8 = new RowConstraints();
		rowConstraints9 = new RowConstraints();
		rowConstraints10 = new RowConstraints();
		checkBox1 = new CheckBox();
		button5 = new Button();
		imageView1 = new ImageView();
		label1 = new Label();
		colorPicker1 = new ColorPicker();
		button6 = new Button();
		button7 = new Button();
		label2 = new Label();
		colorPicker2 = new ColorPicker();

		setMaxHeight(USE_PREF_SIZE);
		setMaxWidth(USE_PREF_SIZE);
		setMinHeight(USE_PREF_SIZE);
		setMinWidth(USE_PREF_SIZE);
		setPrefHeight(273.0);
		setPrefWidth(600.0);
		setTabClosingPolicy(javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE);

		tab.setClosable(false);
		tab.setText("Category");

		columnConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints.setMaxWidth(252.0);
		columnConstraints.setMinWidth(10.0);
		columnConstraints.setPrefWidth(230.0);

		columnConstraints0.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints0.setMaxWidth(300.0);
		columnConstraints0.setMinWidth(10.0);
		columnConstraints0.setPrefWidth(129.0);

		columnConstraints1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints1.setMaxWidth(240.0);
		columnConstraints1.setMinWidth(10.0);
		columnConstraints1.setPrefWidth(240.0);

		rowConstraints.setMaxHeight(88.0);
		rowConstraints.setMinHeight(10.0);
		rowConstraints.setPrefHeight(58.0);
		rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints0.setMaxHeight(119.0);
		rowConstraints0.setMinHeight(10.0);
		rowConstraints0.setPrefHeight(48.0);
		rowConstraints0.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints1.setMaxHeight(202.0);
		rowConstraints1.setMinHeight(10.0);
		rowConstraints1.setPrefHeight(53.0);
		rowConstraints1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints2.setMaxHeight(246.0);
		rowConstraints2.setMinHeight(10.0);
		rowConstraints2.setPrefHeight(101.0);
		rowConstraints2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		checkBox.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
		checkBox.setMnemonicParsing(false);
		checkBox.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
		checkBox.setPrefHeight(18.0);
		checkBox.setPrefWidth(230.0);
		checkBox.setText("Use Custom Category Folder Icon?");

		GridPane.setMargin(checkBox, new Insets(0.0, 5.0, 0.0, 0.0));
		GridPane.setColumnIndex(button, 1);
		GridPane.setHalignment(button, javafx.geometry.HPos.CENTER);
		button.setMnemonicParsing(false);
		button.setPrefHeight(26.0);
		button.setPrefWidth(110.0);
		button.setText("Choose File");
		GridPane.setMargin(button, new Insets(0.0));

		GridPane.setColumnIndex(imageView, 2);
		GridPane.setHalignment(imageView, javafx.geometry.HPos.CENTER);
		GridPane.setValignment(imageView, javafx.geometry.VPos.CENTER);
		imageView.setFitHeight(82.0);
		imageView.setFitWidth(121.0);
		imageView.setPickOnBounds(true);
		imageView.setPreserveRatio(true);

		GridPane.setHalignment(label, javafx.geometry.HPos.RIGHT);
		GridPane.setRowIndex(label, 1);
		label.setText("Choose Category Folder Color");
		GridPane.setMargin(label, new Insets(0.0, 15.0, 0.0, 0.0));

		GridPane.setColumnIndex(colorPicker, 1);
		GridPane.setHalignment(colorPicker, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(colorPicker, 1);
		GridPane.setValignment(colorPicker, javafx.geometry.VPos.CENTER);
		colorPicker.setPrefHeight(26.0);
		colorPicker.setPrefWidth(110.0);

		GridPane.setColumnIndex(button0, 1);
		GridPane.setHalignment(button0, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(button0, 3);
		button0.setMnemonicParsing(false);
		button0.setPrefHeight(26.0);
		button0.setPrefWidth(110.0);
		button0.setText("Reset To Default");

		GridPane.setColumnIndex(button1, 2);
		GridPane.setHalignment(button1, javafx.geometry.HPos.LEFT);
		GridPane.setRowIndex(button1, 1);
		button1.setMnemonicParsing(false);
		button1.setPrefHeight(26.0);
		button1.setPrefWidth(110.0);
		button1.setText("Default Color");
		tab.setContent(gridPane);

		tab0.setClosable(false);
		tab0.setText("Gist");

		columnConstraints2.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints2.setMaxWidth(252.0);
		columnConstraints2.setMinWidth(10.0);
		columnConstraints2.setPrefWidth(230.0);

		columnConstraints3.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints3.setMaxWidth(300.0);
		columnConstraints3.setMinWidth(10.0);
		columnConstraints3.setPrefWidth(133.0);

		columnConstraints4.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints4.setMaxWidth(240.0);
		columnConstraints4.setMinWidth(10.0);
		columnConstraints4.setPrefWidth(236.0);

		rowConstraints3.setMaxHeight(88.0);
		rowConstraints3.setMinHeight(10.0);
		rowConstraints3.setPrefHeight(58.0);
		rowConstraints3.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints4.setMaxHeight(119.0);
		rowConstraints4.setMinHeight(10.0);
		rowConstraints4.setPrefHeight(48.0);
		rowConstraints4.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints5.setMaxHeight(202.0);
		rowConstraints5.setMinHeight(10.0);
		rowConstraints5.setPrefHeight(53.0);
		rowConstraints5.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints6.setMaxHeight(246.0);
		rowConstraints6.setMinHeight(10.0);
		rowConstraints6.setPrefHeight(101.0);
		rowConstraints6.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		checkBox0.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
		checkBox0.setMnemonicParsing(false);
		checkBox0.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
		checkBox0.setPrefHeight(18.0);
		checkBox0.setPrefWidth(230.0);
		checkBox0.setText("Use Custom Gist Folder Icon?");
		GridPane.setMargin(checkBox0, new Insets(0.0, 5.0, 0.0, 0.0));

		GridPane.setColumnIndex(button2, 1);
		GridPane.setHalignment(button2, javafx.geometry.HPos.CENTER);
		button2.setMnemonicParsing(false);
		button2.setPrefHeight(26.0);
		button2.setPrefWidth(110.0);
		button2.setText("Choose File");
		GridPane.setMargin(button2, new Insets(0.0));

		GridPane.setColumnIndex(imageView0, 2);
		GridPane.setHalignment(imageView0, javafx.geometry.HPos.CENTER);
		GridPane.setValignment(imageView0, javafx.geometry.VPos.CENTER);
		imageView0.setFitHeight(82.0);
		imageView0.setFitWidth(121.0);
		imageView0.setPickOnBounds(true);
		imageView0.setPreserveRatio(true);

		GridPane.setHalignment(label0, javafx.geometry.HPos.RIGHT);
		GridPane.setRowIndex(label0, 1);
		label0.setText("Choose Gist Folder Color");
		GridPane.setMargin(label0, new Insets(0.0, 15.0, 0.0, 0.0));

		GridPane.setColumnIndex(colorPicker0, 1);
		GridPane.setHalignment(colorPicker0, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(colorPicker0, 1);
		GridPane.setValignment(colorPicker0, javafx.geometry.VPos.CENTER);
		colorPicker0.setPrefHeight(26.0);
		colorPicker0.setPrefWidth(110.0);

		GridPane.setColumnIndex(button3, 1);
		GridPane.setHalignment(button3, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(button3, 3);
		button3.setMnemonicParsing(false);
		button3.setPrefHeight(26.0);
		button3.setPrefWidth(110.0);
		button3.setText("Reset To Default");

		GridPane.setColumnIndex(button4, 2);
		GridPane.setHalignment(button4, javafx.geometry.HPos.LEFT);
		GridPane.setRowIndex(button4, 1);
		button4.setMnemonicParsing(false);
		button4.setPrefHeight(26.0);
		button4.setPrefWidth(110.0);
		button4.setText("Default Color");
		tab0.setContent(gridPane0);

		tab1.setClosable(false);
		tab1.setText("File");

		columnConstraints5.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints5.setMaxWidth(252.0);
		columnConstraints5.setMinWidth(10.0);
		columnConstraints5.setPrefWidth(230.0);

		columnConstraints6.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints6.setMaxWidth(300.0);
		columnConstraints6.setMinWidth(10.0);
		columnConstraints6.setPrefWidth(131.0);

		columnConstraints7.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints7.setMaxWidth(240.0);
		columnConstraints7.setMinWidth(10.0);
		columnConstraints7.setPrefWidth(238.0);

		rowConstraints7.setMaxHeight(88.0);
		rowConstraints7.setMinHeight(10.0);
		rowConstraints7.setPrefHeight(58.0);
		rowConstraints7.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints8.setMaxHeight(119.0);
		rowConstraints8.setMinHeight(10.0);
		rowConstraints8.setPrefHeight(48.0);
		rowConstraints8.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints9.setMaxHeight(202.0);
		rowConstraints9.setMinHeight(10.0);
		rowConstraints9.setPrefHeight(53.0);
		rowConstraints9.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints10.setMaxHeight(246.0);
		rowConstraints10.setMinHeight(10.0);
		rowConstraints10.setPrefHeight(101.0);
		rowConstraints10.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		checkBox1.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
		checkBox1.setMnemonicParsing(false);
		checkBox1.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
		checkBox1.setPrefHeight(18.0);
		checkBox1.setPrefWidth(230.0);
		checkBox1.setText("Use Custom File Icon?");
		GridPane.setMargin(checkBox1, new Insets(0.0, 5.0, 0.0, 0.0));

		GridPane.setColumnIndex(button5, 1);
		GridPane.setHalignment(button5, javafx.geometry.HPos.CENTER);
		button5.setMnemonicParsing(false);
		button5.setPrefHeight(26.0);
		button5.setPrefWidth(110.0);
		button5.setText("Choose File");
		GridPane.setMargin(button5, new Insets(0.0));

		GridPane.setColumnIndex(imageView1, 2);
		GridPane.setHalignment(imageView1, javafx.geometry.HPos.CENTER);
		GridPane.setValignment(imageView1, javafx.geometry.VPos.CENTER);
		imageView1.setFitHeight(82.0);
		imageView1.setFitWidth(121.0);
		imageView1.setPickOnBounds(true);
		imageView1.setPreserveRatio(true);

		GridPane.setHalignment(label1, javafx.geometry.HPos.RIGHT);
		GridPane.setRowIndex(label1, 1);
		label1.setText("Choose File Icon Color");
		GridPane.setMargin(label1, new Insets(0.0, 15.0, 0.0, 0.0));

		GridPane.setColumnIndex(colorPicker1, 1);
		GridPane.setHalignment(colorPicker1, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(colorPicker1, 1);
		GridPane.setValignment(colorPicker1, javafx.geometry.VPos.CENTER);
		colorPicker1.setPrefHeight(26.0);
		colorPicker1.setPrefWidth(110.0);

		GridPane.setColumnIndex(button6, 1);
		GridPane.setHalignment(button6, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(button6, 3);
		button6.setMnemonicParsing(false);
		button6.setPrefHeight(26.0);
		button6.setPrefWidth(110.0);
		button6.setText("Reset To Default");

		GridPane.setColumnIndex(button7, 2);
		GridPane.setHalignment(button7, javafx.geometry.HPos.LEFT);
		GridPane.setRowIndex(button7, 1);
		button7.setMnemonicParsing(false);
		button7.setPrefHeight(26.0);
		button7.setPrefWidth(110.0);
		button7.setText("Default Color");

		GridPane.setHalignment(label2, javafx.geometry.HPos.RIGHT);
		GridPane.setRowIndex(label2, 2);
		label2.setText("Dirty File Flag Color");
		GridPane.setMargin(label2, new Insets(0.0, 15.0, 0.0, 0.0));

		GridPane.setColumnIndex(colorPicker2, 1);
		GridPane.setHalignment(colorPicker2, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(colorPicker2, 2);
		colorPicker2.setPrefHeight(26.0);
		colorPicker2.setPrefWidth(110.0);
		tab1.setContent(gridPane1);

		gridPane.getColumnConstraints().add(columnConstraints);
		gridPane.getColumnConstraints().add(columnConstraints0);
		gridPane.getColumnConstraints().add(columnConstraints1);
		gridPane.getRowConstraints().add(rowConstraints);
		gridPane.getRowConstraints().add(rowConstraints0);
		gridPane.getRowConstraints().add(rowConstraints1);
		gridPane.getRowConstraints().add(rowConstraints2);
		gridPane.getChildren().add(checkBox);
		gridPane.getChildren().add(button);
		gridPane.getChildren().add(imageView);
		gridPane.getChildren().add(label);
		gridPane.getChildren().add(colorPicker);
		gridPane.getChildren().add(button0);
		gridPane.getChildren().add(button1);
		getTabs().add(tab);
		gridPane0.getColumnConstraints().add(columnConstraints2);
		gridPane0.getColumnConstraints().add(columnConstraints3);
		gridPane0.getColumnConstraints().add(columnConstraints4);
		gridPane0.getRowConstraints().add(rowConstraints3);
		gridPane0.getRowConstraints().add(rowConstraints4);
		gridPane0.getRowConstraints().add(rowConstraints5);
		gridPane0.getRowConstraints().add(rowConstraints6);
		gridPane0.getChildren().add(checkBox0);
		gridPane0.getChildren().add(button2);
		gridPane0.getChildren().add(imageView0);
		gridPane0.getChildren().add(label0);
		gridPane0.getChildren().add(colorPicker0);
		gridPane0.getChildren().add(button3);
		gridPane0.getChildren().add(button4);
		getTabs().add(tab0);
		gridPane1.getColumnConstraints().add(columnConstraints5);
		gridPane1.getColumnConstraints().add(columnConstraints6);
		gridPane1.getColumnConstraints().add(columnConstraints7);
		gridPane1.getRowConstraints().add(rowConstraints7);
		gridPane1.getRowConstraints().add(rowConstraints8);
		gridPane1.getRowConstraints().add(rowConstraints9);
		gridPane1.getRowConstraints().add(rowConstraints10);
		gridPane1.getChildren().add(checkBox1);
		gridPane1.getChildren().add(button5);
		gridPane1.getChildren().add(imageView1);
		gridPane1.getChildren().add(label1);
		gridPane1.getChildren().add(colorPicker1);
		gridPane1.getChildren().add(button6);
		gridPane1.getChildren().add(button7);
		gridPane1.getChildren().add(label2);
		gridPane1.getChildren().add(colorPicker2);
		getTabs().add(tab1);

	}

}
