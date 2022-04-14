package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.Launcher;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.cryptology.Crypto;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.LoginStates;
import com.redmondsims.gistfx.enums.StyleSheet;
import com.redmondsims.gistfx.enums.Theme;
import com.redmondsims.gistfx.help.Help;
import com.redmondsims.gistfx.javafx.CProgressBar;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.redmondsims.gistfx.enums.LoginStates.*;
import static com.redmondsims.gistfx.preferences.UISettings.DataSource.GITHUB;
import static com.redmondsims.gistfx.preferences.UISettings.DataSource.LOCAL;
import static javafx.scene.layout.AnchorPane.*;

/**
 * This class needs a little discussion ... there are two methods which handle the 'busy' work of back end tasks. One is preCheck and the other, postCheck. preCheck
 * gathers the information we need to set up the login screen based on what the user has set up (save token locally or not etc.)  While post check handles all the scenarios that
 * can happen when the user either wants to log in, or create a new password etc. It goes through each possible scenario and returns an appropriate response which we then can act
 * on. For example, if the user has a password hash stored AND an encrypted token, then it returns the fact that both exist. If only one or the other exists, then it returns a
 * message indicating a need for reset.
 * <p>
 * Once the state of the users token and password are known, the appropriate action can be taken such as prompt for a password to log in, or prompt for a token, or inform the user
 * that something was wrong, and they need to reset their password etc. and THAT stuff is done in postCheck ... when the user enters the appropriate authentication (or new password etc.)
 * we call the postCheck method which handles the appropriate action based on what the user is trying to do ... validate their local password, decrypt their token and check that it's
 * valid with GitHub, or go through the process of resetting hte users password etc. etc. And each action has a response that we can act on if necessary.
 * <p>
 * Finally, we have buildScene(), which builds the GUI login window based on the results of the preCheck, and then postLoginTasks() which finishes everything off when
 * everything checks out and is in order.
 * <p>
 */

public class LoginWindow {

	static final         TextArea       taInfo               = new TextArea();
	private static final Label          lblProgress          = new Label(" ");
	private final        StringProperty userPassword         = new SimpleStringProperty("");
	private final        StringProperty userToken            = new SimpleStringProperty("");
	private final        TextField      tfToken;
	private final        PasswordField  tfPassword;
	private final        Button         buttonLogin;
	private final        AnchorPane     ap                   = new AnchorPane();
	private              int            passwordAttempts     = 0;
	private              boolean        passwordValid        = false;
	private              boolean        fadeKitty            = true;
	private              CProgressBar   pBar;
	private static final String         sceneId              = "LoginScreen";
	private              String         hashedPassword;
	private              String         hashedAccessToken;
	private              LoginStates    tokenStatus;
	private Theme startingTheme;


	/**
	 * For preCheck and password state check
	 */
	private boolean hasHashedPassword;
	private boolean hasHashedToken;
	private boolean noHashedToken;
	private boolean noHashedPassword;
	private boolean hasTypedToken;
	private boolean hasTypedPassword;

	public LoginWindow() {
		if (AppSettings.get().firstRun()) {
			Help.showIntro();
		}
		LiveSettings.applyAppSettings();
		tfToken         = newTextField();
		tfPassword      = newPasswordField("GistFX Password");
		buttonLogin     = new Button("Login to GitHub");
		preCheck();
		startLoginForm();
	}

	private void startLoginForm() {
		taInfo.setDisable(true);
		taInfo.textProperty().addListener((observable, oldValue, newValue) -> taInfo.setScrollTop(Double.MAX_VALUE));
		tfToken.setId("login");
		tfPassword.setText("");
		setControlProperties();
		startingTheme = AppSettings.get().theme();
		AppSettings.set().theme(Theme.DARK);
		loginForm();
		tfPassword.setText(LiveSettings.getPassword());
		if(tfPassword.getText().length() > 4) userLogin();
	}

	private static TextField newTextField() {
		TextField tf = new TextField("");
		tf.setPromptText("GitHub Token");
		Tooltip.install(tf, Action.newTooltip("Enter required information then press Enter"));
		return tf;
	}

	private static PasswordField newPasswordField(String prompt) {
		PasswordField pf = new PasswordField();
		pf.setPromptText(prompt);
		Tooltip.install(pf, new Tooltip("Enter required information then press Enter"));
		return pf;
	}

