package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Colors;
import com.redmondsims.gistfx.enums.Theme;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.gist.CodeEditor;
import com.redmondsims.gistfx.ui.trayicon.TrayIcon;
import com.redmondsims.gistfx.utils.Resources;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.redmondsims.gistfx.preferences.settings.onewindow.screens.Utility.newLabelTypeOne;

public class GuiElements {


	public GuiElements() {
		this.callingScene = SceneOne.getScene(Resources.getSceneIdGistWindow());
		makeControls();
		setControlProperties();
		setControlActions();
		installTooltips();
	}

	private final Scene             callingScene;
	private       ChoiceBox<Theme>  cbTheme;
	private       ChoiceBox<Colors> cbLoginScreenColor;
	private       ChoiceBox<Colors> cbSystemTrayColor;
	private       Label             lblAppTheme;
	private       Label             lblLoginScreenColor;
	private       ColorPicker       progressBarColorPicker;
	private       CheckBox          cbCustomProgressColor;
	private       CheckBox          cbToolBar;
	private       CheckBox          cbDisableWarning;
	private       CheckBox          cbTrayIcon;
	private       CheckBox          cbTrayIconLogin;
	private       CheckBox          cbProgressColorLogin;
	private       CheckBox          cbRandomLoginScreen;
	private final double            choiceWidth = 100;
	private final double            labelWidth  = 215;
	private final double            cBoxWidth   = 217.5;

	private void makeControls() {
		lblAppTheme            = newLabelTypeOne("Application Theme");
		lblLoginScreenColor    = newLabelTypeOne("Login Screen Color");
		cbTheme                = new ChoiceBox<>(Theme.themeList());
		cbLoginScreenColor     = new ChoiceBox<>(Colors.loginScreenColorList());
		cbSystemTrayColor      = new ChoiceBox<>(Colors.trayIconColorList());
		cbCustomProgressColor  = Utility.checkBoxLabelLeft("Custom Progress Bar Color");
		cbToolBar              = Utility.checkBoxLabelLeft("Show Toolbar At Launch");
		cbDisableWarning       = Utility.checkBoxLabelLeft("Disable Dirty File Exit Warning");
		cbTrayIcon             = Utility.checkBoxLabelLeft("Run GistFX From System Tray");
		cbTrayIconLogin        = Utility.checkBoxLabelLeft("Login");
		cbProgressColorLogin   = Utility.checkBoxLabelLeft("Login");
		cbRandomLoginScreen    = Utility.checkBoxLabelLeft("Random");
		progressBarColorPicker = new ColorPicker();
	}

	private void installTooltips() {
		Tooltip.install(cbToolBar, Action.newTooltip("Show the tool bar by default. Otherwise,\nyou will need to toggle it on each time\nGistFX loads.\n\nThe tool bar changes depending on what\nis currently selected in the window.\n\nThe functionality of each button is also\nin the programs menu structure."));
		Tooltip.install(cbTheme, Action.newTooltip("Which side of the Force will you chose?"));
		Tooltip.install(cbCustomProgressColor, Action.newTooltip("If this is unchecked, GistFX will\nselect a random color for the two\nprogress bars from a pre-defined\ncolor list"));
		Tooltip.install(progressBarColorPicker, Action.newTooltip("Set the color of the progress bars."));
		Tooltip.install(cbDisableWarning, Action.newTooltip("If checked, this will prevent GistFX\nfrom warning you that you have edited\nGists that have not been uploaded to GitHub."));
		Tooltip.install(cbTrayIcon, Action.newTooltip("Run GistFX from your system tray. This can be be useful because GistFX will\nalways be at the ready and it won't clutter up your screen or your\ntaskbar. When you minimize the main window, it will be out of the way\nbut can always be called back by clicking on the G in the system tray"));
		Tooltip.install(cbTrayIconLogin, Action.newTooltip("Match Tray Icon Color with login screen color choice"));
		Tooltip.install(cbProgressColorLogin, Action.newTooltip("Match Progress Bar Color with login screen color choice"));
		Tooltip.install(cbSystemTrayColor, Action.newTooltip("Any color you like ... as long as it's in the list."));
		Tooltip.install(cbRandomLoginScreen, Action.newTooltip("GistFX selects from the five options, randomly at startup"));
	}

