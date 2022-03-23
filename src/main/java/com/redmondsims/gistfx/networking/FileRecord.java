package com.redmondsims.gistfx.networking;

import java.io.Serial;
import java.io.Serializable;

public record FileRecord(String filename,
						 String content,
						 String description) implements Serializable {
	@Serial private static final long serialVersionUID = 4L;
}
