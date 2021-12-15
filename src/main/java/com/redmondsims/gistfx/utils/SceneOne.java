package com.redmondsims.gistfx.utils;

import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.*;

import java.util.HashMap;
import java.util.Map;

public final class SceneOne {

	public enum Choice {
		FULL_SCREEN,
		APP_MODAL,
		WINDOW_MODAL,
		TRANSPARENT,
		DECORATED,
		UNDECORATED,
		UTILITY,
		UNIFIED,
		CENTERED
	}


	public static void init(boolean centerSceneByDefault) {
		SceneOne.centerSceneByDefault = centerSceneByDefault;
	}

	public static void init(boolean centerSceneByDefault, double width, double height) {
		SceneOne.centerSceneByDefault = centerSceneByDefault;
		SceneOne.width = width;
		SceneOne.height = height;
	}

	private static final Map<String, SceneObject> sceneMap             = new HashMap<>();
	private static final String                   core                 = "default";
	private static       boolean                  centerSceneByDefault = false;
	private static       double                   width                = -1;
	private static       double                   height               = -1;


	public static class Builder {

		public Builder(Parent root) {
			this.root = root;
		}

		public Builder(Parent root, String className) {
			this.root      = root;
			this.className = className;
		}

		private final Parent                    root;
		private       boolean                   centered          = false;
		private       boolean                   fullScreen        = false;
		private       double                    width             = -1;
		private       double                    height            = -1;
		private       Modality                  modality          = null;
		private       StageStyle                initStyle         = null;
		private       String                    className         = "";
		private       String                    title             = "";
		private       EventHandler<WindowEvent> stageEventHandler = null;

		public Builder forClass(String className) {
			this.className = className;
			return this;
		}

		public Builder modality(Modality modality) {
			this.modality = modality;
			return this;
		}

		public Builder initStyle(StageStyle initStyle) {
			this.initStyle = initStyle;

			return this;
		}

		public Builder init(Modality modality, StageStyle initStyle) {
			this.modality  = modality;
			this.initStyle = initStyle;
			return this;
		}

		public Builder widthHeight(double width, double height) {
			this.width  = width;
			this.height = height;
			return this;
		}

		public Builder centered() {
			this.centered = true;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder onCloseEvent(EventHandler<WindowEvent> stageEventHandler) {
			this.stageEventHandler = stageEventHandler;
			return this;
		}

		private Builder fullscreen(boolean fullscreen)  {
			this.fullScreen = fullscreen;
			return this;
		}

		private void setSceneOptions(Choice... choices) {
			for (Choice choice : choices) {
				switch (choice) {
					case FULL_SCREEN -> fullScreen = true;
					case APP_MODAL -> modality = Modality.APPLICATION_MODAL;
					case WINDOW_MODAL -> modality = Modality.WINDOW_MODAL;
					case TRANSPARENT -> initStyle = StageStyle.TRANSPARENT;
					case DECORATED -> initStyle = StageStyle.DECORATED;
					case UNDECORATED -> initStyle = StageStyle.UNDECORATED;
					case UTILITY -> initStyle = StageStyle.UTILITY;
					case UNIFIED -> initStyle = StageStyle.UNIFIED;
					case CENTERED -> centered = true;
				}
			}
		}

		private void buildSceneObject(String mapName) {
			sceneMap.remove(mapName);
			sceneMap.put(mapName, new SceneObject.Builder(root)
					.initModality(modality)
					.initStyle(initStyle)
					.widthHeight(width,height)
					.center(centerSceneByDefault)
					.fullscreen(fullScreen)
					.title(title)
					.stageCloseEvent(stageEventHandler)
					.build());
			sceneMap.get(mapName).setTitle(title != null ? title : "");
			if (width != -1 && height != -1) {
				sceneMap.get(mapName).setWidthHeight(width, height);
			}
		}

		public void show() {
			String mapName = className.isEmpty() ? core : className;
			buildSceneObject(mapName);
			sceneMap.get(mapName).show(centered);
		}

		public void show(Choice... choices) {
			String mapName = className.isEmpty() ? core : className;
			setSceneOptions(choices);
			buildSceneObject(mapName);
			sceneMap.get(mapName).show(centered);
		}

		public void showAndWait(Choice... choices) {
			String mapName = className.isEmpty() ? core : className;
			setSceneOptions(choices);
			buildSceneObject(mapName);
			sceneMap.get(mapName).showAndWait();
		}

		public void build() {
			String mapName = className.isEmpty() ? core : className;
			buildSceneObject(mapName);
		}
	}

	public static Builder set(Parent root) {
		return new Builder(root);
	}

	public static Builder set(Parent root, String className) {return new Builder(root, className);}

	public static void show(Parent root) {
		sceneMap.get(core).show();
	}

