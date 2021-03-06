package com.redmondsims.gistfx.preferences;

import com.redmondsims.gistfx.Launcher;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.ui.trayicon.TrayIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Objects;

public class UISettings {

	public enum Theme {

		DARK,
		LIGHT,
		PROGRESS_BAR,
		TEXT_FIELD,
		TEXT_AREA,
		ANCHOR_PANE,
		LABEL;

		public static final String darkCSS       = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Dark.css")).toExternalForm();
		public static final String lightCSS      = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Light.css")).toExternalForm();
		public static final String progressCSS   = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/ProgressBar.css")).toExternalForm();
		public static final String textFieldCSS  = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/TextField.css")).toExternalForm();
		public static final String textAreaCSS   = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/TextArea.css")).toExternalForm();
		public static final String anchorPaneCSS = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/AnchorPane.css")).toExternalForm();
		public static final String labelCSS      = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Label.css")).toExternalForm();

		public static String get(Theme theme) {
			return switch (theme) {
				case DARK -> "Dark";
				case LIGHT -> "Light";
				case PROGRESS_BAR -> "Progress";
				case TEXT_FIELD -> "TextField";
				case TEXT_AREA -> "TextArea";
				case ANCHOR_PANE -> "AnchorPane";
				case LABEL -> "Label";
			};
		}

		public static Theme get(String theme) {
			return switch (theme) {
				case "Dark" -> DARK;
				case "Light" -> LIGHT;
				case "progress" -> PROGRESS_BAR;
				default -> null;
			};
		}

		public static Node getNode(Scene callingScene) {
			Label                 lblAppTheme     = newLabelTypeOne("Application Theme");
			ObservableList<Theme> themeList = FXCollections.observableList(Arrays.asList(DARK, LIGHT));
			ChoiceBox<Theme>      choiceBox = new ChoiceBox<>(themeList);
			choiceBox.setPrefWidth(cbw);
/*
			choiceBox.setValue(AppSettings.get().theme());
			choiceBox.setOnAction(e -> {
				Theme theme = choiceBox.getValue();
				AppSettings.set().theme(theme);
				LiveSettings.applyAppSettings();
				String styleSheet = theme.getStyleSheet();
				callingScene.getStylesheets().clear();
				callingScene.getStylesheets().add(styleSheet);
				toolWindow.setStyleSheet(styleSheet);
				CodeEditor.get().getEditor().setCurrentTheme(AppSettings.get().theme(LIGHT) ? "vs-light" : "vs-dark");
			});
*/
			Tooltip.install(choiceBox, Action.newTooltip("Which side of the Force will you chose?"));
			return newHBox(hBoxLeft(lblAppTheme), getSpacedHBoxRight(choiceBox, 3.5));
		}

		public String Name(Theme this) {
			return get(this);
		}

		public String getStyleSheet(Theme this) {
			return switch (this) {
				case DARK -> 			darkCSS;
				case LIGHT -> 			lightCSS;
				case PROGRESS_BAR -> 	progressCSS;
				case TEXT_FIELD -> 		textFieldCSS;
				case TEXT_AREA -> 		textAreaCSS;
				case ANCHOR_PANE -> 	anchorPaneCSS;
				case LABEL -> 	labelCSS;
			};
		}

		@Override public String toString() {
			return Name();
		}

	}

	public enum DataSource {
		LOCAL,
		GITHUB;

		public static String get(DataSource pref) {
			return switch (pref) {
				case LOCAL -> "local";
				case GITHUB -> "git";
			};
		}

		public static DataSource get(String pref) {
			return switch (pref) {
				case "local" -> LOCAL;
				case "git" -> GITHUB;
				default -> null;
			};
		}

		public String Name(DataSource this) {
			return switch (this) {
				case LOCAL -> "local";
				case GITHUB -> "git";
			};
		}
	}

	public enum LoginScreen {
		STANDARD,
		GRAPHIC,
		PASSWORD_LOGIN,
		TOKEN_LOGIN,
		UNKNOWN;

