package com.redmondsims.gistfx.ui.gist.factory;

import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

import java.io.Serializable;

public interface GistInterface extends Serializable {

	TreeType getType();

	Gist getGist();

	GistFile getFile();

	String getGistId();

	String getDescription();

	String getName();

	boolean equals(Object obj);

	String getText();

	boolean canContainChildren();

	String toString();

}