	private void setControlProperties() {
		cbTheme.setValue(AppSettings.get().theme());
		cbSystemTrayColor.setValue(AppSettings.get().systrayColor());
		cbLoginScreenColor.setValue(AppSettings.get().loginScreenColor());
		progressBarColorPicker.setValue(AppSettings.get().progressCustomColor());
		cbCustomProgressColor.setSelected(!AppSettings.get().progressColorRandom());
		cbToolBar.setSelected(AppSettings.get().showToolBar());
		cbDisableWarning.setSelected(AppSettings.get().disableDirtyWarning());
		cbTrayIcon.setSelected(AppSettings.get().runInSystemTray());
		cbSystemTrayColor.setValue(AppSettings.get().systrayColor());
		cbProgressColorLogin.setSelected(AppSettings.get().progressColorLogin());
		cbRandomLoginScreen.setSelected(AppSettings.get().loginScreenRandom());
		progressBarColorPicker.visibleProperty().bind(cbCustomProgressColor.selectedProperty());
		progressBarColorPicker.disableProperty().bind(cbProgressColorLogin.selectedProperty());
		cbProgressColorLogin.visibleProperty().bind(cbCustomProgressColor.selectedProperty());
		cbSystemTrayColor.visibleProperty().bind(cbTrayIcon.selectedProperty());
		cbSystemTrayColor.disableProperty().bind(cbTrayIconLogin.selectedProperty());
		cbLoginScreenColor.disableProperty().bind(cbRandomLoginScreen.selectedProperty());
		cbTrayIconLogin.visibleProperty().bind(cbTrayIcon.selectedProperty());
		cbCustomProgressColor.setMinWidth(cBoxWidth);
		cbCustomProgressColor.setMaxWidth(cBoxWidth);
		cbCustomProgressColor.setPrefWidth(cBoxWidth);
		cbTheme.setMinWidth(choiceWidth);
		cbTheme.setMaxWidth(choiceWidth);
		cbTheme.setPrefWidth(choiceWidth);
		cbLoginScreenColor.setMinWidth(choiceWidth);
		cbLoginScreenColor.setMaxWidth(choiceWidth);
		cbLoginScreenColor.setPrefWidth(choiceWidth);
		cbSystemTrayColor.setMinWidth(choiceWidth);
		cbSystemTrayColor.setMaxWidth(choiceWidth);
		cbSystemTrayColor.setPrefWidth(choiceWidth);
		progressBarColorPicker.setMinWidth(choiceWidth);
		progressBarColorPicker.setMaxWidth(choiceWidth);
		progressBarColorPicker.setPrefWidth(choiceWidth);
		lblAppTheme.setMinWidth(labelWidth);
		lblAppTheme.setMaxWidth(labelWidth);
		lblAppTheme.setPrefWidth(labelWidth);
		lblAppTheme.setAlignment(Pos.CENTER_RIGHT);
		lblLoginScreenColor.setMinWidth(labelWidth);
		lblLoginScreenColor.setMaxWidth(labelWidth);
		lblLoginScreenColor.setPrefWidth(labelWidth);
		lblLoginScreenColor.setAlignment(Pos.CENTER_RIGHT);
		cbToolBar.setMinWidth(cBoxWidth);
		cbToolBar.setMaxWidth(cBoxWidth);
		cbToolBar.setPrefWidth(cBoxWidth);
		cbDisableWarning.setMinWidth(cBoxWidth);
		cbDisableWarning.setMaxWidth(cBoxWidth);
		cbDisableWarning.setPrefWidth(cBoxWidth);
		cbTrayIcon.setMinWidth(cBoxWidth);
		cbTrayIcon.setMaxWidth(cBoxWidth);
		cbTrayIcon.setPrefWidth(cBoxWidth);
	}

