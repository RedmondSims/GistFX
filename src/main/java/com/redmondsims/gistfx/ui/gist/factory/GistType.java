package com.redmondsims.gistfx.ui.gist.factory;

import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import javafx.scene.Node;

import java.io.Serializable;

public interface GistType extends Serializable {

	Type getType();

	Gist getGist();

	GistFile getFile();

	String getGistId();

	String getDescription();

	String getName();

	boolean equals(Object obj);

	String getText();

	boolean canContainChildren();

	Node getGraphic();

}
