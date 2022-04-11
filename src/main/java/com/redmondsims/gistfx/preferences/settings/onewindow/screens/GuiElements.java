package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.gist.CodeEditor;
import com.redmondsims.gistfx.utils.Resources;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.Arrays;

import static com.redmondsims.gistfx.preferences.UISettings.ProgressColorSource.RANDOM;
import static com.redmondsims.gistfx.preferences.UISettings.ProgressColorSource.USER_CHOICE;
import static com.redmondsims.gistfx.preferences.UISettings.Theme.DARK;
import static com.redmondsims.gistfx.preferences.UISettings.Theme.LIGHT;
import static com.redmondsims.gistfx.preferences.settings.onewindow.screens.Utility.*;
import static com.redmondsims.gistfx.preferences.UISettings.LoginScreen.*;

public class GuiElements {


	public GuiElements(double sceneWidth) {
		this.callingScene = SceneOne.getScene(Resources.getSceneIdGistWindow());
		this.sceneWidth = sceneWidth;
		makeControls();
		setControlProperties();
		setControlActions();
		installTooltips();
	}

	private static final double cbw = 100;

	private final Scene callingScene;
	private final double sceneWidth;
	private ChoiceBox<UISettings.Theme>       cbTheme;
	private Label                             lblAppTheme;
	private ColorPicker                       progressBarColorPicker;
	private CheckBox                          cbCustomProgressColor;
	private CheckBox                          cbToolBar;
	private CheckBox                          cbDisableWarning;
	private Label                             lblColorPicker;
	private Label                             lblPreferredLoginScreen;
	private ChoiceBox<UISettings.LoginScreen> cbLoginScreen;


	private void makeControls() {
		ObservableList<UISettings.Theme>       themeList          = FXCollections.observableList(Arrays.asList(DARK, LIGHT));
		ObservableList<UISettings.LoginScreen> loginScreenOptions = FXCollections.observableArrayList(STANDARD, GRAPHIC);
		lblAppTheme             = newLabelTypeOne("Application Theme");
		lblColorPicker          = newLabelTypeOne("Progressbar Color");
		lblPreferredLoginScreen = newLabelTypeOne("Preferred Login Screen");
		cbTheme                 = new ChoiceBox<>(themeList);
		cbLoginScreen           = new ChoiceBox<>(loginScreenOptions);
		cbCustomProgressColor   = Utility.checkBoxLabelLeft("Custom Progress Bar Color");
		cbToolBar = Utility.checkBoxLabelLeft("Show Toolbar When Gist Window Loads");
		cbDisableWarning = Utility.checkBoxLabelLeft("Disable Dirty File Exit Warning");
		progressBarColorPicker  = new ColorPicker();
	}

	private void installTooltips() {
		Tooltip.install(cbToolBar, Action.newTooltip("Show the tool bar by default. Otherwise,\nyou will need to toggle it on each time\nGistFX loads.\n\nThe tool bar changes depending on what\nis currently selected in the window.\n\nThe functionality of each button is also\nin the programs menu structure."));
		Tooltip.install(cbTheme, Action.newTooltip("Which side of the Force will you chose?"));
		Tooltip.install(cbCustomProgressColor, Action.newTooltip("If this is unchecked, GistFX will\nselect a random color for the two\nprogress bars from a pre-defined\ncolor list"));
		Tooltip.install(progressBarColorPicker, Action.newTooltip("Set the color of the two progress\nbars: The one on the login screen\nand the one in the main window."));
		Tooltip.install(cbLoginScreen, Action.newTooltip("Choose between the graphic login screen\nor the JavaFX login screen."));
		Tooltip.install(cbDisableWarning, Action.newTooltip("If checked, this will prevent GistFX\nfrom warning you that you have edited\nGists that have not been uploaded to GitHub."));
	}

	private void setControlProperties() {
		cbTheme.setValue(AppSettings.get().theme());
		progressBarColorPicker.setValue(AppSettings.get().progressBarColor());
		cbCustomProgressColor.setSelected(AppSettings.get().progressColorSource().equals(USER_CHOICE));
		lblColorPicker.visibleProperty().bind(cbCustomProgressColor.selectedProperty());
		progressBarColorPicker.visibleProperty().bind(cbCustomProgressColor.selectedProperty());
		cbLoginScreen.setValue(AppSettings.get().loginScreenChoice());
		cbToolBar.setSelected(AppSettings.get().showToolBar());
		cbDisableWarning.setSelected(AppSettings.get().disableDirtyWarning());
		//lblColorPicker.setMaxWidth(100);
		lblColorPicker.setMinWidth(100);
		//lblColorPicker.setPrefWidth(100);
	}

	private void setControlActions() {
		progressBarColorPicker.setOnAction(e -> {
			AppSettings.set().progressBarColor(progressBarColorPicker.getValue());
			LiveSettings.applyAppSettings();
			String colorString = "#" + progressBarColorPicker.getValue().toString().replaceFirst("0x", "").substring(0, 6);
			String style       = "-fx-accent: " + colorString + ";";
			AppSettings.set().progressBarStyle(style);
			WindowManager.setPBarStyle(style);
		});
		cbCustomProgressColor.setOnAction(e -> {
			AppSettings.set().progressColorSource(cbCustomProgressColor.isSelected() ? USER_CHOICE : RANDOM);
			LiveSettings.applyAppSettings();
		});
		cbLoginScreen.setOnAction(e -> {
			AppSettings.set().loginScreenChoice(cbLoginScreen.getValue());
			LiveSettings.applyAppSettings();
		});
		cbTheme.setOnAction(e -> {
			UISettings.Theme theme = cbTheme.getValue();
			AppSettings.set().theme(theme);
			LiveSettings.applyAppSettings();
			callingScene.getStylesheets().clear();
			callingScene.getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
			CodeEditor.get().getEditor().setCurrentTheme(LiveSettings.monacoThemeProperty().getValue());
			SceneOne.getScene("SettingWindow").getStylesheets().clear();
			SceneOne.getScene("SettingWindow").getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		});
		cbToolBar.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.set().showToolBar(newValue));
		cbDisableWarning.setOnAction(e -> {
			AppSettings.set().disableDirtyWarning(cbDisableWarning.isSelected());
			if(cbDisableWarning.isSelected()) {
				Platform.runLater(() -> CustomAlert.showInfo("Disabling this feature prevents GistFX from throwing a warning when you close the app, if you have data that has not been uploaded to GitHub.\n\nHowever, GistFX will automatically upload your unsaved data when you close the app, when this box is checked.", null));
			}
		});
	}

	public VBox controls() {
		HBox boxTheme  = Utility.newHBox(lblAppTheme, cbTheme);
		HBox boxCustomColor  = Utility.newHBox(cbCustomProgressColor);
		HBox boxDisableWarning  = Utility.newHBox(cbDisableWarning);
		HBox boxToolBar  = Utility.newHBox(cbToolBar);
		HBox hBoxColorChoice = Utility.newHBox(Pos.CENTER_LEFT,30,-10,lblColorPicker, progressBarColorPicker);
		HBox boxLoginScreen = Utility.newHBox(lblPreferredLoginScreen, cbLoginScreen);
		VBox vbox = new VBox(boxTheme,boxLoginScreen,boxToolBar,boxDisableWarning,boxCustomColor,hBoxColorChoice);
		vbox.setPadding(new Insets(10,10,10,20));
		vbox.setSpacing(15);
		return vbox;
	}

}