	private void setControlActions() {
		progressBarColorPicker.setOnAction(e -> {
			AppSettings.set().progressCustomColor(progressBarColorPicker.getValue());
			WindowManager.refreshPBarStyle();
		});
		cbCustomProgressColor.setOnAction(e -> {
			AppSettings.set().progressColorRandom(!cbCustomProgressColor.isSelected());
		});
		cbProgressColorLogin.setOnAction(e->{
			AppSettings.set().progressColorLogin(cbProgressColorLogin.isSelected());
		});
		cbTheme.setOnAction(e -> {
			Theme theme = cbTheme.getValue();
			AppSettings.set().theme(theme);
			LiveSettings.applyAppSettings();
			callingScene.getStylesheets().clear();
			callingScene.getStylesheets().add(AppSettings.get().theme().getStyleSheet());
			CodeEditor.get().getEditor().setCurrentTheme(LiveSettings.monacoThemeProperty().getValue());
			SceneOne.getScene("SettingWindow").getStylesheets().clear();
			SceneOne.getScene("SettingWindow").getStylesheets().add(AppSettings.get().theme().getStyleSheet());
		});
		cbLoginScreenColor.setOnAction(e->AppSettings.set().loginScreenColor(cbLoginScreenColor.getValue()));
		cbToolBar.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.set().showToolBar(newValue));
		cbDisableWarning.setOnAction(e -> {
			AppSettings.set().disableDirtyWarning(cbDisableWarning.isSelected());
			if(cbDisableWarning.isSelected())
				Platform.runLater(() -> CustomAlert.showInfo("Disabling this feature prevents GistFX from throwing a warning when you close the app, if you have data that has not been uploaded to GitHub.\n\nHowever, GistFX will automatically upload your unsaved data when you close the app, when this box is checked.", null));

		});
		cbTrayIcon.setOnAction(this::engageTrayIcon);
		cbTrayIconLogin.setOnAction(e -> {
			if(cbTrayIconLogin.isSelected()) {
				AppSettings.set().systrayColor(AppSettings.get().loginScreenColor());
			}
			else {
				AppSettings.set().systrayColor(cbSystemTrayColor.getValue());
			}
			TrayIcon.setGraphic();
		});
		cbSystemTrayColor.setOnAction(e-> {
			AppSettings.set().systrayColor(cbSystemTrayColor.getValue());
			TrayIcon.setGraphic();
		});
		cbRandomLoginScreen.setOnAction(e->{
			AppSettings.set().loginScreenRandom(cbRandomLoginScreen.isSelected());
		});
	}

	public VBox controls() {
		HBox boxTheme  = new HBox(10,lblAppTheme, cbTheme);
		HBox boxLoginScreenColor = new HBox(10,lblLoginScreenColor,cbLoginScreenColor,cbRandomLoginScreen);
		HBox boxDisableWarning  = new HBox(10,cbDisableWarning);
		HBox boxToolBar  = new HBox(cbToolBar);
		HBox boxTrayIcon  = new HBox(10,cbTrayIcon,cbSystemTrayColor,cbTrayIconLogin);
		HBox boxCustomColor  = new HBox(10,cbCustomProgressColor,progressBarColorPicker,cbProgressColorLogin);
		VBox vbox = new VBox(13,boxTheme,boxLoginScreenColor,boxToolBar,boxDisableWarning,boxTrayIcon,boxCustomColor);
		vbox.setPadding(new Insets(25,10,10,20));
		return vbox;
	}

	private void engageTrayIcon(ActionEvent e) {
		AppSettings.set().runInSystemTray(cbTrayIcon.isSelected());
		if (cbTrayIcon.isSelected())
			TrayIcon.show();
		else
			TrayIcon.hide();
	}
}
