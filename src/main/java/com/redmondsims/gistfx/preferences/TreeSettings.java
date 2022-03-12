package com.redmondsims.gistfx.preferences;

import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.utils.Util;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class TreeSettings {

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

	private static final double nodeHeight = 30;

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

	private static File getFile(String title, String initPath) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(initPath));
		fileChooser.setTitle(title);
		return fileChooser.showOpenDialog(null);
	}

	private static VBox categoryVBox() {
		String      label              = "Chose Category Folder Icon";
		String      iconName           = AppSettings.get().userCategoryIconName();
		Label       lblIconPath        = new Label(iconName.isEmpty() ? "No Icon File Selected" : iconName);
		Button      btnChoseIcon       = new Button("Choose Icon");
		Button      btnSetDefaultColor = new Button("Default Color");
		CheckBox    cbUseDefault       = new CheckBox("Use Default Category Folder Icon");
		ColorPicker colorPicker        = new ColorPicker();
		HBox        defaultBox         = newHBox(cbUseDefault);
		VBox        pickColorBox       = newVBox(5, newHBox(10, colorPicker, btnSetDefaultColor));
		VBox        pickIconBox        = newVBox(5, btnChoseIcon, lblIconPath);
		HBox        optionBox          = new HBox();
		colorPicker.setPrefWidth(110);
		lblIconPath.setPrefWidth(sceneWidth);
		colorPicker.setValue(AppSettings.get().categoryFolderIconColor());
		cbUseDefault.setSelected(AppSettings.get().useDefaultCategoryIcon());
		cbUseDefault.selectedProperty().addListener((observable,boxWasChecked,boxIsChecked) -> {
			String name = AppSettings.get().userCategoryIconName();
			Platform.runLater(() -> lblIconPath.setText(name.isEmpty() ? "No Icon File Selected" : name));
			optionBox.getChildren().setAll(boxIsChecked ? pickColorBox : pickIconBox);
			setBox(BOX.CATEGORY, boxIsChecked ? 2 : 3);
			AppSettings.set().useDefaultCategoryIcon(boxIsChecked);
			WindowManager.refreshTree();
		});
		btnChoseIcon.setOnAction(e -> {
			File iconFile = AppSettings.get().userCategoryIcon();
			String path = AppSettings.get().userIconFileFolder().getAbsolutePath();
			if (iconFile != null) {
				path = iconFile.getParent();
			}
			File file = getFile(label,path);
			if (file != null) {
				AppSettings.set().userTreeCategoryIconPath(file.getAbsolutePath());
				AppSettings.set().userIconFileFolder(file.getParent());
				Platform.runLater(() -> lblIconPath.setText(file.getAbsolutePath()));
				WindowManager.refreshTree();
			}
		});
		colorPicker.setOnAction(e -> {
			AppSettings.set().categoryFolderIconColor(colorPicker.getValue());
			WindowManager.refreshTree();
		});
		btnSetDefaultColor.setOnAction(e -> {
			AppSettings.clear().categoryFolderIconColor();
			AppSettings.set().categoryFolderIconColor(AppSettings.get().categoryFolderIconColor());
			colorPicker.setValue(AppSettings.get().categoryFolderIconColor());
			WindowManager.refreshTree();
		});
		setBox(BOX.CATEGORY, cbUseDefault.isSelected() ? 2 : 3);
		optionBox.getChildren().setAll(cbUseDefault.isSelected() ? pickColorBox : pickIconBox);
		return newVBox(5,defaultBox,optionBox);
	}

	private static VBox gistVBox() {
		String      label              = "Chose Gist Folder Icon";
		String      iconName           = AppSettings.get().userGistIconName();
		Label       lblIconPath        = new Label(iconName.isEmpty() ? "No Icon File Selected" : iconName);
		Button      btnChoseIcon       = new Button("Choose Icon");
		Button      btnSetDefaultColor = new Button("Default Color");
		CheckBox    cbUseDefault       = new CheckBox("Use Default Gist Folder Icon");
		ColorPicker colorPicker        = new ColorPicker();
		HBox        defaultBox         = newHBox(cbUseDefault);
		VBox        pickColorBox       = newVBox(5, newHBox(10, colorPicker, btnSetDefaultColor));
		VBox        pickIconBox        = newVBox(5, btnChoseIcon, lblIconPath);
		HBox        optionBox          = new HBox();
		colorPicker.setPrefWidth(110);
		lblIconPath.setPrefWidth(sceneWidth);
		colorPicker.setValue(AppSettings.get().gistFolderIconColor());
		cbUseDefault.setSelected(AppSettings.get().useDefaultGistIcon());
		cbUseDefault.selectedProperty().addListener((observable,boxWasChecked,boxIsChecked) -> {
			String name = AppSettings.get().userGistIconName();
			Platform.runLater(() -> lblIconPath.setText(name.isEmpty() ? "No Icon File Selected" : name));
			optionBox.getChildren().setAll(boxIsChecked ? pickColorBox : pickIconBox);
			setBox(BOX.GIST, boxIsChecked ? 2 : 3);
			AppSettings.set().useDefaultGistIcon(boxIsChecked);
			WindowManager.refreshTree();
		});
		btnChoseIcon.setOnAction(e -> {
			File iconFile = AppSettings.get().userGistIcon();
			String path = AppSettings.get().userIconFileFolder().getAbsolutePath();
			if (iconFile != null) {
				path = iconFile.getParent();
			}
			File file = getFile(label,path);
			if (file != null) {
				AppSettings.set().userTreeGistIconPath(file.getAbsolutePath());
				AppSettings.set().userIconFileFolder(file.getParent());
				Platform.runLater(() -> lblIconPath.setText(file.getAbsolutePath()));
				WindowManager.refreshTree();
			}
		});
		colorPicker.setOnAction(e -> {
			AppSettings.set().gistFolderIconColor(colorPicker.getValue());
			WindowManager.refreshTree();
		});
		btnSetDefaultColor.setOnAction(e -> {
			AppSettings.clear().gistFolderIconColor();
			AppSettings.set().gistFolderIconColor(AppSettings.get().gistFolderIconColor());
			colorPicker.setValue(AppSettings.get().gistFolderIconColor());
			WindowManager.refreshTree();
		});
		setBox(BOX.GIST, cbUseDefault.isSelected() ? 2 : 3);
		optionBox.getChildren().setAll(cbUseDefault.isSelected() ? pickColorBox : pickIconBox);
		return newVBox(5,defaultBox,optionBox);
	}

	private static VBox fileVBox() {
		String      label              = "Chose File Icon";
		String      iconName           = AppSettings.get().userFileIconName();
		Label       lblIconPath        = new Label(iconName.isEmpty() ? "No Icon File Selected" : iconName);
		Button      btnChoseIcon       = new Button("Choose Icon");
		Button      btnSetDefaultColor = new Button("Default Color");
		CheckBox    cbUseDefault       = new CheckBox("Use Default File Icon");
		ColorPicker colorPicker        = new ColorPicker();
		HBox        defaultBox         = newHBox(cbUseDefault);
		VBox        pickColorBox       = newVBox(5, newHBox(10, colorPicker, btnSetDefaultColor));
		VBox        pickIconBox        = newVBox(5, btnChoseIcon, lblIconPath);
		HBox        optionBox          = new HBox();
		colorPicker.setPrefWidth(110);
		lblIconPath.setPrefWidth(sceneWidth);
		colorPicker.setValue(AppSettings.get().fileIconColor());
		cbUseDefault.setSelected(AppSettings.get().useDefaultFileIcon());
		cbUseDefault.selectedProperty().addListener((observable,boxWasChecked,boxIsChecked) -> {
			String name = AppSettings.get().userFileIconName();
			Platform.runLater(() -> lblIconPath.setText(name.isEmpty() ? "No Icon File Selected" : name));
			optionBox.getChildren().setAll(boxIsChecked ? pickColorBox : pickIconBox);
			setBox(BOX.FILE, boxIsChecked ? 2 : 3);
			AppSettings.set().useDefaultFileIcon(boxIsChecked);
			WindowManager.refreshFileIcons();
		});
		btnChoseIcon.setOnAction(e -> {
			File iconFile = AppSettings.get().userFileIcon();
			String path = AppSettings.get().userIconFileFolder().getAbsolutePath();
			if (iconFile != null) {
				path = iconFile.getParent();
			}
			File file = getFile(label,path);
			if (file != null) {
				AppSettings.set().userTreeFileIconPath(file.getAbsolutePath());
				AppSettings.set().userIconFileFolder(file.getParent());
				Platform.runLater(() -> lblIconPath.setText(file.getAbsolutePath()));
				WindowManager.refreshFileIcons();
			}
		});
		colorPicker.setOnAction(e -> {
			AppSettings.set().fileIconColor(colorPicker.getValue());
			WindowManager.refreshFileIcons();
		});
		btnSetDefaultColor.setOnAction(e -> {
			AppSettings.clear().fileIconColor();
			AppSettings.set().fileIconColor(AppSettings.get().fileIconColor());
			colorPicker.setValue(AppSettings.get().fileIconColor());
			WindowManager.refreshFileIcons();
		});
		setBox(BOX.FILE, cbUseDefault.isSelected() ? 2 : 3);
		optionBox.getChildren().setAll(cbUseDefault.isSelected() ? pickColorBox : pickIconBox);
		return newVBox(5,defaultBox,optionBox);
	}

	private static void setBox(BOX box, int count) {
		int sizePer = 35;
		BOX.set(box,count);
		double newHeight = BOX.getTotalItems() * sizePer;
		double offset = Util.reMap(newHeight,210,315,70,50,2);
		sceneHeight = newHeight + offset;
		if(toolWindow != null) toolWindow.resizeHeight(sceneHeight);
	}

	private static double sceneHeight;

	private static final double choseButtonSpacing = 190;

	private static final double sceneWidth = 300;

	private static final VBox formContent = newVBox(20);

	private static ToolWindow toolWindow;

	public static void showWindow(Stage callingStage) {
		formContent.getChildren().setAll(categoryVBox(),
									gistVBox(),
									fileVBox());
		formContent.setPadding(new Insets(10, 10, 10, 10));
		formContent.setAlignment(Pos.CENTER_LEFT);
		toolWindow = new ToolWindow.Builder(formContent, sceneWidth, sceneHeight, callingStage).title("GistFX Tree Settings").build();
		toolWindow.showAndWait();
	}

}
