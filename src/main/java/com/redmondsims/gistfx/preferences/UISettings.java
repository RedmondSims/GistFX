package com.redmondsims.gistfx.preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.PaneState;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.ui.CodeEditor;
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

import java.util.Arrays;
import java.util.Objects;

import static com.redmondsims.gistfx.enums.PaneState.*;

public class UISettings {

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
			Tooltip.install(choiceBox, newTooltip("Which side of the Force will you chose?"));
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
			Label                       label     = newLabelTypeOne("Preferred Login Screen");
			choiceBox = new ChoiceBox<>(list);
			choiceBox.setPrefWidth(cbw);
			choiceBox.setOnAction(e -> {
				AppSettings.set().loginScreenChoice(choiceBox.getValue());
				LiveSettings.applyAppSettings();
			});
			choiceBox.setValue(AppSettings.get().loginScreenChoice());
			Tooltip.install(choiceBox, newTooltip("Chose between the graphic login screen, or the JavaFX login screen."));
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
			Tooltip.install(choiceBox, newTooltip("""
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
			Tooltip.install(checkBox, newTooltip("""
															If this is unchecked, GistFX will
															select a random color for the two
															progress bars from a pre-defined
															color list."""));
			Tooltip.install(colorPicker, newTooltip("""
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

	private static final double           cbw              = 100;
	private static ToolWindow toolWindow;
	private static       PaneSplitSetting paneSplitSetting = new PaneSplitSetting();

	private static final Gson             gson             = new GsonBuilder().setPrettyPrinting().create();

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

	private static Node saveJsonToGist() {
		Label label = newLabelTypeOne("Save Metadata To GitHub");
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(AppSettings.get().saveToGist());
		checkBox.setOnAction( e -> {
			if(!checkBox.isSelected()) {
				if(!CustomAlert.showConfirmation("Disabling this setting means that GistFXD will not be able to guarantee that the names you assign to your Gists will always be as you assign them no matter what happens to this installation of GistFX, since without this option being set, GistFX will need to rely on local storage for obtaining that data.\n\nAre you sure you wish to disable this option?")) {
					checkBox.setSelected(true);
					return;
				}
			}
			AppSettings.set().saveToGist(checkBox.isSelected());
			LiveSettings.applyAppSettings();
			Action.accommodateUserSettingChange();
		});
		Tooltip.install(checkBox, newTooltip("""
								Leave this box checked to allow GistFX
								to store your custom metadata in your
								GitHub account. The Gist that it uses
								is not public, and it will not show up
								as an editable gist in GistFX."""));
		HBox hb = newHBox(hboxLeft(label), getSpacedHBoxRight(checkBox, 88));
		return hboxLeft(hb);
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
		Tooltip.install(disableWarningCheckBox, newTooltip("""
																	If checked, this will prevent GistFX
																	from warning you that you have edited
																	Gists that have not been uploaded to GitHub."""));
		colorPicker.setValue(LiveSettings.getDirtyFileFlagColor());
		colorPicker.setOnAction(e-> LiveSettings.setDirtyFileFlagColor(colorPicker.getValue()));
		colorPicker.setMaxWidth(101);
		Tooltip.install(colorPicker, newTooltip("Chose your desired color for the dirty file flag"));
		HBox hboxSetWarn 	= newHBox(hboxLeft(lblDisableWarning), 	getSpacedHBoxRight(disableWarningCheckBox, 57));
		HBox hboxColor 		= newHBox(hboxLeft(lblColorPicker),		getSpacedHBoxRight(colorPicker,1));
		VBox vbColorPicker = newVBox(15, hboxColor);
		vbColorPicker.setPadding(new Insets(0,0,10,0));
		return newVBox(10, vbColorPicker, hboxSetWarn);
	}

	private static void savePaneSplitSettings(PaneState state, double value) {
		paneSplitSetting.setPosition(state,value);
		String json = gson.toJson(paneSplitSetting);
		AppSettings.set().dividerPositions(json);
	}

	private static VBox getPaneSplitNode() {
		Label label = newLabelTypeOne("Always start in Wide Mode");
		String jsonString = AppSettings.get().dividerPositions();
		if (!jsonString.equals("")) paneSplitSetting = gson.fromJson(jsonString,PaneSplitSetting.class);
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(LiveSettings.getWideMode());
		checkBox.setOnAction(e -> {
			AppSettings.set().wideMode(checkBox.isSelected());
			LiveSettings.applyAppSettings();
		});
		ChoiceBox<Double> cbRest = new ChoiceBox<>();
		ChoiceBox<Double> cbExpand = new ChoiceBox<>();
		ChoiceBox<Double> cbDefault = new ChoiceBox<>();
		ChoiceBox<Double> cbDefaultFull = new ChoiceBox<>();
		cbRest.setValue(paneSplitSetting.getPosition(REST));
		cbExpand.setValue(paneSplitSetting.getPosition(EXPANDED));
		cbDefault.setValue(paneSplitSetting.getPosition(DEFAULT));
		cbDefaultFull.setValue(paneSplitSetting.getPosition(DEFAULT_FULL));
		Button btnUseRefRest = new Button("Get");
		Button btnUseRefExpand = new Button("Get");
		Button btnUseRefDefault = new Button("Get");
		Button btnUseRefDefaultFull = new Button("Get");
		btnUseRefRest.setOnAction(e -> {
			Double value1 = Action.round(LiveSettings.getLastPaneSplitValue());
			Double value2 = cbExpand.getValue();
			if (checkValues(value1,value2,REST)) {
				cbRest.setValue(value1);
				savePaneSplitSettings(REST,value1);
			}
		});
		btnUseRefExpand.setOnAction(e -> {
			Double value1 = Action.round(LiveSettings.getLastPaneSplitValue());
			Double value2 = cbRest.getValue();
			if (checkValues(value1,value2,EXPANDED)) {
				cbExpand.setValue(value1);
				savePaneSplitSettings(EXPANDED,value1);
			}
		});
		btnUseRefDefault.setOnAction(e -> {
			Double value1 = Action.round(LiveSettings.getLastPaneSplitValue());
			Platform.runLater(() -> cbDefault.setValue(value1));
			savePaneSplitSettings(DEFAULT,value1);
		});
		btnUseRefDefaultFull.setOnAction(e -> {
			Double value1 = Action.round(LiveSettings.getLastPaneSplitValue());
			Platform.runLater(() -> cbDefaultFull.setValue(value1));
			savePaneSplitSettings(DEFAULT_FULL,value1);
		});
		ObservableList<Double> choices = FXCollections.observableArrayList();
		for (double x = .01 ; x <= .99 ; x+=.01) {
			choices.add(Action.round(x));
		}
		cbRest.getItems().setAll(choices);
		cbExpand.getItems().setAll(choices);
		cbDefault.getItems().setAll(choices);
		cbDefaultFull.getItems().setAll(choices);
		cbRest.valueProperty().addListener((o, oldV, newV) -> {
			if(!oldV.equals(newV)) {
				double current = oldV;
				double value1 = newV;
				double value2 = cbExpand.getValue();
				if (checkValues(value1,value2,REST)) {
					savePaneSplitSettings(REST,value1);
				}
				else {
					Platform.runLater(() -> cbRest.setValue(current));
				}
			}
		});
		cbExpand.valueProperty().addListener((o, oldV, newV) -> {
			if(!oldV.equals(newV)) {
				double current = oldV;
				double value1 = newV;
				double value2 = cbRest.getValue();
				if (checkValues(value1,value2,EXPANDED)) {
					savePaneSplitSettings(EXPANDED,value1);
				}
				else {
					Platform.runLater(() -> cbExpand.setValue(current));
				}
			}
		});
		cbDefault.valueProperty().addListener((o, oldV, newV) -> {
			double value = newV;
			savePaneSplitSettings(DEFAULT,value);
		});
		cbDefaultFull.valueProperty().addListener((o, oldV, newV) -> {
			double value = newV;
			savePaneSplitSettings(DEFAULT_FULL,value);
		});
		cbRest.setPrefWidth(cbw * .6);
		cbExpand.setPrefWidth(cbw * .6);
		cbDefault.setPrefWidth(cbw * .6);
		cbDefaultFull.setPrefWidth(cbw * .6);
		Tooltip.install(checkBox, newTooltip("""
								Checking this box tells GistFX to set the
								main window to wide mode when it first
								loads. Unchecking this box leaves the window
								in default mode. Wide mode can still be
								toggled regardless of this setting."""));
		Tooltip.install(cbRest, newTooltip("""
								This sets the position of the SplitPane
								divider when wide mode IS enabled and the
								mouse pointer is NOT hovering on the left
								side of the divider. This value must be
								smaller than the next one down."""));
		Tooltip.install(cbExpand, newTooltip("""
								This sets the position of the SplitPane
								divider when wide mode IS enabled and the
								mouse pointer is hovering on the left side
								of the divider. This value must be larger
								than the one above it."""));
		Tooltip.install(cbDefault, newTooltip("""
								This sets the position of the SplitPane
								divider when wide mode IS NOT enabled
								and you are NOT in fullscreen mode."""));
		Tooltip.install(cbDefaultFull, newTooltip("""
								This sets the position of the SplitPane
								divider when wide mode IS NOT enabled
								and you ARE in fullscreen mode."""));
		Tooltip.install(btnUseRefRest, newTooltip("""
								This button takes the last position value you
								set by moving the slider then applies it to the
								at rest position value of the slider when you
								switch to Wide Mode.
								
								This makes it easier to chose the value of the
								slider when the numbers don't always make sense."""));
		Tooltip.install(btnUseRefExpand, newTooltip("""
								This button takes the last position value you
								set by moving the slider then applies it to the
								expanded position value of the slider when you
								switch to Wide Mode.
								
								This makes it easier to chose the value of the
								slider when the numbers don't always make sense."""));
		Tooltip.install(btnUseRefDefault, newTooltip("""
								This button takes the last position value you
								set by moving the slider then applies it to the
								default position value of the slider when you
								are not in wide mode.
								
								This makes it easier to chose the value of the
								slider when the numbers don't always make sense."""));
		Tooltip.install(btnUseRefDefaultFull, newTooltip("""
								This button takes the last position value you
								set by moving the slider then applies it to the
								default position value of the slider when you
								are not in wide mode, but you are in full screen.
								
								This makes it easier to chose the value of the
								slider when the numbers don't always make sense."""));
		Label lblRest    = newLabelTypeTwo("\tRest Position");
		Label lblExpand  = newLabelTypeTwo("\tExpanded Position");
		Label lblDefault  = newLabelTypeTwo("\tDefault Position");
		Label lblDefaultFull  = newLabelTypeTwo("\tFull Screen Position");
		HBox hbCheckBox = newHBox(hboxLeft(label), getSpacedHBoxRight(checkBox, 88));
		HBox hbRest     = newHBox(hboxLeft(lblRest), getSpacedHBoxRight(newHBox(5,btnUseRefRest,cbRest),0));
		HBox hbExpand   = newHBox(hboxLeft(lblExpand), getSpacedHBoxRight(newHBox(5,btnUseRefExpand,cbExpand),0));
		HBox hbDefault   = newHBox(hboxLeft(lblDefault), getSpacedHBoxRight(newHBox(5,btnUseRefDefault,cbDefault),0));
		HBox hbDefaultFull   = newHBox(hboxLeft(lblDefaultFull), getSpacedHBoxRight(newHBox(5,btnUseRefDefaultFull,cbDefaultFull),0));
		return newVBox(5, hbCheckBox,hbRest,hbExpand,hbDefault,hbDefaultFull);
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
		Tooltip.install(checkBox, newTooltip("""
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
		Tooltip tt = new Tooltip(message);
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
		Tooltip.install(btnReset, newTooltip("""
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
		Tooltip.install(chkButtonBar, newTooltip("""
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
									saveJsonToGist(),
									getWarningResetNode(),
									getPaneSplitNode(),
									progressBarChoiceNode(),
									theme(callingStage.getScene()),
									loginScreen(),
									loginScreenColor(),
									getDirtyFileNode(),
									vboxButtonReset);
		formContent.setPadding(new Insets(10, 10, 10, 10));
		formContent.setSpacing(10);
		formContent.setAlignment(Pos.CENTER_LEFT);
		toolWindow = new ToolWindow.Builder(formContent,320,630,callingStage).title("GistFX User Options").build();
		toolWindow.showAndWait();
	}

}
