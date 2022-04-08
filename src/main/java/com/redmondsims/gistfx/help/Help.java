package com.redmondsims.gistfx.help;

import com.redmondsims.gistfx.help.html.*;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings.Theme;
import com.redmondsims.gistfx.utils.Resources;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebView;
import javafx.stage.Modality;

import java.io.File;
import java.util.*;

public class Help {

	private static final String root = Resources.getHelpRoot();
	private static final File rootPath = new File(root);
	private static final File logoFile = new File(rootPath,"Logo.png");
	private static final String header = """
			<html lang="English">
			<head>
			    <style>
			        p {
			            margin: 0 50px 0 10px;
			        }
			    </style>
			    <title>General Help</title>
			</head>
			<body style="~background~;~color~">
			<h1 style="text-align:center"><img alt="" src="file:%s" style="height:219px; width:500px"/></h1>
			<h3 style="text-align:center">A GitHub Utility</h3>
			<hr/>
			""".formatted(logoFile.getAbsolutePath());




	private static String replace(String source, String regex, String replacement) {
		return source.replaceFirst(regex, replacement);
	}

	public static void somethingWrong() {
		String html = header + SomethingWrong.html;
		contentViewer(html);
	}

	public static void showIntro() {
		String html     = header + Intro.html;
		contentViewer(html);
		AppSettings.set().firstRun(false);
	}

	public static void showCreateTokenHelp() {
		String     html    = header + CreateToken.html;
		File       newPath = new File(rootPath, "HowToToken");
		List<File> images  = new ArrayList<>(Arrays.stream(newPath.listFiles()).toList());
		images.sort(Comparator.comparing(File::getAbsolutePath));
		for (int x = 1; x <= 7; x++) {
			String search = String.format("~~File%s~~",x);
			html = replace(html,search, "file:" + images.get(x-1).getAbsolutePath());
		}
		contentViewer(html);
	}

	public static void mainOverview() {
		String html     = header + MainHelp.html;
		File imageFilePath = new File(rootPath,"General");
		for (int x = 1; x <= 2; x++) {
			String fileName = x + ".png";
			File imageFile = new File(imageFilePath,fileName);
			String search = "~~File" + x + "~~";
			String replace = "file:" + imageFile.getAbsolutePath();
			html = replace(html, search, replace);
		}
		contentViewer(html);
	}

	public static void generalHelp() {
		String html     = header + GeneralHelp.html;
		contentViewer(html);
	}

	private static void contentViewer(String html) {
		String background = "~background~";
		String color      = "~color~";
		if (LiveSettings.getTheme().equals(Theme.DARK)) {
			html = replace(html, background, "background-color:#373e43");
			html = replace(html, color, "color:lightgrey");
		}
		else {
			html = replace(html, background, "background-color:#e6e6e6");
			html = replace(html, color, "color:black");
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
	}
}
