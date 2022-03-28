package com.redmondsims.gistfx.data.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class Names {

	private Map<String,String> nameMap = new HashMap<>();

	public String getName(String gistId) {
		return nameMap.getOrDefault(gistId,"");
	}

	public Map<String, String> getMap() {
		return nameMap;
	}

	public void setName(String gistId, String name) {
		if (nameMap.containsKey(gistId)) {
			nameMap.replace(gistId,name);
		}
		else {
			this.nameMap.put(gistId,name);
		}
	}

	public void deleteName(String gistId) {
		nameMap.remove(gistId);
	}

	public void setNameMap(Map<String,String> nameMap) {
		this.nameMap = nameMap;
	}

	public String getGistId (String name) {
		for (String gistId : nameMap.keySet()) {
			String gistName = nameMap.get(gistId);
			if (name.equals(gistName)) {
				return gistId;
			}
		}
		return "";
	}

	public Collection<String> getList() {
		return nameMap.values();
	}

	public boolean hasData() {
		return nameMap.size() > 0;
	}

	public void changeGistId(String oldGistId, String newGistId) {
		String name = nameMap.getOrDefault(oldGistId,"");
		if(!name.isEmpty()) {
			nameMap.remove(oldGistId);
			nameMap.put(newGistId,name);
		}
	}

}
