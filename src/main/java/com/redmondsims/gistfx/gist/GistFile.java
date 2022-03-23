package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.javafx.CBooleanProperty;
import com.redmondsims.gistfx.javafx.CStringProperty;
import com.redmondsims.gistfx.ui.gist.CodeEditor;
import com.redmondsims.gistfx.ui.gist.Icons;
import com.redmondsims.gistfx.utils.Status;
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

	private final CStringProperty      fileName           = new CStringProperty();
	private final CStringProperty      content            = new CStringProperty();
	private final CStringProperty      description        = new CStringProperty();
	private final CBooleanProperty     dirty              = new CBooleanProperty(false);
	private final CBooleanProperty     conflict           = new CBooleanProperty(false);
	private final ObjectProperty<Node> graphicNode        = new SimpleObjectProperty<>();
	private final ImageView            conflictFlag       = Icons.getConflictFlag();
	private final ImageView            dirtyFlag       	  = Icons.getDirtyFlagIcon();
	private       ImageView            fileIcon       	  = Icons.getFileIcon();
	private       StringProperty       monacoStringProperty;
	private final Integer              fileId;
	private       String               gistId;
	private       String               lastLocalSave;
	private       String               gitHubVersion;
	private       Timer                sqlSaveTimer       = new Timer();
	private       Timer                descriptionSaveTimer;
	private       Date                 uploadDate;
	private       boolean              dataNotCommitted   = false;
	private       long                 lastSaveTime       = System.currentTimeMillis();

	public GistFile(Integer fileId, String gistId, String filename, String content, Date uploadDate, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.fileName.setValue(filename);
		this.content.setValue(content);
		this.fileId = fileId;
		this.uploadDate = uploadDate;
		this.dirty.setValue(isDirty);
		this.description.setValue(Action.getFileDescription(this));
		this.description.addListener(descriptionChangeListener);
		lastLocalSave = content;
		dirty.setChangeListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != null) {
				if (!newValue.equals(oldValue)) {
					Action.setDirtyFile(fileId, newValue);
					refreshGraphicNode();
				}
			}
		});
		new Thread(() -> {
			if(dirty.isFalse()) {
				Status.register(fileId);
				while(Status.getState().equals(State.LOADING)) Action.sleep(100);
				gitHubVersion = Action.getLocalGitHubFileContent(gistId, fileName.name());
				conflict.setValue(this.content.notEqualTo(gitHubVersion));
				Status.unRegister(fileId);
			}
			refreshGraphicNode();
		}).start();
	}

	public void refreshGraphicNode() {
		Platform.runLater(() -> {
			if (dirty.isFalse()) dirty.setValue(Action.fileIsDirty(fileId));
			int     option    = 0;
			boolean dirty     = this.dirty.isTrue();
			boolean conflict  = this.conflict.isTrue();
			if(conflict) option = 2;
			else if (dirty) option = 1;
			switch (option) {
				case 0 -> fileIcon = Icons.getFileIcon();
				case 1 -> fileIcon = dirtyFlag;
				case 2 -> fileIcon = conflictFlag;
			}
			graphicNode.set(fileIcon);
			if(option > 0 || this.dirty.changed() || this.conflict.changed()) {
				WindowManager.refreshFileIcons();
				WindowManager.handleButtons();
			}
		});
	}

	/**
		Private getters
	 */

	private String getFileExtension() {
		boolean hasDot = fileName.name().contains(".");
		if (hasDot) {
			return FilenameUtils.getExtension(fileName.name());
		}
		return "";
	}

	/**
		Property binders and related
	 */

	private Timer preCheckTimer;

	ChangeListener<String> contentChangeListener = (observable, oldValue, newValue) -> {
		if (conflict.isTrue()) CodeEditor.get().getEditor().getDocument().setText(lastLocalSave);
		else if(!newValue.equals(oldValue)) {
			if (Status.isComparing()) {
				if (preCheckTimer != null) preCheckTimer.cancel();
				preCheckTimer = new Timer();
				preCheckTimer.schedule(commitLater(newValue),2000);
			}
			else {
				content.setValue(newValue);
				lastSaveTime = System.currentTimeMillis();
				dataNotCommitted = true;
			}
		}
	};

	ChangeListener<String> descriptionChangeListener = (observable, oldValue, newValue) -> {
		if(!newValue.equals(oldValue)) {
			if (descriptionSaveTimer != null) descriptionSaveTimer.cancel();
			descriptionSaveTimer = new Timer();
			descriptionSaveTimer.schedule(saveDescription(newValue),2000);
		}
	};

	private TimerTask saveDescription(String description) {
		return new TimerTask() {
			@Override public void run() {
				Action.setFileDescription(getThis(), description);
			}
		};
	}

	private TimerTask commitLater(String newContent) {
		return new TimerTask() {
			@Override public void run() {
				while(Status.isComparing()) Action.sleep(100);
				if (conflict.isTrue()) {
					CodeEditor.get().getEditor().getDocument().setText(lastLocalSave);
					content.setValue(lastLocalSave);
					dataNotCommitted = false;
				}
				else {
					content.setValue(newContent);
					lastSaveTime = System.currentTimeMillis();
					dataNotCommitted = true;
				}
				refreshGraphicNode();
			}
		};
	}

	private GistFile getThis() {return this;}

	private TimerTask localFileSave() {
		return new TimerTask() {
			@Override public void run() {
				long now = System.currentTimeMillis();
				if (dataNotCommitted) {
					if (!Status.isComparing() && conflict.isFalse()) {
						dirty.setValue(content.notEqualTo(gitHubVersion));
						lastLocalSave = content.get();
						Action.localFileSave(getThis());
						dataNotCommitted = false;
						refreshGraphicNode();
						WindowManager.refreshBranch(getThis());
					}
				}
			}
		};
	}

	public void unbindAll() {
		if(monacoStringProperty != null) monacoStringProperty.removeListener(contentChangeListener);
		sqlSaveTimer.cancel();
	}

	public void setContentListener(StringProperty stringProperty) {
		stringProperty.setValue(content.get());
		stringProperty.removeListener(contentChangeListener);
		stringProperty.addListener(contentChangeListener);
		monacoStringProperty = stringProperty;
		sqlSaveTimer = new Timer();
		sqlSaveTimer.scheduleAtFixedRate(localFileSave(), 1000, 650);
	}

	public void addedToTree() {
		refreshGraphicNode();
	}

	public void reCheckWithGitHub() {
		gitHubVersion = Action.getGitHubFileContent(gistId, fileName.name());
		conflict.setValue(this.content.notEqualTo(gitHubVersion));
		if(conflict.isTrue()) resolveConflict(Type.LOCAL);
	}

	/**
		Public setters
	 */

	public void setDescription(String description) {
		this.description.setValue(description);
	}

	public void undo() {
		CodeEditor.get().getEditor().getDocument().setText(gitHubVersion);
	}

	public void setName(String newFilename) {
		String oldFilename = fileName.getValue();
		CodeEditor.setLanguage(getFileExtension());
		Action.renameFile(gistId,fileId,oldFilename,newFilename,content.getValue());
		Platform.runLater(() -> fileName.setValue(newFilename));
	}

	public void setActive() {
		GistManager.unBindFileObjects();
		CodeEditor.bindDocumentTo(this);
		CodeEditor.setLanguage(getFileExtension());
	}

	public void setGistId(String gistId) {
		this.gistId = gistId;
	}

	/**
		Public getters
	 */

	public String getDescription() {
		return this.description.getValue();
	}

	public ImageView getGraphic() {
		refreshGraphicNode();
		return fileIcon;
	}

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
		return fileName.name();
	}

	public Date getUploadDate() {return uploadDate;}

	public String getGistId() {
		return gistId;
	}

	public Integer getFileId()     {return fileId;}

	public String getNewFilename() {return fileName.name();}

	public String getLanguage() {
		return getFileExtension();
	}

	public ObjectProperty<Node> getGraphicNode() {return graphicNode;}

	private Type gitHubVersionConflict() {
		Type result = Type.OK;
		if(dirty.isTrue()) result = Type.DIRTY;
		else {
			conflict.setValue(content.notEqualTo(gitHubVersion));
			if (conflict.isTrue()) result = Type.CONFLICT;
		}
		refreshGraphicNode();
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
		refreshGraphicNode();
	}

	public boolean flushDirtyData() {
		boolean response = false;
		if (conflict.isFalse()) {
			uploadDate = new Date(System.currentTimeMillis());
			Action.updateGistFile(this);
			lastLocalSave = content.get();
			gitHubVersion = content.get();
			dirty.setFalse();
			conflict.setFalse();
			refreshGraphicNode();
			response = true;
		}
		return response;
	}

	@Override
	public String toString() {
		return fileName.name();
	}
}
