package com.redmondsims.gistfx.preferences;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.ui.CodeEditor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.Objects;

public class UISettings {

	private static final double cbw   = 100;
	private static       Alert  alert = new Alert(Alert.AlertType.NONE);

	private static Label newLabel(String text) {
		Label label = new Label(text);
		label.setMinWidth(155);
		label.setAlignment(Pos.CENTER_LEFT);
		return label;
	}

	private static VBox newVBox(Node... nodes) {
		VBox vbox = new VBox(nodes);
		vbox.setSpacing(20);
		return vbox;
	}

	private static HBox newHBox(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(20);
		return hbox;
	}

	private static HBox hboxLeft(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_LEFT);
		return hbox;
	}

	private static HBox hboxRight(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		return hbox;
	}

	private static Node theme(Scene callingScene) {
		return Theme.getNode(callingScene);
	}

	private static Node loginScreen() {
		return LoginScreen.getNode();
	}

	private static Node loginScreenColor() {
		return LoginScreenColor.getNode();
	}

	private static Node progressBarChoiceNode() {
		return ProgressColorSource.getNode();
	}

	private static Node saveJsonToGist() {
		Label label = new Label("Save Names To Gist");
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(AppSettings.getSaveToGist());
		checkBox.setOnAction( e -> {
			if(!checkBox.isSelected()) {
				if(!CustomAlert.showConfirmation("Disabling this setting means that GistFXD will not be able to guarantee that the names you assign to your Gists will always be as you assign them no matter what happens to this installation of GistFX, since without this option being set, GistFX will need to rely on local storage for obtaining that data.\n\nAre you sure you wish to disable this option?")) {
					checkBox.setSelected(true);
					return;
				}
			}
			AppSettings.setSaveToGist(checkBox.isSelected());
			LiveSettings.applyAppSettings();
			Action.accommodateUserSettingChange();
		});
		Tooltip.install(checkBox,new Tooltip("Enables or disables saving custom Gist names to your GitHub account"));
		HBox hb = newHBox(hboxLeft(label),getCheckHBox(checkBox,125));
		return hboxLeft(hb);
	}

	public static void setStyleSheet(String styleSheet) {
		alert.getDialogPane().getStylesheets().clear();
		alert.getDialogPane().getStylesheets().add(styleSheet);
	}

	private static VBox getDirtyFileNode() {
		Label lblFlagDirty = newLabel("Show Dirty File Flag");
		Label lblDisableWarning = newLabel("\tDisable Exit Warning");
		CheckBox flagDirtyCheckBox = new CheckBox();
		CheckBox disableWarningCheckBox = new CheckBox();
		Label lblColorPicker = newLabel("\tFlag Color");
		lblColorPicker.setMinWidth(125);
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setValue(LiveSettings.getDirtyFileFlagColor());
		flagDirtyCheckBox.setSelected(LiveSettings.flagDirtyFiles());
		disableWarningCheckBox.setSelected(LiveSettings.disableDirtyWarning());
		colorPicker.setOnAction(e-> LiveSettings.setDirtyFileFlagColor(colorPicker.getValue()));
		HBox hboxFlagDirty = newHBox(hboxLeft(lblFlagDirty), getCheckHBox(flagDirtyCheckBox,77));
		HBox hboxColor = newHBox(hboxLeft(lblColorPicker),hboxRight(colorPicker));
		hboxColor.visibleProperty().bind(flagDirtyCheckBox.selectedProperty());
		HBox hboxSetWarn = newHBox(hboxLeft(lblDisableWarning), getCheckHBox(disableWarningCheckBox, 77));
		flagDirtyCheckBox.setOnAction(e -> LiveSettings.setFlagDirtyFiles(flagDirtyCheckBox.isSelected()));
		disableWarningCheckBox.setOnAction(e -> {
			LiveSettings.setDisableDirtyWarning(disableWarningCheckBox.isSelected());
			if(disableWarningCheckBox.isSelected()) {
				CustomAlert.showInfo("Disabling this feature prevents GistFX from throwing a warning when you close the app, if you have data that has not been uploaded to GitHub.\n\nHowever, GistFX will automatically upload your unsaved data when you close the app, when this box is checked.",null);
			}
		});
		return newVBox(hboxFlagDirty, hboxSetWarn, hboxColor);
	}

	public enum Theme {

		DARK,
		LIGHT,
		PROGRESS_BAR,
		TEXT_FIELD,
		TEXT_AREA;

		public static String darkCSS;
		public static String lightCSS;
		public static String progressCSS;
		public static String textFieldCSS;
		public static String textAreaCSS;

		public static String get(Theme theme) {
			return switch (theme) {
				case DARK -> "Dark";
				case LIGHT -> "Light";
				case PROGRESS_BAR -> "Progress";
				case TEXT_FIELD -> "TextField";
				case TEXT_AREA -> "TextArea";
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

		public static void init() {
			darkCSS      = Objects.requireNonNull(Main.class.getResource("StyleSheets/Dark.css")).toExternalForm();
			lightCSS     = Objects.requireNonNull(Main.class.getResource("StyleSheets/Light.css")).toExternalForm();
			progressCSS  = Objects.requireNonNull(Main.class.getResource("StyleSheets/ProgressBar.css")).toExternalForm();
			textFieldCSS = Objects.requireNonNull(Main.class.getResource("StyleSheets/TextField.css")).toExternalForm();
			textAreaCSS  = Objects.requireNonNull(Main.class.getResource("StyleSheets/TextArea.css")).toExternalForm();
		}

		public static Node getNode(Scene callingScene) {
			Label                 label     = newLabel("Application Theme");
			ObservableList<Theme> themeList = FXCollections.observableList(Arrays.asList(DARK, LIGHT));
			ChoiceBox<Theme>      choiceBox = new ChoiceBox<>(themeList);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setValue(AppSettings.getTheme());
			choiceBox.setOnAction(e -> {
				Theme theme = choiceBox.getValue();
				AppSettings.setTheme(theme);
				LiveSettings.applyAppSettings();
				callingScene.getStylesheets().clear();
				callingScene.getStylesheets().add(theme.getStyleSheet());
				UISettings.setStyleSheet(theme.getStyleSheet());
				CodeEditor.get().getEditor().setCurrentTheme(theme.equals(LIGHT) ? "vs-light" : "vs-dark");
			});
			return newHBox(hboxLeft(label), hboxRight(choiceBox));
		}

		public String Name(Theme this) {
			return switch (this) {
				case DARK -> "Dark";
				case LIGHT -> "Light";
				case PROGRESS_BAR -> "Progress";
				case TEXT_FIELD -> "TextField";
				case TEXT_AREA -> "TextArea";
			};
		}

		public String getStyleSheet(Theme this) {
			return switch (this) {
				case DARK -> darkCSS;
				case LIGHT -> lightCSS;
				case PROGRESS_BAR -> progressCSS;
				case TEXT_FIELD -> textFieldCSS;
				case TEXT_AREA -> textAreaCSS;
			};
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
			Label                       label     = newLabel("Preferred Login Screen");
			choiceBox = new ChoiceBox<>(list);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setOnAction(e -> {
				AppSettings.setLoginScreenChoice(choiceBox.getValue());
				LiveSettings.applyAppSettings();
			});
			choiceBox.setValue(AppSettings.getLoginScreenChoice());
			return newHBox(hboxLeft(label), hboxRight(choiceBox));
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

	public enum LoginScreenColor {
		RED,
		GREEN,
		BLUE,
		YELLOW,
		HOTPINK;

		public static String get(LoginScreenColor pref) {
			return switch (pref) {
				case RED -> "Red";
				case GREEN -> "Green";
				case BLUE -> "Blue";
				case YELLOW -> "Yellow";
				case HOTPINK -> "Hotpink";
			};
		}

		public String folderName(LoginScreenColor pref) {
			return switch(pref) {
				case RED -> "Red";
				case GREEN -> "Green";
				case BLUE -> "Blue";
				case YELLOW -> "Yellow";
				case HOTPINK -> "HotPink";
			};
		}

		private static ObservableList<LoginScreenColor> colorList () {
			return FXCollections.observableArrayList(
					LoginScreenColor.RED,
					LoginScreenColor.GREEN,
					LoginScreenColor.BLUE,
					LoginScreenColor.YELLOW,
					LoginScreenColor.HOTPINK);
		}

		public static LoginScreenColor get(String pref) {
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
			ObservableList<LoginScreenColor> list      = colorList();
			Label                            label     = newLabel("Login Screen Color");
			ChoiceBox<LoginScreenColor>      choiceBox = new ChoiceBox<>(list);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setOnAction(e -> {
				AppSettings.setLoginScreenColor(choiceBox.getValue());
				LiveSettings.applyAppSettings();
			});
			choiceBox.setValue(AppSettings.getLoginScreenColor());
			choiceBox.visibleProperty().bind(LoginScreen.choiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(1));
			label.visibleProperty().bind(LoginScreen.choiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(1));
			return newHBox(hboxLeft(label), hboxRight(choiceBox));
		}

		public String Name(LoginScreenColor this) {
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
			Label lblChkBox = newLabel("Custom Progressbar Color");
			lblChkBox.setMinWidth(160);
			CheckBox checkBox = new CheckBox();
			HBox hboxCheck = newHBox(hboxLeft(lblChkBox), getCheckHBox(checkBox,75));
			Label lblColorPicker = newLabel("Progressbar Color");
			lblColorPicker.setMinWidth(127);
			ColorPicker colorPicker = new ColorPicker();
			HBox hboxColor = newHBox(hboxLeft(lblColorPicker),hboxRight(colorPicker));
			colorPicker.setValue(AppSettings.getProgressBarColor());
			hboxColor.visibleProperty().bind(checkBox.selectedProperty());
			checkBox.setSelected(AppSettings.getProgressColorSource().equals(ProgressColorSource.USER_CHOICE));
			colorPicker.setOnAction(e->{
				AppSettings.setProgressBarColor(colorPicker.getValue());
				LiveSettings.applyAppSettings();
				String colorString = "#" + colorPicker.getValue().toString().replaceFirst("0x","").substring(0,6);
				String style ="-fx-accent: " + colorString + ";";
				AppSettings.setProgressBarStyle(style);
				WindowManager.setPBarStyle(style);
			});
			checkBox.setOnAction(e -> {
				AppSettings.setProgressColorSource(checkBox.isSelected() ? USER_CHOICE : RANDOM);
				LiveSettings.applyAppSettings();
			});
			return newVBox(hboxCheck,hboxColor);
		}
	}

	private static HBox getCheckHBox(Node checkBox, double space) {
		Label dummy = new Label(" ");
		dummy.setMinWidth(space);
		HBox hbox = new HBox(dummy,checkBox);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		return hbox;
	}

	public static void showWindow(Scene callingScene) {
		Button btnReset = new Button("Reset Password And Token");
		btnReset.setOnAction(e -> {
			if (CustomAlert.showConfirmation("Are you sure you want to delete your local Token and Password?\n\nYou will be requiured to type in a valid token the next time you launch GistFX.\nYou will also have the option of creating a new password at next login.")) {
				AppSettings.clearPasswordHash();
				AppSettings.clearTokenHash();
				CustomAlert.showInfo("Done!", null);
			}
		});
		HBox hboxButtonReset = newHBox(btnReset);
		hboxButtonReset.setAlignment(Pos.CENTER);
		Label lblButtonBar = new Label("Show button bar when form loads");
		CheckBox chkButtonBar = new CheckBox();
		chkButtonBar.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.setShowButtonBar(newValue));
		chkButtonBar.setSelected(AppSettings.getShowButtonBar());
		HBox hboxBBChk   = newHBox(hboxLeft(lblButtonBar),getCheckHBox(chkButtonBar,53));
		VBox formContent = new VBox(hboxBBChk,
									saveJsonToGist(),
									progressBarChoiceNode(),
									theme(callingScene),
									loginScreen(),
									loginScreenColor(),
									getDirtyFileNode(),
									hboxButtonReset);
		formContent.setPadding(new Insets(10, 10, 10, 10));
		formContent.setSpacing(20);
		formContent.setAlignment(Pos.CENTER);
		alert = new Alert(Alert.AlertType.NONE);
		alert.getButtonTypes().clear();
		alert.getButtonTypes().add(0, new ButtonType("Close", ButtonBar.ButtonData.LEFT));//For some reason, .LEFT centers the button when there is only one button in the alert
		alert.setTitle("GistFX Setting Options");
		alert.getDialogPane().setContent(formContent);
		alert.getDialogPane().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		alert.showAndWait();
	}

}
