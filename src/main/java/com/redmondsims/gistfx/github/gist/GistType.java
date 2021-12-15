package com.redmondsims.gistfx.github.gist;

import com.redmondsims.gistfx.ui.enums.Type;

//This class is used in the Tree Items and provides flexibility
//to include either a Gist as a tree item or a GistFile.
public class GistType {

	private String   gistID;
	private String   description;
	private Type     type;
	private Gist     gist;
	private GistFile file;

	public GistType() {}

	public GistType(Gist gist) {
		this.gistID      = gist.getGistId();
		this.description = gist.getDescription();
		this.type        = Type.GIST;
		this.gist        = gist;
		this.file        = null;
	}

	public GistType(GistFile file) {
		this.gistID = file.getGistId();
		this.type   = Type.FILE;
		this.file   = file;
		this.gist   = GistManager.getGist(file.getGistId());
	}

	public Type getType() {
		return type;
	}

	public Gist getGist() {
		return gist;
	}

	public GistFile getFile() {
		return file;
	}

	public String getGistID() {
		return gistID;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		if (type == Type.GIST) return gist.toString();
		return file.toString();
	}

}
