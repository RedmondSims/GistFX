package com.redmondsims.gistfx;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.LoginWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Locale;

import static com.redmondsims.gistfx.enums.OS.MAC;

public class Main extends Application {

    public static final  String APP_TITLE    = "GistFX";
    private static final String dockIconBase = "ArtWork/%s/Icons/AppleDock.png";
    private static final JFrame jFrame       = new JFrame();

    private static void setAnchors(Node node, double left, double right, double top, double bottom) {
        if (top != -1) AnchorPane.setTopAnchor(node, top);
        if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
        if (left != -1) AnchorPane.setLeftAnchor(node, left);
        if (right != -1) AnchorPane.setRightAnchor(node, right);
    }


    public static void editCategories() {
        String    name                = "Categories";
        double    width               = 400;
        double                         height              = 500;
        javafx.scene.control.Label     lblNewCategory      = new javafx.scene.control.Label("New Category");
        javafx.scene.control.Label     lblSelectedCategory = new javafx.scene.control.Label("Selected Category");
        javafx.scene.control.Label     lblNewCategoryName  = new javafx.scene.control.Label("New Name");
        javafx.scene.control.TextField tfNewCategory       = new javafx.scene.control.TextField();
        javafx.scene.control.TextField tfSelectedCategory  = new javafx.scene.control.TextField();
        javafx.scene.control.TextField tfNewName           = new javafx.scene.control.TextField();
        Tooltip.install(tfNewCategory, new Tooltip("Type in the category name and hit ENTER to save it."));
        Tooltip.install(tfSelectedCategory, new Tooltip("Click on a category from the list, then rename it."));
        Tooltip.install(tfNewName, new Tooltip("Click on a category from the list, then Type in a new name here then press ENTER."));
        tfSelectedCategory.setEditable(false);
        ListView<String> lvCategories = new ListView<>();
        //lvCategories.getItems().setAll(Action.getCategoryList());
        javafx.scene.control.Button btnClose  = new javafx.scene.control.Button("Close");
        javafx.scene.control.Button btnDelete = new Button("Delete Category");
        AnchorPane apCategories = new AnchorPane(lvCategories,
                                                 lblNewCategory,
                                                 lblSelectedCategory,
                                                 lblNewCategoryName,
                                                 tfNewCategory,
                                                 tfSelectedCategory,
                                                 tfNewName,
                                                 btnClose,
                                                 btnDelete);
        apCategories.setPrefSize(width, height);
        lblNewCategory.setMinWidth(85);
        lblSelectedCategory.setMinWidth(85);
        lblNewCategoryName.setMinWidth(55);
        btnClose.setMinWidth(55);
        btnClose.setMinHeight(35);
        btnDelete.setMinWidth(75);
        btnDelete.setMinHeight(35);
        setAnchors(lblNewCategory, 20, -1, 20, -1);
        setAnchors(tfNewCategory, 135, 20, 17.5, -1);
        setAnchors(lblSelectedCategory, 20, -1, 50, -1);
        setAnchors(tfSelectedCategory, 135, 20, 47.5, -1);
        setAnchors(lblNewCategoryName, 20, -1, 80, -1);
        setAnchors(tfNewName, 135, 20, 77.5, -1);
        setAnchors(lvCategories, 20, 20, 125, 65);
        setAnchors(btnClose, 40, -1, -1, 20);
        setAnchors(btnDelete,-1, 100, -1, 20);
        Platform.runLater(() -> SceneOne.set(apCategories, name)
                                    .centered()
                                    .newStage()
                                    .title("Edit Categories")
                                    .show());
    }



    public static void main(String[] args) {
        AppSettings.clear().fileMoveWarning();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        LiveSettings.applyAppSettings();
        String color = LiveSettings.getLoginColor();
        String urlPath = String.format(dockIconBase,color);
        URL imagePath = Main.class.getResource(urlPath);
        Image image = toolkit.getImage(imagePath);
        LiveSettings.setTaskbar(Taskbar.getTaskbar());
        try {
            if (LiveSettings.getOS().equals(MAC)) LiveSettings.getTaskbar().setIconImage(image);
            else {
                jFrame.setUndecorated(true);
                jFrame.setIconImage(image);
                jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
                jFrame.pack();
                jFrame.setVisible(true);
                jFrame.setSize(new Dimension(26,26));
            }
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
                AppSettings.set().dataSource(UISettings.DataSource.GITHUB);
                Action.setDatabaseConnection();
                Action.deleteDatabaseFile();
            }
            if (arg.toLowerCase(Locale.ROOT).startsWith("newdatabase")) {
                Action.deleteDatabaseFile();
                System.out.println("Database file has been reset");
                stopFlag = true;
            }
            if (arg.toLowerCase(Locale.ROOT).startsWith("clearcreds")) {
                AppSettings.clear().tokenHash();
                AppSettings.clear().passwordHash();
                System.out.println("Credentials Cleared");
                stopFlag = true;
            }
            if (arg.toLowerCase(Locale.ROOT).startsWith("masterreset")) {
                LiveSettings.doMasterReset = true;
            }
            if (arg.toLowerCase().startsWith("password=")) {
                String password = arg.replaceFirst("password=","");
                LiveSettings.setPassword(password);
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
        //TreeSettings.showWindow();
        //System.exit(0);
        new LoginWindow();
    }
}
