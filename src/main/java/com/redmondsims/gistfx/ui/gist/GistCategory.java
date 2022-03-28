package com.redmondsims.gistfx.ui.gist;

import com.redmondsims.gistfx.javafx.CBooleanProperty;

public class GistCategory {

	public GistCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	private final CBooleanProperty expandedProperty = new CBooleanProperty(false);
	private       String           categoryName;

	public CBooleanProperty expandedProperty() {
		return expandedProperty;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setExpanded(boolean value) {
		expandedProperty.setValue(value);
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return categoryName;
	}



}
