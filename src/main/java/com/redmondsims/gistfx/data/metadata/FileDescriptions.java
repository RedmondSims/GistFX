package com.redmondsims.gistfx.data.metadata;

import com.redmondsims.gistfx.gist.GistFile;
import java.util.concurrent.ConcurrentHashMap;

public class FileDescriptions {

	ConcurrentHashMap<String,String> descriptionMap = new ConcurrentHashMap<>();

	private String getID(String gistId, String filename) {
		return gistId + "" + filename;
	}

	private String getGistId(String id) {
		String[] parts = id.split("");
		if(parts.length == 2) {
			return parts[0];
		}
		return "";
	}

	private String getFilename(String id) {
		String[] parts = id.split("");
		if(parts.length == 2) {
			return parts[1];
		}
		return "";
	}

	public void setDescription(GistFile gistFile, String description) {
		deleteDescription(gistFile);
		String gistId = gistFile.getGistId();
		String filename = gistFile.getFilename();
		String gistFileId = getID(gistId,filename);
		descriptionMap.put(gistFileId,description);
	}

	public String getDescription(GistFile gistFile) {
		String gistId = gistFile.getGistId();
		String filename = gistFile.getFilename();
		String mapID = getID(gistId,filename);
		if(descriptionMap.containsKey(mapID)) {
			for (String id : descriptionMap.keySet()) {
				String mapGistId = getGistId(id);
				String gistFilename = getFilename(id);
				String description = descriptionMap.get(id);
				if (mapGistId.equals(gistId) && gistFilename.equals(filename)) {
					return description;
				}
			}
		}
		return "";
	}

	public void deleteDescription(GistFile gistFile) {
		String gistId = gistFile.getGistId();
		String filename = gistFile.getFilename();
		descriptionMap.remove(getID(gistId,filename));
	}

	public void setDescriptionMap(ConcurrentHashMap<String,String> descriptionMap) {
		this.descriptionMap = new ConcurrentHashMap<>(descriptionMap);
	}

	public ConcurrentHashMap<String,String> getMap() {
		return this.descriptionMap;
	}
}
