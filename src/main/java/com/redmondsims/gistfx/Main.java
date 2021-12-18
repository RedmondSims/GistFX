package com.redmondsims.gistfx;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.ui.LoginWindow;
import com.redmondsims.gistfx.ui.preferences.AppSettings;
import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.preferences.UISettings;
import com.redmondsims.gistfx.ui.preferences.UISettings.Theme;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    public static final String APP_TITLE = "GistFX";

    public static void main(String[] args) {
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
