package com.redmondsims.gistfx.data.metadata;

import com.redmondsims.gistfx.gist.GistFile;
import java.util.concurrent.ConcurrentHashMap;

public class FileDescriptions {

	private ConcurrentHashMap<String,String> descriptionMap = new ConcurrentHashMap<>();

	private String getID(String gistId, String filename) {
		return gistId + "" + filename;
	}

	private String getGistId(String gistFileId) {
		String[] parts = gistFileId.split("");
		if(parts.length == 2) {
			return parts[0];
		}
		return "";
	}

	private String getFilename(String gistFileId) {
		String[] parts = gistFileId.split("");
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

	public void setDescription(String gistId, String filename, String description) {
		String gistFileId = getID(gistId,filename);
		deleteDescription(gistFileId);
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

	private void deleteDescription(GistFile gistFile) {
		String gistId = gistFile.getGistId();
		String filename = gistFile.getFilename();
		descriptionMap.remove(getID(gistId,filename));
	}

	public void deleteDescription(String gistId, String filename) {
		String gistFileId = getID(gistId,filename);
		deleteDescription(gistFileId);
	}

	public void removeAllGistFiles(String gistId) {
		for(String gistFileId : descriptionMap.keySet()) {
			String fileGistId = getGistId(gistFileId);
			if(fileGistId.equals(gistId)) {
				descriptionMap.remove(gistFileId);
			}
		}
	}

	private void deleteDescription(String gistFileId) {
		descriptionMap.remove(gistFileId);
	}

	public void setDescriptionMap(ConcurrentHashMap<String,String> descriptionMap) {
		this.descriptionMap = new ConcurrentHashMap<>(descriptionMap);
	}

	public ConcurrentHashMap<String,String> getMap() {
		return this.descriptionMap;
	}

	public void changeGistId(String oldGistId, String newGistId) {
		for(String gistFileId : descriptionMap.keySet()) {
			String gistId = getGistId(gistFileId);
			String filename = getFilename(gistFileId);
			String description = descriptionMap.get(gistFileId);
			if (gistId.equals(oldGistId)) {
				descriptionMap.remove(gistFileId);
				String newGistFileId = getID(newGistId,filename);
				descriptionMap.put(newGistFileId,description);
			}
		}
	}
}
