package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.ui.gist.GistCategory;
import com.redmondsims.gistfx.ui.gist.Icons;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Gist {

	private final StringProperty name        = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();
	private final CBooleanProperty expandedProperty = new CBooleanProperty(false);
	private final String         gistURL;
	private final String         gistId;
	private       boolean        isPublic;
	private List<GistFile> fileList    = new ArrayList<>();
	private GistCategory   gistCategory;

	public Gist(String gistId, String name, String description, boolean isPublic, String gistURL) {
		this.gistId = gistId;
		this.name.setValue(name);
		this.description.setValue(description);
		this.isPublic = isPublic;
		this.gistURL  = gistURL;
		this.gistCategory = Action.getGistCategory(gistId);
		expandedProperty.addListener((observable, oldValue, newValue) -> {
			if(gistCategory != null) {
				if (newValue) {
					gistCategory.setExpanded(true);
				}
			}
		});
	}

	private String truncate(String string, int numChars, boolean ellipses) {
		int    len = string.length();
		String out = string.substring(0, Math.min(numChars, string.length()));
		if (len > numChars && ellipses) out += "...";
		return out;
	}

	/**
	 * Public Setters
	 */

	public void setGistCategory(GistCategory gistCategory) {
		this.gistCategory = gistCategory;
	}

	public void addFiles(List<GistFile> fileList) {
		this.fileList = fileList;
	}

	public void addFile(GistFile file) {
		fileList.add(file);
	}

	public void setExpanded(boolean expanded) {
		expandedProperty.setValue(expanded);
	}

	/**
	 * Public Getters
	 */

	public CBooleanProperty expandedProperty() {
		return expandedProperty;
	}

	public String getGistId()      {return gistId;}

	public String getDescription() {return description.get();}

	public String getURL()         {return gistURL;}

	public String getName()        {return name.getValue();}

	public StringProperty getDescriptionProperty() {
		return description;
	}

	public StringProperty getNameProperty() {
		return name;
	}

	public GistFile getFile(int fileId) {
		for (GistFile file : fileList){
			if (file.getFileId().equals(fileId)) return file;
		}
		return null;
	}

	public GistFile getFile(String fileName) {
		for (GistFile file : fileList) {
			if (file.getFilename().equals(fileName)) {
				return file;
			}
		}
		return null;
	}

	/**
	 * Changers to GitHub AND SQL
	 */

	public void setName(String newName) {
		name.setValue(newName);
		Action.setGistName(gistId, newName);
	}

	public boolean isPublic() {return isPublic;}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<GistFile> getFiles() {
		fileList.sort(Comparator.comparing(GistFile::getFilename));
		return fileList;
	}

	public void deleteFile(String filename) {
		fileList.removeIf(file -> file.getFilename().equals(filename));
	}

	public void setDescription(String description) {
		this.description.setValue(description);
		Action.updateGitHubGistDescription(this);
		Action.updateLocalGistDescription(this);
	}

	public int getForkCount() {return Action.getForkCount(gistId);}

	@Override
	public String toString() {return truncate(name.getValue().replaceAll("\\n", " "), 30, true);}
}