	public static void showScene(boolean centered) {
		sceneMap.get(core).show();
		sceneMap.get(core).center();
	}

	public static void showScene(double width, double height, boolean centered) {
		sceneMap.get(core).show(width,height,centered);
	}

	public static void show() {
		sceneMap.get(core).show();
	}

	public static void show(String className) {
		sceneMap.get(className).show();
	}

	public static void showScene(String className, boolean centered) {
		sceneMap.get(className).show();
		sceneMap.get(className).center();
	}

	public static void showScene(String className, double width, double height, boolean centered) {
		sceneMap.get(className).show(width,height,centered);
	}

	public static void sizeToScene() {
		sceneMap.get(core).sizeToScene();
	}

	public static void sizeToScene(String className) {
		sceneMap.get(className).sizeToScene();
	}

	public static void center() {
		sceneMap.get(core).center();
	}

	public static void center(String className) {
		sceneMap.get(className).center();
	}

	public static void setTitle(String title) {
		sceneMap.get(core).setTitle(title);
	}

	public static void setTitle(String className, String title) {
		sceneMap.get(className).setTitle(title);
	}

	public static void setFullScreen(boolean fullScreen) {
		sceneMap.get(core).setFullScreen(fullScreen);
	}

	public static void setFullScreen(String className, boolean fullScreen) {sceneMap.get(className).setFullScreen(fullScreen);}

	public static boolean isShowing(String className) {
		return sceneMap.get(className).isShowing();
	}

	public static boolean isFullScreen() {
		return sceneMap.get(core).isFullScreen();
	}

	public static boolean isFullScreen(String className) {
		return sceneMap.get(className).isFullScreen();
	}

	public static void setOnKeyPressed() {

	}

	public static void setOnKeyPressed(EventHandler<? super KeyEvent> value) {
		sceneMap.get(core).setOnKeyPressed(value);
	}

	public static void setOnKeyPressed(String className, EventHandler<? super KeyEvent> value) {
		sceneMap.get(className).setOnKeyPressed(value);
	}

	public static void setStageCloseEvent(EventHandler<WindowEvent> eventHandler) {
		sceneMap.get(core).setStageCloseEvent(eventHandler);
	}

	public static void setStageCloseEvent(String className, EventHandler<WindowEvent> eventHandler) {
		sceneMap.get(className).setStageCloseEvent(eventHandler);
	}

	public static void exit() {
		for (SceneObject scene : sceneMap.values()) {
			scene.close();
		}
		System.exit(0);
	}

	public static Window getOwner() {
		return sceneMap.get(core).getOwner();
	}

	public static Window getOwner(String className) {
		return sceneMap.get(className).getOwner();
	}

	public static Window getWindow() {
		return sceneMap.get(core).getWindow();
	}

	public static Window getWindow(String className) {
		return sceneMap.get(className).getWindow();
	}

	public static void close() {
		sceneMap.get(core).close();
	}

	public static void close(String className) {
		sceneMap.get(className).close();
	}

	public static void showAndWait() {
		sceneMap.get(core).showAndWait();
	}

	public static void showAndWait(String className) {
		sceneMap.get(className).showAndWait();
	}

	public static Stage getStage() {
		return sceneMap.get(core).getStage();
	}

	public static Scene getScene() {
		return sceneMap.get(core).getScene();
	}

	public static Stage getStage(String className) {
		return sceneMap.get(className).getStage();
	}

	public static Scene getScene(String className) {
		return sceneMap.get(className).getScene();
	}

	private static class SceneObject {

		public static class Builder {

			public Builder(Parent root) {
				this.root = root;
			}

			private       Stage              stage;
			private 	  Scene				 scene;
			private final Parent             root;
			private       boolean            centerSceneByDefault = SceneOne.centerSceneByDefault;
			private       boolean            fullscreen;
			private       double             width                = -1;
			private       double             height               = -1;
			private       Modality           modality             = null;
			private       StageStyle         style                = null;
			private String title = "";
			private       EventHandler<WindowEvent>              sceneEventHandler = null;

			public Builder stage(Stage stage) {
				this.stage = stage;
				return this;
			}

			public Builder width(double width) {
				this.width = width;
				return this;
			}
			public Builder height(double height) {
				this.height = height;
				return this;
			}
			public Builder fullscreen(boolean fullscreen) {
				this.fullscreen = fullscreen;
				return this;
			}

			public Builder widthHeight(double width, double height) {
				this.width = width;
				this.height = height;
				return this;
			}
			public Builder center(boolean centered) {
				this.centerSceneByDefault = centered;
				return this;
			}
			public Builder initModality(Modality modality) {
				this.modality = modality;
				return this;
			}
			public Builder initStyle(StageStyle style) {
				this.style = style;
				return this;
			}
			public Builder centerByDefault(boolean centerSceneByDefault) {
				this.centerSceneByDefault = centerSceneByDefault;
				return this;
			}
			public Builder centered(boolean centered) {
				this.centerSceneByDefault = centered;
				return this;
			}

