package com.redmondsims.gistfx.javafx.controls;

import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.preferences.UISettings;
import com.redmondsims.gistfx.ui.preferences.UISettings.ProgressColorSource;
import com.redmondsims.gistfx.ui.preferences.UISettings.Theme;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;

import java.util.Random;

public class CustomProgressBar extends ProgressBar {

	public CustomProgressBar(DoubleProperty bindProperty, double height) {
		super(0);
		progressProperty().bind(bindProperty);
		setPrefHeight(height);
		setup();
	}

	public CustomProgressBar(DoubleProperty bindProperty, double height, Color color) {
		super(0);
		progressProperty().bind(bindProperty);
		setPrefHeight(height);
		getStylesheets().add(Theme.PROGRESS_BAR.getStyleSheet());
		setTheme();
		String accent = "#" + color.toString().replaceFirst("0x","").substring(0,6);
		setStyle("-fx-accent: " + accent + ";");
	}

	private void setup() {
		getStylesheets().add(Theme.PROGRESS_BAR.getStyleSheet());

		setTheme();
		LiveSettings.applyUserPreferences();
		if (LiveSettings.progressBarColorSource.equals(ProgressColorSource.RANDOM)) {
			addRandomColor();
		}
		else {
			addUserColorChoice();
		}
	}

	private void addRandomColor() {
		getStyleClass().add(randomStyleClass());
	}

	private void addUserColorChoice() {
		String accent = "#" + LiveSettings.progressBarColor.toString().replaceFirst("0x","").substring(0,6);
		setStyle("-fx-accent: " + accent + ";");
	}

	private void setTheme() {
		if (LiveSettings.theme.equals(Theme.DARK)) {
			getStyleClass().add("dark");
		}
		else {
			getStyleClass().add("light");
		}
	}

	private String randomStyleClass() {
		String[] colors = new String[]{
				UISettings.ProgressBarColor.RED.getStyleClass(),
				UISettings.ProgressBarColor.ORANGE.getStyleClass(),
				UISettings.ProgressBarColor.YELLOW.getStyleClass(),
				UISettings.ProgressBarColor.GREEN.getStyleClass(),
				UISettings.ProgressBarColor.BLUE.getStyleClass(),
				UISettings.ProgressBarColor.CYAN.getStyleClass(),
				UISettings.ProgressBarColor.HOTPINK.getStyleClass(),
				UISettings.ProgressBarColor.OCEAN.getStyleClass()
		};

		Random random = new Random();
		return colors[random.nextInt(0, 7)];
	}
}
