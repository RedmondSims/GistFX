package com.redmondsims.gistfx.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomDataFile {

	public CustomDataFile(Map<String, String> nameMap, List<String> categoryList, Map<String, String> categoryMap) {
		this.nameMap = nameMap;
		this.categoryList = categoryList;
		this.categoryMap = categoryMap;
		date = new Date();
	}

	private final Map<String, String> nameMap;
	private final List<String>        categoryList;
	private final Map<String, String> categoryMap;
	private final Date                date;

	public static final String FileName = "GistFXDataMap.json";

	public Categories getCategories() {
		Categories categories = new Categories();
		categories.setMap(categoryMap);
		categories.setList(categoryList);
		return categories;
	}

	public Names getNames() {
		Names names = new Names();
		names.setNameMap(nameMap);
		return names;
	}

	public Date getDate() {
		return date;
	}
}
