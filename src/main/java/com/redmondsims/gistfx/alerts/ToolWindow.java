package com.redmondsims.gistfx.alerts;

import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

public class ToolWindow {

	private final String     sceneId = "Tool Window";
	private       AnchorPane ap;
	private final double     width;
	private final double     height;
	private final Node       content;
	private final Button     closeButton;
	private final String     title;

	public static class Builder {

		public Builder(Node content, double width, double height) {
			this.width = width;
			this.height = height;
			this.content = content;
		}

		private AnchorPane ap;
		private final double width;
		private final double height;
		private final Node content;
		private String title = "";

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public ToolWindow build() {
			return new ToolWindow(this);
		}
	}

	private ToolWindow (Builder build) {
		this.width = build.width;
		this.height = build.height;
		this.content = build.content;
		this.title = build.title;
		this.closeButton = new Button("Close");
		this.closeButton.setPrefHeight(35);
		this.closeButton.setPrefWidth(55);
		this.closeButton.setOnAction(e -> SceneOne.close(sceneId));
	}

	public void showAndWait() {
		ap = new AnchorPane();
		ap.setPrefWidth(width);
		ap.setPrefHeight(height);
		addNode(content,0,0,0,50);
		addNode(closeButton,((width / 2) - 27.5),-1,-1,10);
		SceneOne.set(ap,sceneId)
				.size(width,height)
				.title(title)
				.styleSheets(LiveSettings.getTheme().getStyleSheet())
				.centered()
				.newStage()
				.showAndWait();
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

	public void setStyleSheet() {
		SceneOne.getScene(sceneId).getStylesheets().clear();
		SceneOne.getScene(sceneId).getStylesheets().add(LiveSettings.getTheme().getStyleSheet());
	}

}
