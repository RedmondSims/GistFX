package com.redmondsims.gistfx.enums;

public enum LoginScreenColor {
	RED,
	GREEN,
	BLUE,
	YELLOW,
	HOTPINK;

	public String Name(LoginScreenColor this) {
		return switch (this) {
			case RED -> "Red";
			case GREEN -> "Green";
			case BLUE -> "Blue";
			case YELLOW -> "Yellow";
			case HOTPINK -> "HotPink";
		};
	}

	public static LoginScreenColor get(String pref) {
		return switch (pref) {
			case "Red" -> RED;
			case "Green" -> GREEN;
			case "Blue" -> BLUE;
			case "Yellow" -> YELLOW;
			case "HotPink" -> HOTPINK;
			default -> null;
		};
	}


}