		public static String get(LoginScreen pref) {
			return switch (pref) {
				case STANDARD -> "standard";
				case GRAPHIC -> "graphic";
				case PASSWORD_LOGIN -> "password.login";
				case TOKEN_LOGIN -> "token.login";
				case UNKNOWN -> "unknown";
			};
		}

		public static LoginScreen get(String pref) {
			return switch (pref) {
				case "standard" -> STANDARD;
				case "graphic" -> GRAPHIC;
				case "password.login" -> PASSWORD_LOGIN;
				case "token.login" -> TOKEN_LOGIN;
				case "unknown" -> UNKNOWN;
				default -> null;
			};
		}

		public static ChoiceBox<LoginScreen> choiceBox;

		public static HBox getNode() {
			ObservableList<LoginScreen> list      = FXCollections.observableArrayList(LoginScreen.STANDARD, LoginScreen.GRAPHIC);
			Label                       label     = newLabelTypeOne("Preferred Login Screen");
			choiceBox = new ChoiceBox<>(list);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setOnAction(e -> LiveSettings.applyAppSettings());
			Tooltip.install(choiceBox, Action.newTooltip("Chose between the graphic login screen, or the JavaFX login screen."));
			return newHBox(hBoxLeft(label), getSpacedHBoxRight(choiceBox, 3));
		}

		public String Name(LoginScreen this) {
			return switch (this) {
				case STANDARD -> "standard";
				case GRAPHIC -> "graphic";
				case PASSWORD_LOGIN -> "password.login";
				case TOKEN_LOGIN -> "token.login";
				case UNKNOWN -> "unknown";
			};
		}

	}

	public enum Colors {
		RED,
		GREEN,
		BLUE,
		YELLOW,
		HOTPINK;

		public static String get(Colors pref) {
			return switch (pref) {
				case RED -> "Red";
				case GREEN -> "Green";
				case BLUE -> "Blue";
				case YELLOW -> "Yellow";
				case HOTPINK -> "Hotpink";
			};
		}

		public String folderName(Colors pref) {
			return switch(pref) {
				case RED -> "Red";
				case GREEN -> "Green";
				case BLUE -> "Blue";
				case YELLOW -> "Yellow";
				case HOTPINK -> "HotPink";
			};
		}

		private static ObservableList<Colors> colorList () {
			return FXCollections.observableArrayList(
					Colors.RED,
					Colors.GREEN,
					Colors.BLUE,
					Colors.YELLOW,
					Colors.HOTPINK);
		}

		public static Colors get(String pref) {
			return switch (pref) {
				case "Red" -> RED;
				case "Green" -> GREEN;
				case "Blue" -> BLUE;
				case "Yellow" -> YELLOW;
				case "Hotpink" -> HOTPINK;
				default -> null;
			};
		}

		public static HBox getNode() {
			ObservableList<Colors> list      = colorList();
			Label                            label     = newLabelTypeOne("Login Screen Color");
			ChoiceBox<Colors>      choiceBox = new ChoiceBox<>(list);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setOnAction(e -> {
				LiveSettings.applyAppSettings();
			});
			choiceBox.visibleProperty().bind(LoginScreen.choiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(1));
			Tooltip.install(choiceBox, Action.newTooltip("""
															When using the graphic login screen (default setting)
															you can chose from the list, which color you prefer
															that screen to be."""));
			label.visibleProperty().bind(LoginScreen.choiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(1));
			return newHBox(hBoxLeft(label), getSpacedHBoxRight(choiceBox, 3));
		}

		public String Name(Colors this) {
			return switch (this) {
				case RED -> "Red";
				case GREEN -> "Green";
				case BLUE -> "Blue";
				case YELLOW -> "Yellow";
				case HOTPINK -> "Hotpink";
			};
		}

	}

	public enum ProgressBarColor {

		GREEN,
		YELLOW,
		ORANGE,
		RED,
		BLUE,
		CYAN,
		HOTPINK,
		OCEAN,
		BLACK;

		public static ProgressBarColor get(String color) {
			return switch (color) {
				case "green-bar" -> GREEN;
				case "yellow-bar" -> YELLOW;
				case "orange-bar" -> ORANGE;
				case "red-bar" -> RED;
				case "blue-bar" -> BLUE;
				case "cyan-bar" -> CYAN;
				case "hotpink-bar" -> HOTPINK;
				case "ocean-bar" -> OCEAN;
				default -> null;
			};
		}

