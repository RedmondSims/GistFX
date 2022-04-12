package com.redmondsims.gistfx.alerts;

import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.layout.AnchorPane.*;

public class ToolWindow {

	private final String                    sceneId;
	private       AnchorPane                ap;
	private final double                    width;
	private final double                    height;
	private final boolean                   noButtons;
	private final boolean                   transparentStyle;
	private       Node                      content;
	private final String                    title;
	private final List<Button>              buttonList;
	private final Stage                     callingStage;
	private final EventHandler<WindowEvent> onCloseHandler;
	private final EventHandler<Event>       afterCloseEvent;
	private       String                    response = "";
	private final Modality initModality;
	private final boolean  alwaysOnTop;

	public static class Builder {

		public Builder(Node content) {
			this.content = content;
		}

		public Builder (AnchorPane ap) {
			this.ap = ap;
		}

		private       Parent parent;
		private       String                    sceneId          = "Tool Window";
		private       AnchorPane                ap;
		private       double                    width;
		private       double                    height;
		private       boolean                   noButtons        = false;
		private       boolean                   transparentStyle = false;
		private       Node                      content;
		private       String                    title            = "";
		private       String                    closeName        = "Close";
		private final List<Button>              buttonList       = new ArrayList<>();
		private       Stage                     stage;
		private       EventHandler<WindowEvent> onCloseHandler;
		private       EventHandler<Event>       afterCloseEvent;
		private Modality initModality;
		private boolean alwaysOnTop;


        public Builder attachToStage(Stage stage){
			this.stage = stage;
			return this;
		}

		public Builder alwaysOnTop() {
			this.alwaysOnTop = true;
			return this;
		}

		public Builder initModality(Modality initModality) {
			this.initModality = initModality;
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

		public Builder noButtons() {
			this.noButtons = true;
			return this;
		}

		public Builder transparentStyle() {
			this.transparentStyle = true;
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

		public Builder afterCloseEvent (EventHandler<Event> event) {
			this.afterCloseEvent = event;
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
		this.ap               = build.ap;
		this.sceneId          = build.sceneId;
		this.width            = build.width;
		this.height           = build.height;
		this.noButtons        = build.noButtons;
		this.transparentStyle = build.transparentStyle;
		this.content          = build.content;
		this.title            = build.title;
		this.buttonList       = build.buttonList;
		this.callingStage     = build.stage;
		this.onCloseHandler   = build.onCloseHandler;
		this.afterCloseEvent  = build.afterCloseEvent;
		this.initModality = build.initModality;
		this.alwaysOnTop = build.alwaysOnTop;
		if (!this.noButtons) {
			if(buttonList.size() == 0) {
				Button closeButton = new Button(build.closeName);
				closeButton.setMinHeight(35);
				closeButton.setMinWidth(55);
				closeButton.setOnAction(e -> SceneOne.close(sceneId));
				buttonList.add(closeButton);
			}
		}
	}

	public void resizeHeight(Double height) {
		SceneOne.resizeHeight(sceneId,height);
	}

	public void showAndWait() {
		if(!SceneOne.sceneExists(sceneId)) {
			defineParent();
			if (callingStage != null) {
				SceneOne.set(ap,sceneId,callingStage)
						.size(width,height)
						.title(title)
						.styleSheets(AppSettings.get().theme().getStyleSheet())
						.size(width,height)
						.onCloseEvent(onCloseHandler)
						.modality(initModality)
						.initStyle(transparentStyle ? StageStyle.TRANSPARENT : StageStyle.DECORATED)
						.showAndWait();
			}
			else {
				SceneOne.set(ap,sceneId)
						.size(width,height)
						.title(title)
						.styleSheets(AppSettings.get().theme().getStyleSheet())
						.initStyle(transparentStyle ? StageStyle.TRANSPARENT : StageStyle.DECORATED)
						.modality(initModality)
						.size(width,height)
						.onCloseEvent(onCloseHandler)
						.alwaysOnTop()
						.showAndWait();
			}
			if (afterCloseEvent != null) afterCloseEvent.handle(new ActionEvent());
		}
		else {
			SceneOne.showScene(sceneId);
		}
	}

	private void defineParent() {
		HBox bbox = new HBox();
		if (!noButtons) {
			bbox.getChildren().setAll(FXCollections.observableArrayList(buttonList));
			bbox.setPrefHeight(35);
			bbox.setPrefWidth(width);
			bbox.setSpacing(10);
			bbox.setAlignment(Pos.CENTER);
			if (ap == null){
				ap = new AnchorPane(content, bbox);
				setNodePosition(content,0,0,0,50);
				setNodePosition(bbox, 0, 0, -1, 10);
			}
		}
		else {
			if(ap == null){
				ap = new AnchorPane(content);
				setNodePosition(content,0,0,0,0);
			}
		}
		ap.setPrefSize(width,height);
	}

	public String showAndWaitResponse() {
		showAndWait();
		return response;
	}

	public void close(){
		if(SceneOne.sceneExists(sceneId))
			SceneOne.close(sceneId);
	}

	public void setResponse(String response) {
		this.response = response;
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

	public String getSceneId() {
		return sceneId;
	}

	public void hide() {
		SceneOne.hide(sceneId);
	}

	public void show() {
		SceneOne.showScene(sceneId);
		setContent(this.content);
	}

	public void setContent(Node content) {
		if (this.content != null) {
			ap.getChildren().remove(this.content);
			this.content = content;
			ap.getChildren().add(this.content);
			setNodePosition(this.content,0,0,0,noButtons ? 0 : 50);
		}
	}
}
