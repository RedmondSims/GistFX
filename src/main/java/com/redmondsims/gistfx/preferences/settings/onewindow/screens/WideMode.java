package com.redmondsims.gistfx.preferences.settings.onewindow.screens;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.sceneone.SceneOne;
import com.redmondsims.gistfx.ui.gist.GistWindow;
import com.redmondsims.gistfx.utils.Resources;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.Precision;

import java.awt.*;
import java.text.DecimalFormat;

public class WideMode {


	private enum State {
		REST,
		EXPANDED,
		IDLE
	}

	GistWindow gistWindow;
	private       State            state            = State.IDLE;
	private       Label            lblAtRest;
	private       TextField        tfAtRest;
	private       Button           btnAtRest;
	private       Label            lblExpanded;
	private       TextField        tfExpanded;
	private       Button           btnExpanded;
	private final DoubleProperty   positionProperty = new SimpleDoubleProperty();
	private       Label            lblMessage;
	private       Label            lblIconSize;
	private       Spinner<Integer> spinner;
	private final Dimension        dimension        = Toolkit.getDefaultToolkit().getScreenSize();
	private       double           atRestValue      = AppSettings.get().dividerAtRest();
	private       double           expandedValue    = AppSettings.get().dividerExpanded();
	private       double           sceneWidth       = 0;
	private       double           splitPosition    = 0;
	private       double           X                = 0;
	private       double           Y                = 0;

	public VBox content(GistWindow gistWindow) {
		this.gistWindow = gistWindow;
		lblAtRest       = new Label("Position At Rest");
		tfAtRest        = new TextField();
		btnAtRest       = new Button("Enable At Rest");
		lblExpanded     = new Label("Position Expanded");
		tfExpanded      = new TextField();
		btnExpanded     = new Button("Enable Expanded");
		lblMessage      = new Label();
		lblIconSize     = new Label("Icon Size");
		spinner         = new Spinner<>(1,200,(int) AppSettings.get().iconBaseSize());
		lblMessage.setId("LargerFont");

		lblAtRest.setAlignment(Pos.CENTER_RIGHT);
		lblExpanded.setAlignment(Pos.CENTER_RIGHT);
		lblIconSize.setAlignment(Pos.CENTER_RIGHT);
		spinner.setMinWidth(75);
		spinner.setMaxWidth(75);
		spinner.setPrefWidth(75);
		lblIconSize.setMinWidth(250);
		lblIconSize.setMaxWidth(250);
		lblIconSize.setPrefWidth(250);
		lblAtRest.setMinWidth(110);
		lblAtRest.setMaxWidth(110);
		lblAtRest.setPrefWidth(110);
		lblExpanded.setMinWidth(110);
		lblExpanded.setMaxWidth(110);
		lblExpanded.setPrefWidth(110);
		tfAtRest.setMinWidth(60);
		tfAtRest.setMaxWidth(60);
		tfAtRest.setPrefWidth(60);
		tfExpanded.setMinWidth(60);
		tfExpanded.setMaxWidth(60);
		tfExpanded.setPrefWidth(60);
		btnExpanded.setMinWidth(120);
		btnExpanded.setMaxWidth(120);
		btnExpanded.setPrefWidth(120);
		btnAtRest.setMinWidth(120);
		btnAtRest.setMaxWidth(120);
		btnAtRest.setPrefWidth(120);
		tfAtRest.setAlignment(Pos.CENTER_LEFT);
		tfExpanded.setAlignment(Pos.CENTER_LEFT);

		tfAtRest.setText(round(AppSettings.get().dividerAtRest()));
		tfExpanded.setText(round(AppSettings.get().dividerExpanded()));
		HBox boxAtRest   = new HBox(5, lblAtRest, tfAtRest, btnAtRest);
		HBox boxExpanded = new HBox(5, lblExpanded, tfExpanded, btnExpanded);
		HBox boxIcon = new HBox(5, lblIconSize, spinner);
		boxAtRest.setAlignment(Pos.CENTER);
		boxExpanded.setAlignment(Pos.CENTER);

		positionProperty.addListener((observable, oldValue, newValue) -> {
			String valueString = round((double) newValue);

			if (state.equals(State.REST)) {
				atRestValue = (double) newValue;
				AppSettings.set().dividerAtRest(atRestValue);
				Platform.runLater(() -> tfAtRest.setText(valueString));
			}
			if (state.equals(State.EXPANDED)) {
				expandedValue = (double) newValue;
				AppSettings.set().dividerExpanded(expandedValue);
				Platform.runLater(() -> tfExpanded.setText(valueString));
			}
		});

		btnAtRest.setOnAction(e -> {
			state = State.IDLE;
			if (sceneWidth == 0) {
				sceneWidth    = SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().getWidth();
				X             = SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().getX();
				Y             = SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().getY();
				splitPosition = gistWindow.getSplitPane().getDividers().get(0).getPosition();
			}
			SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().setWidth(dimension.getWidth());
			SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().centerOnScreen();
			gistWindow.getSplitPane().getDividers().get(0).setPosition(AppSettings.get().dividerAtRest());
			positionProperty.bind(gistWindow.getSplitPane().getDividers().get(0).positionProperty());
			lblMessage.setText("Set divider to desired AT REST position");
			state = State.REST;
		});

		btnExpanded.setOnAction(e -> {
			state = State.IDLE;
			if (sceneWidth == 0) {
				sceneWidth    = SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().getWidth();
				X             = SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().getX();
				Y             = SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().getY();
				splitPosition = gistWindow.getSplitPane().getDividers().get(0).getPosition();
			}
			SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().setWidth(dimension.getWidth());
			SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().centerOnScreen();
			gistWindow.getSplitPane().getDividers().get(0).setPosition(AppSettings.get().dividerExpanded());
			positionProperty.bind(gistWindow.getSplitPane().getDividers().get(0).positionProperty());
			lblMessage.setText("Set divider to desired EXPANDED position");
			state = State.EXPANDED;
		});

		spinner.setOnMouseClicked(e->{
			gistWindow.setIconSize((double) spinner.getValue());
			AppSettings.set().iconBaseSize((double) spinner.getValue());
		});

		Tooltip.install(btnAtRest, Action.newTooltip("This will put the main screen into a wide mode with the\ndivider at resting position. Set the desired location of\nthe divider when in the 'at rest' state."));
		Tooltip.install(btnExpanded, Action.newTooltip("This will put the main screen into a wide mode with the\ndivider at the extended position. Set the desired location of\nthe divider when in the 'extended' state."));

		VBox content = new VBox(15, boxAtRest, boxExpanded, boxIcon, lblMessage);
		content.setPadding(new Insets(30, 0, 0, 0));
		content.setAlignment(Pos.CENTER);
		return content;
	}

	private String round(double number) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return decimalFormat.format(100 * Precision.round(number, 3));
	}

	public void close() {
		state = State.IDLE;
		positionProperty.unbind();
		if (sceneWidth > 0) {
			SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().setWidth(sceneWidth);
			SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().setX(X);
			SceneOne.getScene(Resources.getSceneIdGistWindow()).getWindow().setY(Y);
			gistWindow.getSplitPane().getDividers().get(0).setPosition(splitPosition);
		}
	}
}
