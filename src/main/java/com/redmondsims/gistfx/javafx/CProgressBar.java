package com.redmondsims.gistfx.javafx;

import com.redmondsims.gistfx.enums.StyleSheet;
import com.redmondsims.gistfx.enums.Theme;
import com.redmondsims.gistfx.preferences.AppSettings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;

public class CProgressBar extends ProgressBar {

	public CProgressBar(DoubleProperty valueProperty, double height) {
		super(0);
		progressProperty().bind(valueProperty);
		setPrefHeight(height);
		setStyle();
	}

	private void setStyle() {
		Color color = AppSettings.get().progressBarColor();
		String colorString  = "#" + color.toString().replaceFirst("0x", "").substring(0, 6);
		String style = "-fx-accent: " + colorString + ";";
		getStylesheets().add(StyleSheet.PROGRESS.getStyleSheet());
		getStyleClass().add(AppSettings.get().theme().equals(Theme.DARK) ? "dark" : "light");
		setStyle(style);
	}

	public void refreshStyle() {
		setStyle();
	}
}
