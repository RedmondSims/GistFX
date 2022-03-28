package com.redmondsims.gistfx.data.metadata;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class MetadataFile {

	public MetadataFile(Map<String, String> nameMap,
						CopyOnWriteArrayList<String> categoryList,
						ConcurrentHashMap<String, String> categoryMap,
						ConcurrentHashMap<String, String> descriptionMap,
						List<String> hostList) {
		this.nameMap        = nameMap;
		this.categoryList   = categoryList;
		this.categoryMap    = categoryMap;
		this.descriptionMap = descriptionMap;
		this.hostList       = hostList;
		date = new Date();
	}

	private final Map<String, String>               nameMap;
	private final CopyOnWriteArrayList<String>      categoryList;
	private final ConcurrentHashMap<String, String> categoryMap;
	private final ConcurrentHashMap<String, String> descriptionMap;
	private final List<String>                      hostList;
	private final Date                              date;
	private       Date                              lastReWrite;

	public static final String FileName = "GistFXMetadata.json";

	public static String GistDescription = com.redmondsims.gistfx.enums.Names.GITHUB_METADATA.Name();

	public void setLastReWrite() {
		lastReWrite = new Date();
	}

	public Date getLastReWrite() {
		return lastReWrite;
	}

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

	public Hosts getHosts() {
		Hosts hosts = new Hosts();
		hosts.setHostList(this.hostList);
		return hosts;
	}

	public Date getDate() {
		return date;
	}
}
