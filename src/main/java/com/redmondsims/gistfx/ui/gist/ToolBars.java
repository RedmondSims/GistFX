package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.ui.TreeIcons;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ToolBars {

	public ToolBars(GistWindow gistWindow) {
		this.gistWindow = gistWindow;
		createIcons();
		setIconActions();
	}

	private ImageView ivCopyToClipboard;
	private ImageView ivPasteFromClipboard;
	private ImageView ivWide;
	private ImageView ivFull;
	private ImageView ivDistractionFree;
	private ImageView ivSaveFile;
	private ImageView ivDeleteGist;
	private ImageView ivNewGist;
	private ImageView ivEditCategories;
	private ImageView ivUndo;
	private Image imgWideUp;
	private Image imgWideDown;
	private Image imgKittyUp;
	private Image imgKittyDown;
	private Image imgFullUp;
	private Image imgFullDown;
	private Image imgCloudUp;
	private Image imgCloudDown;
	private Image imgUndoUp;
	private Image imgUndoDown;
	private Image imgCategoriesUp;
	private Image imgCategoriesDown;
	private Image imgSaveUp;
	private Image imgSaveDown;
	private Image imgDeleteUp;
	private Image imgDeleteDown;
	private final GistWindow gistWindow;

	private void createIcons() {
		imgWideUp         = new Image(TreeIcons.getToolBarIcon("Wide/WideUp.png"));
		imgWideDown       = new Image(TreeIcons.getToolBarIcon("Wide/WideDown.png"));
		imgKittyUp        = new Image(TreeIcons.getToolBarIcon("Kitty/KittyUp.png"));
		imgKittyDown      = new Image(TreeIcons.getToolBarIcon("Kitty/KittyDown.png"));
		imgFullUp         = new Image(TreeIcons.getToolBarIcon("Full/FullUp.png"));
		imgFullDown       = new Image(TreeIcons.getToolBarIcon("Full/FullDown.png"));
		imgCloudUp        = new Image(TreeIcons.getToolBarIcon("Cloud/CloudUp.png"));
		imgCloudDown      = new Image(TreeIcons.getToolBarIcon("Cloud/CloudDown.png"));
		imgUndoUp         = new Image(TreeIcons.getToolBarIcon("UndoArrow/UndoArrowUp.png"));
		imgUndoDown       = new Image(TreeIcons.getToolBarIcon("UndoArrow/UndoArrowDown.png"));
		imgCategoriesUp   = new Image(TreeIcons.getToolBarIcon("Category/CategoryUp.png"));
		imgCategoriesDown = new Image(TreeIcons.getToolBarIcon("Category/CategoryDown.png"));
		imgSaveUp         = new Image(TreeIcons.getToolBarIcon("Disk/SaveUp.png"));
		imgSaveDown       = new Image(TreeIcons.getToolBarIcon("Disk/SaveDown.png"));
		imgDeleteUp       = new Image(TreeIcons.getToolBarIcon("Kitty/KittyDeleteUp.png"));
		imgDeleteDown     = new Image(TreeIcons.getToolBarIcon("Kitty/KittyDeleteDown.png"));
		Image iCopyToClipboard    = new Image(TreeIcons.getToolBarIcon("CopyToClipboard.png"));
		Image iPasteFromClipboard = new Image(TreeIcons.getToolBarIcon("PasteFromClipboard.png"));
		Image iFull               = new Image(TreeIcons.getToolBarIcon("FullScreen.png"));
		Image iDistractionFree    = new Image(TreeIcons.getToolBarIcon("DistractionFree.png"));
		Image iSaveFile           = new Image(TreeIcons.getToolBarIcon("SaveFile.png"));
		Image iDeleteGist         = new Image(TreeIcons.getToolBarIcon("DeleteGist.png"));
		Image iEditCategories     = new Image(TreeIcons.getToolBarIcon("EditCategories.png"));
		Image iUndo			      = new Image(TreeIcons.getToolBarIcon("Undo.png"));
		ivCopyToClipboard    = new ImageView(iCopyToClipboard);
		ivPasteFromClipboard = new ImageView(iPasteFromClipboard);
		ivWide               = new ImageView(imgWideUp);
		ivFull               = new ImageView(imgFullUp);
		ivDistractionFree    = new ImageView(imgCloudUp);
		ivSaveFile           = new ImageView(imgSaveUp);
		ivDeleteGist         = new ImageView(imgDeleteUp);
		ivNewGist            = new ImageView(imgKittyUp);
		ivEditCategories     = new ImageView(imgCategoriesUp);
		ivUndo			     = new ImageView(imgUndoUp);

		ivWide.setOnMousePressed(e->{ivWide.setImage(imgWideDown);});
		ivWide.setOnMouseReleased(e->{ivWide.setImage(imgWideUp);});

		ivNewGist.setOnMousePressed(e->{ivNewGist.setImage(imgKittyDown);});
		ivNewGist.setOnMouseReleased(e->{ivNewGist.setImage(imgKittyUp);});

		ivFull.setOnMousePressed(e->{ivFull.setImage(imgFullDown);});
		ivFull.setOnMouseReleased(e->{ivFull.setImage(imgFullUp);});

		ivDistractionFree.setOnMousePressed(e->{ivDistractionFree.setImage(imgCloudDown);});
		ivDistractionFree.setOnMouseReleased(e->{ivDistractionFree.setImage(imgCloudUp);});

		ivUndo.setOnMousePressed(e->{ivUndo.setImage(imgUndoDown);});
		ivUndo.setOnMouseReleased(e->{ivUndo.setImage(imgUndoUp);});

		ivEditCategories.setOnMousePressed(e->{ivEditCategories.setImage(imgCategoriesDown);});
		ivEditCategories.setOnMouseReleased(e->{ivEditCategories.setImage(imgCategoriesUp);});

		ivSaveFile.setOnMousePressed(e->{ivSaveFile.setImage(imgSaveDown);});
		ivSaveFile.setOnMouseReleased(e->{ivSaveFile.setImage(imgSaveUp);});

		ivDeleteGist.setOnMousePressed(e->{ivDeleteGist.setImage(imgDeleteDown);});
		ivDeleteGist.setOnMouseReleased(e->{ivDeleteGist.setImage(imgDeleteUp);});

		ivCopyToClipboard.setPreserveRatio(true);
		ivPasteFromClipboard.setPreserveRatio(true);
		ivWide.setPreserveRatio(true);
		ivFull.setPreserveRatio(true);
		ivDistractionFree.setPreserveRatio(true);
		ivSaveFile.setPreserveRatio(true);
		ivDeleteGist.setPreserveRatio(true);
		ivNewGist.setPreserveRatio(true);
		ivEditCategories.setPreserveRatio(true);
		ivUndo.setPreserveRatio(true);

		double fitWidth = 35;
		double fitWidthP = 50;

		ivCopyToClipboard		.setFitWidth(fitWidth);
		ivPasteFromClipboard	.setFitWidth(fitWidth);
		ivWide					.setFitWidth(fitWidth+35);
		ivFull					.setFitWidth(fitWidthP);
		ivDistractionFree		.setFitWidth(fitWidthP+20);
		ivSaveFile				.setFitWidth(fitWidthP);
		ivDeleteGist			.setFitWidth(fitWidthP);
		ivNewGist				.setFitWidth(fitWidth+25);
		ivEditCategories		.setFitWidth(fitWidthP);
		ivUndo					.setFitWidth(fitWidthP);

		Tooltip.install(ivCopyToClipboard, Action.newTooltip("Copy selected file to clipboard"));
		Tooltip.install(ivPasteFromClipboard, Action.newTooltip("Paste clipboard to selected file and overwrite"));
		Tooltip.install(ivWide, Action.newTooltip("Toggle Wide Mode"));
		Tooltip.install(ivFull, Action.newTooltip("Toggle fullscreen"));
		Tooltip.install(ivDistractionFree, Action.newTooltip("Toggle Distraction Free"));
		Tooltip.install(ivSaveFile, Action.newTooltip("Save Selected File to GitHub"));
		Tooltip.install(ivDeleteGist, Action.newTooltip("Delete selected Gist"));
		Tooltip.install(ivNewGist, Action.newTooltip("Create New Gist"));
		Tooltip.install(ivEditCategories, Action.newTooltip("Edit Gist Categories"));
		Tooltip.install(ivUndo, Action.newTooltip("Revert File to GitHub Version"));
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
		ivSaveFile.setOnMouseClicked(e -> {
			gistWindow.saveFile();
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
		ivUndo.setOnMouseClicked(e -> {
			gistWindow.getActions().undoFile();
		});
	}


	private HBox getIconBox(ObservableList<ImageView> activeIconList) {
		HBox hbox = new HBox(30);
		hbox.getChildren().setAll(activeIconList);
		hbox.setPadding(new Insets(5,5,5,5));
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	public HBox gistSelected() {
		ObservableList<ImageView> iconList = FXCollections.observableArrayList();
		iconList.setAll(ivWide, ivFull, ivDeleteGist, ivNewGist);
		return getIconBox(iconList);
	}

	public HBox categorySelected() {
		ObservableList<ImageView> iconList = FXCollections.observableArrayList();
		iconList.setAll(ivWide, ivFull, ivEditCategories);
		return getIconBox(iconList);
	}

	public HBox fileSelected() {
		ObservableList<ImageView> iconList = FXCollections.observableArrayList();
		iconList.setAll(ivWide, ivFull, ivDistractionFree, ivSaveFile, ivUndo);
		return getIconBox(iconList);
	}

	public HBox nothingSelected() {
		ObservableList<ImageView> iconList = FXCollections.observableArrayList();
		iconList.setAll(ivWide, ivFull, ivNewGist, ivEditCategories);
		return getIconBox(iconList);
	}
}
