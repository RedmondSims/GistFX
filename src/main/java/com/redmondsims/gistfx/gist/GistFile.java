package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.FileState;
import com.redmondsims.gistfx.enums.Type;
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

import static com.redmondsims.gistfx.enums.FileState.*;

public class GistFile {

	private final CStringProperty      fileName         = new CStringProperty();
	private final CStringProperty      content          = new CStringProperty();
	private final CStringProperty      description      = new CStringProperty();
	private final ObjectProperty<Node> graphicNode   = new SimpleObjectProperty<>();
	private       FileState            lastFileState = NORMAL;
	private       FileState            fileState     = NORMAL;
	private       StringProperty       monacoStringProperty;
	private final Integer              fileId;
	private       String               gistId;
	private       String               lastLocalSave;
	private       String               gitHubVersion;
	private       Timer                sqlSaveTimer     = new Timer();
	private       Timer                descriptionSaveTimer;
	private       Date                 uploadDate;
	private       boolean              dataNotCommitted = false;

	public GistFile(Integer fileId, String gistId, String filename, String content, Date uploadDate, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.fileName.setValue(filename);
		this.content.setValue(content);
		this.fileId = fileId;
		this.uploadDate = uploadDate;
		if(isDirty) fileState = DIRTY;
		this.description.setValue(Action.getFileDescription(this));
		this.description.addListener(descriptionChangeListener);
		lastLocalSave = content;
		refreshGraphicNode();
	}

	public void refreshGraphicNode() {
		Platform.runLater(() -> {
			WindowManager.handleButtons();
			graphicNode.set(getIcon());
			if (!fileState.equals(lastFileState)) {
				lastFileState = fileState;
				WindowManager.refreshFileIcons();
			}
		});
	}

	private ImageView getIcon() {
		return switch(fileState) {
			case DIRTY -> Icons.getDirtyIcon();
			case CONFLICT -> Icons.getConflictIcon();
			case NORMAL -> Icons.getFileIcon();
		};
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
		if(!newValue.equals(oldValue)) {
			if (Status.comparingLocalDataWithGitHub()) {
				if (preCheckTimer != null) preCheckTimer.cancel();
				preCheckTimer = new Timer();
				preCheckTimer.schedule(commitLater(newValue),2000);
			}
			else {
				content.setValue(newValue);
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
				while(Status.comparingLocalDataWithGitHub()) Action.sleep(100);
				if (fileState.equals(CONFLICT)) {
					CodeEditor.get().getEditor().getDocument().setText(lastLocalSave);
					content.setValue(lastLocalSave);
					dataNotCommitted = false;
				}
				else {
					content.setValue(newContent);
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
				if (dataNotCommitted) {
					if (!Status.comparingLocalDataWithGitHub() && !fileState.equals(CONFLICT)) {
						if(content.notEqualTo(gitHubVersion)) fileState = DIRTY;
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
		if(fileState.equals(NORMAL)) {
			if (!gitHubVersion.equals(content.get())) {
				fileState = CONFLICT;
			}
		}
	}

	public void compareWithGitHub(String gitHubVersion) {
		this.gitHubVersion = gitHubVersion;
		if(content.notEqualTo(gitHubVersion) && fileState.equals(NORMAL)) {
			fileState = CONFLICT;
			refreshGraphicNode();
		}
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
		CodeEditor.get().setDisable(fileState.equals(CONFLICT));
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
		return fileState.equals(DIRTY);
	}

	public boolean isInConflict() {return fileState.equals(CONFLICT);}

	public String getFilename() {
		return fileName.name();
	}

	public Date getUploadDate() {return uploadDate;}

	public String getGistId() {
		return gistId;
	}

	public Integer getFileId()     {return fileId;}

	public String getLanguage() {
		return getFileExtension();
	}

	public ObjectProperty<Node> getGraphicNode() {return graphicNode;}

	/**
		SQL Actions
	 */

	public void resolveConflict(Type choice) {
		fileState = NORMAL;
		if (choice.equals(Type.GITHUB)) {
			CodeEditor.get().getEditor().getDocument().setText(gitHubVersion);
		}
		else {
			Action.updateGistFile(getGistId(),getFilename(),getContent());
		}
		refreshGraphicNode();
	}

	public boolean flushDirtyData() {
		if (!fileState.equals(CONFLICT)) {
			uploadDate = new Date(System.currentTimeMillis());
			Action.updateGistFile(this);
			lastLocalSave = content.get();
			gitHubVersion = content.get();
			fileState = NORMAL;
			refreshGraphicNode();
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return fileName.name();
	}
}
