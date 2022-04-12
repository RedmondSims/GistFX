package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ResetOptions {

	//TODO Relocate the options from the main MasterReset class over to this class for in-app access

	private Label label;

	public VBox content() {
		label = new Label("This will be used for resetting various options such as user password, App settings, database refresh and metadata resetting etc.");
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER_LEFT);
		label.setMinWidth(200);
		label.setMaxWidth(200);
		label.setPrefWidth(200);
		VBox vbox = new VBox(label);
		vbox.setAlignment(Pos.CENTER);
		return vbox;
	}

}
