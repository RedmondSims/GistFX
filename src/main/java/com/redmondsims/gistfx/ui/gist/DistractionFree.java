package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.OS;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import eu.mihosoft.monacofx.MonacoFX;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;


public class DistractionFree {


	public DistractionFree() {
		keyEvent = (EventHandler<KeyEvent>) e -> {
			if ((e.getCode() == KeyCode.W && e.isMetaDown()) || e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.Q && e.isAltDown()) {
				updatedContent = monaco.getEditor().getDocument().getText();
				new Thread(() -> {
					Action.sleep(400);
					Platform.runLater(() -> {
						WindowManager.updateFileContent(updatedContent);
						stage.close();
					});
				}).start();
			}
		};
	}

	private       AnchorPane                     ap;
	private       MonacoFX                       monaco;
	private       Stage                          stage;
	private       Scene                          scene;
	private       String                         updatedContent;
	private final EventHandler<? super KeyEvent> keyEvent;


	public void start(String content, String language) {
		monaco = new MonacoFX();
		monaco.getEditor().setCurrentLanguage(language);
		monaco.getEditor().setCurrentTheme(LiveSettings.getTheme().equals(UISettings.Theme.DARK) ? "vs-dark" : "vs-light");
		monaco.getEditor().getDocument().setText(content);
		ap = new AnchorPane(monaco);
		setNodePosition(monaco,0,0,0,0);
		stage = new Stage();
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.W, KeyCombination.META_DOWN));
		String hint = LiveSettings.getOS().equals(OS.MAC) ? "CMD + W to Exit Fullscreen" : "ALT + Q to Exit Fullscreen";
		stage.setFullScreenExitHint(hint);
		stage.setFullScreen(true);
		scene = new Scene(ap);
		scene.setOnKeyPressed(keyEvent);
		stage.setScene(scene);
		stage.show();
	}

	private static void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

}
