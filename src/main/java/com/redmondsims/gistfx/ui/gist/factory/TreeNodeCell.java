package com.redmondsims.gistfx.ui.gist.factory;

import com.redmondsims.gistfx.gist.WindowManager;
import javafx.scene.control.*;

public class TreeNodeCell extends TreeCell<TreeNode> {

	private final ContextMenu gistMenu;
	private final ContextMenu fileMenu;
	private final ContextMenu categoryMenu;

	private MenuItem newGist() {
		MenuItem newGist = new MenuItem("New Gist");
		newGist.setOnAction(e -> WindowManager.newGist());
		return newGist;
	}

	private MenuItem newFile() {
		MenuItem newFile = new MenuItem("New File");
		newFile.setOnAction(e -> WindowManager.newFile());
		return newFile;
	}

	private MenuItem newShare() {
		MenuItem emailItem = new MenuItem("Transport");
		emailItem.setOnAction(e -> WindowManager.shareObject());
		return emailItem;
	}

	private SeparatorMenuItem newSep() {
		return new SeparatorMenuItem();
	}

	public TreeNodeCell() {
		MenuItem editCategories = new MenuItem("Edit Categories");
		MenuItem renameCategory = new MenuItem("Rename");
		MenuItem renameGist     = new MenuItem("Rename");
		MenuItem renameFile     = new MenuItem("Rename");
		MenuItem deleteCategory = new MenuItem("Delete");
		MenuItem deleteGist     = new MenuItem("Delete");
		MenuItem deleteFile     = new MenuItem("Delete");
		editCategories.setOnAction(e -> WindowManager.editCategories());
		renameCategory.setOnAction(e -> WindowManager.renameCategory());
		renameGist.setOnAction(e -> WindowManager.renameGist());
		renameFile.setOnAction(e -> WindowManager.renameFile());
		deleteCategory.setOnAction(e -> WindowManager.deleteCategory());
		deleteFile.setOnAction(e -> WindowManager.deleteFile());
		deleteGist.setOnAction(e -> WindowManager.deleteGist());
		gistMenu     = new ContextMenu(newFile(), newSep(), renameGist, deleteGist, newGist(), newSep(), newShare());
		fileMenu     = new ContextMenu(renameFile, deleteFile, newSep(), newFile(), newGist(),newSep(),newShare());
		categoryMenu = new ContextMenu(newGist(), newSep(), renameCategory, deleteCategory, editCategories, newSep(), newShare());
	}

	@Override
	public void updateItem(TreeNode treeNode, boolean empty) {
		super.updateItem(treeNode, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
			setContextMenu(null);
		} else {
			if (!isEditing()) {
				if (treeNode != null) {
					switch (treeNode.getType()) {
						case GIST -> setContextMenu(gistMenu);
						case FILE -> setContextMenu(fileMenu);
						case CATEGORY -> setContextMenu(categoryMenu);
						default -> setContextMenu(null);
					}
				}
			}
		}
	}

	public String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}
