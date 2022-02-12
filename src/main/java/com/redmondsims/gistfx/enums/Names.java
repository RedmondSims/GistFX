package com.redmondsims.gistfx.enums;

public enum Names {
	CATEGORIES,
	CATEGORY_MAP,
	NAME_MAP,
	GIST_DATA_DESCRIPTION;

	public String Name(Names this) {
		return switch(this) {
			case CATEGORIES -> "categories";
			case CATEGORY_MAP -> "categoryMap";
			case NAME_MAP -> "nameMap";
			case GIST_DATA_DESCRIPTION -> "GistFX!Data!";
		};
	}
}
