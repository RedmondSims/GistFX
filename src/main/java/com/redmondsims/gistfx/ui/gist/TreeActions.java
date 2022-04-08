package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.ui.TreeIcons;
import com.redmondsims.gistfx.ui.gist.factory.TreeNode;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import static com.redmondsims.gistfx.enums.TreeType.*;
import java.util.*;

public class TreeActions {

	public TreeActions(GistWindow gistWindow) {
		this.gistWindow = gistWindow;
	}

	private final GistWindow         gistWindow;

	/**
	 * Tree Methods
	 */

	public void refreshGistBranch(String gistId) {
		try {
			TreeItem<TreeNode> branch = getBranch(gistId);
			if(branch != null) {
				if(gistWindow.getTreeView().getSelectionModel().getSelectedItem() != branch) {
					gistWindow.getTreeView().getSelectionModel().select(branch);
				}
				gistWindow.setSelectedNode(branch.getValue());
				Gist selectedGist = branch.getValue().getGist();
				branch.getChildren().clear();
				addFilesToBranch(selectedGist, branch);
			}
		}
		catch(StackOverflowError e) {
			System.out.println(e.getCause().getLocalizedMessage());
		}
	}

	public void refreshGistLeaf(GistFile gistFile) {
		String gistId   = gistFile.getGistId();
		String filename = gistFile.getFilename();
		TreeItem<TreeNode> branch = getBranch(gistId);
		gistWindow.expandBranch(branch);
		TreeItem<TreeNode> leaf = getLeaf(gistId, filename);
		if(leaf != null) {
			gistWindow.getTreeView().getSelectionModel().select(leaf);
			gistWindow.setSelectedNode(leaf.getValue());
		}
	}

	public void addFileToBranch(String gistId, String filename) {
		GistFile           file   = GistManager.getFile(gistId, filename);
		TreeItem<TreeNode> branch = getBranch(gistId);
		TreeItem<TreeNode> leaf   = new TreeItem<>(new TreeNode(filename, file));
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

	public TreeItem<TreeNode> getLeaf(String gistId, String filename) {
		TreeItem<TreeNode> branch = getBranch(gistId);
		for (TreeItem<TreeNode> leaf : branch.getChildren()) {
			if (leaf.getValue().getFile().getFilename().equals(filename))
				return leaf;
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
		String             name = Action.getGistName(gistId);
		branch = new TreeItem<>(new TreeNode(name, gist));
		branch.setGraphic(TreeIcons.getGistIcon());
		boolean isDirty = addFilesToBranch(gist, branch);
		branch.setExpanded(isDirty);
		branch.getValue().getGist().expandedProperty().setValue(isDirty);
		return branch;
	}

	private boolean addFilesToBranch(Gist gist, TreeItem<TreeNode> branch) {
		boolean response = false;
		for (GistFile file : gist.getFiles()) {
			TreeItem<TreeNode> leaf = getNewLeaf(file);
			file.addedToTree();
			if(file.isDirty()) response = true;
			branch.getChildren().add(leaf);
		}
		return response;
	}

	public TreeItem<TreeNode> getNewLeaf(GistFile file) {
		TreeItem<TreeNode> leaf =  new TreeItem<>(new TreeNode(file.getFilename(), file));
		leaf.graphicProperty().bind(file.getGraphicNode());
		return leaf;
	}

	Timer refreshTimer;

	public void resetNodes() {
		if (refreshTimer != null) refreshTimer.cancel();
		refreshTimer = new Timer();
		refreshTimer.schedule(resetTask(),500);
	}

	public TimerTask resetTask() {
		return new TimerTask() {
			@Override public void run() {
				boolean value = gistWindow.getTreeRoot().getChildren().get(0).isExpanded();
				gistWindow.getTreeRoot().getChildren().get(0).setExpanded(!value);
				gistWindow.getTreeRoot().getChildren().get(0).setExpanded(value);
			}
		};
	}

	public void refreshIcons() {
		ObservableList<TreeItem<TreeNode>> firstLevelNodes = gistWindow.getTreeRoot().getChildren();
		List<TreeItem<TreeNode>>           gistNodes       = new ArrayList<>();
		for(TreeItem<TreeNode> levelOneNode : firstLevelNodes) {
			if (levelOneNode.getValue().getType().equals(CATEGORY)) {
				levelOneNode.setGraphic(TreeIcons.getGistCategoryIcon());
				gistNodes.addAll(levelOneNode.getChildren());
			}
			else gistNodes.add(levelOneNode);
		}
		for(TreeItem<TreeNode> gistNode : gistNodes) {
			gistNode.setGraphic(TreeIcons.getGistIcon());
			for(TreeItem<TreeNode> leaf : gistNode.getChildren()) {
				GistFile gistFile = leaf.getValue().getFile();
				if (gistFile.isAlertable()) {
					if(!gistNode.getParent().equals(gistWindow.getTreeRoot())) {
						gistNode.getParent().setExpanded(true);
					}
					gistNode.setExpanded(true);
				}
				gistFile.refreshGraphicNode();
			}
		}
		resetNodes();
	}

	public void createBranchCategories() {
		branchCategoryMap.clear();
		categoryList = Action.getCategoryList();
		gistWindow.getTreeRoot().getChildren().clear();
		for(String category : categoryList) {
			GistCategory gistCategory = new GistCategory(category);
			TreeItem<TreeNode> categoryBranch = new TreeItem<>(new TreeNode(gistCategory));
			categoryBranch.setGraphic(TreeIcons.getGistCategoryIcon());
			branchCategoryMap.put(category, categoryBranch);
			gistWindow.getTreeRoot().getChildren().add(categoryBranch);
		}
	}

	public boolean gistBranchInCategory(TreeItem<TreeNode> gistBranch) {
		for(TreeItem<TreeNode> categoryBranch : branchCategoryMap.values()) {
			if(categoryBranch.getValue().getGistCategory().hasGist(gistBranch.getValue().getGist())) {
				categoryBranch.getChildren().add(gistBranch);
				return true;
			}
		}
		return false;
	}

	private final Map<String,TreeItem<TreeNode>> branchCategoryMap = new HashMap<>();

	public void refreshTree() {
		gistWindow.fillTree();
	}

	public void handleTreeEvent(TreeItem<TreeNode> selected) {
		if (selected != null) {
			gistWindow.setSelectedNode(selected.getValue());
		}
	}

}
