/*
 * File: TreeNode.java
 * Copyright (C) 29/03/2021 David Thaler.
 * All rights reserved
 */
package com.redmondsims.gistfx.ui.gist.treefactory;

import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.ui.gist.GistCategory;
import com.redmondsims.gistfx.utils.Util;

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
		this.nodeName = Util.truncate(gistCategory.getCategoryName().replaceAll("\\n", " "), 30, true);
	}

	public TreeNode(Gist gist) {
		this.gistId      = gist.getGistId();
		this.description = gist.getDescription();
		this.type        = TreeType.GIST;
		this.nodeName = Util.truncate(gist.getName().replaceAll("\\n", " "), 30, true);
	}

	public TreeNode(GistFile file) {
		this.gistId = file.getGistId();
		this.fileId = file.getFileId();
		this.type   = TreeType.FILE;
		this.nodeName = Util.truncate(file.getFilename().replaceAll("\\n", " "), 30, true);
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
