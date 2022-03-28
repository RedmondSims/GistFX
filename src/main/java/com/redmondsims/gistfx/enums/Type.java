package com.redmondsims.gistfx.enums;

public enum Type {

	GIST,
	FILE,
	CATEGORY,
	LOCAL,
	GITHUB,
	NEW,
	UPDATE,
	FILENAME_CHANGE,
	DELETE,
	INTEGER,
	DOUBLE,
	RANDOM,
	STANDARD,
	STRING,
	CONFLICT,
	DIRTY,
	OK;

	public String Name(Type this) {
		return switch(this) {
		case GIST -> "GIST";
		case FILE -> "FILE";
		case CATEGORY -> "CATEGORY";
		case LOCAL -> "LOCAL";
		case GITHUB -> "GITHUB";
		case NEW -> "NEW";
		case UPDATE -> "UPDATE";
		case FILENAME_CHANGE -> "FILENAME_CHANGE";
		case DELETE -> "DELETE";
		case INTEGER -> "INTEGER";
		case DOUBLE -> "DOUBLE";
		case RANDOM -> "RANDOM";
		case STANDARD -> "STANDARD";
		case STRING -> "STRING";
		case CONFLICT -> "CONFLICT";
	    case DIRTY -> "DIRTY";
		case OK -> "OK";
		};
	}

	public String nameCased(Type this) {
		return switch(this) {
			case GIST -> "Gist";
			case FILE -> "File";
			case CATEGORY -> "Category";
			case LOCAL -> "Local";
			case GITHUB -> "Github";
			case NEW -> "New";
			case UPDATE -> "Update";
			case FILENAME_CHANGE -> "Filename_change";
			case DELETE -> "Delete";
			case INTEGER -> "Integer";
			case DOUBLE -> "Double";
			case RANDOM -> "Random";
			case STANDARD -> "Standard";
			case STRING -> "String";
			case CONFLICT -> "Conflict";
			case DIRTY -> "Dirty";
			case OK -> "Ok";
		};
	}
}
