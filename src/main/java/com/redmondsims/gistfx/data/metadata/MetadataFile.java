package com.redmondsims.gistfx.data.metadata;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class MetadataFile {

	public MetadataFile(Map<String, String> nameMap,
						CopyOnWriteArrayList<String> categoryList,
						ConcurrentHashMap<String, String> categoryMap,
						ConcurrentHashMap<String, String> descriptionMap) {
		this.nameMap        = nameMap;
		this.categoryList   = categoryList;
		this.categoryMap    = categoryMap;
		this.descriptionMap = descriptionMap;
		date = new Date();
	}

	private final Map<String, String>               nameMap;
	private final CopyOnWriteArrayList<String>      categoryList;
	private final ConcurrentHashMap<String, String> categoryMap;
	private final ConcurrentHashMap<String, String> descriptionMap;
	private final Date                              date;

	public static final String FileName = "GistFXMetadata.json";

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

	public FileDescriptions getFileDescriptions() {
		FileDescriptions descriptions = new FileDescriptions();
		descriptions.setDescriptionMap(this.descriptionMap);
		return descriptions;
	}

	public Date getDate() {
		return date;
	}
}
