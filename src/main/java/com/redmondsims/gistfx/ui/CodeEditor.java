package com.redmondsims.gistfx.ui;

import com.redmondsims.gistfx.github.gist.GistFile;
import eu.mihosoft.monacofx.MonacoFX;

import java.util.ArrayList;
import java.util.List;

public class CodeEditor {

	private static final List<GistFile> GIST_FILE_LIST = new ArrayList<>();
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
		if (!GIST_FILE_LIST.contains(fileObject)) {
			GIST_FILE_LIST.add(fileObject);
		}
		fileObject.bindContentTo(monacoFX.getEditor().getDocument().textProperty());
	}

	public static void requestFocus() {monacoFX.requestFocus();}

	public static void hide()         {monacoFX.setVisible(false);}

	public static void show()         {monacoFX.setVisible(true);}

	public static MonacoFX get() {
		checkNull();
		return monacoFX;
	}

}
