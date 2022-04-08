package com.redmondsims.gistfx.data;

import java.sql.Date;

public record GistFileRecord(int fileId, String gistId, String filename, String content, boolean dirty) {
}
