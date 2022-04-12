package com.redmondsims.gistfx.enums;

import com.redmondsims.gistfx.Launcher;
import com.redmondsims.gistfx.preferences.AppSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

public enum Theme {
	
	LIGHT,
	DARK,
	PROGRESS;

	public String Name(Theme this) {
		return switch (this) {
			case LIGHT -> "Light";
			case DARK -> "Dark";
			case PROGRESS -> "Progress";
		};
	}

	public static Theme get(String pref) {
		return switch (pref) {
			case "Light" -> LIGHT;
			case "Dark" -> DARK;
			case "Progress" -> PROGRESS;
			default -> null;
		};
	}

	public static ObservableList<Theme> themeList() {
		return FXCollections.observableArrayList(DARK, LIGHT);
	}

	public static final String darkCSS       = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Dark.css")).toExternalForm();
	public static final String lightCSS      = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Light.css")).toExternalForm();
	public static final String progressCSS      = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/ProgressBar.css")).toExternalForm();

	public String getStyleSheet(Theme this) {
		return switch (this) {
			case DARK -> darkCSS;
			case LIGHT -> lightCSS;
			case PROGRESS -> progressCSS;
		};
	}

	public static String getMonacoTheme() {
		return switch(AppSettings.get().theme()) {
			case DARK -> "vs-dark";
			case LIGHT -> "vs-light";
			default -> "";
		};
	}

}
