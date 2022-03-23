package com.redmondsims.gistfx.preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.PaneState;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.ui.gist.CodeEditor;
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
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static com.redmondsims.gistfx.enums.PaneState.EXPANDED;
import static com.redmondsims.gistfx.enums.PaneState.REST;

public class UISettings {

	public enum Theme {

		DARK,
		LIGHT,
		PROGRESS_BAR,
		TEXT_FIELD,
		TEXT_AREA;

		public static final String darkCSS      = Objects.requireNonNull(Main.class.getResource("StyleSheets/Dark.css")).toExternalForm();
		public static final String lightCSS     = Objects.requireNonNull(Main.class.getResource("StyleSheets/Light.css")).toExternalForm();
		public static final String progressCSS  = Objects.requireNonNull(Main.class.getResource("StyleSheets/ProgressBar.css")).toExternalForm();
		public static final String textFieldCSS = Objects.requireNonNull(Main.class.getResource("StyleSheets/TextField.css")).toExternalForm();
		public static final String textAreaCSS  = Objects.requireNonNull(Main.class.getResource("StyleSheets/TextArea.css")).toExternalForm();

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

		public static Node getNode(Scene callingScene) {
			Label                 lblAppTheme     = newLabelTypeOne("Application Theme");
			ObservableList<Theme> themeList = FXCollections.observableList(Arrays.asList(DARK, LIGHT));
			ChoiceBox<Theme>      choiceBox = new ChoiceBox<>(themeList);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setValue(AppSettings.get().theme());
			choiceBox.setOnAction(e -> {
				Theme theme = choiceBox.getValue();
				AppSettings.set().theme(theme);
				LiveSettings.applyAppSettings();
				String styleSheet = theme.getStyleSheet();
				callingScene.getStylesheets().clear();
				callingScene.getStylesheets().add(styleSheet);
				toolWindow.setStyleSheet(styleSheet);
				CodeEditor.get().getEditor().setCurrentTheme(theme.equals(LIGHT) ? "vs-light" : "vs-dark");
			});
			Tooltip.install(choiceBox, Action.newTooltip("Which side of the Force will you chose?"));
			return newHBox(hboxLeft(lblAppTheme), getSpacedHBoxRight(choiceBox,3.5));
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
				case DARK -> 			darkCSS;
				case LIGHT -> 			lightCSS;
				case PROGRESS_BAR -> 	progressCSS;
				case TEXT_FIELD -> 		textFieldCSS;
				case TEXT_AREA -> 		textAreaCSS;
			};
		}

