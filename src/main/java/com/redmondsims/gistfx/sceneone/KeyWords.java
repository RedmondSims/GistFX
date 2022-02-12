package com.redmondsims.gistfx.sceneone;

import javafx.stage.Modality;
import javafx.stage.StageStyle;

public enum KeyWords {
	MODALITY_APPLICATION,
	MODALITY_WINDOW,
	STYLE_TRANSPARENT,
	STYLE_DECORATED,
	STYLE_UNDECORATED,
	STYLE_UTILITY,
	STYLE_UNIFIED,
	FULLSCREEN,
	MAXIMIZED,
	MINIMIZED,
	CENTERED,
	FITSCENE,
	EXACT,
	FLOAT;

	public static KeyWords get(StageStyle style) {
		switch (style) {
			case DECORATED:
				return STYLE_DECORATED;
			case TRANSPARENT:
				return STYLE_TRANSPARENT;
			case UNIFIED:
				return STYLE_UNIFIED;
			case UNDECORATED:
				return STYLE_UNDECORATED;
			case UTILITY:
				return STYLE_UTILITY;
		}
		return null;
	}

	public static KeyWords get(Modality modality) {
		switch (modality) {
			case APPLICATION_MODAL:
				return MODALITY_APPLICATION;
			case WINDOW_MODAL:
				return MODALITY_WINDOW;
		}
		return null;
	}
}
