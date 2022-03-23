package com.redmondsims.gistfx.alerts;

import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.layout.AnchorPane.*;

public class ToolWindow {

	private final String                    sceneId;
	private       AnchorPane                ap;
	private final double                    width;
	private final double                    height;
	private final Node                      content;
	private       Button                    closeButton;
	private final String                    title;
	private final List<Button>              buttonList;
	private final Stage                     callingStage;
	private final EventHandler<WindowEvent> onCloseHandler;

	public static class Builder {

		public Builder(Node content) {
			this.content = content;
		}

		private       String       sceneId    = "Tool Window";
		private       AnchorPane   ap;
		private       double       width;
		private       double       height;
		private final Node         content;
		private       String       title      = "";
		private       String       closeName  = "Close";
		private final List<Button> buttonList = new ArrayList<>();
		private       Stage        stage;
		private EventHandler<WindowEvent> onCloseHandler;

        public Builder attachToStage(Stage stage){
			this.stage = stage;
			return this;
		}

		public Builder size(double width, double height) {
			this.height = height;
			this.width = width;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder setSceneId (String sceneId) {
			this.sceneId = sceneId;
			return this;
		}

		public Builder addButton(String name, double width, EventHandler<ActionEvent> action) {
			Button button = new Button(name);
			button.setOnAction(action);
			button.setMinWidth(width);
			button.setMinHeight(30);
			buttonList.add(button);
			return this;
		}

		public Builder addButton(Button button) {
			buttonList.add(button);
			return this;
		}

		public Builder onCloseEvent (EventHandler<WindowEvent> onCloseHandler) {
			this.onCloseHandler = onCloseHandler;
			return this;
		}

		public Builder nameCloseButton(String name) {
			this.closeName = name;
			return this;
		}

		public ToolWindow build() {
			return new ToolWindow(this);
		}
	}

	private ToolWindow (Builder build) {
		this.sceneId        = build.sceneId;
		this.width          = build.width;
		this.height         = build.height;
		this.content        = build.content;
		this.title          = build.title;
		this.buttonList     = build.buttonList;
		this.callingStage   = build.stage;
		this.onCloseHandler = build.onCloseHandler;
		if(buttonList.size() == 0) {
			this.closeButton = new Button(build.closeName);
			this.closeButton.setMinHeight(35);
			this.closeButton.setMinWidth(55);
			this.closeButton.setOnAction(e -> SceneOne.close(sceneId));
			buttonList.add(closeButton);
		}
	}

	public void resizeHeight(Double height) {
		SceneOne.resizeHeight(sceneId,height);
	}

	public void showAndWait() {
		HBox bbox = new HBox();
		bbox.getChildren().setAll(FXCollections.observableArrayList(buttonList));
		bbox.setPrefHeight(35);
		bbox.setPrefWidth(width);
		bbox.setSpacing(10);
		bbox.setAlignment(Pos.CENTER);
		ap = new AnchorPane(content,bbox);
		ap.setPrefSize(width,height);
		setNodePosition(content,0,0,0,50);
		setNodePosition(bbox,0,0,-1,10);
		if (callingStage != null) {
			SceneOne.set(ap,sceneId,callingStage)
					.size(width,height)
					.title(title)
					.styleSheets(LiveSettings.getTheme().getStyleSheet())
					.size(width,height)
					.centered()
					.onCloseEvent(onCloseHandler)
 					.showAndWait();
		}
		else {
			SceneOne.set(ap,sceneId)
					.size(width,height)
					.title(title)
					.styleSheets(LiveSettings.getTheme().getStyleSheet())
					.centered()
					.size(width,height)
					.onCloseEvent(onCloseHandler)
					.showAndWait();
		}
	}

	public void close(){
		if(SceneOne.sceneExists(sceneId))
			SceneOne.close(sceneId);
	}

	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}

	public void setStyleSheet (String styleSheet) {
		SceneOne.getScene(sceneId).getStylesheets().clear();
		SceneOne.getScene(sceneId).getStylesheets().add(styleSheet);
	}

}