		public String getStyleClass(ProgressBarColor this) {
			return Name();
		}

		public String Name() {
			return switch (this) {
				case GREEN -> "green-bar";
				case YELLOW -> "yellow-bar";
				case ORANGE -> "orange-bar";
				case RED -> "red-bar";
				case BLUE -> "blue-bar";
				case CYAN -> "cyan-bar";
				case HOTPINK -> "hotpink-bar";
				case OCEAN -> "ocean-bar";
				case BLACK -> "black-bar";
			};
		}

	}

	public enum ProgressColorSource {
		RANDOM,
		USER_CHOICE;

		private static final String userChoice   = "USER_CHOICE";
		private static final String randomString = "RANDOM";

		public static ProgressColorSource get(String pref) {
			return switch (pref) {
				case randomString -> RANDOM;
				case userChoice -> USER_CHOICE;
				default -> null;
			};

		}

		public String getName(ProgressColorSource this) {
			return switch (this) {
				case RANDOM -> randomString;
				case USER_CHOICE -> userChoice;
			};
		}

		private static Node getNode() {
			Label lblChkBox = newLabelTypeOne("Custom Progressbar Color");
			CheckBox checkBox = new CheckBox();
			Label lblColorPicker = newLabelTypeOne("Progressbar Color");
			ColorPicker colorPicker = new ColorPicker();
			colorPicker.setMaxWidth(100);
			colorPicker.setValue(AppSettings.get().progressBarColor());
			colorPicker.setOnAction(e->{
				LiveSettings.applyAppSettings();
				String colorString = "#" + colorPicker.getValue().toString().replaceFirst("0x","").substring(0,6);
				String style ="-fx-accent: " + colorString + ";";
			});
			Tooltip.install(checkBox, Action.newTooltip("""
															If this is unchecked, GistFX will
															select a random color for the two
															progress bars from a pre-defined
															color list."""));
			Tooltip.install(colorPicker, Action.newTooltip("""
															Set the color of the two progress
															bars: The one on the login screen
															and the one in the main window.
															"""));
			checkBox.setOnAction(e -> {
				LiveSettings.applyAppSettings();
			});
			HBox hBoxCheck = newHBox(hBoxLeft(lblChkBox), getSpacedHBoxRight(checkBox, 87.8));
			HBox hBoxColor = newHBox(hBoxLeft(lblColorPicker), getSpacedHBoxRight(colorPicker, 3.5));
			hBoxColor.visibleProperty().bind(checkBox.selectedProperty());
			return newVBox(15, hBoxCheck,hBoxColor);
		}
	}

