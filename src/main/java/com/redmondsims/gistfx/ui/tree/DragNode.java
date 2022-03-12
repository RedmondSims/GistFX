/*
 * File: DragNode.java
 * Copyright (C) 29/03/2021 David Thaler.
 * All rights reserved
 */
package com.redmondsims.gistfx.ui.tree;

import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import com.redmondsims.gistfx.ui.icons.Icons;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import static com.redmondsims.gistfx.enums.Type.*;

public class DragNode implements GistType  {

    private       String text = "";
    private final Type   type;
    private       String gistId;
    private       String description;
    private       String category;
    private       int    fileId;

    public DragNode(){
        this.type = STANDARD;
    }

    public DragNode(String category) {
        this.text = category;
        this.category = category;
        this.type = Type.CATEGORY;
    }

    public DragNode(String text, Gist gist) {
        this.gistId = gist.getGistId();
        this.description = gist.getDescription();
        this.type = Type.GIST;
        this.text = text;
    }

    public DragNode(String text, GistFile file) {
        this.gistId = file.getGistId();
        this.fileId = file.getFileId();
        this.type = Type.FILE;
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
        return category;
    }

    @Override public String toString() {
        return text;
    }

    @Override public Type getType() {
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

    @Override public Node getGraphic() {
        if(type.equals(FILE)) {
            ImageView iv = getFile().getGraphic();
            return getFile().getGraphic();
        }
        if(type.equals(GIST)) {
            return Icons.getGistFolderIcon();
        }
        if(type.equals(CATEGORY)) {
            return Icons.getCategoryFolderIcon();
        }
        return null;
    }
}
