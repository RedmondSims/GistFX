package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.enums.Theme;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import eu.mihosoft.monacofx.MonacoFX;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class CodeEditor {

	private static       MonacoFX       monacoFX;

	private static void checkNull() {
		if (monacoFX == null) monacoFX = new MonacoFX();
	}

	public static void setTheme(String theme) {
		monacoFX.getEditor().setCurrentTheme(theme);
	}

	public static String getLanguage() {
		return monacoFX.getEditor().getCurrentLanguage();
	}

	public static void setLanguage(String language) {
		checkNull();
		monacoFX.getEditor().setCurrentLanguage(language);
	}

	public static void bindDocumentTo(GistFile fileObject) {
		fileObject.setContentListener(monacoFX.getEditor().getDocument().textProperty());
	}

	public static void setContent(String content) {
		Platform.runLater(() -> monacoFX.getEditor().getDocument().setText(content));
	}

	public static void requestFocus() {monacoFX.requestFocus();}

	public static void hide()         {monacoFX.setVisible(false);}

	public static void show()         {monacoFX.setVisible(true);}

	public static void setEditorTheme() {
		monacoFX.getEditor().setCurrentTheme(Theme.getMonacoTheme());
	}

	public static MonacoFX get() {
		checkNull();
		return monacoFX;
	}

}
