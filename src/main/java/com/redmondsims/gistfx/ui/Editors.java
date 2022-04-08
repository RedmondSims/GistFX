package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.preferences.LiveSettings;
import eu.mihosoft.monacofx.MonacoFX;

public class Editors {

	/**
	 * Because of the way MonacoFX applies themes, it was necessary to create this class so that visual components are pre-rendered and ready to go.
	 */

	private static final MonacoFX monacoOne = new MonacoFX();
	private static final MonacoFX monacoTwo = new MonacoFX();

	public static void init() {
		monacoOne.getEditor().currentThemeProperty().bind(LiveSettings.monacoThemeProperty());
		monacoTwo.getEditor().currentThemeProperty().bind(LiveSettings.monacoThemeProperty());
	}


	public static MonacoFX getMonacoOne() {
		monacoOne.getEditor().getDocument().setText("");
		return monacoOne;
	}

	public static MonacoFX getMonacoTwo() {
		monacoTwo.getEditor().getDocument().setText("");
		return monacoTwo;
	}
}
