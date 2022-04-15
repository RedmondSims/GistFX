package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.ColorOption;
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
	private       ChoiceBox<Colors> cbTrayIconColor;
	private       Label             lblAppTheme;
	private       Label             lblLoginScreenColor;
	private       ColorPicker       progressBarColorPicker;
	private       CheckBox          cbxCustomProgressColor;
	private       CheckBox          cbxToolBar;
	private       CheckBox          cbxDisableWarning;
	private       CheckBox          cbxTrayIcon;
	private       CheckBox          cbxTrayIconLogin;
	private       CheckBox          cbxProgressColorLogin;
	private       CheckBox          cbxRandomLoginScreen;
	private       CheckBox          cbxSearchFileContents;
	private final double            choiceWidth = 100;
	private final double            labelWidth  = 215;
	private final double            cBoxWidth   = 217.5;

	private void makeControls() {
		lblAppTheme            = newLabelTypeOne("Application Theme");
		lblLoginScreenColor    = newLabelTypeOne("Login Screen Color");
		cbTheme                = new ChoiceBox<>(Theme.themeList());
		cbLoginScreenColor     = new ChoiceBox<>(Colors.loginScreenColorList());
		cbTrayIconColor        = new ChoiceBox<>(Colors.trayIconColorList());
		cbxCustomProgressColor = Utility.checkBoxLabelLeft("Custom Progress Bar Color");
		cbxToolBar             = Utility.checkBoxLabelLeft("Show Toolbar At Launch");
		cbxDisableWarning     = Utility.checkBoxLabelLeft("Disable Dirty File Exit Warning");
		cbxTrayIcon       = Utility.checkBoxLabelLeft("Run GistFX From System Tray");
		cbxTrayIconLogin = Utility.checkBoxLabelLeft("Login");
		cbxProgressColorLogin = Utility.checkBoxLabelLeft("Login");
		cbxRandomLoginScreen  = Utility.checkBoxLabelLeft("Random");
		cbxSearchFileContents  = Utility.checkBoxLabelLeft("Use File Contents In Tree Search");
		progressBarColorPicker = new ColorPicker();
	}

	private void installTooltips() {
		Tooltip.install(cbxToolBar, Action.newTooltip("Show the tool bar by default. Otherwise,\nyou will need to toggle it on each time\nGistFX loads.\n\nThe tool bar changes depending on what\nis currently selected in the window.\n\nThe functionality of each button is also\nin the programs menu structure."));
		Tooltip.install(cbTheme, Action.newTooltip("Which side of the Force will you chose?"));
		Tooltip.install(cbxCustomProgressColor, Action.newTooltip("If this is unchecked, GistFX will\nselect a random color for the two\nprogress bars from a pre-defined\ncolor list"));
		Tooltip.install(progressBarColorPicker, Action.newTooltip("Set the color of the progress bars."));
		Tooltip.install(cbxDisableWarning, Action.newTooltip("If checked, this will prevent GistFX\nfrom warning you that you have edited\nGists that have not been uploaded to GitHub."));
		Tooltip.install(cbxTrayIcon, Action.newTooltip("Run GistFX from your system tray. This can be be useful because GistFX will\nalways be at the ready and it won't clutter up your screen or your\ntaskbar. When you minimize the main window, it will be out of the way\nbut can always be called back by clicking on the G in the system tray"));
		Tooltip.install(cbxTrayIconLogin, Action.newTooltip("Match Tray Icon Color with login screen color choice"));
		Tooltip.install(cbxProgressColorLogin, Action.newTooltip("Match Progress Bar Color with login screen color choice"));
		Tooltip.install(cbTrayIconColor, Action.newTooltip("Any color you like ... as long as it's in the list."));
		Tooltip.install(cbxRandomLoginScreen, Action.newTooltip("GistFX selects from the five options, randomly at startup"));
		Tooltip.install(cbxSearchFileContents, Action.newTooltip("Includes the text in Gist files when performing a search in the tree"));
	}

	private void setControlProperties() {
		cbTheme.setValue(AppSettings.get().theme());
		cbTrayIconColor.setValue(AppSettings.get().trayIconColor());
		cbLoginScreenColor.setValue(AppSettings.get().loginScreenColor());
		progressBarColorPicker.setValue(AppSettings.get().progressCustomColor());
		cbxCustomProgressColor.setSelected(!AppSettings.get().progressColorRandom());
		cbxToolBar.setSelected(AppSettings.get().showToolBar());
		cbxDisableWarning.setSelected(AppSettings.get().disableDirtyWarning());
		cbxTrayIcon.setSelected(AppSettings.get().runInSystemTray());
		cbTrayIconColor.setValue(AppSettings.get().trayIconUserColor());
		cbxProgressColorLogin.setSelected(AppSettings.get().progressColorLogin());
		cbxRandomLoginScreen.setSelected(AppSettings.get().loginScreenRandom());
		cbxSearchFileContents.setSelected(AppSettings.get().searchFileContents());
		progressBarColorPicker.visibleProperty().bind(cbxCustomProgressColor.selectedProperty());
		progressBarColorPicker.disableProperty().bind(cbxProgressColorLogin.selectedProperty());
		cbxProgressColorLogin.visibleProperty().bind(cbxCustomProgressColor.selectedProperty());
		cbTrayIconColor.visibleProperty().bind(cbxTrayIcon.selectedProperty());
		cbTrayIconColor.disableProperty().bind(cbxTrayIconLogin.selectedProperty());
		cbLoginScreenColor.disableProperty().bind(cbxRandomLoginScreen.selectedProperty());
		cbxTrayIconLogin.visibleProperty().bind(cbxTrayIcon.selectedProperty());
		cbxCustomProgressColor.setMinWidth(cBoxWidth);
		cbxCustomProgressColor.setMaxWidth(cBoxWidth);
		cbxCustomProgressColor.setPrefWidth(cBoxWidth);
		cbTheme.setMinWidth(choiceWidth);
		cbTheme.setMaxWidth(choiceWidth);
		cbTheme.setPrefWidth(choiceWidth);
		cbLoginScreenColor.setMinWidth(choiceWidth);
		cbLoginScreenColor.setMaxWidth(choiceWidth);
		cbLoginScreenColor.setPrefWidth(choiceWidth);
		cbTrayIconColor.setMinWidth(choiceWidth);
		cbTrayIconColor.setMaxWidth(choiceWidth);
		cbTrayIconColor.setPrefWidth(choiceWidth);
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
		cbxToolBar.setMinWidth(cBoxWidth);
		cbxToolBar.setMaxWidth(cBoxWidth);
		cbxToolBar.setPrefWidth(cBoxWidth);
		cbxDisableWarning.setMinWidth(cBoxWidth);
		cbxDisableWarning.setMaxWidth(cBoxWidth);
		cbxDisableWarning.setPrefWidth(cBoxWidth);
		cbxTrayIcon.setMinWidth(cBoxWidth);
		cbxTrayIcon.setMaxWidth(cBoxWidth);
		cbxTrayIcon.setPrefWidth(cBoxWidth);
		cbxSearchFileContents.setMinWidth(cBoxWidth);
		cbxSearchFileContents.setMaxWidth(cBoxWidth);
		cbxSearchFileContents.setPrefWidth(cBoxWidth);

		switch(AppSettings.get().trayIconColorOption()) {
			case DEFAULT, USER_SELECTED -> {
				cbxTrayIconLogin.setSelected(false);
			}
			case FOLLOW_LOGIN -> {
				cbxTrayIconLogin.setSelected(true);
			}
		}
	}

	private void setControlActions() {
		progressBarColorPicker.setOnAction(e -> {
			AppSettings.set().progressCustomColor(progressBarColorPicker.getValue());
			WindowManager.refreshPBarStyle();
		});
		cbxCustomProgressColor.setOnAction(e -> {
			AppSettings.set().progressColorRandom(!cbxCustomProgressColor.isSelected());
		});
		cbxProgressColorLogin.setOnAction(e->{
			AppSettings.set().progressColorLogin(cbxProgressColorLogin.isSelected());
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
		cbxToolBar.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.set().showToolBar(newValue));
		cbxDisableWarning.setOnAction(e -> {
			AppSettings.set().disableDirtyWarning(cbxDisableWarning.isSelected());
			if(cbxDisableWarning.isSelected())
				Platform.runLater(() -> CustomAlert.showInfo("Disabling this feature prevents GistFX from throwing a warning when you close the app, if you have data that has not been uploaded to GitHub.\n\nHowever, GistFX will automatically upload your unsaved data when you close the app, when this box is checked.", null));

		});
		cbxTrayIcon.setOnAction(this::engageTrayIcon);
		cbxTrayIconLogin.setOnAction(e -> {
			if(cbxTrayIconLogin.isSelected()) {
				AppSettings.set().trayIconColorOption(ColorOption.FOLLOW_LOGIN);
			}
			else {
				AppSettings.set().trayIconColorOption(ColorOption.USER_SELECTED);
			}
			TrayIcon.setGraphic();
		});
		cbTrayIconColor.setOnAction(e-> {
			AppSettings.set().trayIconColorOption(ColorOption.USER_SELECTED);
			AppSettings.set().trayIconUserColor(cbTrayIconColor.getValue());
			TrayIcon.setGraphic();
		});
		cbxRandomLoginScreen.setOnAction(e->{
			AppSettings.set().loginScreenRandom(cbxRandomLoginScreen.isSelected());
		});
		cbxSearchFileContents.setOnAction(e->{
			AppSettings.set().searchFileContents(cbxSearchFileContents.isSelected());
		});
	}

	public VBox controls() {
		HBox boxTheme  = new HBox(10,lblAppTheme, cbTheme);
		HBox boxLoginScreenColor = new HBox(10, lblLoginScreenColor, cbLoginScreenColor, cbxRandomLoginScreen);
		HBox boxDisableWarning  = new HBox(10, cbxDisableWarning);
		HBox boxToolBar  = new HBox(cbxToolBar);
		HBox boxTrayIcon  = new HBox(10, cbxTrayIcon, cbTrayIconColor, cbxTrayIconLogin);
		HBox boxCustomColor  = new HBox(10, cbxCustomProgressColor, progressBarColorPicker, cbxProgressColorLogin);
		HBox boxSearchFileContents  = new HBox(10, cbxSearchFileContents);
		VBox vbox = new VBox(13,boxTheme,boxLoginScreenColor,boxTrayIcon,boxCustomColor,boxToolBar,boxDisableWarning,boxSearchFileContents);
		vbox.setPadding(new Insets(25,10,10,20));
		return vbox;
	}

	private void engageTrayIcon(ActionEvent e) {
		AppSettings.set().runInSystemTray(cbxTrayIcon.isSelected());
		if (cbxTrayIcon.isSelected()){
			TrayIcon.show();
			AppSettings.set().trayIconColorOption(ColorOption.USER_SELECTED);
		}
		else {
			TrayIcon.hide();
			AppSettings.set().trayIconColorOption(ColorOption.DEFAULT);
		}
	}
}
