package com.redmondsims.gistfx.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Modify {

	public static StringUtil string() {
		return new StringUtil();
	}

	public static class StringUtil {

		public String truncate(String string, int numChars, boolean ellipses) {
			int    len = string.length();
			String out = string.substring(0, Math.min(numChars, string.length()));
			if (len > numChars && ellipses) out += "...";
			return out;
		}

		public String replace(String source, String regex, String replacement) {
			return source.replaceFirst(regex, replacement);
		}

		public List<String> extractList(String string, String regex) {
			List<String> list = new ArrayList<>();
			Pattern      p    = Pattern.compile("(" + regex + ")");
			Matcher      m    = p.matcher(string);
			while (m.find()) {
				list.add(m.group(0));
			}
			return list;
		}

	}

}
