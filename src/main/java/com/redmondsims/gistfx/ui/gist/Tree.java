package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.ui.gist.factory.DragNode;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;

import java.util.*;

import static com.redmondsims.gistfx.enums.Type.*;

public class Tree {

	public Tree (GistWindow gistWindow) {
		this.gistWindow = gistWindow;
	}

	private final GistWindow         gistWindow;

	/**
	 * Tree Methods
	 */

	public void refreshGistBranch(String gistId) {
		TreeItem<DragNode> branch = getBranch(gistId);
		gistWindow.getTreeView().getSelectionModel().select(branch);
		gistWindow.setSelectedNode(branch.getValue());
		Gist selectedGist = branch.getValue().getGist();
		branch.getChildren().clear();
		addFilesToBranch(selectedGist, branch);
	}

	public void addFilesToBranch(Gist gist, TreeItem<DragNode> branch) {
		for (GistFile file : gist.getFiles()) {
			TreeItem<DragNode> leaf = getNewLeaf(file);
			file.addedToTree();
			branch.getChildren().add(leaf);
		}
	}

	public void refreshGistBranch(GistFile gistFile) {
		String gistId   = gistFile.getGistId();
		String filename = gistFile.getFilename();
		refreshGistBranch(gistId);
		TreeItem<DragNode> branch = getBranch(gistId);
		gistWindow.expandBranch(branch);
		TreeItem<DragNode> leaf = getLeaf(gistId, filename);
		gistWindow.getTreeView().getSelectionModel().select(leaf);
		gistWindow.setSelectedNode(leaf.getValue());
	}

	public void addFileToBranch(String gistId, String filename) {
		GistFile           file   = GistManager.getFile(gistId, filename);
		TreeItem<DragNode> branch = getBranch(gistId);
		TreeItem<DragNode> leaf   = new TreeItem<>(new DragNode(filename, file));
		leaf.setGraphic(leaf.getValue().getGraphic());
		branch.getChildren().add(leaf);
	}

	public void addBranch(String gistId) {
		Gist               newGist   = GistManager.getGist(gistId);
		TreeItem<DragNode> newBranch = getNewBranch(newGist);
		gistWindow.getTreeRoot().getChildren().add(newBranch);
	}

	public List<String> categoryList = new ArrayList<>();

	public void removeLeaf(GistFile file) {
		Objects.requireNonNull(getBranch(file.getGistId())).getChildren().removeIf(leaf -> leaf.getValue().toString().equals(file.getFilename()));
	}

	public boolean removeBranch(String gistId) {
		TreeItem<DragNode> branch = getBranch(gistId);
		if (branch == null) return false;
		TreeItem<DragNode> parent = branch.getParent();
		if (parent == null) return false;
		parent.getChildren().remove(branch);;
		return true;
	}

	public TreeItem<DragNode> getLeaf(String gistId, String filename) {
		TreeItem<DragNode> branch = getBranch(gistId);
		for (TreeItem<DragNode> leaf : branch.getChildren()) {
			if (leaf.getValue().getFile().getFilename().equals(filename)) return leaf;
		}
		return null;
	}

	public TreeItem<DragNode> getBranch(String gistId) {
		for (TreeItem<DragNode> branch : gistWindow.getTreeRoot().getChildren()) {
			if (branch.getValue().getType().equals(CATEGORY)) {
				for (TreeItem<DragNode> branchInCategory : branch.getChildren()) {
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

	public TreeItem<DragNode> getNewBranch(Gist gist) {
		TreeItem<DragNode> branch;
		String gistId = gist.getGistId();
		String name = Action.getGistName(gistId);
		branch = new TreeItem<>(new DragNode(name, gist));
		branch.setGraphic(branch.getValue().getGraphic());
		branch.expandedProperty().addListener((observable, oldValue, newValue) -> branch.getValue().getGist().setExpanded(newValue));
		addFilesToBranch(gist, branch);
		branch.setExpanded(branch.getValue().getGist().isExpanded());
		return branch;
	}

	public TreeItem<DragNode> getNewLeaf(GistFile file) {
		TreeItem<DragNode> leaf =  new TreeItem<>(new DragNode(file.getFilename(),file));
		leaf.graphicProperty().bind(file.getGraphicNode());
		return leaf;
	}

	public List<TreeItem<DragNode>> getFileNodes() {
		List<TreeItem<DragNode>> list = new ArrayList<>();
		for(TreeItem<DragNode> branch : gistWindow.getTreeRoot().getChildren()) {
			if(branch.isExpanded()) list.add(branch);
			for(TreeItem<DragNode> twig : branch.getChildren()) {
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

	public List<TreeItem<DragNode>> getAllNodes() {
		List<TreeItem<DragNode>> list = new ArrayList<>();
		for(TreeItem<DragNode> branch : gistWindow.getTreeRoot().getChildren()) {
			list.add(branch);
			list.addAll(branch.getChildren());
			for(TreeItem<DragNode> twig : branch.getChildren()) {
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
		Platform.runLater(() -> {
			List<TreeItem<DragNode>> list = getAllNodes();
			for(TreeItem<DragNode> treeItem : list) {
				if(!treeItem.getValue().getType().equals(FILE)) {
					treeItem.setGraphic(treeItem.getValue().getGraphic());
				}
				else {
					treeItem.getValue().getFile().refreshGraphicNode();
				}
			}
			resetNodes();
		});
	}

	public void setFileDirtyState(GistFile gistFile, Type state, boolean selected){
		String             gistId = gistFile.getGistId();
		TreeItem<DragNode> branch = getBranch(gistId);
		if (branch != null) {
			if (!state.equals(Type.OK)) {
				for (TreeItem<DragNode> leaf : branch.getChildren()) {
					if (leaf.getValue().getFile().equals(gistFile)) {
						gistWindow.getTreeView().getSelectionModel().select(branch);
					}
				}
			}
			if(state.equals(Type.CONFLICT) && selected) {
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
		Collections.sort(categoryList);
		for(String category : categoryList) {
			TreeItem<DragNode> categoryBranch = new TreeItem<>(new DragNode(category));
			categoryBranch.setGraphic(categoryBranch.getValue().getGraphic());
			branchCategoryMap.put(category, categoryBranch);
			gistWindow.getTreeRoot().getChildren().add(categoryBranch);
		}
	}

	public final Map<String,TreeItem<DragNode>> branchCategoryMap = new HashMap<>();

	public void refreshTree() {
		gistWindow.fillTree();
	}

	public TreeItem<DragNode> getBranchCategory(String category) {
		return branchCategoryMap.getOrDefault(category, null);
	}

	public void handleTreeEvent(TreeItem<DragNode> selected) {
		if (selected != null) {
			System.out.println("handleTreeEvent - " + selected.getValue());
			gistWindow.setSelectedNode(selected.getValue());
		}
	}

}
