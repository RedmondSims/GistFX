package com.redmondsims.gistfx.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.alerts.ToolWindow;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Hosts {

	private enum BOX {
		CATEGORY,
		GIST,
		FILE;

		private static final Map<BOX,Integer> boxItems = new HashMap<>();

		public static void set(BOX box, Integer items) {
			boxItems.remove(box);
			boxItems.put(box,items);
		}

		public static Integer getTotalItems() {
			Integer total = 0;
			for (Integer count : boxItems.values()) {
				total += count;
			}
			return total;
		}
	}

	private static final String     name        = "MailServerSettings";
	private static final Gson       gson        = new GsonBuilder().setPrettyPrinting().create();
	private static final double     nodeHeight  = 30;
	private static       double     sceneHeight;
	private static final double     choseButtonSpacing = 190;
	private static final double     sceneWidth  = 400;
	private static final VBox       formContent = newVBox(20);
	private static       ToolWindow toolWindow;


	private static Label newLabelTypeOne(String text) {
		return newLabel(text, "SettingsOne");
	}

	private static Label newLabelTypeTwo(String text) {
		return newLabel(text, "SettingsTwo");
	}

	private static Label newLabel(String text, String labelId) {
		Label label = new Label(text);
		label.setMinWidth(155);
		label.setAlignment(Pos.CENTER_LEFT);
		label.setId(labelId);
		label.setPrefHeight(nodeHeight);
		return label;
	}

	private static VBox newVBox(double spacing, Node... nodes) {
		VBox vbox = new VBox(nodes);
		vbox.setSpacing(spacing);
		vbox.setPrefHeight(nodeHeight * nodes.length);
		return vbox;
	}

	private static HBox newHBox(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(20);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static HBox newHBox(double spacing, Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(spacing);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static Label newBlank() {
		return new Label("       ");
	}

	private static HBox newHBox(Pos position, double spacing, Node... nodes) {
		HBox hbox = newHBox(spacing, nodes);
		hbox.setAlignment(position);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static HBox getSpacedHBoxRight(Node node, double space) {
		Label dummy = new Label(" ");
		dummy.setMinWidth(space);
		HBox hbox = new HBox(dummy,node);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPrefHeight(nodeHeight);
		return hbox;
	}

	private static Node theme(Scene callingScene) {
		return UISettings.Theme.getNode(callingScene);
	}

	private static void setAnchors(Node node, double left, double right, double top, double bottom) {
		if (top != -1) AnchorPane.setTopAnchor(node, top);
		if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
		if (left != -1) AnchorPane.setLeftAnchor(node, left);
		if (right != -1) AnchorPane.setRightAnchor(node, right);
	}

	public static Collection<String> showWindow(Stage callingStage) {
		String    sceneId         = "EditHosts";
		double    width           = 400;
		double    height          = 500;
		Label     lblNewHost      = new Label("New Host");
		Label     lblSelectedHost = new Label("Selected Host");
		Label     lblRenameHost   = new Label("New Name");
		TextField tfNewHost       = new TextField();
		TextField tfSelectedHost  = new TextField();
		TextField tfRenameHost    = new TextField();
		Tooltip.install(tfNewHost, Action.newTooltip("Type in the recipient host name or ip address that you will send data to,then hit ENTER to save it\n\nHostname should be resolvable by DNS. to category, use command prompt and type:\n\nnslookup hostname"));
		Tooltip.install(tfSelectedHost, Action.newTooltip("Click on a host name from the list, then rename or delete it.\n\nTo rename, just type in the new name and press enter."));
		tfRenameHost.setEditable(false);
		tfSelectedHost.setEditable(false);
		ListView<String> lvHosts = new ListView<>();
		Collection<String> hostCollection = Action.getHostCollection();
		if (hostCollection != null) lvHosts.getItems().setAll();
		Button btnClose = new Button("Close");
		Button btnDelete = new Button("Delete Host");
		AnchorPane apCategories = new AnchorPane(lvHosts,
												 lblNewHost,
												 lblSelectedHost,
												 lblRenameHost,
												 tfNewHost,
												 tfSelectedHost,
												 tfRenameHost,
												 btnClose,
												 btnDelete);
		apCategories.setPrefSize(width, height);
		lblNewHost.setMinWidth(85);
		lblSelectedHost.setMinWidth(85);
		lblRenameHost.setMinWidth(55);
		btnClose.setMinWidth(55);
		btnClose.setMinHeight(35);
		btnDelete.setMinWidth(75);
		btnDelete.setMinHeight(35);
		setAnchors(lblNewHost, 20, -1, 20, -1);
		setAnchors(tfNewHost, 135, 20, 17.5, -1);
		setAnchors(lblSelectedHost, 20, -1, 50, -1);
		setAnchors(tfSelectedHost, 135, 20, 47.5, -1);
		setAnchors(lblRenameHost, 20, -1, 80, -1);
		setAnchors(tfRenameHost, 135, 20, 77.5, -1);
		setAnchors(lvHosts, 20, 20, 125, 65);
		setAnchors(btnClose, 80, -1, -1, 20);
		setAnchors(btnDelete,-1, 80, -1, 20);
		btnDelete.setDisable(true);
		tfSelectedHost.textProperty().addListener((observable, oldValue, newValue) -> btnDelete.setDisable(newValue.length() == 0));
		tfNewHost.setOnMouseClicked(e -> {
			tfSelectedHost.clear();
			tfRenameHost.clear();
		});
		tfNewHost.setOnAction(e -> {
			String hostName = tfNewHost.getText();
			Action.addHost(hostName);
			lvHosts.getItems().setAll(Action.getHostCollection());
			tfNewHost.clear();
			tfNewHost.requestFocus();
		});
		tfRenameHost.setOnAction(e -> {
			Action.renameHost(tfSelectedHost.getText(), tfRenameHost.getText());
			tfSelectedHost.clear();
			tfRenameHost.clear();
			lvHosts.getItems().setAll(Action.getHostCollection());
			tfRenameHost.clear();
			tfNewHost.requestFocus();
			tfRenameHost.setEditable(false);
		});
		lvHosts.setOnMouseClicked(e -> {
			tfRenameHost.setEditable(true);
			tfSelectedHost.setText(lvHosts.getSelectionModel().getSelectedItem());
			tfRenameHost.requestFocus();
		});
		btnClose.setOnAction(e -> {
			if (!tfNewHost.getText().isEmpty()) {
				String hostName = tfNewHost.getText();
				Action.addHost(hostName);
				lvHosts.getItems().setAll(Action.getHostCollection());
				tfNewHost.clear();
				tfNewHost.requestFocus();
			}
			SceneOne.close(sceneId);
		});
		btnDelete.setOnAction(e -> {
			Action.removeHost(tfSelectedHost.getText());
			Platform.runLater(() -> tfSelectedHost.setText(""));
			lvHosts.getItems().setAll(Action.getHostCollection());
		});
		lvHosts.getItems().setAll(Action.getHostCollection());
		SceneOne.set(apCategories, sceneId, callingStage)
				.centered()
				.size(width,height)
				.title("Edit Hosts")
				.showAndWait();
		return Action.getHostCollection();
	}

}
