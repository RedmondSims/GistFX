package com.redmondsims.gistfx.enums;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.Random;

public enum Colors {
	WHITE,
	BLACK,
	RED,
	GREEN,
	BLUE,
	YELLOW,
	HOTPINK;

	public String Name(Colors this) {
		return switch (this) {
			case WHITE -> "White";
			case BLACK -> "Black";
			case RED -> "Red";
			case GREEN -> "Green";
			case BLUE -> "Blue";
			case YELLOW -> "Yellow";
			case HOTPINK -> "HotPink";
		};
	}

	public static Colors get(String pref) {
		return switch (pref) {
			case "White" -> WHITE;
			case "Black" -> BLACK;
			case "Red" -> RED;
			case "Green" -> GREEN;
			case "Blue" -> BLUE;
			case "Yellow" -> YELLOW;
			case "HotPink" -> HOTPINK;
			default -> null;
		};
	}

	public Color getColor(Colors this) {
		return switch(this) {
			case WHITE -> Color.WHITE;
			case BLACK -> Color.BLACK;
			case RED -> Color.RED;
			case GREEN -> Color.rgb(0,255,234);
			case BLUE -> Color.rgb(0,135,255);
			case YELLOW -> Color.YELLOW;
			case HOTPINK -> Color.rgb(255,0,175);
		};
	}

	public static ObservableList<Colors> loginScreenColorList() {
		return FXCollections.observableArrayList(BLUE, GREEN, RED, YELLOW, HOTPINK);
	}

	public static ObservableList<Colors> trayIconColorList() {
		return FXCollections.observableArrayList(WHITE,BLACK,BLUE,HOTPINK,GREEN,RED,YELLOW);
	}

	public static Color random() {
		int    max    = 255;
		int    min    = 0;
		Random rRed   = new Random();
		Random rGreen = new Random();
		Random rBlue  = new Random();
		int    red    = rRed.nextInt((max - min) + 1) + min;
		int    green  = rGreen.nextInt((max - min) + 1) + min;
		int    blue   = rBlue.nextInt((max - min) + 1) + min;
		return Color.rgb(red, green, blue);
	}

	public static Colors randomLoginScreen() {
		int min = 1;
		int max = 5;
		Random random = new Random();
		int response = random.nextInt((max - min) + 1) + min;
		return switch(response) {
			case 1 -> RED;
			case 2 -> GREEN;
			case 3 -> BLUE;
			case 4 -> YELLOW;
			case 5 -> HOTPINK;
			default -> null;
		};
	}

}
