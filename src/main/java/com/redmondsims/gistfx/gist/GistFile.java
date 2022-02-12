package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.javafx.CStringProperty;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.CodeEditor;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FilenameUtils;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GistFile {

	private final CStringProperty      file               = new CStringProperty();
	private final CStringProperty      content            = new CStringProperty();
	private final CStringProperty      languageInfo       = new CStringProperty();
	private final CBooleanProperty     dirty              = new CBooleanProperty(false);
	private final CBooleanProperty     conflict           = new CBooleanProperty(false);
	private final ObjectProperty<Node> graphicNode        = new SimpleObjectProperty<>();
	private final ImageView            conflictFlag       = LiveSettings.getConflictFlag();
	private       boolean              comparedWithGitHub = false;
	private       StringProperty       monacoStringProperty;
	private final Integer              fileId;
	private final String               gistId;
	private       String               lastLocalSave;
	private       String               oldFilename;
	private       String               gitHubVersion;
	private       Timer                sqlSaveTimer       = new Timer();
	private       Date                 uploadDate;
	private       boolean              dataNotCommitted   = false;
	private       long                 lastSaveTime       = System.currentTimeMillis();

	public GistFile(Integer fileId, String gistId, String filename, String content, Date uploadDate, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.file.setValue(filename);
		this.content.setValue(content);
		this.fileId = fileId;
		this.uploadDate = uploadDate;
		this.dirty.setValue(isDirty);
		lastLocalSave = content;
		dirty.setChangeListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != null) {
				if (!newValue.equals(oldValue)) {
					Action.setDirtyFile(fileId, newValue);
					refreshFileFlag();
				}
			}
		});
	}

	public void refreshFileFlag() {
		Platform.runLater(() -> {
			if(dirty.isFalse()) dirty.setValue(Action.fileIsDirty(fileId));
			if (dirty.isTrue() && LiveSettings.flagDirtyFiles()) graphicNode.set(LiveSettings.getDirtyFlag());
			else if (conflict.isTrue()) graphicNode.set(conflictFlag);
			else graphicNode.set(null);
			WindowManager.handleButtons();
		});
	}

	/**
		Private getters
	 */

	private String getFileExtension() {
		boolean hasDot = file.name().contains(".");
		if (hasDot) {
			return FilenameUtils.getExtension(file.name());
		}
		return "";
	}

	/**
		Property binders and related
	 */


	ChangeListener<String> contentChangeListener = (observable, oldValue, newValue) -> {
		if (conflict.isTrue()) CodeEditor.get().getEditor().getDocument().setText(lastLocalSave);
		else if(!newValue.equals(oldValue)) {
			content.setValue(newValue);
			lastSaveTime = System.currentTimeMillis();
			dataNotCommitted = true;
		}
	};

	private GistFile getThis() {return this;}

	private TimerTask localFileSave() {
		return new TimerTask() {
			@Override public void run() {
				long now = System.currentTimeMillis();
				boolean time = (now - lastSaveTime) > 1300;
				if (time && dataNotCommitted) {
					dirty.setValue(content.notEqualTo(gitHubVersion));
					lastLocalSave = content.get();
					Action.localFileSave(getThis());
					dataNotCommitted = false;
				}
			}
		};
	}

	public void unbindAll() {
		languageInfo.setValue("");
		languageInfo.unbind();
		if(monacoStringProperty != null) monacoStringProperty.removeListener(contentChangeListener);
		sqlSaveTimer.cancel();
	}

	public void setContentListener(StringProperty stringProperty) {
		if (!comparedWithGitHub) {
			new Thread(() -> {
				gitHubVersion = Action.getGitHubFileContent(gistId, file.name());
				WindowManager.setConflict(this,gitHubVersionConflict());
				comparedWithGitHub = true;
			}).start();
		}
		stringProperty.setValue(content.get());
		stringProperty.removeListener(contentChangeListener);
		stringProperty.addListener(contentChangeListener);
		monacoStringProperty = stringProperty;
		sqlSaveTimer = new Timer();
		sqlSaveTimer.scheduleAtFixedRate(localFileSave(), 1000, 500);
	}

	public void addedToTree() {
		refreshFileFlag();
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

	public CStringProperty getContentProperty() {
		return content;
	}

	public String getGitHubVersion() {
		return gitHubVersion;
	}

	public boolean isDirty() {
		return dirty.getValue();
	}

	public boolean isInConflict() {return conflict.isTrue() && dirty.isFalse();}

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

	public String getLanguage() {
		return getFileExtension();
	}

	public ObjectProperty<Node> getFlagNode() {return graphicNode;}

	private Type gitHubVersionConflict() {
		Type result = Type.OK;
		if(dirty.isTrue()) result = Type.DIRTY;
		else {
			conflict.setValue(content.notEqualTo(gitHubVersion));
			if (conflict.isTrue()) result = Type.CONFLICT;
		}
		refreshFileFlag();
		return result;
	}

	/**
		SQL Actions
	 */

	public void resolveConflict(Type choice) {
		conflict.setFalse();
		if (choice.equals(Type.GITHUB)) {
			CodeEditor.get().getEditor().getDocument().setText(gitHubVersion);
		}
		else {
			Action.updateGistFile(getGistId(),getFilename(),getContent());
		}
		refreshFileFlag();
	}

	public boolean flushDirtyData() {
		boolean response = false;
		if (conflict.isFalse()) {
			uploadDate = new Date(System.currentTimeMillis());
			if (Action.updateGistFile(this)) {
				lastLocalSave = content.get();
				gitHubVersion = content.get();
				dirty.setFalse();
				conflict.setFalse();
				refreshFileFlag();
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
