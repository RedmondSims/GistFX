package com.redmondsims.gistfx.sceneone;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.stage.*;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.redmondsims.gistfx.sceneone.KeyWords.*;

public final class SceneOne {

	private static final Map<String, SceneObject> sceneMap = new HashMap<>();
	private static final Map<String,StageObject> stageMap         = new HashMap<>();
	private static final String            defaultSceneName = "!DEFAULT!";
	private static String                  lastScene        = "";

	/**
	 * Default Methods call Named methods with defaultScene
	 */

	public static SceneBuilder set(Parent root) {
		return set(root, defaultSceneName);
	}

	public static void setParent(Parent root) {
		setParent(defaultSceneName, root);
	}

	public static Show show() {
		return show(defaultSceneName);
	}

	public static Show showScene() {
		return show();
	}

	public static Show showAndWait() {
		return showAndWait(defaultSceneName);
	}

	public static void setPrimaryStage(Stage stage) {
		StageObject stageObject = new StageObject(stage);
		if (!stageMap.containsKey(defaultSceneName)) {
			stageMap.put(defaultSceneName, stageObject);
		}
		else {
			stageMap.replace(defaultSceneName, stageObject);
		}
	}

	public static void showLastScene() {
		if (!lastScene.isEmpty()) show(lastScene);
	}

	public static boolean lastSceneAvailable() {
		return !lastScene.isEmpty();
	}

	public static boolean isShowing() {
		return isShowing(defaultSceneName);
	}

	public static void setOnKeyPressed(EventHandler<? super KeyEvent> event) {
		setOnKeyPressed(defaultSceneName, event);
	}

	public static void setOnKeyReleased(EventHandler<? super KeyEvent> event) {
		setOnKeyReleased(defaultSceneName, event);
	}

	public static void setStageCloseEvent(EventHandler<WindowEvent> eventHandler) {
		setStageCloseEvent(defaultSceneName, eventHandler);
	}

	public static void setMaximized(boolean value) {
		setMaximized(defaultSceneName, value);
	}

	public static void setMinimized(boolean value) {
		setMinimized(defaultSceneName, value);
	}

	public static void setFullScreen(boolean fullscreen) {
		setFullScreen(defaultSceneName, fullscreen);
	}

	public static void setHiddenOnLostFocus(boolean hiddenOnLostFocus) {
		setHiddenOnLostFocus(defaultSceneName, hiddenOnLostFocus);
	}

	public static void setWindowSize(double width, double height) {
		setWindowSize(defaultSceneName, width, height);
	}

	public static void runOnShown(EventHandler<Event> handler) {
		runOnShown(defaultSceneName, handler);
	}

	public static void runOnHidden(EventHandler<Event> handler) {
		runOnHidden(defaultSceneName, handler);
	}

	public static Stage getStage() {
		return getStage(defaultSceneName);
	}

	public static Scene getScene() {
		return getScene(defaultSceneName);
	}

	public static Window getOwner() {
		return getOwner(defaultSceneName);
	}

	public static Window getWindow() {
		return getWindow(defaultSceneName);
	}

	public static void hide() {
		hide(defaultSceneName);
	}

	public static void close() {
		for(SceneObject scene : sceneMap.values()) {
			if(scene.isShowing()) scene.close();
		}
	}

	public static void resizeWidth(Double width) {
		resizeWidth(defaultSceneName,width);
	}

	public static void resizeHeight(Double height) {
		resizeHeight(defaultSceneName,height);
	}

	public static void autoSize() {
		autoSize(defaultSceneName);
	}

	public static void doSize(String sceneName) {
		Platform.runLater(() -> {
			autoSize(sceneName);
		});
	}

	public static void setWidth(String sceneName, double width) {
		sceneMap.get(sceneName).sizeToWidth(width);
	}

	/**
	 * Named methods (called by above default Methods or called externally by passing in a sceneName
	 */

	public static SceneBuilder set(Parent root, String sceneName) {
		return new SceneBuilder(root, sceneName);
	}

	public static SceneBuilder set(Parent root, String sceneName, Stage ownerStage) {
		return new SceneBuilder(root, sceneName, ownerStage);
	}

