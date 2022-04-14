package com.redmondsims.gistfx.utils;

public class Util {

	public static double reMap(double sourceNumber, double fromRangeStart, double fromRangeEnd, double toRangeStart, double toRangeEnd, int decimalPrecision) {
		double deltaA      = fromRangeEnd - fromRangeStart;
		double deltaB      = toRangeEnd - toRangeStart;
		double scale       = deltaB / deltaA;
		double negA        = -1 * fromRangeStart;
		double offset      = (negA * scale) + toRangeStart;
		double finalNumber = (sourceNumber * scale) + offset;
		int    calcScale   = (int) Math.pow(10, decimalPrecision);
		return (double) Math.round(finalNumber * calcScale) / calcScale;
	}

	public static int reMap(double sourceNumber, double fromRangeStart, double fromRangeEnd, double toRangeStart, double toRangeEnd) {
		double deltaA      = fromRangeEnd - fromRangeStart;
		double deltaB      = toRangeEnd - toRangeStart;
		double scale       = deltaB / deltaA;
		double negA        = -1 * fromRangeStart;
		double offset      = (negA * scale) + toRangeStart;
		double finalNumber = (sourceNumber * scale) + offset;
		int    calcScale   = (int) Math.pow(10, 0);
		return (int) Math.round(finalNumber * calcScale) / calcScale;
	}

	public static String truncate(String string, int numChars, boolean ellipses) {
		int    len = string.length();
		String out = string.substring(0, Math.min(numChars, string.length()));
		if (len > numChars && ellipses) out += "...";
		return out;
	}


}
