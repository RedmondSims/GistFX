package com.redmondsims.gistfx.utils;

import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.LiveSettings;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.*;
import javafx.stage.Window;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
		FLOATER,
		MAIN,
		CENTERED
	}


	public static void init(boolean centerSceneByDefault, double width, double height) {
		SceneOne.width = width;
		SceneOne.height = height;
	}

	private static final Map<String, SceneObject> sceneMap             = new HashMap<>();
	private static final String                   core                 = "default";
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

		private Choice type = Choice.MAIN;

		private final Parent                    root;
		private       boolean                   fullScreen        = false;
		private       double                    width             = -1;
		private       double                    height            = -1;
		private       Modality                  modality          = null;
		private       StageStyle                initStyle         = null;
		private       String                    className         = "";
		private       String                    title             = "";
		private       EventHandler<WindowEvent> stageEventHandler = null;
		private       boolean                   centerSceneByDefault = false;

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
			this.centerSceneByDefault = true;
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
					case CENTERED -> centerSceneByDefault = true;
					case FLOATER -> type = Choice.FLOATER;
					case MAIN -> type = Choice.MAIN;
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
					.type(type)
					.stageCloseEvent(stageEventHandler)
					.build());
			sceneMap.get(mapName).setTitle(title != null ? title : "");
			if (width != -1 && height != -1) {
				sceneMap.get(mapName).setWidthHeight(width, height);
			}
		}

		public Builder showOptions(Choice... choices) {
			setSceneOptions(choices);
			return this;
		}

		public void show() {
			String mapName = className.isEmpty() ? core : className;
			buildSceneObject(mapName);
			sceneMap.get(mapName).show();
		}

		public void show(Choice... choices) {
			String mapName = className.isEmpty() ? core : className;
			setSceneOptions(choices);
			buildSceneObject(mapName);
			sceneMap.get(mapName).show();
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
		if (exists(core)) {
			sceneMap.get(core).show();
		}
	}

	public static void showScene(boolean centered) {
		if (exists(core)) {
			sceneMap.get(core).show();
			sceneMap.get(core).placeScene();
		}
	}

	public static void showScene(double width, double height, boolean centered) {
		if (exists(core)) {
			sceneMap.get(core).show(width,height,centered);
		}
	}

	public static void show() {
		if (exists(core)) {
			sceneMap.get(core).show();
		}
	}

	public static void show(String className) {
		if (exists(className)) {
			sceneMap.get(className).show();
		}
	}

	public static void showScene(String className, boolean centered) {
		if (exists(className)) {
			sceneMap.get(className).show();
			sceneMap.get(className).placeScene();
		}
	}

	public static void showScene(String className, double width, double height, boolean centered) {
		if (exists(className)) {
			sceneMap.get(className).show(width,height,centered);
		}
	}

	public static void sizeToScene() {
		if (exists(core)) {
			sceneMap.get(core).sizeToScene();
		}
	}

	public static void sizeToScene(String className) {
		if (exists(className)) {
			sceneMap.get(className).sizeToScene();
		}
	}

	public static void center() {
		sceneMap.get(core).placeScene();
	}

	public static void center(String className) {
		if (exists(className)) {
			sceneMap.get(className).placeScene();
		}
	}

	public static void setTitle(String title) {
		if (exists(core)) {
			sceneMap.get(core).setTitle(title);
		}
	}

	public static void setTitle(String className, String title) {
		if (exists(className)) {
			sceneMap.get(className).setTitle(title);
		}
	}

	public static void setFullScreen(boolean fullScreen) {
		if (exists(core)) {
			sceneMap.get(core).setFullScreen(fullScreen);
		}
	}

	public static void setFullScreen(String className, boolean fullScreen) {
		if (exists(className)) {
			sceneMap.get(className).setFullScreen(fullScreen);
		}
	}

	public static boolean isShowing(String className) {
		if (exists(className)) {
			return sceneMap.get(className).isShowing();
		}
		return false;
	}

	public static boolean isFullScreen() {
		if (exists(core)) {
			return sceneMap.get(core).isFullScreen();
		}
		return false;
	}

	public static boolean isFullScreen(String className) {
		if (exists(className)) {
			return sceneMap.get(className).isFullScreen();
		}
		return false;
	}

	public static void setOnKeyPressed(EventHandler<? super KeyEvent> value) {
		if (exists(core)) {
			sceneMap.get(core).setOnKeyPressed(value);
		}
	}

	public static void setOnKeyPressed(String className, EventHandler<? super KeyEvent> value) {
		if (exists(className)) {
			sceneMap.get(className).setOnKeyPressed(value);
		}
	}

	public static void setStageCloseEvent(EventHandler<WindowEvent> eventHandler) {
		if (exists(core)) {
			sceneMap.get(core).setStageCloseEvent(eventHandler);
		}
	}

	public static void setStageCloseEvent(String className, EventHandler<WindowEvent> eventHandler) {
		if (exists(className)) {
			sceneMap.get(className).setStageCloseEvent(eventHandler);
		}
	}

	public static void exit() {
		for (SceneObject scene : sceneMap.values()) {
			scene.close();
		}
		System.exit(0);
	}

	public static Window getOwner() {
		if (exists(core)) {
			return sceneMap.get(core).getOwner();
		}
		return null;
	}

	public static Window getOwner(String className) {
		if (exists(className)) {
			return sceneMap.get(className).getOwner();
		}
		return null;
	}

	public static Window getWindow() {
		if (exists(core)) {
			return sceneMap.get(core).getWindow();
		}
		return null;
	}

	public static Window getWindow(String className) {
		if (exists(className)) {
			return sceneMap.get(className).getWindow();
		}
		return null;
	}

	public static void close() {
		if (exists(core)) {
			sceneMap.get(core).close();
		}
	}

	public static void close(String className) {
		if (exists(className)) {
			sceneMap.get(className).close();
		}
	}

	public static void showAndWait() {
		if (exists(core)) {
			sceneMap.get(core).showAndWait();
		}
	}

	public static void showAndWait(String className) {
		if (exists(className)) {
			sceneMap.get(className).showAndWait();
		}
	}

	public static Stage getStage() {
		if (exists(core)) {
			return sceneMap.get(core).getStage();
		}
		return null;
	}

	public static Scene getScene() {
		if (exists(core)) {
			return sceneMap.get(core).getScene();
		}
		return null;
	}

	public static Stage getStage(String className) {
		if (exists(className)) {
			return sceneMap.get(className).getStage();
		}
		return null;
	}

	public static Scene getScene(String className) {
		if (exists(className)) {
			return sceneMap.get(className).getScene();
		}
		return null;
	}

	public static void toggleWideMode(String className) {
		if(exists(className)){
			sceneMap.get(className).toggleWideMode();
		}
	}

	public static boolean isInWideMode(String className) {
		if (exists(className)) {
			return sceneMap.get(className).isWideMode();
		}
		return false;
	}

	public static boolean windowIsResizing(String className) {
		if (exists(className)) {
			return sceneMap.get(className).isWindowResizing();
		}
		return true;
	}

	private static boolean exists (String className) {
		return sceneMap.containsKey(className);
	}

	private static class SceneObject {

		public static class Builder {

			public Builder(Parent root) {
				this.root = root;
			}

			private       Stage              stage;
			private 	  Scene				 scene;
			private final Parent             root;
			private       boolean            centerSceneByDefault = false;
			private       boolean            fullscreen;
			private       double             width                = -1;
			private       double             height               = -1;
			private       Modality           modality             = null;
			private       StageStyle         style                = null;
			private String title = "";
			private       EventHandler<WindowEvent>              sceneEventHandler = null;
			private Choice type;

			public Builder type(Choice type) {
				this.type = type;
				return this;
			}

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
				scene.getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
				stage.setScene(scene);
				return new SceneObject(this);
			}

		}

		private final Stage                     stage;
		private       Scene                     scene;
		private       boolean                   center;
		private       boolean                   fullscreen;
		private       double                    width;
		private       double                    height;
		private       double                    snapWidth;
		private       boolean                   wideMode       = false;
		private final Modality                  modality;
		private final StageStyle                style;
		private       String                    title;
		private       EventHandler<WindowEvent> stageEventHandler;
		private       boolean                   windowResizing = false;
		private Timer resizeTimer;

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
			if (build.sceneEventHandler == null && build.type.equals(Choice.MAIN)) {
				setStageCloseEvent(e -> System.exit(13));
			}
			scene.addPreLayoutPulseListener(() -> {
				new Thread(() -> {
					windowResizing = true;
					if (resizeTimer != null) resizeTimer.cancel();
					resizeTimer = new Timer();
					resizeTimer.schedule(setWindowSizingTask(), 1000);
				}).start();
			});
		}

		private void setScene() {
			stage.setScene(scene);
		}

		private void finalShow() {
			setScene();
			setSize();
			stage.show();
			placeScene();
			assignCloseEventToStage();
		}

		private TimerTask setWindowSizingTask() {
			return new TimerTask() {
				@Override public void run() {
					windowResizing = false;
				}
			};
		}

		public void show() {
			finalShow();
		}

		public void show(Parent root) {
			scene = new Scene(root);
			finalShow();
		}

		public void show(double width, double height) {
			setWidthHeight(width,height);
			finalShow();
		}

		public void show(double width, double height, boolean centered) {
			setWidthHeight(width,height);
			center = centered;
			finalShow();
		}

		public void showAndWait() {
			setScene();
			stage.showAndWait();
		}

		private void setSize() {
			if (width != -1) stage.setWidth(width);
			if (height != -1) stage.setHeight(height);
		}

		public void placeScene() {
			if (center)	stage.getScene().getWindow().centerOnScreen();
			stage.setFullScreen(fullscreen);
		}

		public Scene getScene() {return scene;}

		public Stage getStage() {return stage;}

		public void setModality(Modality modality) {
			stage.initModality(modality);
		}

		public void setStyle(StageStyle style) {
			stage.initStyle(style);
		}

		public void setWidthHeight(double width, double height) {
			this.width = width;
			this.height = height;
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

		public void toggleWideMode() {
			if (!wideMode) {
				snapWidth = stage.getWidth();
				Toolkit   toolkit   = Toolkit.getDefaultToolkit();
				Dimension dimension = toolkit.getScreenSize();
				scene.getWindow().setWidth(dimension.getWidth());
				scene.getWindow().centerOnScreen();
			}
			else {
				scene.getWindow().setWidth(snapWidth);
				scene.getWindow().centerOnScreen();
			}
			wideMode = !wideMode;
		}

		public boolean isWideMode() {return wideMode;}

		public Window getOwner() {
			return stage.getOwner();
		}

		public Window getWindow() {
			return stage.getScene().getWindow();
		}

		public void close() {
			Platform.runLater(stage::close);
		}

		public boolean isWindowResizing() {return windowResizing;}


	}
}
