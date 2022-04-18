package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.javafx.CStringProperty;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.ui.gist.treefactory.TreeNode;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Search {

	private final CStringProperty searchText = new CStringProperty();
	private final CStringProperty feedbackProperty = new CStringProperty();
	private final Map<Integer, TreeItem<TreeNode>> treeMap = new HashMap<>();
	private final Map<Integer, String> wordMap = new HashMap<>();
	private final LinkedList<Integer> hitList = new LinkedList<>();
	private String results;

	public void bindTo(StringProperty searchFieldProperty) {
		searchText.bind(searchFieldProperty);
	}

	public CStringProperty getFeedbackProperty() {
		return feedbackProperty;
	}

	public void startSearch(TreeItem<TreeNode> treeRoot) {
		int index = -1;
		for(TreeItem<TreeNode> treeNode : treeRoot.getChildren()) {
			index++;
			treeMap.put(index,treeNode);
			wordMap.put(index,treeNode.getValue().getName());
			if (treeNode.getValue().getType().equals(TreeType.CATEGORY)) {
				for (TreeItem<TreeNode> gistNode : treeNode.getChildren()) {
					String gistName = gistNode.getValue().getName();
					String gistDescription = gistNode.getValue().getGist().getDescription();
					String finalString = gistName + " " + gistDescription;
					index++;
					treeMap.put(index, gistNode);
					wordMap.put(index, finalString);
					for(TreeItem<TreeNode> fileNode : gistNode.getChildren()) {
						String filename = fileNode.getValue().getName();
						String fileDescription = fileNode.getValue().getFile().getDescription();
						finalString = filename + " " + fileDescription;
						if(AppSettings.get().searchFileContents()) {
							finalString = finalString + " " + fileNode.getValue().getFile().getFileContents().replaceAll("\\n"," ");
						}
						index++;
						treeMap.put(index,fileNode);
						wordMap.put(index,finalString);
					}
				}
			}
			else {
				for(TreeItem<TreeNode> fileNode : treeNode.getChildren()) {
					String filename = fileNode.getValue().getName();
					String fileDescription = fileNode.getValue().getFile().getDescription();
					String finalString = filename + " " + fileDescription;
					index++;
					treeMap.put(index,fileNode);
					wordMap.put(index,finalString);
				}
			}
		}

		searchText.addListener((observable, oldValue, newValue) -> {
			hitList.clear();
			String value = newValue.toLowerCase();
			if(value.length() > 0) {
				totalHits = 0;
				for (Integer idx : wordMap.keySet()) {
					String line = wordMap.get(idx).toLowerCase();
					if (line.contains(value)) {
						hitList.addLast(idx);
						totalHits++;
						results = "Found " + totalHits + " hits";
						feedbackProperty.setValue(results);
						hitIndex = 0;
					}
				}
			}
			else {
				feedbackProperty.setValue("");
			}
		});
	}

	private int hitIndex = 0;
	private int totalHits = 0;
	public TreeItem<TreeNode> getNextHit() {
		TreeItem<TreeNode> node = null;
		if(hitList.size() > 0) {
			int nodeIndex = hitList.get(hitIndex);
			hitIndex++;
			String feedback = results + " (" + hitIndex + ")";
			feedbackProperty.setValue(feedback);
			node = treeMap.get(nodeIndex);
			if (hitIndex > hitList.size() - 1) {
				hitIndex = 0;
			}
		}
		return node;
	}

	public TreeItem<TreeNode> getPreviousHit() {
		TreeItem<TreeNode> node = null;
		if(hitList.size() > 0) {
			if (hitIndex <= 0) hitIndex = hitList.size() - 1;
			hitIndex--;
			int nodeIndex = hitList.get(hitIndex);
			node = treeMap.get(nodeIndex);
		}
		return node;
	}


	public Integer[] getHits() {
		return (Integer[])hitList.toArray();
	}

	public Map<Integer, TreeItem<TreeNode>> getTreeMap() {
		return treeMap;
	}
}
