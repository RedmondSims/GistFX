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
import javafx.beans.property.BooleanProperty;

public class TreeNode implements GistInterface {

    private       String       text                     = "";
    private       GistCategory gistCategory;
    private final TreeType     type;
    private       String           gistId;
    private       String           description;
    private       int              fileId;
    private final CBooleanProperty categoryExpandedProperty = new CBooleanProperty(false);

    public TreeNode(){
        this.type = null;
    }

    public TreeNode(GistCategory gistCategory) {
        this.gistCategory = gistCategory;
        this.type = TreeType.CATEGORY;
        this.text = gistCategory.getCategoryName();
    }

    public TreeNode(String text, Gist gist) {
        this.gistId = gist.getGistId();
        this.description = gist.getDescription();
        this.type = TreeType.GIST;
        this.text = text;
    }

    public TreeNode(String text, GistFile file) {
        this.gistId = file.getGistId();
        this.fileId = file.getFileId();
        this.type = TreeType.FILE;
        this.text = text;
    }

    public void setText(final String displayText) {
        text = displayText;
    }

    public String getText() {
        return text;
    }

    public boolean canContainChildren() {
        return true;
    }

    public String getCategory() {
        return gistCategory.getCategoryName();
    }

    @Override public String toString() {
        return text;
    }

    @Override public TreeType getType() {
        return type;
    }

    @Override public Gist getGist() {
        return GistManager.getGist(gistId);
    }

    @Override public GistFile getFile() {
        return GistManager.getFile(gistId,fileId);
    }

    @Override public String getGistId() {
        return gistId;
    }

    @Override public String getDescription() {
        return description;
    }

    @Override public String getName() {
        return text;
    }

    @Override public BooleanProperty expandedProperty() {
        return switch (type) {
            case CATEGORY -> gistCategory.expandedProperty();
            case GIST -> getGist().expandedProperty();
            case FILE -> null;
        };
    }

    @Override public void setExpanded(boolean value) {
        switch(type) {
            case GIST -> getGist().setExpanded(value);
            case CATEGORY -> this.gistCategory.setExpanded(value);
        }
    }

}
