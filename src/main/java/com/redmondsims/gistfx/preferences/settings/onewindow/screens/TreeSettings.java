package com.redmondsims.gistfx.preferences.settings.onewindow.screens;


import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.ui.TreeIcons;
import com.redmondsims.gistfx.utils.Resources;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TreeSettings {

	public TreeSettings(double width) {
		sceneWidth = width;
	}

	private final double sceneWidth;
	private Path         startFolder;

	public VBox controls(Type type) {

		if (startFolder == null) startFolder = Paths.get(System.getProperty("user.home"));

		ColorPicker colorPicker     = new ColorPicker(getCurrentColor(type));
		String      thisType        = getTypeString(type);
		CheckBox    cbUseCustomIcon = Utility.checkBoxLabelLeft("Use custom " + thisType + " Icon?");
		Button      btnChoseFile    = new Button("Chose File");
		ImageView   iconImage       = getImageView(type);
		Label       lblColor        = new Label("Chose " + thisType + " Color");
		Button      btnDefaultColor = new Button("Default Color");
		Button      btnResetDefault = new Button("Reset To Default");
		ColorPicker cpDirty         = new ColorPicker(AppSettings.get().dirtyFileFlagColor());

		iconImage.setPreserveRatio(true);
		iconImage.setFitWidth(28);
		lblColor.setAlignment(Pos.CENTER_RIGHT);

		//Row HBoxes
		HBox boxRow1 = Utility.newHBox(5, 10, Pos.CENTER_LEFT, cbUseCustomIcon, btnChoseFile, iconImage);
		HBox boxRow2 = Utility.newHBox(5, 10, Pos.CENTER_LEFT, lblColor, Utility.getSpacedHBoxRight(colorPicker,14), btnDefaultColor);
		HBox boxRow4 = Utility.getCenteredHBox(0,10, btnResetDefault);

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


		VBox vbox = new VBox(-5, boxRow1, boxRow2);
		if(type.equals(Type.FILE)) {
			Label lblDirty = new Label("Dirty File Flag Color");
			cpDirty.setOnAction(e -> {
				AppSettings.set().dirtyFileFlagColor(cpDirty.getValue());
				WindowManager.refreshTreeIcons();
			});
			cpDirty.setMinHeight(25);
			cpDirty.setPrefWidth(100);
			HBox boxRow3 = Utility.newHBox(0,5, Pos.CENTER_LEFT,lblDirty,Utility.getSpacedHBoxRight(cpDirty,-10));
			vbox.getChildren().add(boxRow3);
			vbox.setPadding(new Insets(3,3,3,25));
		}
		vbox.getChildren().add(boxRow4);
		return vbox;
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
