package com.redmondsims.gistfx.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categories {

	private Map<String,String> categoryMap  = new HashMap<>();
	private List<String>       categoryList = new ArrayList<>();

	public void addCategory(String category) {
		if (!categoryList.contains(category)) {
			categoryList.add(category);
		}
	}

	public List<String> getList() {
		return categoryList;
	}

	public void setList(List<String> categoryList) {
		this.categoryList = categoryList;
	}

	public void setMap(Map<String,String> categoryMap) {
		this.categoryMap = categoryMap;
	}

	public void mapCategory(String gistId, String category) {
		if(categoryMap.containsKey(gistId)) {
			categoryMap.replace(gistId,category);
		}
		else {
			categoryMap.put(gistId, category);
		}
	}

	public void unMapCategory(String gistId) {
		categoryMap.remove(gistId);
	}

	public void deleteCategory(String category) {
		for(String gistId : categoryMap.keySet()) {
			if(categoryMap.get(gistId).equals(category)) {
				categoryMap.remove(gistId);
			}
		}
		categoryList.remove(category);
	}

	public void renameCategory(String oldName, String newName) {
		for(String gistId : categoryMap.keySet()){
			String category = categoryMap.get(gistId);
			if (category.equals(oldName)) {
				categoryMap.replace(gistId,newName);
			}
		}
		List<String> newList = new ArrayList<>();
		for(String category : categoryList) {
			if(category.equals(oldName)) newList.add(newName);
			else newList.add(category);
		}
		setList(newList);
	}

	public String getMappedCategory(String gistId) {
		return categoryMap.getOrDefault(gistId,"");
	}

	public Map<String,String> getFormattedCategoryMap() {
		Map<String,String> map = new HashMap<>();
		for(String gistIdString : categoryMap.keySet()) {
			String gistId = new String(gistIdString);
			String category = categoryMap.get(gistIdString);
			map.put(gistId,category);
		}
		return map;
	}

	public Map<String,String> getCategoryMap() {
		Map<String,String> map = new HashMap<>();
		for(String gistIdString : categoryMap.keySet()) {
			String gistId = new String(gistIdString);
			String category = categoryMap.get(gistIdString);
			map.put(gistId,category);
		}
		return map;
	}

	public Map<String,String> getMap() {
		return categoryMap;
	}


	public boolean hasData() {
		return categoryMap.size() > 0;
	}



}
