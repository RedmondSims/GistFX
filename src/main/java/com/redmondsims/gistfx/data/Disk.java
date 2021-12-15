package com.redmondsims.gistfx.data;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

class Disk {

	private StringBuilder sb = new StringBuilder();

	public String loadTextFile(File file) {
		sb = new StringBuilder();
		try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
			stream.forEach(s -> sb.append(s).append("\n"));
		}
		catch (IOException e) {
			sb = null;
			e.printStackTrace();
		}
		if (sb == null) {return "null";}
		else {return sb.toString();}
	}

	public void writeToTextFile(File file, String content) {
		try {
			FileUtils.writeStringToFile(file,content,StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}