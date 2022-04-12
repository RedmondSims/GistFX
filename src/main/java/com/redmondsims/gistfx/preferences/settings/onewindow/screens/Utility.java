package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.WindowManager;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;

public class Utility {


	public static HBox newHBox(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(20);
		return hbox;
	}

	public static HBox newHBox(double height, double spacing, Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(spacing);
		hbox.setPrefHeight(height);
		return hbox;
	}

	public static HBox newHBox(Pos alignment, double height, double spacing, Node... nodes) {
		HBox hbox = newHBox(spacing, nodes);
		hbox.setAlignment(alignment);
		hbox.setPrefHeight(height);
		return hbox;
	}

	public static HBox newHBox(double spacing, Node... nodes) {
		return new HBox(spacing, nodes);
	}

	public static HBox newHBox(Pos alignment, double height, double spacing, double padding, Node... nodes) {
		HBox hbox = newHBox(spacing, nodes);
		hbox.setAlignment(alignment);
		hbox.setPrefHeight(height);
		hbox.setPadding(new Insets(padding, padding, padding, padding));
		return hbox;
	}

	public static HBox newHBox(double spacing, double padding, Pos alignment, Node... nodes) {
		HBox hbox = newHBox(spacing, nodes);
		hbox.setAlignment(alignment);
		hbox.setPadding(new Insets(padding, padding, padding, padding));
		return hbox;
	}

	public static HBox hBoxLeft(Node... nodes) {
		HBox hbox = new HBox(nodes);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_LEFT);
		return hbox;
	}

	public static HBox getSpacedHBoxRight(Node node, double space) {
		Label dummy = new Label(" ");
		dummy.setMinWidth(space);
		HBox hbox = new HBox(dummy,node);
		hbox.setSpacing(0);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		return hbox;
	}

	public static HBox getSpacedHBoxRight(double spacing, double padding, Node node, double space) {
		Label dummy = new Label(" ");
		dummy.setMinWidth(space);
		HBox hbox = new HBox(spacing, dummy, node);
		hbox.setPadding(new Insets(padding, padding, padding, padding));
		hbox.setAlignment(Pos.CENTER_LEFT);
		return hbox;
	}

	public static HBox getCenteredHBox(double spacing, double padding, Node... nodes) {
		HBox hbox = new HBox(spacing,nodes);
		hbox.setPadding(new Insets(padding,padding,padding,padding));
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	public static CheckBox checkBoxLabelLeft(String text) {
		CheckBox checkBox = new CheckBox(text);
		checkBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		return checkBox;
	}

	public static VBox newVBox(double height, double spacing, double padding, double width, Node... nodes) {
		VBox vbox = new VBox(nodes);
		vbox.setSpacing(spacing);
		vbox.setPrefHeight(height * nodes.length);
		vbox.setPadding(new Insets(padding,padding,padding,padding));
		vbox.setPrefWidth(width);
		vbox.setMinWidth(width);
		vbox.setMaxWidth(width);
		return vbox;
	}

	public static VBox newVBox(double spacing, double padding, double width, Node... nodes) {
		VBox vbox = new VBox(nodes);
		vbox.setSpacing(spacing);
		vbox.setPadding(new Insets(padding,padding,padding,padding));
		vbox.setPrefWidth(width);
		vbox.setMinWidth(width);
		vbox.setMaxWidth(width);
		vbox.getStylesheets().add(AppSettings.get().theme().getStyleSheet());
		return vbox;
	}

	public static Label newLabel(double height, String text, String labelId) {
		Label label = new Label(text);
		label.setMinWidth(155);
		label.setAlignment(Pos.CENTER_LEFT);
		label.setId(labelId);
		label.setPrefHeight(height);
		return label;
	}

	public static void setAnchors(Node node, double left, double right, double top, double bottom) {
		if (top != -1) AnchorPane.setTopAnchor(node, top);
		if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
		if (left != -1) AnchorPane.setLeftAnchor(node, left);
		if (right != -1) AnchorPane.setRightAnchor(node, right);
	}

	public static Label newLabelTypeOne(String text) {
		return newLabel(text, "SettingsOne");
	}

	public static Label newLabelTypeTwo(String text) {
		return newLabel(text, "SettingsTwo");
	}

	public static Label newLabel(String text, String labelId) {
		Label label = new Label(text);
		label.setMinWidth(155);
		label.setAlignment(Pos.CENTER_LEFT);
		label.setId(labelId);
		return label;
	}

	public static String toTitleCase(String str) {

		if(str == null || str.isEmpty())
			return "";

		if(str.length() == 1)
			return str.toUpperCase();

		//split the string by space
		String[] parts = str.split(" ");

		StringBuilder sb = new StringBuilder( str.length() );

		for(String part : parts){

			if(part.length() > 1 )
				sb.append( part.substring(0, 1).toUpperCase() )
				  .append( part.substring(1).toLowerCase() );
			else
				sb.append(part.toUpperCase());

			sb.append(" ");
		}

		return sb.toString().trim();
	}

}
