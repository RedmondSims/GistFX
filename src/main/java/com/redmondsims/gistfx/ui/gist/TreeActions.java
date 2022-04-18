package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.ui.TreeIcons;
import com.redmondsims.gistfx.ui.gist.treefactory.TreeNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import static com.redmondsims.gistfx.enums.TreeType.*;

import java.util.*;

public class TreeActions {

	public TreeActions(GistWindow gistWindow) {
		this.gistWindow = gistWindow;
	}

	private final GistWindow gistWindow;

	/**
	 * Tree Methods
	 */

	public void addGistToCategory(String gistId, String category) {
		TreeItem<TreeNode> gistBranch = new TreeItem<>(new TreeNode(GistManager.getGist(gistId)));
		addFilesToBranch(gistBranch);
		for(TreeItem<TreeNode> rootBranch : gistWindow.getTreeRoot().getChildren()) {
			if (rootBranch.getValue().getType().equals(CATEGORY) && rootBranch.getValue().getName().equals(category)) {
				rootBranch.getChildren().add(gistBranch);
				break;
			}
		}
	}

	public void refreshGistBranch(String gistId) {
		try {
			TreeItem<TreeNode> branch = getBranch(gistId);
			if (branch != null) {
				if (gistWindow.getTreeView().getSelectionModel().getSelectedItem() != branch) {
					gistWindow.getTreeView().getSelectionModel().select(branch);
				}
				gistWindow.setSelectedNode(branch.getValue());
				branch.getChildren().clear();
				addFilesToBranch(branch);
			}
		}
		catch (StackOverflowError e) {
			System.out.println(e.getCause().getLocalizedMessage());
		}
	}

	public void refreshGistLeaf(GistFile gistFile) {
		String             gistId   = gistFile.getGistId();
		String             filename = gistFile.getFilename();
		TreeItem<TreeNode> branch   = getBranch(gistId);
		gistWindow.expandBranch(branch);
		TreeItem<TreeNode> leaf = getLeaf(gistId, filename);
		if (leaf != null) {
			gistWindow.getTreeView().getSelectionModel().select(leaf);
			gistWindow.setSelectedNode(leaf.getValue());
		}
	}

	public void addFileToBranch(String gistId, String filename) {
		GistFile           file   = GistManager.getFile(gistId, filename);
		TreeItem<TreeNode> branch = getBranch(gistId);
		TreeItem<TreeNode> leaf   = new TreeItem<>(new TreeNode(file));
		leaf.graphicProperty().bind(file.getGraphicNode());
		branch.getChildren().add(leaf);
	}

	public List<String> categoryList = new ArrayList<>();

	public void removeLeaf(GistFile file) {
		Objects.requireNonNull(getBranch(file.getGistId())).getChildren().removeIf(leaf -> leaf.getValue().toString().equals(file.getFilename()));
	}

	public boolean removeBranch(String gistId) {
		TreeItem<TreeNode> branch = getBranch(gistId);
		if (branch == null) return false;
		TreeItem<TreeNode> parent = branch.getParent();
		if (parent == null) return false;
		parent.getChildren().remove(branch);
		return true;
	}

	public void renameCategory(String oldName, String newName) {
		TreeItem<TreeNode>                 categoryBranch = branchCategoryMap.get(oldName);
		ObservableList<TreeItem<TreeNode>> gistList       = categoryBranch.getChildren();
		TreeItem<TreeNode>                 newNode        = new TreeItem<>(new TreeNode(new GistCategory(newName)));
		newNode.setGraphic(TreeIcons.getGistCategoryIcon());
		newNode.getChildren().setAll(gistList);
		branchCategoryMap.get(oldName).getValue().setName(newName);
		branchCategoryMap.remove(oldName);
		branchCategoryMap.put(newName, newNode);
		LinkedList<TreeItem<TreeNode>> categoryList = new LinkedList<>(branchCategoryMap.values());
		categoryList.sort(Comparator.comparing(TreeItem<TreeNode>::toString).reversed());
		gistWindow.getTreeRoot().getChildren().removeIf(branch -> branch.getValue().getType().equals(CATEGORY));
		for(TreeItem<TreeNode> branch : categoryList) {
			gistWindow.getTreeRoot().getChildren().add(0,branch);
		}
	}

	public TreeItem<TreeNode> getLeaf(String gistId, String filename) {
		TreeItem<TreeNode> branch = getBranch(gistId);
		for (TreeItem<TreeNode> leaf : branch.getChildren()) {
			if (leaf.getValue().getFile().getFilename().equals(filename)) {return leaf;}
		}
		return null;
	}

	public TreeItem<TreeNode> getBranch(String gistId) {
		for (TreeItem<TreeNode> branch : gistWindow.getTreeRoot().getChildren()) {
			if (branch.getValue().getType().equals(CATEGORY)) {
				for (TreeItem<TreeNode> branchInCategory : branch.getChildren()) {
					if (branchInCategory.getValue().getType().equals(GIST) && branchInCategory.getValue().getGistId().equals(gistId)) {
						return branchInCategory;
					}
				}
			}
			if (branch.getValue().getType().equals(GIST) && branch.getValue().getGistId().equals(gistId)) {
				return branch;
			}
		}
		return null;
	}

	public TreeItem<TreeNode> getNewBranch(Gist gist) {
		TreeItem<TreeNode> branch;
		String             gistId = gist.getGistId();
		String             name   = Action.getGistName(gistId);
		branch = new TreeItem<>(new TreeNode(gist));
		branch.setGraphic(TreeIcons.getGistIcon());
		boolean isDirty = addFilesToBranch(branch);
		if(isDirty)
			branch.setExpanded(true);
		branch.getValue().getGist().expandedProperty().setValue(isDirty);
		return branch;
	}