		private static File styleSheetFolder;
		private static File darkCSSFile;
		private static File lightCSSFile;
		private static File progressBarCSSFile;
		private static File textFieldCSSFile;
		private static File textAreaCSSFile;

	}

	public static void clearStyleSheets() {
		try {
			FileUtils.forceDelete(Theme.darkCSSFile);
			FileUtils.forceDelete(Theme.lightCSSFile);
			FileUtils.forceDelete(Theme.progressBarCSSFile);
			FileUtils.forceDelete(Theme.textFieldCSSFile);
			FileUtils.forceDelete(Theme.textAreaCSSFile);
			FileUtils.forceDelete(Theme.styleSheetFolder);
		}
		catch (IOException e) {
			e.printStackTrace();
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
			choiceBox.setOnAction(e -> {
				AppSettings.set().loginScreenChoice(choiceBox.getValue());
				LiveSettings.applyAppSettings();
			});
			choiceBox.setValue(AppSettings.get().loginScreenChoice());
			Tooltip.install(choiceBox, Action.newTooltip("Chose between the graphic login screen, or the JavaFX login screen."));
			return newHBox(hboxLeft(label), getSpacedHBoxRight(choiceBox,3));
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
			Label                            label     = newLabelTypeOne("Login Screen Color");
			ChoiceBox<LoginScreenColor>      choiceBox = new ChoiceBox<>(list);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setOnAction(e -> {
				AppSettings.set().loginScreenColor(choiceBox.getValue());
				LiveSettings.applyAppSettings();
			});
			choiceBox.setValue(AppSettings.get().loginScreenColor());
			choiceBox.visibleProperty().bind(LoginScreen.choiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(1));
			Tooltip.install(choiceBox, Action.newTooltip("""
															When using the graphic login screen (default setting)
															you can chose from the list, which color you prefer
															that screen to be."""));
			label.visibleProperty().bind(LoginScreen.choiceBox.getSelectionModel().selectedIndexProperty().isEqualTo(1));
			return newHBox(hboxLeft(label), getSpacedHBoxRight(choiceBox, 3));
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
			Label lblChkBox = newLabelTypeOne("Custom Progressbar Color");
			CheckBox checkBox = new CheckBox();
			Label lblColorPicker = newLabelTypeOne("Progressbar Color");
			ColorPicker colorPicker = new ColorPicker();
			colorPicker.setMaxWidth(100);
			colorPicker.setValue(AppSettings.get().progressBarColor());
			checkBox.setSelected(AppSettings.get().progressColorSource().equals(ProgressColorSource.USER_CHOICE));
			colorPicker.setOnAction(e->{
				AppSettings.set().progressBarColor(colorPicker.getValue());
				LiveSettings.applyAppSettings();
				String colorString = "#" + colorPicker.getValue().toString().replaceFirst("0x","").substring(0,6);
				String style ="-fx-accent: " + colorString + ";";
				AppSettings.set().progressBarStyle(style);
				WindowManager.setPBarStyle(style);
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
				AppSettings.set().progressColorSource(checkBox.isSelected() ? USER_CHOICE : RANDOM);
				LiveSettings.applyAppSettings();
			});
			HBox hboxCheck = newHBox(hboxLeft(lblChkBox), getSpacedHBoxRight(checkBox, 87));
			HBox hboxColor = newHBox(hboxLeft(lblColorPicker), getSpacedHBoxRight(colorPicker, 3.5));
			hboxColor.visibleProperty().bind(checkBox.selectedProperty());
			return newVBox(15, hboxCheck,hboxColor);
		}
	}

	private static final double     cbw  = 100;
	private static       ToolWindow toolWindow;
	private static final Gson       gson = new GsonBuilder().setPrettyPrinting().create();

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

	private static HBox newHBox(double spacing, Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(spacing);
		return hbox;
	}

	private static HBox hboxLeft(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_LEFT);
		return hbox;
	}

	private static Node theme(Scene callingScene) {
		return Theme.getNode(callingScene);
	}

	private static boolean checkValues(Double value1, Double value2, PaneState state) {
		boolean validateResult = false;
		if (state.equals(REST)) {
			validateResult = value1 < value2;
			if (!validateResult) {
				CustomAlert.showWarning("The value of the divider position at rest cannot be greater than the expanded value.");
			}
		}
		if (state.equals(EXPANDED)) {
			validateResult = value1 > value2;
			if (!validateResult) {
				CustomAlert.showWarning("The value of the divider position while expanded cannot be less than the at rest value");
			}
		}
		return validateResult;
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
		colorPicker.setOnAction(e-> LiveSettings.setDirtyFileFlagColor(colorPicker.getValue()));
		colorPicker.setMaxWidth(101);
		Tooltip.install(colorPicker, Action.newTooltip("Chose your desired color for the dirty file flag"));
		HBox hboxSetWarn 	= newHBox(hboxLeft(lblDisableWarning), 	getSpacedHBoxRight(disableWarningCheckBox, 57));
		HBox hboxColor 		= newHBox(hboxLeft(lblColorPicker),		getSpacedHBoxRight(colorPicker,1));
		VBox vbColorPicker = newVBox(15, hboxColor);
		vbColorPicker.setPadding(new Insets(0,0,10,0));
		return newVBox(10, vbColorPicker, hboxSetWarn);
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
		HBox hb = newHBox(hboxLeft(label), getSpacedHBoxRight(checkBox, 88));
		return hboxLeft(hb);
	}

	private static HBox getSpacedHBoxRight(Node node, double space) {
		Label dummy = new Label(" ");
		dummy.setMinWidth(space);
		HBox hbox = new HBox(dummy,node);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		return hbox;
	}

	private static Tooltip newTooltip(String message) {
		Tooltip tt = Action.newTooltip(message);
		tt.setShowDuration(Duration.seconds(120));
		return tt;
	}

	public static void showWindow(Stage callingStage) {
		Button btnReset = new Button("Reset Password And Token");
		btnReset.setOnAction(e -> Platform.runLater(() -> {
			if (CustomAlert.showConfirmation("Are you sure you want to delete your local Token and Password?\n\nYou will be required to type in a valid token the next time you launch GistFX.\nYou will also have the option of creating a new password at next login.")) {
				AppSettings.clear().passwordHash();
				AppSettings.clear().tokenHash();
				CustomAlert.showInfo("Done!", null);
			}
		}));
		HBox hboxButtonReset = newHBox(btnReset);
		VBox vboxButtonReset = newVBox(10,hboxButtonReset);
		vboxButtonReset.setPadding(new Insets(10,0,0,0));
		hboxButtonReset.setAlignment(Pos.CENTER);
		Label lblButtonBar = newLabelTypeOne("Show button bar when form loads");
		CheckBox chkButtonBar = new CheckBox();
		chkButtonBar.selectedProperty().addListener((observable, oldValue, newValue) -> AppSettings.set().showButtonBar(newValue));
		chkButtonBar.setSelected(AppSettings.get().showButtonBar());
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
		Tooltip.install(chkButtonBar, Action.newTooltip("""
								Show the dynamic button bar by default.
								Otherwise, you will need to toggle it on
								each time GistFX loads.
								
								The button bar changes depending on whats
								being shown on the screen or what you
								currently have selected in the window.
								
								The functionality of each button is also
								in the programs menu structure."""));
		HBox hboxBBChk   = newHBox(hboxLeft(lblButtonBar), getSpacedHBoxRight(chkButtonBar, 61));
		VBox formContent = new VBox(hboxBBChk,
									getWarningResetNode(),
									progressBarChoiceNode(),
									theme(callingStage.getScene()),
									loginScreen(),
									loginScreenColor(),
									getDirtyFileNode(),
									vboxButtonReset);
		formContent.setPadding(new Insets(10, 10, 10, 10));
		formContent.setSpacing(10);
		formContent.setAlignment(Pos.CENTER_LEFT);
		toolWindow = new ToolWindow.Builder(formContent).attachToStage(callingStage).size(320,530).title("GistFX User Options").build();
		toolWindow.showAndWait();
	}

}
