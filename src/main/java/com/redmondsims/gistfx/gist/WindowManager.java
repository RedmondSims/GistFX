package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.ui.GistWindow;
import javafx.application.Platform;

public class WindowManager {

	private static GistWindow gistWindow;

	public static void newGistWindow(State launchState) {
		Platform.runLater(() -> {
			gistWindow = new GistWindow();
			gistWindow.showMainWindow(launchState);
		});
	}

	public static void handleButtons() {
		if (gistWindow != null) {
			gistWindow.handleButtonBar();
		}
	}

	public static void setConflict(GistFile file, Type conflict) {
		gistWindow.setFileDirtyState(file,conflict);
		gistWindow.handleButtonBar();
	}

	public static void setPBarStyle(String style) {
		gistWindow.setPBarStyle(style);
	}


}
