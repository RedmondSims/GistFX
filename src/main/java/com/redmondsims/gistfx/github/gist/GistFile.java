package com.redmondsims.gistfx.github.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.javafx.controls.CBooleanProperty;
import com.redmondsims.gistfx.javafx.controls.CStringProperty;
import com.redmondsims.gistfx.ui.CodeEditor;
import com.redmondsims.gistfx.ui.enums.Type;
import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.github.GHGistFile;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GistFile {

	private final CStringProperty      file          = new CStringProperty();
	private final CStringProperty      content       = new CStringProperty();
	private final CStringProperty      languageInfo  = new CStringProperty();
	private final CBooleanProperty     dirty         = new CBooleanProperty(false);
	private final CBooleanProperty     conflict      = new CBooleanProperty(false);
	private final ObjectProperty<Node> graphicNode   = new SimpleObjectProperty<>();
	private       StringProperty monacoStringProperty;
	private final Integer              fileId;
	private       String               language;
	private       String               type;
	private       int                  size;
	private final String               gistId;
	private       String               lastLocalSave = "";
	private       String          oldFilename;
	private       String gitHubVersion;
	private       long            startTime     = System.currentTimeMillis();
	private       Timer                sqlSaveTimer  = new Timer();
	private       Date                 uploadDate;

	public GistFile(String gistId, String file, String content, Integer fileId, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.file.setValue(file);
		this.content.setValue(content);
		this.fileId = fileId;
		this.dirty.setValue(isDirty);
		init();
	}

	public GistFile(Integer fileId, String gistId, String file, String content, Date uploadDate, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.file.setValue(file);
		this.content.setValue(content);
		this.fileId = fileId;
		this.uploadDate = uploadDate;
		this.dirty.setValue(isDirty);
		init();
	}

	public GistFile(GHGistFile ghGistFile, Integer fileId, String gistId, boolean isDirty) {
		this.gistId = gistId;
		this.file.setValue(ghGistFile.getFileName());
		this.content.setValue(ghGistFile.getContent());
		this.language = ghGistFile.getLanguage();
		this.type = ghGistFile.getType();
		this.size = ghGistFile.getSize();
		this.fileId = fileId;
		this.dirty.setValue(isDirty);
		init();
	}

	private void checkSetFileFlag() {
		Platform.runLater(() -> {
			graphicNode.set(null);
			if(dirty.isFalse()) dirty.setValue(Action.fileIsDirty(fileId));
			boolean fileDirty = dirty.isTrue();
			boolean fileInConflict = conflict.isTrue();
			if (!fileInConflict) {
				if (fileDirty && LiveSettings.flagDirtyFiles) graphicNode.set(LiveSettings.getDirtyFlag());
			}
			else graphicNode.set(LiveSettings.getConflictFlag());
			GistManager.handleButtons();
		});
	}

	private void init() {
		dirty.setChangeListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != null) {
				if (!oldValue.equals(newValue)) {
					Action.setDirtyFile(fileId, newValue);
					checkSetFileFlag();
				}
			}
		});
	}

	public void refreshDirtyFlag() {
		checkSetFileFlag();
	}

	/**
		Private getters
	 */

	private String getFileExtension() {
		boolean noDot = !file.name().contains(".");
		if (noDot) {
			return "";
		}
		return FilenameUtils.getExtension(file.name());
	}

	/**
		Property binders and related
	 */

	ChangeListener<String> contentChangeListener = (observable, oldValue, newValue) -> {
		if(!newValue.equals(oldValue)) {
			content.setValue(newValue);
			startTime = System.currentTimeMillis();
			sqlSaveTimer.cancel();
			sqlSaveTimer = new Timer();
			sqlSaveTimer.scheduleAtFixedRate(localFileSave(), 1000, 100);
		}
	};

	private GistFile getThis() {return this;}

	private TimerTask localFileSave() {
		return new TimerTask() {
			@Override public void run() {
				if ((System.currentTimeMillis() - startTime) > 800) {
					if (content.notEqualTo(lastLocalSave)) {
						lastLocalSave = content.get();
						dirty.setValue(content.notEqualTo(gitHubVersion));
						Action.saveToSQL(getThis());
						sqlSaveTimer.cancel();
					}
				}
			}
		};
	}

	public void unbindAll() {
		languageInfo.setValue("");
		languageInfo.unbind();
		if(monacoStringProperty != null) monacoStringProperty.removeListener(contentChangeListener);
	}

	public void setContentListener(StringProperty stringProperty) {
		stringProperty.setValue(content.get());
		stringProperty.removeListener(contentChangeListener);
		stringProperty.addListener(contentChangeListener);
		monacoStringProperty = stringProperty;
	}

	public void addedToTree() {
		checkSetFileFlag();
	}

	/**
		Public setters
	 */

	public void undo() {
		CodeEditor.get().getEditor().getDocument().setText(gitHubVersion);
	}

	public void setActiveWith(StringProperty filenameProperty, StringProperty languageInfoProperty) {
		GistManager.unBindFileObjects();
		CodeEditor.bindDocumentTo(this);
		filenameProperty.bind(file.getProperty());
		languageInfoProperty.bind(languageInfo.getProperty());
		CodeEditor.setLanguage(getFileExtension());
		languageInfo.setValue("Detected language: " + CodeEditor.getLanguage());
	}

	public void setGitHubVersion(String gitHubVersion) {
		this.gitHubVersion = gitHubVersion;
	}

	public void renameFile(String newFilename) {
		this.oldFilename = file.name();
		file.setValue(newFilename);
		CodeEditor.setLanguage(getFileExtension());
		Action.renameFile(this);
	}

	/**
		Public getters
	 */

	public String getContent() {
		return content.get();
	}

	public String getGitHubVersion() {
		return gitHubVersion;
	}

	public boolean isDirty() {
		return dirty.getValue();
	}

	public boolean isInConflict() {return conflict.getValue();}

	public String getFilename() {
		return file.name();
	}

	public Date getUploadDate() {return uploadDate;}

	public String getGistId() {
		return gistId;
	}

	public Integer getFileId()     {return fileId;}

	public String getNewFilename() {return file.name();}

	public String getOldFilename() {return oldFilename;}

	public String getToolTip() {
		return "Language: " + this.language + "\n" +
						 "Type: " + this.type + "\n" +
						 "Bytes: " + this.size;
	}

	public ObjectProperty<Node> getFlagNode() {return graphicNode;}

	public Type gitHubVersionConflict() {
		Type result = Type.OK;
		if (content.notEqualTo(gitHubVersion)) {
			if (dirty.isTrue()) {
				result = Type.DIRTY;
				checkSetFileFlag();
			}
			else {
				Date lastGitUpdate = Action.getGistUpdateDate(gistId);
				long gitTime = lastGitUpdate.getTime();
				long localTime = uploadDate.getTime();
				if (gitTime > localTime) {
					result = Type.CONFLICT;
					conflict.setTrue();
					checkSetFileFlag();
				}
			}
		}
		else {
			dirty.setFalse();
			checkSetFileFlag();
		}
		return result;
	}

	/**
		SQL Actions
	 */

	public void resolveConflict(Type choice) {
		if (choice.equals(Type.GITHUB)) {
			CodeEditor.get().getEditor().getDocument().setText(gitHubVersion);
		}

		if (choice.equals(Type.LOCAL)) {
			Action.updateGistFile(getGistId(),getFilename(),getContent());
		}
		conflict.setFalse();
		checkSetFileFlag();
	}

	public boolean flushDirtyData() {
		boolean response = conflict.isTrue();
		if (conflict.isFalse()) {
			uploadDate = new Date(System.currentTimeMillis());
			if (Action.updateGistFile(this)) {
				lastLocalSave = content.get();
				gitHubVersion = content.get();
				dirty.setFalse();
				conflict.setFalse();
				checkSetFileFlag();
				response = true;
			}
		}
		return response;
	}

	@Override
	public String toString() {
		return file.name();
	}
}
