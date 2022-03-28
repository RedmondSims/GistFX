package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.FileState;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.ui.gist.factory.TreeNode;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import static com.redmondsims.gistfx.enums.FileState.*;
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
		TreeItem<TreeNode> branch = getBranch(gistId);
		gistWindow.getTreeView().getSelectionModel().select(branch);
		gistWindow.setSelectedNode(branch.getValue());
		Gist selectedGist = branch.getValue().getGist();
		branch.getChildren().clear();
		addFilesToBranch(selectedGist, branch);
	}

	public void addFilesToBranch(Gist gist, TreeItem<TreeNode> branch) {
		for (GistFile file : gist.getFiles()) {
			TreeItem<TreeNode> leaf = getNewLeaf(file);
			file.addedToTree();
			branch.getChildren().add(leaf);
		}
	}

	public void refreshGistBranch(GistFile gistFile) {
		String gistId   = gistFile.getGistId();
		String filename = gistFile.getFilename();
		refreshGistBranch(gistId);
		TreeItem<TreeNode> branch = getBranch(gistId);
		gistWindow.expandBranch(branch);
		TreeItem<TreeNode> leaf = getLeaf(gistId, filename);
		gistWindow.getTreeView().getSelectionModel().select(leaf);
		gistWindow.setSelectedNode(leaf.getValue());
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
		branch.setGraphic(Icons.getGistIcon());
		branch.expandedProperty().bindBidirectional(branch.getValue().expandedProperty());
		branch.expandedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue)
				branch.getValue().setExpanded(newValue);
		});
		addFilesToBranch(gist, branch);
		return branch;
	}

	public TreeItem<TreeNode> getNewLeaf(GistFile file) {
		TreeItem<TreeNode> leaf =  new TreeItem<>(new TreeNode(file.getFilename(), file));
		leaf.graphicProperty().bind(file.getGraphicNode());
		return leaf;
	}

	public List<TreeItem<TreeNode>> getFileNodes() {
		List<TreeItem<TreeNode>> list = new ArrayList<>();
		for(TreeItem<TreeNode> branch : gistWindow.getTreeRoot().getChildren()) {
			if(branch.isExpanded()) list.add(branch);
			for(TreeItem<TreeNode> twig : branch.getChildren()) {
				if(twig.isExpanded()) list.add(twig);
				if(twig.getChildren().size() > 0) {
					list.addAll(twig.getChildren());
				}
				else {
					list.add(twig);
				}
			}
		}
		return list;
	}

	public List<TreeItem<TreeNode>> getAllNodes() {
		List<TreeItem<TreeNode>> list = new ArrayList<>();
		for(TreeItem<TreeNode> branch : gistWindow.getTreeRoot().getChildren()) {
			list.add(branch);
			list.addAll(branch.getChildren());
			for(TreeItem<TreeNode> twig : branch.getChildren()) {
				list.addAll(twig.getChildren());
			}
		}
		return list;
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
				levelOneNode.setGraphic(Icons.getGistCategoryIcon());
				gistNodes.addAll(levelOneNode.getChildren());
			}
			else gistNodes.add(levelOneNode);
		}
		for(TreeItem<TreeNode> gistNode : gistNodes) {
			gistNode.setGraphic(Icons.getGistIcon());
			for(TreeItem<TreeNode> leaf : gistNode.getChildren()) {
				leaf.getValue().getFile().refreshGraphicNode();
			}
		}
		resetNodes();
	}

	public void setFileDirtyState(GistFile gistFile, FileState state, boolean selected){
		String             gistId = gistFile.getGistId();
		TreeItem<TreeNode> branch = getBranch(gistId);
		if (branch != null) {
			if (!state.equals(NORMAL)) {
				for (TreeItem<TreeNode> leaf : branch.getChildren()) {
					if (leaf.getValue().getFile().equals(gistFile)) {
						gistWindow.getTreeView().getSelectionModel().select(branch);
					}
				}
			}
			if(state.equals(CONFLICT) && selected) {
				Platform.runLater(() -> {
					CustomAlert.showWarning("This Gist file is in conflict with the version on GitHub. Perhaps it was edited in between GistFX sessions...\n\nThe next window will show you the GitHub version and the locally stored version so that you can decide which one to keep.\n\nYou will not be able to edit the file until you resolve the conflict.");
					gistWindow.openCompareWindow();
				});
			}
		}
	}

	public void createBranchCategories() {
		branchCategoryMap.clear();
		categoryList = Action.getCategoryList();
		gistWindow.getTreeRoot().getChildren().clear();
		for(String category : categoryList) {
			GistCategory gistCategory = new GistCategory(category);
			TreeItem<TreeNode> categoryBranch = new TreeItem<>(new TreeNode(gistCategory));
			categoryBranch.setGraphic(Icons.getGistCategoryIcon());
			categoryBranch.expandedProperty().bindBidirectional(categoryBranch.getValue().expandedProperty());
			branchCategoryMap.put(category, categoryBranch);
			gistWindow.getTreeRoot().getChildren().add(categoryBranch);
		}
	}

	public boolean haveCategoryBranch(String category) {
		return branchCategoryMap.containsKey(category);
	}

	private final Map<String,TreeItem<TreeNode>> branchCategoryMap = new HashMap<>();

	public void refreshTree() {
		gistWindow.fillTree();
	}

	public TreeItem<TreeNode> getCategoryBranch(String category) {
		return branchCategoryMap.getOrDefault(category, null);
	}

	public void handleTreeEvent(TreeItem<TreeNode> selected) {
		if (selected != null) {
			gistWindow.setSelectedNode(selected.getValue());
		}
	}

}