	private boolean addFilesToBranch(TreeItem<TreeNode> branch) {
		boolean response = false;
		for (GistFile file : branch.getValue().getGist().getFiles()) {
			TreeItem<TreeNode> leaf = getNewLeaf(file);
			file.addedToTree();
			if (file.isAlertable()) response = true;
			branch.getChildren().add(leaf);
		}
		return response;
	}

	public TreeItem<TreeNode> getNewLeaf(GistFile file) {
		TreeItem<TreeNode> leaf = new TreeItem<>(new TreeNode(file));
		leaf.graphicProperty().bind(file.getGraphicNode());
		file.refreshGraphicNode();
		return leaf;
	}

	public void createBranchCategories() {
		branchCategoryMap.clear();
		categoryList = Action.getCategoryList();
		gistWindow.getTreeRoot().getChildren().clear();
		for (String category : categoryList) {
			GistCategory       gistCategory   = new GistCategory(category);
			TreeItem<TreeNode> categoryBranch = new TreeItem<>(new TreeNode(gistCategory));
			categoryBranch.setGraphic(TreeIcons.getGistCategoryIcon());
			branchCategoryMap.put(category, categoryBranch);
			gistWindow.getTreeRoot().getChildren().add(categoryBranch);
		}
	}

	public void refreshTreeIcons() {
		TreeItem<TreeNode> selectedTreeItem = gistWindow.getSelectedTreeItem();
		ObservableList<TreeItem<TreeNode>> firstLevelNodes = gistWindow.getTreeRoot().getChildren();
		List<TreeItem<TreeNode>>           gistBranches       = new ArrayList<>();
		for (TreeItem<TreeNode> levelOneNode : firstLevelNodes) {
			if (levelOneNode.getValue().getType().equals(CATEGORY)) {
				levelOneNode.setGraphic(TreeIcons.getGistCategoryIcon());
				gistBranches.addAll(levelOneNode.getChildren());
			}
			else {gistBranches.add(levelOneNode);}
		}
		TreeItem<TreeNode> treeRoot = gistWindow.getTreeRoot();
		for (TreeItem<TreeNode> branch : gistBranches) {
			branch.setGraphic(TreeIcons.getGistIcon());
			for (TreeItem<TreeNode> leaf : branch.getChildren()) {
				GistFile gistFile = leaf.getValue().getFile();
				if (gistFile.isAlertable()) {
					if (!branch.getParent().equals(treeRoot)) {
						branch.getParent().setExpanded(true);
					}
					branch.setExpanded(true);
				}
				gistFile.refreshGraphicNode();
			}
		}
		new Thread(() -> {
			//We have to kick one node in the tree in a separate thread WITH a delay in order to get the icons to refresh after a change.
			Action.sleep(100);
			boolean value = gistWindow.getTreeRoot().getChildren().get(0).isExpanded();
			gistWindow.getTreeRoot().getChildren().get(0).setExpanded(!value);
			gistWindow.getTreeRoot().getChildren().get(0).setExpanded(value);
			if(selectedTreeItem != null)
				gistWindow.getTreeView().getSelectionModel().select(selectedTreeItem);
		}).start();
	}

	public boolean gistBranchInCategory(TreeItem<TreeNode> gistBranch) {
		for (TreeItem<TreeNode> categoryBranch : branchCategoryMap.values()) {
			if (categoryBranch.getValue().getGistCategory().hasGist(gistBranch.getValue().getGist())) {
				categoryBranch.getChildren().add(gistBranch);
				if(gistBranch.isExpanded())
					categoryBranch.setExpanded(true);
				return true;
			}
		}
		return false;
	}

	private final Map<String, TreeItem<TreeNode>> branchCategoryMap = new HashMap<>();

	public void refreshTree() {
		ObservableList<TreeItem<TreeNode>> categoryList  = FXCollections.observableArrayList();
		ObservableList<TreeItem<TreeNode>> gistList = FXCollections.observableArrayList();

		for (TreeItem<TreeNode> rootBranch : gistWindow.getTreeRoot().getChildren()) {
			getIcon(rootBranch);
			if (rootBranch.getValue().getType().equals(CATEGORY)) {
				for(TreeItem<TreeNode> gistBranch : rootBranch.getChildren()) {
					getIcon(gistBranch);
					for(TreeItem<TreeNode> gistLeaf : gistBranch.getChildren()) {
						getIcon(gistLeaf);
					}
				}
				categoryList.add(rootBranch);
			}
			else {
				for(TreeItem<TreeNode> gistLeaf : rootBranch.getChildren()) {
					getIcon(gistLeaf);
				}
				gistList.add(rootBranch);
			}
		}
		gistWindow.getTreeRoot().getChildren().clear();
		categoryList.sort(Comparator.comparing(TreeItem<TreeNode>::toString));
		gistList.sort(Comparator.comparing(TreeItem<TreeNode>::toString));
		for (TreeItem<TreeNode> categoryBranch : categoryList) {
			gistWindow.getTreeRoot().getChildren().add(categoryBranch);
		}
		for (TreeItem<TreeNode> gistBranch : gistList) {
			gistWindow.getTreeRoot().getChildren().add(gistBranch);
		}
	}

	private void getIcon(TreeItem<TreeNode> node) {
		switch (node.getValue().getType()) {
			case GIST -> node.setGraphic(TreeIcons.getGistIcon());
			case CATEGORY -> node.setGraphic(TreeIcons.getGistCategoryIcon());
			case FILE -> node.getValue().getFile().refreshGraphicNode();
		}
	}

	public void handleTreeEvent(TreeItem<TreeNode> selected) {
		if (selected != null) {
			gistWindow.setSelectedNode(selected.getValue());
		}
	}

}
