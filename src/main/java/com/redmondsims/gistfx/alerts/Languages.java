package com.redmondsims.gistfx.alerts;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Languages {

	public static List<String> langList() {
		List<String> list = new ArrayList<>(Arrays.asList(
				"abap",
				"apex",
				"azcli",
				"bat",
				"Class",
				"cameligo",
				"clojure",
				"coffee",
				"cpp",
				"csharp",
				"csp",
				"css",
				"dockerfile",
				"fsharp",
				"go",
				"graphql",
				"handlebars",
				"html",
				"ini",
				"java",
				"javascript",
				"kotlin",
				"less",
				"lua",
				"markdown",
				"mips",
				"msdax",
				"mysql",
				"objective-Class",
				"pascal",
				"pascaligo",
				"perl",
				"pgsql",
				"php",
				"postiats",
				"powerquery",
				"powershell",
				"pug",
				"python",
				"r",
				"razor",
				"redis",
				"redshift",
				"restructuredtext",
				"ruby",
				"rust",
				"sb",
				"scheme",
				"scss",
				"shell",
				"solidity",
				"sophia",
				"sql",
				"st",
				"swift",
				"tcl",
				"twig",
				"typescript",
				"vb",
				"xml",
				"yaml"));
		Collections.sort(list);
		return list;
	}

	private static VBox getColumnList(String text) {
		int      index     = 0;
		String[] labelLine = new String[3];
		Label    topLabel  = new Label(text);
		topLabel.setPrefWidth(375);
		topLabel.setWrapText(true);
		VBox vbox = new VBox(topLabel);
		vbox.setSpacing(4);
		vbox.setPadding(new Insets(0, 0, 0, 20));
		for (String lang : langList()) {
			labelLine[index] = lang;
			index++;
			if (index > 2) {
				Label label0 = new Label(labelLine[0]);
				Label label1 = new Label(labelLine[1]);
				Label label2 = new Label(labelLine[2]);
				label0.setPrefWidth(125);
				label1.setPrefWidth(125);
				label2.setPrefWidth(125);
				HBox hbox = new HBox(label0, label1, label2);
				hbox.setSpacing(10);
				vbox.getChildren().add(hbox);
				labelLine[0] = null;
				labelLine[1] = null;
				labelLine[2] = null;
				index        = 0;
			}
		}
		HBox hbox = new HBox();
		for (int x = 0; x < 3; x++) {
			if (labelLine[x] != null) {
				Label label = new Label(labelLine[x]);
				label.setPrefWidth(125);
				hbox.getChildren().add(label);
			}
		}
		hbox.setSpacing(10);
		vbox.getChildren().add(hbox);
		return vbox;
	}

	public static void showCodeInformation() {
		Platform.runLater(() -> {
			String information = "Assigning the right file name extension to your file will change the way that the get handles code syntax highlighting and folding. These are the KNOWN file extensions that the get recognizes, feel free to try others as they might work\n\n";
			CustomAlert.showInfo("Language Information", getColumnList(information), null);
		});
	}
}
