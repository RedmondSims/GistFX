package com.redmondsims.gistfx.data.metadata;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class MetadataJson {

	public MetadataJson(Map<String, String> nameMap,
						CopyOnWriteArrayList<String> categoryList,
						ConcurrentHashMap<String, String> categoryMap,
						ConcurrentHashMap<String, String> descriptionMap,
						List<String> hostList) {
		this.nameMap        = nameMap;
		this.categoryList   = categoryList;
		this.categoryMap    = categoryMap;
		this.descriptionMap = descriptionMap;
		this.hostList       = hostList;
		timestamp           = new Timestamp(System.currentTimeMillis()).getTime();
	}

	private final Map<String, String>               nameMap;
	private final CopyOnWriteArrayList<String>      categoryList;
	private final ConcurrentHashMap<String, String> categoryMap;
	private final ConcurrentHashMap<String, String> descriptionMap;
	private final List<String>                      hostList;
	private       long                              timestamp;
	private       Timestamp                         lastReWrite;

	public static final String FileName = "GistFXMetadata.json";

	public static String GistDescription = com.redmondsims.gistfx.enums.Names.GITHUB_METADATA.Name();

	public void setLastGistRecreate() {
		lastReWrite = new Timestamp(System.currentTimeMillis());
	}

	public Date getLastGistRecreate() {
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

	public long getTimestamp() {
		return timestamp;
	}

	public void saveToSQL() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		timestamp = new Timestamp(System.currentTimeMillis()).getTime();
		String jsonString = gson.toJson(this);
		Action.saveMetadata(jsonString);
	}

	public void saveToSettings() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		timestamp = new Timestamp(System.currentTimeMillis()).getTime();
		String jsonString = gson.toJson(this);
		AppSettings.set().metadata(jsonString);
	}

	public void saveToGitHub(String gistId) {
		if(!LiveSettings.isOffline()) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			timestamp = new Timestamp(System.currentTimeMillis()).getTime();
			String jsonString = gson.toJson(this);
			Action.updateGistFile(gistId, FileName, jsonString);
		}
	}
}
