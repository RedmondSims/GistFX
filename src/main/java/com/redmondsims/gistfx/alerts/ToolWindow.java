package com.redmondsims.gistfx.alerts;

import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.layout.AnchorPane.*;

public class ToolWindow {

	private final String       sceneId;
	private       AnchorPane   ap;
	private final double       width;
	private final double       height;
	private final Node         content;
	private final Button       closeButton;
	private final String       title;
	private final List<Button> buttonList;
	private final Stage        callingStage;

	public static class Builder {

		public Builder(Node content, double width, double height) {
			this.width   = width;
			this.height  = height;
			this.content = content;
		}

		public Builder(Node content, double width, double height, Stage callingStage) {
			this.width   = width;
			this.height  = height;
			this.content = content;
			this.callingStage = callingStage;
		}

		private       String       sceneId = "Tool Window";
		private       AnchorPane   ap;
		private final double       width;
		private final double       height;
		private final Node         content;
		private       String       title      = "";
		private       String       closeName  = "Close";
		private final List<Button> buttonList = new ArrayList<>();
		private       Stage        callingStage;

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
			button.setPrefWidth(width);
			button.setPrefHeight(35);
			buttonList.add(button);
			return this;
		}

		public Builder addButton(Button button) {
			buttonList.add(button);
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
		this.sceneId      = build.sceneId;
		this.width        = build.width;
		this.height       = build.height;
		this.content      = build.content;
		this.title        = build.title;
		this.buttonList   = build.buttonList;
		this.callingStage = build.callingStage;
		this.closeButton  = new Button(build.closeName);
		this.closeButton.setPrefHeight(35);
		this.closeButton.setPrefWidth(55);
		this.closeButton.setOnAction(e -> SceneOne.close(sceneId));
		if (buttonList.size() == 0) buttonList.add(closeButton);
	}

	public void resizeHeight(Double height) {
		SceneOne.resizeHeight(sceneId,height);
	}

	public void showAndWait() {
		ap = new AnchorPane();
		ap.setPrefWidth(width);
		ap.setPrefHeight(height);
		HBox bbox = new HBox();
		for(Button button : buttonList) {
			bbox.getChildren().add(button);
		}
		bbox.setPrefHeight(35);
		bbox.setPrefWidth(width);
		bbox.setSpacing(10);
		bbox.setAlignment(Pos.CENTER);
		bbox.setPadding(new Insets(0,0,0,0));
		bbox.setCenterShape(true);
		addNode(content,0,0,0,50);
		addNode(bbox,0,0,-1,10);
		if (callingStage != null) {
			SceneOne.set(ap,sceneId,callingStage)
					.size(width,height)
					.title(title)
					.styleSheets(LiveSettings.getTheme().getStyleSheet())
					.centered()
					.newStage()
					.showAndWait();
		}
		else {
			SceneOne.set(ap,sceneId)
					.size(width,height)
					.title(title)
					.styleSheets(LiveSettings.getTheme().getStyleSheet())
					.centered()
					.newStage()
					.showAndWait();
		}
	}

	private void addNode(Node node, double left, double right, double top, double bottom) {
		ap.getChildren().add(node);
		setNodePosition(node, left, right, top, bottom);
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
