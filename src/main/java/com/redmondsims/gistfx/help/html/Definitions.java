package com.redmondsims.gistfx.help.html;

public class Definitions {

	public static String payload() {
		return
				"""
				A Payload is a class that is used to transmait Gist data to another computer.
				The data is firt extracted from the Gist objects, then encrypted and stored
				into the payload, then sent to the recipient where it is decrypted and
				built into new Gist objects for that user, then added to their GitHub Gists.
				""";
	}
}
