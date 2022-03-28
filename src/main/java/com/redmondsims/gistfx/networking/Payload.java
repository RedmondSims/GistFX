package com.redmondsims.gistfx.networking;

import com.redmondsims.gistfx.enums.TreeType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record Payload(String title,
					  String senderName,
					  List<GistRecord> gistRecordList,
					  FileRecord fileRecord,
					  String categoryName,
					  boolean usePassword,
					  String passwordHash,
					  TreeType type) implements Serializable {

	@Serial private static final long serialVersionUID = 4L;

}
