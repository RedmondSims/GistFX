package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.data.Action;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ToolBar {

	public ToolBar(GistWindow gistWindow) {
		this.gistWindow = gistWindow;
		createIcons();
		setIconActions();
	}

	private ImageView ivCopyToClipboard;
	private ImageView ivPasteFromClipboard;
	private ImageView ivWide;
	private ImageView ivFull;
	private ImageView ivDistractionFree;
	private ImageView ivDeleteGist;
	private ImageView ivNewGist;
	private ImageView ivEditCategories;

	private final GistWindow gistWindow;

	private void createIcons() {
		Image iCopyToClipboard    = new Image(Icons.getToolBarIcon("CopyToClipboard.png"));
		Image iPasteFromClipboard = new Image(Icons.getToolBarIcon("PasteFromClipboard.png"));
		Image iWide               = new Image(Icons.getToolBarIcon("Wide.png"));
		Image iFull               = new Image(Icons.getToolBarIcon("FullScreen.png"));
		Image iDistractionFree    = new Image(Icons.getToolBarIcon("DistractionFree.png"));
		Image iDeleteGist         = new Image(Icons.getToolBarIcon("DeleteGist.png"));
		Image iNewGist            = new Image(Icons.getToolBarIcon("NewGist.png"));
		Image iEditCategories     = new Image(Icons.getToolBarIcon("EditCategories.png"));
		ivCopyToClipboard    = new ImageView(iCopyToClipboard);
		ivPasteFromClipboard = new ImageView(iPasteFromClipboard);
		ivWide               = new ImageView(iWide);
		ivFull               = new ImageView(iFull);
		ivDistractionFree    = new ImageView(iDistractionFree);
		ivDeleteGist         = new ImageView(iDeleteGist);
		ivNewGist            = new ImageView(iNewGist);
		ivEditCategories     = new ImageView(iEditCategories);
		ivCopyToClipboard.setPreserveRatio(true);
		ivPasteFromClipboard.setPreserveRatio(true);
		ivWide.setPreserveRatio(true);
		ivFull.setPreserveRatio(true);
		ivDistractionFree.setPreserveRatio(true);
		ivDeleteGist.setPreserveRatio(true);
		ivNewGist.setPreserveRatio(true);
		ivEditCategories.setPreserveRatio(true);
		double fitWidth = 35;
		ivCopyToClipboard.setFitWidth(fitWidth);
		ivPasteFromClipboard.setFitWidth(fitWidth);
		ivWide.setFitWidth(fitWidth+15);
		ivFull.setFitWidth(fitWidth);
		ivDistractionFree.setFitWidth(fitWidth);
		ivDeleteGist.setFitWidth(fitWidth);
		ivNewGist.setFitWidth(fitWidth);
		ivEditCategories.setFitWidth(fitWidth);
		Tooltip.install(ivCopyToClipboard, Action.newTooltip("Copy selected file to clipboard"));
		Tooltip.install(ivPasteFromClipboard, Action.newTooltip("Paste clipboard to selected file and overwrite"));
		Tooltip.install(ivWide, Action.newTooltip("Toggle Wide Mode"));
		Tooltip.install(ivFull, Action.newTooltip("Toggle fullscreen"));
		Tooltip.install(ivDistractionFree, Action.newTooltip("Toggle Distraction Free"));
		Tooltip.install(ivDeleteGist, Action.newTooltip("Delete selected Gist"));
		Tooltip.install(ivNewGist, Action.newTooltip("Create New Gist"));
		Tooltip.install(ivEditCategories, Action.newTooltip("Edit Gist Categories"));

	}

	private void setIconActions() {
		ivCopyToClipboard.setOnMouseClicked(e -> {
			gistWindow.copyToClipboard();
		});
		ivPasteFromClipboard.setOnMouseClicked(e -> {
			gistWindow.pasteFromClipboard();
		});
		ivWide.setOnMouseClicked(e -> {
			gistWindow.inWideMode.toggle();
		});
		ivFull.setOnMouseClicked(e -> {
			gistWindow.inFullScreen.toggle();
		});
		ivDistractionFree.setOnMouseClicked(e -> {
			gistWindow.distractionFree();
		});
		ivDeleteGist.setOnMouseClicked(e -> {
			gistWindow.deleteGist();
		});
		ivNewGist.setOnMouseClicked(e -> {
			gistWindow.newGist();
		});
		ivEditCategories.setOnMouseClicked(e -> {
			gistWindow.getActions().editCategories();
		});
	}

	public HBox getIconBox() {
		HBox hbox = new HBox(ivCopyToClipboard, ivPasteFromClipboard, ivWide, ivFull, ivDistractionFree, ivDeleteGist, ivNewGist, ivEditCategories);
		hbox.setPadding(new Insets(5,5,5,5));
		hbox.setSpacing(30);
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

}