	public static void updateProgress(String info) {
		String message = info;
		if (info.startsWith("clear")) {
			taInfo.clear();
			message = info.replaceFirst("clear","");
		}
		taInfo.appendText(message + "\n");
		final String msg = message;
		Platform.runLater(() -> lblProgress.setText(msg));
	}

	public static void updateProgress(String info, boolean sameLine) {
		String message = sameLine ? info : info + "\n";
		if (info.startsWith("clear")) {
			taInfo.clear();
			message = info.replaceFirst("clear","");
		}
		taInfo.appendText(message);
		final String msg = message;
		Platform.runLater(() -> {
			if (info.startsWith("clear")) {lblProgress.setText("");}
			String current = lblProgress.getText();
			lblProgress.setText(current + " " + msg);
		});
	}

	private void setControlProperties() {
		userPassword.bind(tfPassword.textProperty());
		userToken.bindBidirectional(tfToken.textProperty());
		buttonLogin.setOnAction(e -> userLogin());
		tfToken.setOnAction(e -> userLogin());
		tfPassword.setOnAction(e -> userLogin());
		tfToken.setMinWidth(200);
		tfPassword.setMinWidth(200);
		tfToken.setPromptText("GitHub Token");
		tfPassword.setPromptText("GistFX Password");
	}

	private void resetToken() {
		if(CustomAlert.showConfirmation("Reset Token","Are you sure you wish to reset your token?\n\n(No locally saved data will be changed)")) {
			AppSettings.clear().hashedToken();
			AppSettings.clear().hashedPassword();
			tfToken.setPromptText("Paste Token (enter)");
			tfPassword.setPromptText("Type In New Password");
			tfToken.setEditable(true);
			tfPassword.setEditable(true);
			tfToken.setDisable(false);
			tfPassword.setDisable(false);
			tfToken.requestFocus();
		}
	}

	private void loginForm() {
		pBar = Action.getProgressBar(13);
		ap.setStyle("-fx-background-color: black");
		String    background       = "Artwork/%s/LoginForm/Background/BackMain.png";
		String    questionBase     = "Artwork/%s/LoginForm/QuestionMark.png";
		String    resetTokenBase   = "Artwork/%s/LoginForm/ResetToken.png";
		String    kittyKittyBase   = "Artwork/%s/LoginForm/KittyKitty.png";
		String      colorOption    = AppSettings.get().loginScreenColor().Name();
		InputStream pathBack       = Objects.requireNonNull(Launcher.class.getResourceAsStream(String.format(background, colorOption)));
		InputStream rtPath         = Objects.requireNonNull(Launcher.class.getResourceAsStream(String.format(resetTokenBase, colorOption)));
		InputStream qmPath         = Objects.requireNonNull(Launcher.class.getResourceAsStream(String.format(questionBase, colorOption)));
		InputStream kittyKittyPath = Objects.requireNonNull(Launcher.class.getResourceAsStream(String.format(kittyKittyBase, colorOption)));
		Image     imageBack        = new Image(pathBack);
		Image     imgReset         = new Image(rtPath);
		Image     imgQMark         = new Image(qmPath);
		Image     imgKittyKitty    = new Image(kittyKittyPath);
		ImageView ivBack           = new ImageView(imageBack);
		ImageView ivReset          = new ImageView(imgReset);
		ImageView ivQMark          = new ImageView(imgQMark);
		ImageView ivKittyKitty     = new ImageView(imgKittyKitty);
		tfPassword.setId(colorOption);
		tfToken.setId(colorOption);

		ivQMark.setPreserveRatio(true);
		ivQMark.setFitWidth(75);
		ivQMark.setFitHeight(75);
		ivQMark.setOnMouseClicked(e -> Help.showCreateTokenHelp());
		ivReset.setOnMouseClicked(e -> resetToken());
		ivBack.setPreserveRatio(true);
		ivBack.setFitWidth(700);
		ivReset.setPreserveRatio(true);
		ivReset.setFitWidth(700);
		ivKittyKitty.setPreserveRatio(true);
		ivKittyKitty.setFitWidth(700);

		addAPNode(ivBack, 0, 0, 0, 0);
		addAPNode(ivQMark, -1, 30, -1, 30);
		addAPNode(ivKittyKitty,0,0,0,0);
		addAPNode(tfToken, 184, -1, 216, -1);
		addAPNode(tfPassword, 184, -1, 269, -1);
		addAPNode(taInfo, 20, 250, 90, 125);
		addAPNode(pBar, 20, 20, -1, 118);
		addAPNode(ivReset, 0, 0, 0, 0);

		tfToken.setMinHeight(40);
		tfToken.setMinWidth(400);
		tfPassword.setMinHeight(40);
		tfPassword.setMinWidth(400);
		if(hasHashedToken && hasHashedPassword) {
			tfToken.setEditable(false);
			tfToken.setPromptText("Token Saved, Enter Password");
		}
		SceneOne.set(ap, sceneId)
				.newStage()
				.initStyle(StageStyle.TRANSPARENT)
				.size(700,323)
				.onCloseEvent(e -> System.exit(0))
				.styleSheets(StyleSheet.DARK.getStyleSheet())
				.show();
		fadeKitty(ivKittyKitty);
		if(noHashedToken) tfToken.requestFocus();
		else tfPassword.requestFocus();
		Editors.init();
	}

