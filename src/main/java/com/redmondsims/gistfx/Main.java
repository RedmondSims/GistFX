package com.redmondsims.gistfx;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.ui.LoginWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    public static final String APP_TITLE = "GistFX";
    private static String dockIconBase = "Artwork/%s/Icons/AppleDock.png";

    private static void sleep(long time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        LiveSettings.applyAppSettings();
        String color = LiveSettings.getLoginColor();
        String urlPath = String.format(dockIconBase,color);
        URL imagePath = Main.class.getResource(urlPath);
        Image image = toolkit.getImage(imagePath);
        Taskbar taskbar = Taskbar.getTaskbar();
        try {
            taskbar.setIconImage(image);
        }
        catch(UnsupportedOperationException e) {
            System.out.println("This os does not support taskbar.setIconImage()");
        }
        catch (final SecurityException e) {
            System.out.println("There was a security exception for: 'taskbar.setIconImage'");
        }

        boolean stopFlag = false;
        for (String arg : args) {
            if (arg.toLowerCase(Locale.ROOT).startsWith("gitsource")) {
                AppSettings.setDataSource(UISettings.DataSource.GITHUB);
                Action.setDatabaseConnection();
                Action.deleteDatabaseFile();
            }
            if (arg.toLowerCase(Locale.ROOT).startsWith("newdatabase")) {
                Action.deleteDatabaseFile();
                System.out.println("Database file has been reset");
                stopFlag = true;
            }
            if (arg.toLowerCase(Locale.ROOT).startsWith("clearcreds")) {
                AppSettings.clearTokenHash();
                AppSettings.clearPasswordHash();
                System.out.println("Credentials Cleared");
                stopFlag = true;
            }
            if (arg.toLowerCase(Locale.ROOT).startsWith("masterreset")) {
                LiveSettings.doMasterReset = true;
            }
            
        }
        if (stopFlag) System.exit(100);
        launch(args);
    }

    @Override public void start(Stage primaryStage) {
        Theme.init();
        LiveSettings.applyAppSettings();
        Action.setDatabaseConnection();
        setUserAgentStylesheet(STYLESHEET_MODENA);
        new LoginWindow();
    }
}
