package com.redmondsims.gistfx.enums;

public enum TreeType {
	GIST,
	CATEGORY,
	FILE;

	public String Name(TreeType this) {
		return switch(this) {
			case GIST -> "GIST";
			case FILE -> "FILE";
			case CATEGORY -> "CATEGORY";
		};
	}
}