	private void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void fadeKitty(ImageView iv) {
		new Thread(() -> {
			iv.setOpacity(0);
			sleep(2500);
			while(fadeKitty) {
				for (double x = .02; x < .25; x+=.009) {
					final double opacity = x;
					Platform.runLater(() -> iv.setOpacity(opacity));
					sleep(75);
				}
				sleep(200);
				for (double x = .24; x >= .02; x-=.009) {
					final double opacity = x;
					Platform.runLater(() -> iv.setOpacity(opacity));
					sleep(75);
				}
				sleep(7000);
			}
		}).start();
	}

	private void stopFadeKitty() {
		fadeKitty = false;
	}

	private void addAPNode(Node node, double left, double right, double top, double bottom) {
		ap.getChildren().add(node);
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

	private void setBooleans() {
		hashedPassword    = AppSettings.get().hashedPassword();
		hashedAccessToken = AppSettings.get().hashedToken();
		hasHashedPassword = hashedPassword.length() != 0;
		hasHashedToken    = hashedAccessToken.length() != 0;
		noHashedToken     = !hasHashedToken;
		noHashedPassword  = !hasHashedPassword;
		hasTypedToken     = userToken.getValue().length() > 20;
		hasTypedPassword  = userPassword.getValue().length() > 5;
	}

	private void preCheck() {
		setBooleans();
	}

	private LoginStates checkToken(String token) {
		LoginStates ts = Action.tokenValid(token);
		if (ts.equals(TOKEN_VALID)) {
			Action.updateProgress("Token Valid");
			Action.loadMetaData();
		}
		if (ts.equals(TOKEN_FAILURE)) {
			Action.updateProgress("\nToken NOT Valid");
		}
		if (ts.equals(INTERNET_DOWN)) {
			Action.updateProgress("\nCannot Reach GitHub - Internet Down?");
		}
		else {
			tfPassword.setDisable(false);
			tfToken.setDisable(false);
		}
		if (ts == null) {
			System.out.println("Problem!");
			System.exit(0);
		}
		return ts;
	}

	private void checkPassword() {
		passwordValid = Crypto.validatePassword(userPassword.getValue(), hashedPassword);
		if (passwordValid) {
			Action.updateProgress("Password Valid");
			Crypto.setSessionKey(userPassword.getValue());
		}
		else {
			tfToken.setDisable(false);
			tfPassword.setDisable(false);
		}
	}

	private void userLogin() {
		setBooleans();
		new Thread(() -> {
			if (hasHashedPassword && hasTypedPassword && hasHashedToken) {
				Action.updateProgress("Checking Password...");
				checkPassword();
				if (passwordValid) {
					Crypto.setSessionKey(userPassword.get());
					Action.updateProgress("Checking Token...");
					if(hasTypedToken) {
						tokenStatus = checkToken(userToken.get());
						if(tokenStatus.equals(TOKEN_VALID)) {
							AppSettings.set().hashedToken(Crypto.encryptWithPassword(userToken.get(),userPassword.get()));
						}
					}
					else {
						tokenStatus = checkToken(Crypto.decryptWithPassword(hashedAccessToken, userPassword.get()));
					}
					if(tokenStatus.equals(TOKEN_VALID)) {
						new Thread(this::postLoginTasks).start();
					}
					else if (tokenStatus.equals(TOKEN_FAILURE)) {
						Platform.runLater(() -> {
							CustomAlert.showInfo("Your token is invalid. Perhaps it has expired?",SceneOne.getWindow(sceneId));
							tfToken.setDisable(false);
							tfToken.setEditable(true);
							tfPassword.setDisable(false);
							tfToken.clear();
							tfToken.requestFocus();
						});
					}
					else if(tokenStatus.equals(INTERNET_DOWN)) internetDown();

				}
				else {
					CustomAlert.showWarning("Incorrect Password", "The password you entered is incorrect. After six failed attempts, GistFX will reset your password and access token so that you can create a new password with a working access token.\n\nYou have " + (6 - passwordAttempts) + " attempts remaining.");
					passwordAttempts++;
					if (passwordAttempts > 5) {
						passwordAttempts = 0;
						AppSettings.clear().hashedPassword();
						AppSettings.clear().hashedToken();
						CustomAlert.showWarning("Mandatory Reset", "You have entered the wrong password too many times. Your Gist access token and password have both been reset. You can enter a new (or the same) access token and type in a new password.");
						Action.wipeSQLAndLocalData();
						AppSettings.set().dataSource(GITHUB);
						LiveSettings.applyAppSettings();
						tfPassword.clear();
						tfToken.clear();
						tfToken.requestFocus();
					}
				}
			}
			else if (noHashedPassword && hasTypedPassword && noHashedToken && hasTypedToken) {
				//Handle the creation of a new local password with an access token.
				tokenStatus = checkToken(userToken.get());
				if (tokenStatus.equals(TOKEN_VALID)) {
					boolean passwordsMatch = Password.verify(userPassword.getValue(), SceneOne.getStage(sceneId));
					if (passwordsMatch) {
						new Thread(() -> {
							Action.updateProgress("Hashing Password...");
							String passwordHash = Crypto.hashPassword(userPassword.getValue());
							AppSettings.set().hashedPassword(passwordHash);
							AppSettings.set().hashedToken(Crypto.encryptWithPassword(userToken.getValue(), userPassword.get()));
						}).start();
						Crypto.setSessionKey(userPassword.get());
						new Thread(this::postLoginTasks).start();
					}
					else {
						Platform.runLater(() -> {
							tfPassword.setDisable(false);
							tfPassword.setEditable(true);
							tfPassword.clear();
							tfPassword.requestFocus();
						});
					}
				}
				else {
					if(tokenStatus.equals(TOKEN_FAILURE)) {
						Platform.runLater(() -> {
							CustomAlert.showInfo("Your token is invalid. Perhaps it has expired?",SceneOne.getWindow(sceneId));
							tfToken.setDisable(false);
							tfToken.setEditable(true);
							tfPassword.setDisable(false);
							tfToken.clear();
							tfToken.requestFocus();
						});
					}
					if(tokenStatus.equals(INTERNET_DOWN)) internetDown();
				}
			}
			else {
				Platform.runLater(() -> {
					CustomAlert.showInfo("You must enter a valid token and password (Password needs to be more than 5 characters)\nIf you're having problems with known good credentials, click on Reset Token.",SceneOne.getWindow(sceneId));
					tfToken.setDisable(false);
					tfPassword.setDisable(false);
				});
			}
		}).start();
		buttonLogin.setDisable(true); //Lock button to prevent double clicking
		tfToken.setDisable(true);
		tfPassword.setDisable(true);
	}

	private void internetDown() {
		Platform.runLater(() -> {
			if(LiveSettings.getDataSource().equals(LOCAL)) {
				if(setOfflineMode()) {
					tfToken.setDisable(false);
					tfPassword.setDisable(false);
					tfToken.requestFocus();
					Action.updateProgress("clear");
				}
			}
			else {
				CustomAlert.showInfo("Could not contact GitHub Servers ... Internet Down?",SceneOne.getWindow(sceneId));
				tfToken.setDisable(false);
				tfPassword.setDisable(false);
				tfToken.requestFocus();
			}
		});
	}

	private boolean setOfflineMode() {
		if (CustomAlert.showConfirmation("It appears that your Internet is down.\n\nWould you like to work in setOfflineMode mode?")) {
			LiveSettings.setOfflineMode(true);
			return true;
		}
		return false;
	}

	private void postLoginTasks() {
		LiveSettings.applyAppSettings();
		if(LiveSettings.isOffline())
			Action.loadMetaData();
		if (LiveSettings.doMasterReset) {
			Platform.runLater(MasterResetWindow::new);
		}
		else {
			if(LiveSettings.getDataSource().equals(LOCAL)) {
				Action.updateProgress("Loading local Gist data and creating window");
			}
			Action.loadWindow();
			Platform.runLater(() -> pBar.progressProperty().unbind());
		}
		Platform.runLater(() -> SceneOne.hide(sceneId));
		AppSettings.set().theme(startingTheme);
		stopFadeKitty();
	}


}
