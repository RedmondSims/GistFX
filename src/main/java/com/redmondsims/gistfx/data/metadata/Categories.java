package com.redmondsims.gistfx.data.metadata;

import com.redmondsims.gistfx.alerts.CustomAlert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


class Categories {

	private ConcurrentHashMap<String,String> categoryMap  = new ConcurrentHashMap<>();
	private CopyOnWriteArrayList<String>     categoryList = new CopyOnWriteArrayList<>();

	public void addCategory(String category) {
		if (!categoryList.contains(category)) {
			categoryList.add(category);
		}
	}

	public void setList(CopyOnWriteArrayList<String> categoryList) {
		this.categoryList = categoryList;
	}

	public void setMap(ConcurrentHashMap<String,String> categoryMap) {
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
		for(String categoryName : categoryMap.values()) {
			if(categoryName.equals(newName)) {
				CustomAlert.showWarning("Category " +newName+" already exists.");
				return;
			}
		}
		for(String gistId : categoryMap.keySet()){
			String category = categoryMap.get(gistId);
			if (category.equals(oldName)) {
				categoryMap.replace(gistId,newName);
			}
		}
		CopyOnWriteArrayList<String> newList = new CopyOnWriteArrayList<>();
		for(String category : categoryList) {
			if(category.equals(oldName)) newList.add(newName);
			else newList.add(category);
		}
		setList(newList);
	}

	public String getGistCategoryName(String gistId) {
		return categoryMap.getOrDefault(gistId,"");
	}

	public Map<String,String> getFormattedCategoryMap() {
		Map<String,String> map = new HashMap<>();
		for(String gistId : categoryMap.keySet()) {
			String category = categoryMap.get(gistId);
			map.put(gistId, category);
		}
		return map;
	}

	public Map<String,String> getCategoryMap() {
		Map<String,String> map = new HashMap<>();
		for(String gistId : categoryMap.keySet()) {
			String category = categoryMap.get(gistId);
			map.put(gistId, category);
		}
		return map;
	}

	public List<String> getGistIdsInCategory(String category) {
		List<String> gistIdList = new ArrayList<>();
		for(String gistId : categoryMap.keySet()) {
			String gistCategory = categoryMap.get(gistId);
			if(gistCategory.equals(category)) {
				gistIdList.add(gistId);
			}
		}
		return gistIdList;
	}

	public ConcurrentHashMap<String,String> getMap() {
		return categoryMap;
	}

	public CopyOnWriteArrayList<String> getList() {
		categoryList.sort(Comparator.comparing(String::toString));
		return categoryList;
	}
}
