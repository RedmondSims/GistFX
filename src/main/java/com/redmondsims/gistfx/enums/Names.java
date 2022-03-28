package com.redmondsims.gistfx.enums;

public enum Names {
	CATEGORIES,
	CATEGORY_MAP,
	NAME_MAP,
	GITHUB_METADATA;

	public String Name(Names this) {
		return switch(this) {
			case CATEGORIES -> "categories";
			case CATEGORY_MAP -> "categoryMap";
			case NAME_MAP -> "nameMap";
			case GITHUB_METADATA -> "GistFX!Metadata!";
		};
	}
}
