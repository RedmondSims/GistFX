package com.redmondsims.gistfx.data.metadata;

import javafx.collections.FXCollections;
import java.util.*;

class Hosts {

	private List<String> hostList = new ArrayList<>();

	public void addHost(String host) {
		if (!hostList.contains(host)) {
			hostList.add(host);
		}
	}

	public void setHostList(List<String> hostList) {
		if (hostList != null) this.hostList = hostList;
	}

	public Collection<String> getCollection() {
		Collections.sort(hostList);
		return FXCollections.observableArrayList(hostList);
	}

	public List<String> getList() {
		Collections.sort(hostList);
		return hostList;
	}

	public void removeHost(String host) {
		hostList.remove(host);
	}

	public void renameHost(String oldName, String newName) {
		removeHost(oldName);
		addHost(newName);
	}

	public boolean hasData() {
		return hostList.size() > 0;
	}

}
