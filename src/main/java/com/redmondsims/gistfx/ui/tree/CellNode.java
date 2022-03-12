package com.redmondsims.gistfx.ui.tree;

import com.redmondsims.gistfx.gist.WindowManager;
import javafx.scene.control.*;

public class CellNode<T> extends TreeCell<DragNode> {

	private       TextField         textField;
	private final DragNode          dragNode;
	private final ContextMenu       gistMenu;
	private final ContextMenu       fileMenu;
	private final ContextMenu       categoryMenu;

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

	private SeparatorMenuItem newSep() {
		return new SeparatorMenuItem();
	}


	public CellNode() {
		this.dragNode = new DragNode();
		MenuItem editCategories = new MenuItem("Edit Categories");
		MenuItem renameCategory = new MenuItem("Change Name");
		MenuItem renameGist     = new MenuItem("Change Name");
		MenuItem renameFile     = new MenuItem("Change Name");
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
		gistMenu     = new ContextMenu(newFile(), newSep(), renameGist, deleteGist, newSep(), newGist());
		fileMenu     = new ContextMenu(renameFile, deleteFile, newSep(), newFile(), newGist());
		categoryMenu = new ContextMenu(newGist(), newSep(), renameCategory, deleteCategory, newSep(), editCategories);
	}

	@Override
	public void updateItem(DragNode item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
			setContextMenu(null);
		} else {
			if (!isEditing()) {
				if (item != null) {
					switch (item.getType()) {
						case GIST -> setContextMenu(gistMenu);
						case FILE -> setContextMenu(fileMenu);
						case CATEGORY -> setContextMenu(categoryMenu);
						default -> setContextMenu(null);
					}
				}
			}
		}
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}
