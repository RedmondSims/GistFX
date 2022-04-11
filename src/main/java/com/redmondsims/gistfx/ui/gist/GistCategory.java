package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.ui.gist.factory.TreeNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GistCategory {

	public GistCategory(String categoryName) {
		this.categoryName.setValue(categoryName);
		this.gistList.addAll(Action.getGistsInCategory(categoryName));
	}

	private final StringProperty   categoryName = new SimpleStringProperty();
	private final LinkedList<Gist> gistList = new LinkedList<>();

	public String getCategoryName() {
		return categoryName.getValue();
	}

	public void setCategoryName(String categoryName) {
		this.categoryName.setValue(categoryName);
	}

	public void addGist(Gist gist) {
		if(!gistList.contains(gist))
			gistList.add(gist);
	}

	public void removeGist(Gist gist) {
		gistList.remove(gist);
	}

	public boolean hasGist(Gist gist) {
		return gistList.contains(gist);
	}

	public StringProperty getCategoryNameProperty() {
		return categoryName;
	}

	@Override
	public String toString() {
		return categoryName.getValue();
	}
}
