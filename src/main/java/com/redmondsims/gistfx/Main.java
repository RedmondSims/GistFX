package com.redmondsims.gistfx;

import com.redmondsims.gistfx.cryptology.Crypto;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Colors;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.LoginWindow;
import com.redmondsims.gistfx.ui.Password;
import com.redmondsims.gistfx.ui.TreeIcons;
import com.redmondsims.gistfx.ui.trayicon.TrayIcon;
import com.redmondsims.gistfx.utils.Resources;
import com.redmondsims.gistfx.utils.Util;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static com.redmondsims.gistfx.enums.OS.MAC;

public class Main extends Application {


	private static final String  dockIconBase   = "Artwork/%s/Icons/AppleDock.png";
	private static final JFrame  jFrame         = new JFrame();
	private static       boolean changePassword = false;


	@Override public void start(Stage primaryStage) throws Exception {
		LiveSettings.init();
		Resources.init();
		TreeIcons.init();
		TrayIcon.start(primaryStage, AppSettings.get().runInSystemTray());
		AppSettings.clear().fileMoveWarning();
		LiveSettings.applyAppSettings();
		String      color       = LiveSettings.getLoginScreenColor().Name();
		String      urlPath     = String.format(dockIconBase, color);
		InputStream imageStream = Main.class.getResourceAsStream(urlPath);
		try {
			if (imageStream != null) {
				Image image = ImageIO.read(imageStream);
				LiveSettings.setTaskbar(Taskbar.getTaskbar());
				if (LiveSettings.getOS().equals(MAC)) {LiveSettings.getTaskbar().setIconImage(image);}
				else {
					jFrame.setUndecorated(true);
					jFrame.setIconImage(image);
					jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
					jFrame.pack();
					jFrame.setVisible(true);
					jFrame.setSize(new Dimension(26, 26));
				}
			}
		}
		catch (UnsupportedOperationException e) {
			System.out.println("This os does not support taskbar.setIconImage()");
		}
		catch (final SecurityException e) {
			System.out.println("There was a security exception for: 'taskbar.setIconImage'");
		}
		catch (IOException e) {
			e.printStackTrace();
		}


		LiveSettings.applyAppSettings();
		Action.setDatabaseConnection();
		setUserAgentStylesheet(STYLESHEET_MODENA);
		if (changePassword) {
			if (Password.change(primaryStage)) {
				System.out.println("Password Changed Successfully");
			}
			else {
				System.out.println("Password Not Changed");
			}
		}
		new LoginWindow();

	}

	public static void main(String[] args) {
		boolean stopFlag = false;
		LiveSettings.setTempIconSize(22);
		for (String arg : args) {
			if (arg.toLowerCase(Locale.ROOT).startsWith("newdatabase")) {
				com.redmondsims.gistfx.data.Action.deleteDatabaseFile();
				System.out.println("Database file has been reset");
				stopFlag = true;
			}
			if (arg.toLowerCase(Locale.ROOT).startsWith("clearcreds")) {
				AppSettings.clear().hashedToken();
				AppSettings.clear().hashedPassword();
				System.out.println("Credentials Cleared");
				stopFlag = true;
			}
			if (arg.toLowerCase(Locale.ROOT).startsWith("masterreset")) {
				LiveSettings.doMasterReset = true;
			}
			if (arg.toLowerCase().startsWith("password=")) {
				String password = arg.replaceFirst("password=", "");
				LiveSettings.setPassword(password);
			}
			if (arg.toLowerCase().startsWith("changepassword")) {
				changePassword = true;
				stopFlag       = true;
			}
			if (arg.toLowerCase().startsWith("wipesql")) {
				Action.wipeSQLOnly();
			}
			if (arg.toLowerCase().startsWith("iconsize=")) {
				String num   = arg.replaceFirst("iconsize=", "");
				int value = Integer.parseInt(num);
				LiveSettings.setTempIconSize(value);
			}
		}
		if (stopFlag) System.exit(100);
		launch(args);
	}
}
