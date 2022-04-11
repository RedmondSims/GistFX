/*
 * File: TreeNode.java
 * Copyright (C) 29/03/2021 David Thaler.
 * All rights reserved
 */
package com.redmondsims.gistfx.ui.gist.factory;

import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.ui.gist.GistCategory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeNode implements GistInterface {

	private       String  nodeName;
	private       GistCategory   gistCategory;
	private final TreeType         type;
	private       String           gistId;
	private       String           description;
	private       int              fileId;
	private final CBooleanProperty expandedProperty = new CBooleanProperty(false);

	public TreeNode() {
		this.type = null;
	}

	public TreeNode(GistCategory gistCategory) {
		this.gistCategory = gistCategory;
		this.type         = TreeType.CATEGORY;
		this.nodeName = gistCategory.getCategoryName();
	}

	public TreeNode(String text, Gist gist) {
		this.gistId      = gist.getGistId();
		this.description = gist.getDescription();
		this.type        = TreeType.GIST;
		this.nodeName = gist.getName();
	}

	public TreeNode(String text, GistFile file) {
		this.gistId = file.getGistId();
		this.fileId = file.getFileId();
		this.type   = TreeType.FILE;
		this.nodeName = file.getFilename();
	}

	public boolean canContainChildren() {
		return true;
	}

	public String getCategoryName() {
		return gistCategory.getCategoryName();
	}

	public GistCategory getGistCategory() {
		return gistCategory;
	}

    public void setName(String name) {
        switch(type) {
            case CATEGORY -> getGistCategory().setCategoryName(name);
            case GIST -> getGist().setName(name);
            case FILE -> getFile().setName(name);
        }
    }

	@Override public String toString() {
		return nodeName;
	}

	@Override public TreeType getType() {
		return type;
	}

	@Override public Gist getGist() {
		return GistManager.getGist(gistId);
	}

	@Override public GistFile getFile() {
		return GistManager.getFile(gistId, fileId);
	}

	@Override public String getGistId() {
		return gistId;
	}

	@Override public String getDescription() {
		return description;
	}

	@Override public String getName() {
		return nodeName;
	}

}