	private static final double     cbw  = 100;
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
		return label;
	}

	private static VBox newVBox(double spacing, Node... nodes) {
		VBox vbox = new VBox(nodes);
		vbox.setSpacing(spacing);
		return vbox;
	}

	private static HBox newHBox(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(20);
		return hbox;
	}

	private static HBox hBoxLeft(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_LEFT);
		return hbox;
	}

	private static Node theme(Scene callingScene) {
		return Theme.getNode(callingScene);
	}

	private static Node loginScreen() {
		return LoginScreen.getNode();
	}

	private static Node loginScreenColor() {
		return Colors.getNode();
	}

	private static Node progressBarChoiceNode() {
		return ProgressColorSource.getNode();
	}

	private static VBox getDirtyFileNode() {
		Label lblDisableWarning = newLabelTypeTwo("\tDisable Dirty File Exit Warning");
		Label lblColorPicker = newLabelTypeOne("Dirty File Flag Color");
		CheckBox disableWarningCheckBox = new CheckBox();
		ColorPicker colorPicker = new ColorPicker();
		disableWarningCheckBox.setSelected(LiveSettings.disableDirtyWarning());
		disableWarningCheckBox.setOnAction(e -> {
			LiveSettings.setDisableDirtyWarning(disableWarningCheckBox.isSelected());
			if(disableWarningCheckBox.isSelected()) {
				Platform.runLater(() -> CustomAlert.showInfo("Disabling this feature prevents GistFX from throwing a warning when you close the app, if you have data that has not been uploaded to GitHub.\n\nHowever, GistFX will automatically upload your unsaved data when you close the app, when this box is checked.", null));
			}
		});
		Tooltip.install(disableWarningCheckBox, Action.newTooltip("""
																	If checked, this will prevent GistFX
																	from warning you that you have edited
																	Gists that have not been uploaded to GitHub."""));
		colorPicker.setValue(LiveSettings.getDirtyFileFlagColor());
		colorPicker.setOnAction(e-> {
			LiveSettings.setDirtyFileFlagColor(colorPicker.getValue());
			WindowManager.refreshTreeIcons();
		});
		colorPicker.setMaxWidth(101);
		Tooltip.install(colorPicker, Action.newTooltip("Chose your desired color for the dirty file flag"));
		HBox hBoxSetWarn 	= newHBox(hBoxLeft(lblDisableWarning), getSpacedHBoxRight(disableWarningCheckBox, 57));
		HBox hBoxColor 		= newHBox(hBoxLeft(lblColorPicker), getSpacedHBoxRight(colorPicker, 1));
		VBox vbColorPicker = newVBox(15, hBoxColor);
		vbColorPicker.setPadding(new Insets(0,0,10,0));
		return newVBox(10, vbColorPicker, hBoxSetWarn);
	}

	private static VBox getSysTrayNode() {
		Label lblSysTrayOption = newLabelTypeTwo("\tRun GistFX from System Tray");
		Label lblShowAppIcon = newLabelTypeTwo(  "\tShow Application Icon");
		CheckBox cbLaunchInSystray = new CheckBox();
		CheckBox cbShowAppIcon = new CheckBox();
		ChoiceBox<String> cbColor = new ChoiceBox<>();
		cbColor.getItems().add("White");
		cbColor.getItems().add("Black");
		//cbColor.setValue(AppSettings.get().trayIconColor());
		cbColor.visibleProperty().bind(cbLaunchInSystray.selectedProperty());
		cbShowAppIcon.visibleProperty().bind(cbLaunchInSystray.selectedProperty());
		lblShowAppIcon.visibleProperty().bind(cbLaunchInSystray.selectedProperty());
		cbLaunchInSystray.setSelected(AppSettings.get().runInSystemTray());
		cbShowAppIcon.setSelected(AppSettings.get().showAppIcon());
		cbLaunchInSystray.setOnAction(e -> {
			AppSettings.set().runInSystemTray(cbLaunchInSystray.isSelected());
			if(cbLaunchInSystray.isSelected())
				TrayIcon.show();
			else
				TrayIcon.hide();
		});
		cbShowAppIcon.setOnAction(e -> {
			AppSettings.set().showAppIcon(cbShowAppIcon.isSelected());
		});
		cbColor.setOnAction(e->{
			String selection = cbColor.getValue();
			if(selection == null || selection.isEmpty()) {
				selection = "White";
			}
		//	AppSettings.set().trayIconColor(selection);
			cbColor.setValue(selection);
		});
		Tooltip.install(cbLaunchInSystray, Action.newTooltip("""
																	If checked, this will cause GistFX to live
																	in the System tray on your desktop, making
																	it readily available (restart is required)."""));
		Tooltip.install(cbColor, Action.newTooltip("""
																	If your desktop theme is light, then chose
																	Black, otherwise, leave it White (default)."""));
		Tooltip.install(cbShowAppIcon, Action.newTooltip("""
																	Normally, when you run an app from the
																	system tray, the standard running icon in
																	the Dock (Mac) or the Taskbar (Windows) is
																	not there. Checking this box keeps it there,
																	making it easier to get back to GistFX.
																	
																	By unchecking this box, GistFX will hide
																	once it loses focus (you click somewhere
																	else, but you can quickly un-hide it from
																	the system tray icon. This can help reduce
																	clutter on your desktop."""));
		HBox hBoxSetTray = newHBox(hBoxLeft(lblSysTrayOption), getSpacedHBoxRight(cbLaunchInSystray, 59));
		HBox hBoxSetAppIcon = newHBox(hBoxLeft(lblShowAppIcon), getSpacedHBoxRight(cbShowAppIcon, 86));
		HBox hBoxColor   = newHBox(getSpacedHBoxRight(cbColor, 210));
		return newVBox(10,hBoxSetTray,hBoxSetAppIcon,hBoxColor);
	}

	private static HBox getWarningResetNode() {
		Label label = newLabelTypeOne("Reset Warning Dialogs");
		CheckBox checkBox = new CheckBox();
		checkBox.setOnAction( e -> {
			if(checkBox.isSelected()) {
				AppSettings.clear().fileMoveWarning();
				AppSettings.clear().disableDirtyWarning();
			}
		});
		Tooltip.install(checkBox, Action.newTooltip("""
								Checking this box will immediately reset
								any warning dialogs that you chose to not
								show again.
								"""));
		HBox hb = newHBox(hBoxLeft(label), getSpacedHBoxRight(checkBox, 88));
		return hBoxLeft(hb);
	}

	private static HBox getSpacedHBoxRight(Node node, double space) {
		Label dummy = new Label(" ");
		dummy.setMinWidth(space);
		HBox hbox = new HBox(dummy,node);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		return hbox;
	}

	public static void showWindow(Stage callingStage) {
		Button btnReset = new Button("Reset Password And Token");
		btnReset.setOnAction(e -> Platform.runLater(() -> {
			if (CustomAlert.showConfirmation("Are you sure you want to delete your local Token and Password?\n\nYou will be required to type in a valid token the next time you launch GistFX.\nYou will also have the option of creating a new password at next login.")) {
				AppSettings.clear().hashedPassword();
				AppSettings.clear().hashedToken();
				CustomAlert.showInfo("Done!", null);
			}
		}));
		HBox hBoxButtonReset = newHBox(btnReset);
		VBox vboxButtonReset = newVBox(10,hBoxButtonReset);
		vboxButtonReset.setPadding(new Insets(10,0,0,0));
		hBoxButtonReset.setAlignment(Pos.CENTER);
		Label lblToolBar = newLabelTypeOne("Show tool bar when form loads");
		CheckBox chkToolBar = new CheckBox();
		chkToolBar.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.set().showToolBar(newValue));
		chkToolBar.setSelected(AppSettings.get().showToolBar());
		Tooltip.install(btnReset, Action.newTooltip("""
								Click this button to have GistFX
								go into master reset mode the next
								time you launch GistFX.
								
								When in master reset mode, you will
								be taken to a screen after you login
								which has various options for re-setting
								different facets of GistFX such as
								wiping out the database, or resetting
								your token and password or all of your
								custom data etc., depending on your needs.
								
								You are not required to authenticate to
								GitHub when this mode is enabled so you
								can just hit enter at the login screen,
								however, you cannot remove the GitHub
								stored version of your metadata without
								first authenticating."""));
		Tooltip.install(chkToolBar, Action.newTooltip("""
								Show the tool bar by default. Otherwise,
								you will need to toggle it on each time
								GistFX loads.
								
								The tool bar changes depending on whats
								being shown on the screen or what you
								currently have selected in the window.
								
								The functionality of each button is also
								in the programs menu structure."""));
		HBox hBoxBBChk   = newHBox(hBoxLeft(lblToolBar), getSpacedHBoxRight(chkToolBar, 75.8));
		VBox formContent = new VBox(hBoxBBChk,
									getWarningResetNode(),
									progressBarChoiceNode(),
									theme(callingStage.getScene()),
									loginScreen(),
									loginScreenColor(),
									getDirtyFileNode(),
									getSysTrayNode(),
									vboxButtonReset);
		formContent.setPadding(new Insets(10, 10, 10, 10));
		formContent.setSpacing(10);
		formContent.setAlignment(Pos.CENTER_LEFT);
		toolWindow = new ToolWindow.Builder(formContent).attachToStage(callingStage).size(320,530).title("GistFX User Options").build();
		toolWindow.showAndWait();
	}

}
