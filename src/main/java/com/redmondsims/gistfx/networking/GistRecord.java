package com.redmondsims.gistfx.networking;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record GistRecord(String gistName,
						 String gistDescription,
						 List<FileRecord> fileRecordList) implements Serializable {

	@Serial private static final long serialVersionUID = 4L;
}
