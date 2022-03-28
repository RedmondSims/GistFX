package com.redmondsims.gistfx.data;

public class GistFileId {

	public GistFileId(String gistId, String fileName) {
		this.gistId   = gistId;
		this.fileName = fileName;
	}

	private final String gistId;
	private final String fileName;

	public String getGistId() {
		return gistId;
	}

	public String getFileName() {
		return fileName;
	}
}
