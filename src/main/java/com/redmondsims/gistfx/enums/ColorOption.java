package com.redmondsims.gistfx.enums;

public enum ColorOption {
	RANDOM,
	USER_SELECTED,
	DEFAULT,
	FOLLOW_LOGIN;

	public String Name(ColorOption this) {
		return switch (this) {
			case RANDOM -> "Random";
			case USER_SELECTED -> "User_Selected";
			case DEFAULT -> "Default";
			case FOLLOW_LOGIN -> "Follow_Login";
		};
	}

	public static ColorOption get(String pref) {
		return switch (pref) {
			case "Random" -> RANDOM;
			case "User_Selected" -> USER_SELECTED;
			case "Default" -> DEFAULT;
			case "Follow_Login" -> FOLLOW_LOGIN;
			default -> null;
		};
	}


}