	public static void setParent(String sceneName, Parent root) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setParent(root);
	}

	public static Show show(String sceneName) {
		checkScene(sceneName);
		lastScene = sceneName;
		sceneMap.get(sceneName).unHide();
		//return new Show(sceneName,false);
		return null;
	}

	public static Show show(String sceneName, Parent root) {
		checkScene(sceneName);
		lastScene = sceneName;
		return new Show(sceneName,root);
	}

	public static Show showScene(String sceneName) {
		return show(sceneName);
	}

	public static Show showScene(String sceneName, Parent root) {
		return show(sceneName,root);
	}

	public static Show showAndWait(String sceneName) {
		checkScene(sceneName);
		return new Show(sceneName,true);
	}

	public static void showAndWait(String sceneName, double width, double height) {
		checkScene(sceneName);
		sceneMap.get(sceneName).showAndWait(width, height);
	}

	public static boolean isShowing(String sceneName) {
		if(sceneMap.containsKey(sceneName)) {
			return sceneMap.get(sceneName).isShowing();
		}
		return false;
	}

	public static boolean sceneExists(String sceneName) {
		return sceneMap.containsKey(sceneName);
	}

	public static void removeScene(String sceneName) {
		checkScene(sceneName);
		sceneMap.remove(sceneName);
	}

	public static void setOnKeyPressed(String sceneName, EventHandler<? super KeyEvent> event) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setOnKeyPressed(event);
	}

	public static void setOnKeyReleased(String sceneName, EventHandler<? super KeyEvent> keyReleasedEventHandler) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setOnKeyReleased(keyReleasedEventHandler);
	}

	public static void setStageCloseEvent(String sceneName, EventHandler<WindowEvent> eventHandler) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setStageCloseEvent(eventHandler);
	}

	public static void setMaximized(String sceneName, boolean maximized) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setMaximized(maximized);
	}

	public static void setMinimized(String sceneName, boolean minimized) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setMinimized(minimized);
	}

	public static void setFullScreen(String sceneName, boolean fullscreen) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setFullScreen(fullscreen);
	}

	public static void setHiddenOnLostFocus(String sceneName, boolean hiddenOnLostFocus) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setHiddenOnLostFocus(hiddenOnLostFocus);
	}

	public static void setWindowSize(String sceneName,double width, double height) {
		checkScene(sceneName);
		sceneMap.get(sceneName).setSize(width,height);
	}

	public static void runOnShown(String sceneName, EventHandler<Event> handler) {
		checkScene(sceneName);
		sceneMap.get(sceneName).runOnShown(handler);
	}

	public static void runOnHidden(String sceneName, EventHandler<Event> handler) {
		checkScene(sceneName);
		sceneMap.get(sceneName).runOnHidden(handler);
	}

	public static Stage getStage(String sceneName) {
		checkScene(sceneName);
		return sceneMap.get(sceneName).getStage();
	}

	public static Scene getScene(String sceneName) {
		checkScene(sceneName);
		return sceneMap.get(sceneName).getScene();
	}

	public static Window getOwner(String sceneName) {
		checkScene(sceneName);
		return sceneMap.get(sceneName).getOwner();
	}

	public static Window getWindow(String sceneName) {
		checkScene(sceneName);
		return sceneMap.get(sceneName).getWindow();
	}

	public static void hide(String sceneName) {
		checkScene(sceneName);
		sceneMap.get(sceneName).hide();
	}

	public static void close(String sceneName) {
		checkScene(sceneName);
		sceneMap.get(sceneName).close();
	}

	public static void closeAllExcept(String sceneName) {
		for (String name : sceneMap.keySet()) {
			if (!name.equals(sceneName)) {
				sceneMap.get(name).getStage().hide();
			}
		}
	}

	public static void closeAll() {
		for (String name : sceneMap.keySet()) {
			sceneMap.get(name).getStage().close();
		}
	}

	public static boolean isFullScreen (String sceneName) {
		checkScene(sceneName);
		return sceneMap.get(sceneName).isFullscreen();
	}

	public static void resizeWidth(String sceneName, Double width) {
		checkScene(sceneName);
		sceneMap.get(sceneName).resizeWidth(width);
	}

	public static void resizeHeight(String sceneName, Double height) {
		checkScene(sceneName);
		sceneMap.get(sceneName).resizeHeight(height);
	}

	public static void exit() {
		for (SceneObject sceneObject : sceneMap.values()) {
			sceneObject.close();
		}
		System.exit(0);
	}

	private static void checkScene(String sceneName) {
		if (!sceneMap.containsKey(sceneName)) {
			throw noSceneError(sceneName);
		}
	}

	public static void toggleWideMode(String sceneName, boolean wideMode) {
		checkScene(sceneName);
			sceneMap.get(sceneName).setWideMode(wideMode);
	}

	public static void autoSize(String className) {
		if(exists(className)) {
			sceneMap.get(className).autoSize();
		}
	}

	private static boolean exists (String className) {
		return sceneMap.containsKey(className);
	}

	/**
	 * Error Methods
	 */

	private static UnsupportedOperationException noSceneError(String sceneName) {
		String message = "* Scene " + sceneName + " does not exist, you need to run SceneOne.set(Parent, <optional sceneName>).build(); to complete a Scene *";
		String frame = getBoardersFor(message);
		String finalMessage = "\n\n"+ frame + "\n" + message + "\n" + frame + "\n";
		return new UnsupportedOperationException(finalMessage);
	}

	private static UnsupportedOperationException generalError(String message) {
		String frame = getBoardersFor(message);
		String finalMessage = "\n\n"+ frame + "\n" + message + "\n" + frame + "\n";
		return new UnsupportedOperationException(finalMessage);
	}

	private static String getBoardersFor(String message) {
		int length = message.length();
		StringBuilder frame = new StringBuilder();
		frame.append("*".repeat(length));
		return frame.toString();
	}

	/**
	 * SceneObject class is the meat of SceneOne and the SceneBuilder class is the interface to building a scene
	 */
	public static class SceneBuilder{
		public SceneBuilder(Parent root, String sceneName) {
			this.root      = root;
			this.sceneName = sceneName;
			buildWatcher();
		}

		public SceneBuilder(Parent root, String sceneName, Stage ownerStage) {
			this.root      = root;
			this.sceneName = sceneName;
			this.owner     = ownerStage;
		}

		private final Parent                         root;
		private final String                         sceneName;
		private final List<String>                   styleSheets            = new ArrayList<>();
		private final List<KeyWords>                 showOptions            = new ArrayList<>();
		private       double                         width                  = -1;
		private       double                         height                 = -1;
		private       double                         posX                   = -1;
		private       double                         posY                   = -1;
		private       boolean                        exitClose              = false;
		private       boolean                        hideOnLostFocus        = false;
		private       boolean                        autoSize        		= false;
		private       Modality                       modality               = null;
		private       StageStyle                     stageStyle             = null;
		private       String                         title                  = "";
		private       EventHandler<WindowEvent>      stageCloseEventHandler = null;
		private       EventHandler<? super KeyEvent> keyPressedEventHandler;
		private       EventHandler<? super KeyEvent> keyReleasedEventHandler;
		private       EventHandler<Event>            onShowEventHandler;
		private       EventHandler<Event>            onHideEventHandler;
		private       boolean                        buildExecuted          = false;
		private       boolean                        newStage               = false;
		private Stage stage;
		private Stage owner;
		private boolean alwaysOnTop = false;

		private void buildWatcher() {
			new Thread(() -> {
				try {
					TimeUnit.MILLISECONDS.sleep(3000);
					if(!buildExecuted) {
						throw generalError("You didn't finish your SceneOne.set() sentence with either build(), show() or showAndWait()");
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
		}

		public SceneBuilder alwaysOnTop() {
			this.alwaysOnTop = true;
			return this;
		}
		public SceneBuilder modality(Modality modality) {
			this.modality = modality;
			return this;
		}
		public SceneBuilder initStyle(StageStyle stageStyle) {
			this.stageStyle = stageStyle;
			return this;
		}
		public SceneBuilder init(Modality modality, StageStyle initStyle) {
			this.modality   = modality;
			this.stageStyle = initStyle;
			return this;
		}
		public SceneBuilder size(double width, double height) {
			this.width  = width;
			this.height = height;
			return this;
		}
		public SceneBuilder sizeToScene() {
			showOptions.add(FITSCENE);
			return this;
		}
		public SceneBuilder position(double posX, double posY) {
			this.posX = posX;
			this.posY = posY;
			showOptions.add(KeyWords.EXACT);
			return this;
		}
		public SceneBuilder title(String title) {
			this.title = title;
			return this;
		}
		public SceneBuilder showOptions(KeyWords... showOptions) {
			int plot = 0;
			int style = 0;
			int mod = 0;
			List<KeyWords> invalidOptions = Arrays.asList(FITSCENE,FLOAT);
			for(KeyWords option : showOptions) {
				if (invalidOptions.contains(option)) throw generalError(option.name() + " Is not a valid showOptions choice");
				switch (option) {
					case FULLSCREEN, MAXIMIZED, MINIMIZED, CENTERED -> plot++;
					case MODALITY_APPLICATION, MODALITY_WINDOW -> mod++;
					case STYLE_TRANSPARENT, STYLE_DECORATED, STYLE_UNDECORATED, STYLE_UTILITY -> style++;
				}
			}
			if (plot > 1) {
				throw generalError("You can only specify one window layout for the scene: FULLSCREEN, MAXIMIZED, MINIMIZED, CENTERED");
			}
			if (style > 1) {
				throw generalError("You can only specify one stage style for your scene");
			}
			if (mod > 1) {
				throw generalError("You can only specify one stage modality for your scene");
			}
			this.showOptions.addAll(Arrays.asList(showOptions));
			return this;
		}
		public SceneBuilder centered() {
			showOptions.add(CENTERED);
			return this;
		}
		public SceneBuilder fullscreen() {
			showOptions.add(FULLSCREEN);
			return this;
		}
		public SceneBuilder maximized() {
			showOptions.add(MAXIMIZED);
			return this;
		}
		public SceneBuilder autoSize() {
			this.autoSize = true;
			return this;
		}
		public SceneBuilder minimized() {
			showOptions.add(MINIMIZED);
			return this;
		}
		public SceneBuilder exitOnClose() {
			this.exitClose = true;
			return this;
		}
		public SceneBuilder hideOnLostFocus() {
			this.hideOnLostFocus = true;
			return this;
		}
		public SceneBuilder newStage() {
			stage = new Stage();
			StageObject stageObject = new StageObject(stage);
			if(stageMap.containsKey(sceneName)) {
				stageMap.replace(sceneName,stageObject);
			}
			else {
				stageMap.put(sceneName, stageObject);
			}
			newStage = true;
			return this;
		}
		public SceneBuilder onCloseEvent(EventHandler<WindowEvent> stageCloseEventHandler) {
			this.stageCloseEventHandler = stageCloseEventHandler;
			return this;
		}
		public SceneBuilder onKeyPressed(EventHandler<? super KeyEvent> keyPressedEventHandler) {
			this.keyPressedEventHandler = keyPressedEventHandler;
			return this;
		}
		public SceneBuilder onKeyReleased(EventHandler<? super KeyEvent> keyReleasedEventHandler) {
			this.keyReleasedEventHandler = keyReleasedEventHandler;
			return this;
		}
		public SceneBuilder styleSheets(String... styleSheets) {
			this.styleSheets.addAll(Arrays.asList(styleSheets));
			return this;
		}
		public SceneBuilder onShowScene(Integer sceneId, EventHandler<Event> onShowEventHandler) {
			this.onShowEventHandler = onShowEventHandler;
			return this;
		}
		public SceneBuilder onHideScene(Integer sceneId, EventHandler<Event> onHideEventHandler) {
			this.onHideEventHandler = onHideEventHandler;
			return this;
		}
		public void build() {
			buildExecuted = true;
			stageMap.remove(sceneName);
			stage = new Stage();
			stage.setAlwaysOnTop(alwaysOnTop);
			if(stageStyle != null) stage.initStyle(stageStyle);
			if(modality != null) stage.initModality(modality);
			stageMap.put(sceneName,new StageObject(stage));
			boolean optionsHasStyle = showOptions.contains(STYLE_DECORATED) || showOptions.contains(STYLE_UTILITY) || showOptions.contains(STYLE_UNIFIED) || showOptions.contains(STYLE_UNDECORATED) || showOptions.contains(STYLE_TRANSPARENT);
			boolean optionsHasModality =  showOptions.contains(MODALITY_WINDOW)  || showOptions.contains(MODALITY_APPLICATION);
			boolean hasNoWindowHandle = showOptions.contains(STYLE_UNDECORATED) || showOptions.contains(STYLE_TRANSPARENT) || showOptions.contains(STYLE_UTILITY);
			if (stageStyle != null && optionsHasStyle) throw generalError("You cannot set the initStyle specifically and also include a STYLE_ in showOptions");
			if (modality != null && optionsHasModality) throw generalError("You cannot set the modality specifically and also include a MODALITY_ in showOptions");
			if (showOptions.contains(FITSCENE)  && hasNoWindowHandle) throw generalError("You cannot set a style that has no Window handle and also set FITSCENE");
			sceneMap.remove(sceneName);
			sceneMap.put(sceneName, new SceneObject(this));
		}
		public void show() {
			build();
			sceneMap.get(sceneName).show();
		}
		public void showAndWait() {
			build();
			sceneMap.get(sceneName).showAndWait();
		}
	}

	public static class Show {
		private       boolean splitX = false;
		private       boolean splitY = false;
		private       double  mouseX = -1;
		private       double  mouseY = -1;
		private       double  posX   = -1;
		private       double  posY   = -1;
		private       boolean wait   = false;
		private       Parent  root;
		private final String  sceneName;
		private       int     called = 0;

		public Show(String sceneName, boolean wait) {
			this.sceneName = sceneName;
			this.wait = wait;
			this.show();
		}
		public Show(String sceneName, Parent root) {
			this.sceneName = sceneName;
			this.root = root;
			this.show();
		}
		public Show splitXAtY(double mouseX, double posY) {
			this.mouseX = mouseX;
			this.posY = posY;
			splitX       = true;
			return this;
		}
		public Show splitYAtX(double mouseY, double posX) {
			this.mouseY = mouseY;
			this.posX = posX;
			splitY       = true;
			return this;
		}
		public Show andWait() {
			wait = true;
			return this;
		}
		public void show() {
			called++;
			if (called > 1) return;
			new Thread(() -> {
				try {TimeUnit.MILLISECONDS.sleep(300);}catch (InterruptedException e) {e.printStackTrace();}
				if((mouseX > 0 || mouseY > 0) && wait) throw generalError("Cannot both show a Scene using split coordinates and also use showAndWait");
				if (root != null) sceneMap.get(sceneName).setParent(root);
				if (mouseX > 0) sceneMap.get(sceneName).splitXAtY(mouseX,posY);
				if (mouseY > 0) sceneMap.get(sceneName).splitYAtX(mouseY,posX);
				Platform.runLater(() -> {
					if(wait) sceneMap.get(sceneName).showAndWait();
					else sceneMap.get(sceneName).show();
				});
			}).start();
		}
	}

	private static class StageObject {
		private final Stage stage;
		private boolean hasShown;
		public StageObject(Stage stage) {
			this.stage = stage;
		}
		public void setHasShown() {
			this.hasShown = true;
		}
		public boolean hasShown() {
			return hasShown;
		}
		public boolean hasNotShown() {
			return !hasShown;
		}
		public Stage get(){
			return stage;
		}
	}

	private static class SceneObject {

		private enum Term {
			SHOW,
			SHOW_WAIT,
			CENTERED,
			EXACT
		}

		protected static class Position {
			private enum POSITION {
				X,
				Y,
				MOUSEX,
				MOUSEY,
				SPLITX,
				SPLITY
			}

			private final Map<POSITION, Double> VALUES = new HashMap<>();

			public Position(double posX, double posY) {
				VALUES.put(POSITION.X,posX);
				VALUES.put(POSITION.Y,posY);
				VALUES.put(POSITION.MOUSEX,0.0);
				VALUES.put(POSITION.MOUSEY,0.0);
				VALUES.put(POSITION.SPLITX,0.0);
				VALUES.put(POSITION.SPLITY,0.0);
			}

			public void set(Double valueX, Double valueY) {
				VALUES.replace(POSITION.X, valueX);
				VALUES.replace(POSITION.Y, valueY);
			}
			public Double getX() {
				return VALUES.get(POSITION.X);
			}

			public Double getY() {
				return VALUES.get(POSITION.Y);
			}

			public boolean XisGreaterThan(double value) {
				return VALUES.get(POSITION.X) > value;
			}

			public boolean YisGreaterThan(double value) {
				return VALUES.get(POSITION.Y) > value;
			}

			public boolean notSet() {
				return !isSet();
			}

			public boolean isSet() {
				return XisGreaterThan(0) && YisGreaterThan(0);
			}

			public void adjust(Stage stage) {
				stage.setX(VALUES.get(POSITION.X));
				stage.setY(VALUES.get(POSITION.Y));
			}

			public void adjust(Window window) {
				window.setX(VALUES.get(POSITION.X));
				window.setY(VALUES.get(POSITION.Y));
			}

			public void setMouseXAtY(double mouseX, double posY) {
				VALUES.replace(POSITION.MOUSEX,mouseX);
				VALUES.replace(POSITION.SPLITY, posY);
			}

			public void setMouseYAtX(double mouseY, double posX) {
				VALUES.replace(POSITION.MOUSEY,mouseY);
				VALUES.replace(POSITION.SPLITX, posX);
			}

			public void showSplitXAtY(Window window) {
				double width = window.getWidth();
				double newX = VALUES.get(POSITION.MOUSEX) - (width / 2);
				double newY = VALUES.get(POSITION.SPLITY);
				window.setX(newX);
				window.setY(newY);
			}

			public void showSplitYAtX(Window window) {
				double height = window.getHeight();
				double newX = VALUES.get(POSITION.SPLITX);
				double newY = VALUES.get(POSITION.MOUSEY) - (height / 2);
				window.setX(newX);
				window.setY(newY);
			}

			public void showSplitXY(Window window) {
				double height = window.getHeight();
				double width = window.getWidth();
				double newX = VALUES.get(POSITION.MOUSEX) + (width / 2);
				double newY = VALUES.get(POSITION.MOUSEY) + (height / 2);
				window.setX(newX);
				window.setY(newY);
			}
		}

		protected static class Size {

			private enum SIZE {
				WIDTH,
				HEIGHT
			}

			private final Map<SIZE,Double> VALUES = new HashMap<>();
			private final String           sceneName;

			public Size(double width, double height, String sceneName) {
				VALUES.put(SIZE.WIDTH,width);
				VALUES.put(SIZE.HEIGHT,height);
				this.sceneName = sceneName;
			}

			public void set(Double width, Double height) {
				VALUES.replace(SIZE.WIDTH, width);
				VALUES.replace(SIZE.HEIGHT, height);
			}

			public void setWidth(Double width) {
				VALUES.replace(SIZE.WIDTH, width);
			}

			public void setHeight(Double height) {
				VALUES.replace(SIZE.HEIGHT, height);
			}

			public Double getWidth() {
				return VALUES.get(SIZE.WIDTH);
			}

			public Double getHalfWidth() {
				return VALUES.get(SIZE.WIDTH) / 2;
			}

			public Double getHeight() {
				return VALUES.get(SIZE.HEIGHT);
			}

			public Double getHalfHeight() {
				return VALUES.get(SIZE.HEIGHT) / 2;
			}

			private boolean XisGreaterThanZero() {
				return VALUES.get(SIZE.WIDTH) > 0.0;
			}

			private boolean YisGreaterThanZero() {
				return VALUES.get(SIZE.HEIGHT) > 0.0;
			}

			public boolean notSet() {
				return !isSet();
			}

			public boolean isSet() {
				return XisGreaterThanZero() && YisGreaterThanZero();
			}

			public void resize(Stage stage) {
				if(isSet()) {
					stage.setWidth(VALUES.get(SIZE.WIDTH));
					stage.setHeight(VALUES.get(SIZE.HEIGHT));
				}
			}
		}

		private       Parent                         root;
		private final String                         sceneName;
		private       Stage                          stage;
		private       Scene                          scene;
		private       KeyWords                       plot;
		private       Modality                       modality;
		private       StageStyle                     stageStyle;
		private final List<String>                   styleSheets;
		private       String                         title;
		private       EventHandler<WindowEvent>      stageCloseEventHandler;
		private       EventHandler<? super KeyEvent> keyPressedEventHandler;
		private       EventHandler<? super KeyEvent> keyReleasedEventHandler;
		private final List<KeyWords>                 showOptions;
		private       Term                           showOption;
		private       EventHandler<Event>            onShowEventHandler;
		private       EventHandler<Event>            onHideEventHandler;
		private       boolean                        hideOnLostFocus;
		private final Size                           sceneSize;
		private final Position                       scenePosition;
		private       Window                         window;
		private final boolean                        autoSize;
		private final Dimension                      dimension = Toolkit.getDefaultToolkit().getScreenSize();
		private final Stage                          owner;
		private double originalWidth = 0;
		private double originalHeight= 0;
		private double desiredWidth = 0;
		private double desiredHeight = 0;
		private boolean alwaysOnTop;

		private final ChangeListener<Boolean> lostFocusListener = (observable, oldValue, newValue) -> {
			if (!newValue) {
				hide();
			}
		};

		public SceneObject(SceneBuilder build) {
			root                    = build.root;
			sceneName               = build.sceneName;
			modality                = build.modality;
			stageCloseEventHandler  = build.stageCloseEventHandler;
			stageStyle              = build.stageStyle;
			title                   = build.title;
			styleSheets             = build.styleSheets;
			keyPressedEventHandler  = build.keyPressedEventHandler;
			keyReleasedEventHandler = build.keyReleasedEventHandler;
			showOptions             = build.showOptions;
			onShowEventHandler      = build.onShowEventHandler;
			onHideEventHandler      = build.onHideEventHandler;
			hideOnLostFocus         = build.hideOnLostFocus;
			autoSize                = build.autoSize;
			stage                   = build.stage;
			owner                   = build.owner;
			alwaysOnTop = build.alwaysOnTop;

			if(owner != null) stage.initOwner(owner);
			setRoot();
			processShowOptions();
			for(String styleSheet : styleSheets) {
				scene.getStylesheets().add(styleSheet);
			}
			scene.getStylesheets().add(AppSettings.get().theme().getStyleSheet());
			if (build.exitClose) {
				setStageCloseEvent(e -> SceneOne.exit());
			}
			setHiddenOnLostFocus(hideOnLostFocus);

			scenePosition = new Position(build.posX, build.posY);

			sceneSize = new Size(build.width,build.height,sceneName);
		}

		public void setHiddenOnLostFocus(boolean hideOnLostFocus) {
			this.hideOnLostFocus = hideOnLostFocus;
			if (stage != null) {
				if (hideOnLostFocus) {
					stage.focusedProperty().addListener(lostFocusListener);
				}
				else {
					stage.focusedProperty().removeListener(lostFocusListener);
				}
			}
		}

		private void processShowOptions() {
			plot = CENTERED;
			if (showOptions.size() == 0) return;
			stageStyle = null;
			modality = null;
			StageObject stageObject = stageMap.getOrDefault(sceneName, stageMap.get(defaultSceneName));
			for (KeyWords option : showOptions) {
				switch (option) {
					case FULLSCREEN -> plot = FULLSCREEN;
					case MAXIMIZED -> plot = MAXIMIZED;
					case CENTERED -> plot = CENTERED;
					case MINIMIZED -> plot = MINIMIZED;
					case MODALITY_APPLICATION -> modality = Modality.APPLICATION_MODAL;
					case MODALITY_WINDOW -> modality = Modality.WINDOW_MODAL;
					case STYLE_TRANSPARENT -> stageStyle = StageStyle.TRANSPARENT;
					case STYLE_DECORATED -> stageStyle = StageStyle.DECORATED;
					case STYLE_UNDECORATED -> stageStyle = StageStyle.UNDECORATED;
					case STYLE_UNIFIED -> stageStyle = StageStyle.UNIFIED;
					case STYLE_UTILITY -> stageStyle = StageStyle.UTILITY;
				}
			}
			if (stageObject.hasNotShown()) {
				if (stageStyle != null) {
					stage.initStyle(stageStyle);
				}
				if (modality != null) {
					stage.initModality(modality);
				}
			}
		}

		private void setTitle() {
			if (!title.isEmpty()) stage.setTitle(title);
		}

		private void getStageForScene() {
			StageObject stageObject = stageMap.getOrDefault(sceneName,stageMap.get(defaultSceneName));
			stage = stageObject.get();
			if(!stage.isShowing() && !stageObject.hasShown) {
				if (stageStyle != null) stage.initStyle(stageStyle);
				if (modality != null) stage.initModality(modality);
			}
		}

		private void setScene() {
			stage.setScene(scene);
		}

		private void setCalculatedCenter() {
			if (sceneSize.isSet()) {
				Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
				double    screenWidth      = screenDimensions.width;
				double    screenHeight     = screenDimensions.height;
				double    posX             = (screenWidth /2) - (sceneSize.getWidth() /2);
				double    posY             = (screenHeight/2) - (sceneSize.getHeight()/2);
				scenePosition.set(posX, posY);
				setStagePosition();
			}
		}

		private void setRoot() {
			scene = new Scene(root);
		}

		private void setStageSize() {
			sceneSize.resize(this.stage);
		}

		private void setStagePosition() {
			scenePosition.adjust(this.stage);
		}

		private void assignKeyPressedEventHandler() {
			if (this.keyPressedEventHandler != null) {
				scene.setOnKeyPressed(keyPressedEventHandler);
			}
		}

		private void assignKeyReleasedEventHandler() {
			if (this.keyReleasedEventHandler != null) {
				scene.setOnKeyReleased(keyReleasedEventHandler);
			}
		}

		private void assignCloseEventHandler() {
			if (stageCloseEventHandler != null) {
				stage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, stageCloseEventHandler);
				stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, stageCloseEventHandler);
				stage.setOnCloseRequest(stageCloseEventHandler);
			}
		}

		private void ignoreCloseEventHandler() {
			if(stageCloseEventHandler != null) {
				stage.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, stageCloseEventHandler);
			}
			stage.setOnCloseRequest(null);
		}

		private void setupScene() {
			getStageForScene();
			setTitle();
			assignCloseEventHandler();
			assignKeyPressedEventHandler();
			assignKeyReleasedEventHandler();
			setHideOnLostFocus();
			setStageSize();
			setStagePosition();
			setScene();
		}

		private void preShowProcessing() {
			if(showOption.equals(Term.SHOW_WAIT) && plot.equals(CENTERED))
				setCalculatedCenter();
			if (onShowEventHandler != null) {
				onShowEventHandler.handle(null);
			}
		}

		private void postShowProcessing() {
			window = scene.getWindow();
			if (showOptions.contains(FITSCENE)) {
				window.sizeToScene();
				sceneSize.set(window.getWidth(), window.getHeight());
			}
			switch (plot) {
				case FULLSCREEN, MAXIMIZED -> stage.setMaximized(true);
				case MINIMIZED -> stage.setMaximized(false);
				case CENTERED -> {
					window.centerOnScreen();
					scenePosition.set(window.getX(), window.getY());
					if (sceneSize.notSet())
						sceneSize.set(window.getWidth(), window.getHeight());
				}
				case EXACT -> {
					scenePosition.adjust(this.window);
					if (sceneSize.notSet())
						sceneSize.set(window.getWidth(), window.getHeight());
				}
			}
			if (splitX || splitY) splitPosition();
			if (onShowEventHandler != null) {
				onShowEventHandler.handle(null);
			}
			sceneSize.resize(this.stage);
		}

		private void showScene() {
			setupScene();
			switch (showOption) {
				case SHOW_WAIT -> {
					preShowProcessing();
					if(!stage.isShowing())
						stage.showAndWait();
				}
				case SHOW -> {
					if(!stage.isShowing())
						stage.show();
					postShowProcessing();
				}
			}
			if(autoSize) {
				autoSize();
				scene.getWindow().centerOnScreen();
			}
			StageObject stageObject = stageMap.getOrDefault(sceneName, stageMap.get(defaultSceneName));
			stageObject.setHasShown();
			originalWidth = scene.getWidth();
			originalHeight = scene.getHeight();
		}

		public void show() {
			showOption = Term.SHOW;
			showScene();
		}

		public void showAndWait()  {
			showOption = Term.SHOW_WAIT;
			showScene();
		}

		public void showAndWait(double width, double height) {
			sceneSize.setWidth(width);
			sceneSize.setHeight(height);
			showAndWait();
		}

		private Term getDisplacement() {
			if (scenePosition.isSet()) return Term.EXACT;
			else return Term.CENTERED;
		}

		private boolean splitX = false;
		private boolean splitY = false;

		public void splitXAtY(double mouseX, double posY) {
			scenePosition.setMouseXAtY(mouseX,posY);
			this.splitX = true;
		}

		public void splitYAtX(double mouseY, double posX) {
			scenePosition.setMouseYAtX(mouseY, posX);
			this.splitY = true;
		}

		private void splitPosition() {
			if (splitX && splitY) scenePosition.showSplitXY(this.window);
			else if (splitX) scenePosition.showSplitXAtY(this.window);
			else if (splitY) scenePosition.showSplitYAtX(this.window);
		}

		public void setParent(Parent root) {
			this.root = root;
			setRoot();
		}

		public void setSize(double width, double height) {
			sceneSize.set(width,height);
			sceneSize.resize(this.stage);
		}

		public void setPosition(double posX, double posY) {
			scenePosition.set(posX,posY);
		}

		public void setFullScreen(boolean value) {
			if (value) {
				snapWidth = stage.getWidth();
				plot = MAXIMIZED;
			}
			else {
				plot = CENTERED;
			}
			stage.setMaximized(value);
			centerScene();
		}

		private void centerScene() {
			new Thread(() -> {
				Action.sleep(500);
				double sceneWidth = scene.getWidth();
				double sceneHeight = scene.getHeight();
				double screenWidth = dimension.getWidth();
				double screenHeight = dimension.getHeight();
				double posX = (screenWidth - sceneWidth) - (.5 * (screenWidth - sceneWidth));
				double posY = (screenHeight - sceneHeight) - (.5 * (screenHeight - sceneHeight));
				Platform.runLater(() -> {
					scene.getWindow().setX(posX);
					scene.getWindow().setY(posY);
				});
			}).start();
		}

		public void setMaximized(boolean maximized) {
			stage.setMaximized(maximized);
			if (maximized) {
				plot = MAXIMIZED;
			}
			else {
				plot = CENTERED;
			}
		}

		public void setMinimized(boolean minimized) {
			stage.setIconified(minimized);
		}

		public void setTitle(String title) {
			this.title = title;
			setTitle();
		}

		public void runOnHidden(EventHandler<Event> handler) {
			onHideEventHandler = handler;
		}

		public void runOnShown(EventHandler<Event> handler) {
			onShowEventHandler = handler;
		}

		public Scene getScene() {
			return scene;
		}

		public Stage getStage() {
			return stage;
		}

		public boolean isShowing() {
			return stage.isShowing();
		}

		public void setHideOnLostFocus() {
			if (hideOnLostFocus) {
				stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
					if (!newValue) {
						Platform.runLater(() -> window.hide());
						if (onHideEventHandler != null) {
							Platform.runLater(() -> onHideEventHandler.handle(null));
						}
					}
				});
			}
		}

		public void setOnKeyPressed(EventHandler<? super KeyEvent> keyPressedEventHandler) {
			this.keyPressedEventHandler = keyPressedEventHandler;
		}

		public void setOnKeyReleased(EventHandler<? super KeyEvent> keyReleasedEventHandler) {
			this.keyReleasedEventHandler = keyReleasedEventHandler;
		}

		public void setStageCloseEvent(EventHandler<WindowEvent> stageCloseEventHandler) {
			this.stageCloseEventHandler = stageCloseEventHandler;
		}

		public Window getOwner() {
			return stage.getOwner();
		}

		public Window getWindow() {
			return window;
		}

		public void close() {
			if(stageCloseEventHandler != null) {
				Platform.runLater(() -> stageCloseEventHandler.handle(null));
			}
			Platform.runLater(stage::close);
		}

		public void unHide() {
			Platform.runLater(stage::show);
		}

		public void hide() {
			Platform.runLater(scene.getWindow()::hide);
			if (onHideEventHandler != null) {
				Platform.runLater(() -> onHideEventHandler.handle(null));
			}
		}

		private double snapWidth = 0;

		public void setWideMode(boolean wideMode) {
			if (stage != null) {
				if(getStage().isMaximized()) return;
				if (wideMode) {
					if (snapWidth == 0) snapWidth = stage.getWidth();
					Platform.runLater(() -> {
						scene.getWindow().setWidth(dimension.getWidth());
						centerScene();
					});
				}
				else {
					if (snapWidth > (dimension.getWidth() * .98)) {
						if (snapWidth == dimension.getWidth()) snapWidth = dimension.getWidth() * .75;
						Platform.runLater(() -> {
							scene.getWindow().setWidth(snapWidth);
							centerScene();
						});
					}
					else {
						Platform.runLater(() -> {
							scene.getWindow().setWidth(snapWidth);
							centerScene();
						});
					}
				}
			}
		}

		public void autoSize() {
			scene.getWindow().sizeToScene();
		}

		public void sizeToWidth(double width) {
			stage.setWidth(width);
		}

		public boolean isFullscreen() {
			return stage.isMaximized();
		}

		public void resizeWidth(Double width) {
			sceneSize.setWidth(width);
			resize();
		}

		public void resizeHeight(Double height) {
			sceneSize.setHeight(height);
			resize();
		}

		public void resize() {
			sceneSize.resize(stage);
		}
	}
}
