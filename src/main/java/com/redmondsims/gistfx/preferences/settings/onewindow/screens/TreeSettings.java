package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.ui.TreeIcons;
import com.redmondsims.gistfx.utils.Resources;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TreeSettings {

	private Path startFolder;

	public Tab contents (Type type) {

		if (startFolder == null) startFolder = Paths.get(System.getProperty("user.home"));

		String            thisType           = getTypeString(type);
		Tab               tab                = new Tab(type.nameCased());
		GridPane          gridPane           = new GridPane();
		ColumnConstraints columnConstraints  = new ColumnConstraints();
		ColumnConstraints columnConstraints0 = new ColumnConstraints();
		ColumnConstraints columnConstraints1 = new ColumnConstraints();
		RowConstraints    rowConstraints     = new RowConstraints();
		RowConstraints    rowConstraints0    = new RowConstraints();
		RowConstraints    rowConstraints1    = new RowConstraints();
		RowConstraints    rowConstraints2    = new RowConstraints();
		ColorPicker       colorPicker        = new ColorPicker(getCurrentColor(type));
		CheckBox          cbUseCustomIcon    = new CheckBox("Use custom " + thisType + " Icon?");
		Button            btnChoseFile       = new Button("Chose File");
		ImageView         iconImage          = getImageView(type);
		Label             lblColor           = new Label("Chose " + thisType + " Color");
		Button            btnDefaultColor    = new Button("Default Color");
		Button            btnResetDefault    = new Button("Reset To Default");
		ColorPicker       cpDirty            = new ColorPicker(AppSettings.get().dirtyFileFlagColor());


		tab.setClosable(false);
		tab.setText(type.nameCased());

		columnConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints.setMaxWidth(230.0);
		columnConstraints.setMinWidth(230.0);
		columnConstraints.setPrefWidth(230.0);

		columnConstraints0.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints0.setMaxWidth(125.0);
		columnConstraints0.setMinWidth(125.0);
		columnConstraints0.setPrefWidth(125.0);

		columnConstraints1.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
		columnConstraints1.setMaxWidth(175.0);
		columnConstraints1.setMinWidth(175.0);
		columnConstraints1.setPrefWidth(175.0);

		rowConstraints.setMaxHeight(30.0);
		rowConstraints.setMinHeight(30.0);
		rowConstraints.setPrefHeight(30.0);
		rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints0.setMaxHeight(45.0);
		rowConstraints0.setMinHeight(45.0);
		rowConstraints0.setPrefHeight(45.0);
		rowConstraints0.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints1.setMaxHeight(45.0);
		rowConstraints1.setMinHeight(45.0);
		rowConstraints1.setPrefHeight(45.0);
		rowConstraints1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		rowConstraints2.setMaxHeight(45.0);
		rowConstraints2.setMinHeight(45.0);
		rowConstraints2.setPrefHeight(45.0);
		rowConstraints2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

		cbUseCustomIcon.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
		cbUseCustomIcon.setMnemonicParsing(false);
		cbUseCustomIcon.setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
		cbUseCustomIcon.setPrefHeight(18.0);
		cbUseCustomIcon.setPrefWidth(230.0);

		GridPane.setMargin(cbUseCustomIcon, new Insets(35.0, 5.0, 30.0, 0.0));
		GridPane.setMargin(btnChoseFile, new Insets(35.0, 5.0, 30.0, 0.0));
		GridPane.setColumnIndex(btnChoseFile, 1);
		GridPane.setHalignment(btnChoseFile, javafx.geometry.HPos.CENTER);
		btnChoseFile.setMnemonicParsing(false);
		btnChoseFile.setPrefHeight(26.0);
		btnChoseFile.setPrefWidth(110.0);

		GridPane.setMargin(iconImage, new Insets(35.0, 5.0, 30.0, 0.0));
		GridPane.setColumnIndex(iconImage, 2);
		GridPane.setHalignment(iconImage, HPos.LEFT);
		GridPane.setValignment(iconImage, javafx.geometry.VPos.CENTER);
		iconImage.setPreserveRatio(true);
		iconImage.setFitWidth(25.0);
		iconImage.setPickOnBounds(true);

		GridPane.setHalignment(lblColor, javafx.geometry.HPos.RIGHT);
		GridPane.setRowIndex(lblColor, 1);
		GridPane.setMargin(lblColor, new Insets(0.0, 15.0, 0.0, 0.0));

		GridPane.setColumnIndex(colorPicker, 1);
		GridPane.setHalignment(colorPicker, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(colorPicker, 1);
		GridPane.setValignment(colorPicker, javafx.geometry.VPos.CENTER);
		colorPicker.setPrefHeight(26.0);
		colorPicker.setPrefWidth(110.0);

		GridPane.setColumnIndex(btnResetDefault, 1);
		GridPane.setHalignment(btnResetDefault, javafx.geometry.HPos.CENTER);
		GridPane.setRowIndex(btnResetDefault, 3);
		btnResetDefault.setMnemonicParsing(false);
		btnResetDefault.setPrefHeight(26.0);
		btnResetDefault.setPrefWidth(110.0);

		GridPane.setColumnIndex(btnDefaultColor, 2);
		GridPane.setHalignment(btnDefaultColor, javafx.geometry.HPos.LEFT);
		GridPane.setRowIndex(btnDefaultColor, 1);
		btnDefaultColor.setMnemonicParsing(false);
		btnDefaultColor.setPrefHeight(26.0);
		btnDefaultColor.setPrefWidth(110.0);

		if (type.equals(Type.FILE)) {
			Label lblCP2 = new Label("Dirty File Flag Color");
			GridPane.setHalignment(lblCP2, javafx.geometry.HPos.RIGHT);
			GridPane.setRowIndex(lblCP2, 2);
			GridPane.setMargin(lblCP2, new Insets(0.0, 15.0, 0.0, 0.0));
			GridPane.setColumnIndex(cpDirty, 1);
			GridPane.setHalignment(cpDirty, javafx.geometry.HPos.CENTER);
			GridPane.setRowIndex(cpDirty, 2);
			gridPane.getChildren().add(lblCP2);
			gridPane.getChildren().add(cpDirty);
			cpDirty.setPrefHeight(26.0);
			cpDirty.setPrefWidth(110.0);
			cpDirty.setOnAction(e -> {
				AppSettings.set().dirtyFileFlagColor(cpDirty.getValue());
				WindowManager.refreshTreeIcons();
			});
		}

		tab.setContent(gridPane);

		gridPane.getColumnConstraints().add(columnConstraints);
		gridPane.getColumnConstraints().add(columnConstraints0);
		gridPane.getColumnConstraints().add(columnConstraints1);
		gridPane.getRowConstraints().add(rowConstraints);
		gridPane.getRowConstraints().add(rowConstraints0);
		gridPane.getRowConstraints().add(rowConstraints1);
		gridPane.getRowConstraints().add(rowConstraints2);
		gridPane.getChildren().add(cbUseCustomIcon);
		gridPane.getChildren().add(btnChoseFile);
		gridPane.getChildren().add(iconImage);
		gridPane.getChildren().add(lblColor);
		gridPane.getChildren().add(colorPicker);
		gridPane.getChildren().add(btnResetDefault);
		gridPane.getChildren().add(btnDefaultColor);


		lblColor.visibleProperty().bind(cbUseCustomIcon.selectedProperty().not());
		btnDefaultColor.visibleProperty().bind(cbUseCustomIcon.selectedProperty().not());
		colorPicker.visibleProperty().bind(cbUseCustomIcon.selectedProperty().not());
		colorPicker.setPrefWidth(100);

		//Control Actions
		cbUseCustomIcon.setOnAction(e -> {
			if (cbUseCustomIcon.isSelected()) {
				setFilePathLabel(type);
			}
			else {
				resetUserIconSettings(type);
			}
			WindowManager.refreshTreeIcons();
			iconImage.setImage(getIconImage(type));
		});
		btnChoseFile.setOnAction(e -> {
			setFilePathLabel(type);
			iconImage.setImage(getIconImage(type));
			cbUseCustomIcon.setSelected(!useCustomIcon(type));
			WindowManager.refreshTreeIcons();
		});
		colorPicker.setOnAction(e -> {
			setIconColor(type,colorPicker.getValue());
			iconImage.setImage(getIconImage(type));
			WindowManager.refreshTreeIcons();
		});
		btnDefaultColor.setOnAction(e -> {
			colorPicker.setValue(getDefaultIconColor(type));
			iconImage.setImage(getIconImage(type));
			WindowManager.refreshTreeIcons();
		});
		cbUseCustomIcon.setSelected(!useCustomIcon(type));
		btnResetDefault.setOnAction(e->{
			cbUseCustomIcon.setSelected(false);
			resetUserIconSettings(type);
			colorPicker.setValue(getDefaultIconColor(type));
			iconImage.setImage(getIconImage(type));
			if(type.equals(Type.FILE)) {
				AppSettings.set().dirtyFileFlagColor(Color.RED);
				cpDirty.setValue(Color.RED);
			}
			WindowManager.refreshTreeIcons();
		});
		return tab;
	}

	private boolean useCustomIcon(Type type) {
		return switch(type) {
			case CATEGORY -> AppSettings.get().useDefaultCategoryIcon();
			case GIST -> AppSettings.get().useDefaultGistIcon();
			case FILE -> AppSettings.get().useDefaultFileIcon();
			default -> false;
		};
	}

	private ImageView getImageView(Type type) {
		return switch(type) {
			case CATEGORY -> TreeIcons.getGistCategoryIcon();
			case GIST -> TreeIcons.getGistIcon();
			case FILE -> TreeIcons.getFileIcon();
			default -> null;
		};
	}

	private Image getIconImage(Type type) {
		ImageView iv = getImageView(type);
		return iv.getImage();
	}

	private void setFilePathLabel(Type type) {
		File file = getFile("Chose Icon File");
		if (file != null) {
			if (file.exists()) {
				Resources.copyUserIcon(file.getAbsolutePath());
				setUserIcon(type, file);
				startFolder = file.getParentFile().toPath();
			}
		}
	}

	private File getFile(String title) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(startFolder.toFile());
		fileChooser.setTitle(title);
		return fileChooser.showOpenDialog(null);
	}

	private Color getDefaultIconColor(Type type) {
		Color color = null;
		switch (type) {
			case CATEGORY -> {
				AppSettings.set().categoryFolderIconColor(null);
				color = AppSettings.get().categoryFolderIconColor();
			}
			case GIST -> {
				AppSettings.set().gistFolderIconColor(null);
				color = AppSettings.get().gistFolderIconColor();
			}
			case FILE -> {
				AppSettings.set().fileIconColor(null);
				color = AppSettings.get().fileIconColor();
			}
		}
		return color;
	}

	private Color getCurrentColor(Type type) {
		return switch (type) {
			case CATEGORY -> AppSettings.get().categoryFolderIconColor();
			case GIST -> AppSettings.get().gistFolderIconColor();
			case FILE -> AppSettings.get().fileIconColor();
			default -> null;
		};
	}

	private void setIconColor(Type type, Color color) {
		switch (type) {
			case CATEGORY -> AppSettings.set().categoryFolderIconColor(color);
			case GIST -> AppSettings.set().gistFolderIconColor(color);
			case FILE -> AppSettings.set().fileIconColor(color);
		}
	}

	private void setUserIcon(Type type, File file) {
		switch (type) {
			case CATEGORY -> AppSettings.set().userCategoryIcon(file.getName());
			case GIST -> AppSettings.set().userGistIcon(file.getName());
			case FILE -> AppSettings.set().userFileIcon(file.getName());
		}
	}

	private void resetUserIconSettings(Type type) {
		switch (type) {
			case GIST -> {
				AppSettings.set().useDefaultGistIcon(true);
				AppSettings.set().userGistIcon("");
				AppSettings.clear().userGistIcon();
			}
			case CATEGORY -> {
				AppSettings.set().useDefaultCategoryIcon(true);
				AppSettings.set().userCategoryIcon("");
				AppSettings.clear().userCategoryIcon();
			}
			case FILE -> {
				AppSettings.set().useDefaultFileIcon(true);
				AppSettings.set().userFileIcon("");
				AppSettings.clear().userFileIcon();
			}
		}
	}


	private String getTypeString(Type type) {
		return switch(type) {
			case CATEGORY -> "Category Folder";
			case GIST -> "Gist Folder";
			case FILE -> "File";
			default -> "";
		};
	}

}
