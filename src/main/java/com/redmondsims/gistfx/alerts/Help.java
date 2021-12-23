package com.redmondsims.gistfx.alerts;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.utils.AppConstants;
import com.redmondsims.gistfx.utils.Modify;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebView;
import javafx.stage.Modality;

import java.io.File;
import java.net.URL;
import java.util.*;

public class Help {

	public static void somethingWrong() {
		String htmlFilePath = Objects.requireNonNull(Main.class.getResource("HelpFiles/IfSomethingWrong.html")).toExternalForm();
		File   htmlFile     = new File(htmlFilePath.replaceFirst("file:", ""));
		String html         = Action.loadTextFile(htmlFile);
		showHelp(html);
	}

	public static void showIntro() {
		File   htmlFile = new File(AppConstants.htmlFilePath, "Intro.html");
		String html     = Action.loadTextFile(htmlFile).replaceFirst("LogoFile", getLogoPath().toString());
		showHelp(html);
	}

	public static void showCreateTokenHelp() {
		File   htmlFile = new File(AppConstants.htmlFilePath, "HelpCreateToken.html");
		String html     = Action.loadTextFile(htmlFile);
		String search   = "LogoFile";
		String replace  = "file:" + AppConstants.getHtmlFilePathWith("GistFXLogo.png");
		html = Modify.string().replace(html, search, replace);
		List<String> searchList = Modify.string().extractList(html, "~~File\\d{1,}~~");
		Collections.sort(searchList);
		List<File>   images             = Arrays.stream(Objects.requireNonNull(new File(AppConstants.getHtmlFilePathWith("HowToToken")).listFiles())).toList();
		List<String> imageAbsolutePaths = new ArrayList<>();
		for (File file : images) {
			String imgAbsPath = "file:" + file.getAbsolutePath();
			imageAbsolutePaths.add(imgAbsPath);
		}
		Map<String,String> fileMap = new HashMap<>();
		int index = 0;
		for (int x = 0; x < searchList.size(); x++) {
			String find = searchList.get(x);
			String rep = imageAbsolutePaths.get(x);
			fileMap.put(find,rep);
		}
		Collections.sort(imageAbsolutePaths);
		String[] htmlArray = html.split("\n");
		for (int x = 0; x < htmlArray.length; x++) {
			for (String find : searchList) {
				if (htmlArray[x].contains(find)) {
					StringBuilder repl = new StringBuilder();
					char[] replChar = fileMap.get(find).toCharArray();
					for (char c : replChar) {
						if (Integer.valueOf(c).equals(92)) { //Replace each \ with \\, or they are removed in the replaceFirst method that follows
							repl.append(c).append(c);
						}
						else {
							repl.append(c);
						}
					}
					String newH = htmlArray[x].replaceFirst(find,repl.toString());
					htmlArray[x] = newH;
				}
			}
		}
		StringBuilder newHTML = new StringBuilder();
		for (String line : htmlArray) {
			newHTML.append(line).append("\n");
		}

		showHelp(newHTML.toString());
	}

	private static String getMain() {
		File   htmlFile = new File(AppConstants.htmlFilePath, "Main.html");
		String html     = Action.loadTextFile(htmlFile).replaceFirst("LogoFile", getLogoPath().toString());

		for (int x = 1; x <= 2; x++) {
			URL    url    = Main.class.getResource("HelpFiles/General/" + x + ".png");
			String search = "~~File" + x + "~~";
			assert url != null;
			String replace = url.toString();
			html = Modify.string().replace(html, search, replace);
		}
		return html;
	}

	public static void mainOverview() {
		showHelp(getMain());
	}

	public static void generalHelp() {
		File   htmlFile = new File(AppConstants.htmlFilePath, "GeneralHelp.html");
		String html     = Action.loadTextFile(htmlFile);
		showHelp(html);
	}

	private static void showHelp(String html) {
		String background = "~background~";
		String color      = "~color~";
		if (LiveSettings.getTheme().equals(Theme.DARK)) {
			html = Modify.string().replace(html, background, "background-color:#373e43");
			html = Modify.string().replace(html, color, "color:lightgrey");
		}
		else {
			html = Modify.string().replace(html, background, "background-color:#e6e6e6");
			html = Modify.string().replace(html, color, "color:black");
		}
		WebView webView = new WebView();
		webView.getEngine().loadContent(html);
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.initModality(Modality.WINDOW_MODAL);
		alert.getButtonTypes().clear();
		alert.getDialogPane().getScene().getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
		alert.getButtonTypes().add(ButtonType.OK);
		alert.getDialogPane().setContent(webView);
		alert.getDialogPane().setPadding(new Insets(10, 20, 0, 10));
		alert.showAndWait();
		AppSettings.setFirstRun(false);
	}

	private static URL getLogoPath() {
		return Main.class.getResource("HelpFiles/GistFXLogo.png");
	}
}
