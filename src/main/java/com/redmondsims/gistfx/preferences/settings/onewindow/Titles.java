package com.redmondsims.gistfx.preferences.settings.onewindow;

import com.redmondsims.gistfx.preferences.settings.onewindow.screens.Utility;

enum Titles {
	GUI_ELEMENTS,
	TREE_SETTINGS,
	WIDE_MODE,
	RESET_OPTIONS,
	ROOT;

	public String get(Titles this) {
		return switch(this) {
			case GUI_ELEMENTS -> "GUI Elements";
			case TREE_SETTINGS -> "Tree Settings";
			case WIDE_MODE -> "Wide Mode";
			case RESET_OPTIONS -> "Reset Options";
			case ROOT -> "Root";
		};
	}

	public String toString(Titles this) {
		String response = name().replace("_"," ");
		return Utility.toTitleCase(response);
	}

}
