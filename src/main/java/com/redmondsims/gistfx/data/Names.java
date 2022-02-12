package com.redmondsims.gistfx.data;

import java.util.HashMap;
import java.util.Map;

public class Names {

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

	public boolean hasData() {
		return nameMap.size() > 0;
	}

}
