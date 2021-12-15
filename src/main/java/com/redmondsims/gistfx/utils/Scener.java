package com.redmondsims.gistfx.utils;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.ui.GistWindow;
import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;


import java.util.*;

public class Scener {

	private static final Map<Integer, Stage>                     stageMap       = new HashMap<>();
	private static final Map<Integer, SceneObject>               sceneObjectMap = new HashMap<>();
	private static final Map<Integer, EventHandler<WindowEvent>> eventMap       = new HashMap<>();
	private static final List<Integer>                           sceneIdList    = new ArrayList<>();
	private static GistWindow gistWindow;
	public static void start(Stage primaryStage, Integer sceneID) {
		stageMap.put(sceneID, primaryStage);
	}

	public static Integer addScene(Parent root, GistWindow gistWindow) {
		Scener.gistWindow = gistWindow;
		return addScene(root);
	}

	public static Integer addScene(Parent root) {
		Stage   stage;
		Integer sceneId = getNewSceneId();
		if (stageMap.containsKey(sceneId)) {
			stage = stageMap.get(sceneId);
		}
		else {
			stage = new Stage();
		}
		SceneObject sceneObject = new SceneObject(new Scene(root), stage);
		stageMap.put(sceneId, new Stage());
		sceneObjectMap.put(sceneId, sceneObject);
		return sceneId;
	}

	public static Integer addScene(Parent root, EventHandler<WindowEvent> eventHandler) {
		Integer sceneId = addScene(root);
		setSceneCloseEvent(sceneId, eventHandler);
		return sceneId;
	}

	public static Integer addScene(Parent root, double width, double height) {
		Stage stage = new Stage();
		stage.setWidth(width);
		stage.setHeight(height);
		Integer     sceneId     = getNewSceneId();
		SceneObject sceneObject = new SceneObject(new Scene(root), stage, width, height);
		sceneObjectMap.put(sceneId, sceneObject);
		return sceneId;
	}

	public static void showScene(Integer sceneId) {
		for (Integer sId : sceneObjectMap.keySet()) {
			if (!sId.equals(sceneId)) sceneObjectMap.get(sId).hide();
		}
		sceneObjectMap.get(sceneId).showScene();
	}

	public static void showOnTop(Integer sceneId) {
		sceneObjectMap.get(sceneId).showScene();
	}

	public static void showAndWait(Integer sceneId) {sceneObjectMap.get(sceneId).showAndWait();}

	public static Window getOwner(Integer sceneId) {
		return sceneObjectMap.get(sceneId).getScene().getWindow();
	}

	public static void setTitleContext(Integer sceneId, String context) {
		stageMap.get(sceneId).setTitle(String.format("%s - %s", Main.APP_TITLE, context));
	}

	public static Stage getStage(Integer sceneId) {
		return sceneObjectMap.get(sceneId).getStage();
	}

	public static void close(Integer sceneId) {
		sceneObjectMap.get(sceneId).hide();
	}

	public static Scene getScene(Integer sceneId) {
		return sceneObjectMap.get(sceneId).getScene();
	}

	public static void setRoot(Integer sceneId, Parent root) {
		sceneObjectMap.get(sceneId).setRoot(root);
	}

	public static void setSceneCloseEvent(Integer sceneId, EventHandler<WindowEvent> eventHandler) {
		EventHandler<WindowEvent> oldEventHandler = eventMap.getOrDefault(sceneId, null);
		if (oldEventHandler != null) sceneObjectMap.get(sceneId).getScene().getWindow().removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, oldEventHandler);
		eventMap.replace(sceneId, eventHandler);
		sceneObjectMap.get(sceneId).getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, eventHandler);
	}

	public static void resizeWindow(Integer sceneId) {
		sceneObjectMap.get(sceneId).getWindow().sizeToScene();
	}

	public static void initModality(Integer sceneId, Modality initModality) {sceneObjectMap.get(sceneId).getStage().initModality(initModality);}

	public static void setWidthHeight(Integer sceneId, double width, double height) {
		sceneObjectMap.get(sceneId).getStage().setWidth(width);
		sceneObjectMap.get(sceneId).getStage().setHeight(height);
	}

	public static void exit() {
		for (SceneObject so : sceneObjectMap.values()) {
			so.close();
		}
		System.exit(155);
	}

	private static Integer getNewSceneId() {
		Random  random = new Random();
		Integer newId  = random.nextInt(100000);
		while (sceneIdList.contains(newId)) {
			newId = random.nextInt(100000);
		}
		sceneIdList.add(newId);
		return newId;
	}

	private static class SceneObject {

		private final Scene  scene;
		private final Stage  stage;
		private       double width  = -1;
		private       double height = -1;

		private SceneObject(Scene scene, Stage stage) {
			this.stage = stage;
			this.scene = scene;
			stage.setScene(scene);
			scene.getStylesheets().clear();
			scene.getStylesheets().add(LiveSettings.theme.getStyleSheet());
		}

		private SceneObject(Scene scene, Stage stage, double width, double height) {
			this.stage  = stage;
			this.scene  = scene;
			this.width  = width;
			this.height = height;
			stage.setScene(scene);
			scene.getStylesheets().clear();
			scene.getStylesheets().add(LiveSettings.theme.getStyleSheet());
		}

		public Scene getScene() {
			return scene;
		}

		public Window getWindow() {
			return scene.getWindow();
		}

		public void setRoot(Parent root) {
			scene.setRoot(root);
			stage.setScene(scene);
		}

		public Stage getStage() {
			return stage;
		}

		public void showScene() {
			stage.setScene(scene);
			stage.show();
			if (width != -1) stage.setWidth(width);
			if (height != -1) stage.setHeight(height);
			stage.getScene().getWindow().centerOnScreen();
		}

		public void showAndWait() {
			stage.setScene(scene);
			stage.showAndWait();
		}

		public void hide() {
			stage.hide();
		}

		public void close() {
			stage.close();
		}
	}

}
