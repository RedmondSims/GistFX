package com.redmondsims.gistfx.enums;

import com.redmondsims.gistfx.Launcher;

import java.util.Objects;

public enum StyleSheet {

	PROGRESS,
	TEXT_AREA,
	TEXT_FIELD,
	LABEL,
	ANCHOR_PANE,
	DARK,
	LIGHT;

	public static final String progressCSS  = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/ProgressBar.css")).toExternalForm();
	public static final String textAreaCSS  = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/TextArea.css")).toExternalForm();
	public static final String textFieldCSS = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/TextField.css")).toExternalForm();
	public static final String labelCSS     = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Label.css")).toExternalForm();
	public static final String anchorPane   = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/AnchorPane.css")).toExternalForm();
	public static final String darkCSS      = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Dark.css")).toExternalForm();
	public static final String lightCSS     = Objects.requireNonNull(Launcher.class.getResource("StyleSheets/Light.css")).toExternalForm();


	public String getStyleSheet(StyleSheet this) {
		return switch (this) {
			case PROGRESS -> progressCSS;
			case TEXT_AREA -> textAreaCSS;
			case TEXT_FIELD -> textFieldCSS;
			case LABEL -> labelCSS;
			case ANCHOR_PANE -> anchorPane;
			case DARK -> darkCSS;
			case LIGHT -> lightCSS;
		};
	}
}
