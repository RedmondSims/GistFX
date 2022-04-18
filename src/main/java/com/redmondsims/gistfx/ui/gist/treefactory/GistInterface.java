package com.redmondsims.gistfx.ui.gist.treefactory;

import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;

import java.io.Serializable;

public interface GistInterface extends Serializable {

	TreeType getType();

	Gist getGist();

	GistFile getFile();

	String getGistId();

	String getDescription();

	boolean equals(Object obj);

	String getName();

	boolean canContainChildren();

	String toString();

}