			public Builder title(String title) {
				this.title = title;
				return this;
			}

			public Builder stageCloseEvent(EventHandler<WindowEvent> eventHandler) {
				sceneEventHandler = eventHandler;
				return this;
			}

			public SceneObject build() {
				if(stage == null) stage = new Stage();
				scene = new Scene(root);
				scene.getStylesheets().add(LiveSettings.theme.getStyleSheet());
				stage.setScene(scene);
				return new SceneObject(this);
			}

		}

		private final Stage                                  stage;
		private       Scene                     scene;
		private       boolean                                center;
		private       boolean                                fullscreen;
		private       double                                 width;
		private       double                                 height;
		private final Modality                               modality;
		private final StageStyle                             style;
		private String                          title;
		private       EventHandler<WindowEvent> stageEventHandler = null;

		private SceneObject(Builder build) {
			stage             = build.stage;
			scene		      = build.scene;
			width             = build.width;
			height            = build.height;
			modality          = build.modality;
			fullscreen        = build.fullscreen;
			stageEventHandler = build.sceneEventHandler;
			style             = build.style;
			center            = build.centerSceneByDefault;
			title             = build.title;
			if (modality != null) stage.initModality(modality);
			if (style != null) stage.initStyle(style);
			if (width != -1) stage.setWidth(width);
			if (height != -1) stage.setWidth(height);
			setTitle(title);
			if (build.sceneEventHandler == null) {
				setStageCloseEvent(e -> System.exit(13));
			}
		}

		public void show(boolean centered) {
			setScene();
			setSize();
			stage.show();
			center = centered;
			center();
		}

		private void setScene() {
			stage.setScene(scene);
		}

		public void show() {
			setScene();
			setSize();
			stage.show();
			center();
		}

		public void show(Parent root) {
			setScene();
			scene = new Scene(root);
			stage.setScene(scene);
			setSize();
			stage.show();
			center();
			assignCloseEventToStage();
		}

		public void show(double width, double height) {
			setScene();
			this.width = width;
			this.height = height;
			setWidthHeight(width,height);
			setSize();
			stage.show();
			center();
			assignCloseEventToStage();
		}

		public void show(double width, double height, boolean centered) {
			setScene();
			setWidthHeight(width,height);
			center = centered;
			setSize();
			stage.show();
			center();
			assignCloseEventToStage();
		}

		public void showAndWait() {
			setScene();
			stage.showAndWait();
		}

		private void setSize() {
			if (width != -1) stage.setWidth(width);
			if (height != -1) stage.setHeight(height);
		}

		public void center() {
			if (center)	stage.getScene().getWindow().centerOnScreen();
			stage.setFullScreen(fullscreen);
		}

		public Scene getScene() {return scene;}

		public Stage getStage() {return stage;}

		public void setCenter(boolean center) {
			centerSceneByDefault = center;
		}

		public void setModality(Modality modality) {
			stage.initModality(modality);
		}

		public void setStyle(StageStyle style) {
			stage.initStyle(style);
		}

		public void setWidthHeight(double width, double height) {
			stage.setWidth(width);
			stage.setHeight(height);
		}

		public boolean isShowing() {
			return stage.isShowing();
		}

		public void setFullScreen(boolean fullScreen) {
			this.fullscreen = fullScreen;
			stage.setFullScreen(fullScreen);
		}

		public void sizeToScene() {
			scene.getWindow().sizeToScene();
		}

		public void setTitle(String title) {
			this.title = title;
			stage.setTitle(title);
		}

		public boolean isFullScreen() {
			return stage.isFullScreen();
		}

		public void setOnKeyPressed(EventHandler<? super KeyEvent> value) {
			scene.setOnKeyPressed(value);
		}

		private void assignCloseEventToStage() {
			if (stageEventHandler != null) {
				stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, stageEventHandler);
				stage.setOnCloseRequest(stageEventHandler);
			}
		}

		public void setStageCloseEvent(EventHandler<WindowEvent> eventHandler) {
			if (stageEventHandler != null) {
				stage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, stageEventHandler);
			}
			stageEventHandler = eventHandler; //We use the class visible stageEventHandler, so we can remove it above if the user changes it instead of adding more to the collection - under the assumption that the word ADD, in addEventFilter means to accumulate vs SET which would mean to set it to only one.
			assignCloseEventToStage();
		}

		public Window getOwner() {
			return stage.getOwner();
		}

		public Window getWindow() {
			return stage.getScene().getWindow();
		}

		public void close() {
			Platform.runLater(stage::close);
		}

	}
}
