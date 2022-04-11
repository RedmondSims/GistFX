package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.FileState;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.javafx.CStringProperty;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.TreeIcons;
import com.redmondsims.gistfx.ui.gist.CodeEditor;
import com.redmondsims.gistfx.utils.Status;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FilenameUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.redmondsims.gistfx.enums.FileState.*;

public class GistFile {

	private final CStringProperty      fileName           = new CStringProperty();
	private final CStringProperty      liveVersion        = new CStringProperty();
	private final CStringProperty      gitHubVersion      = new CStringProperty();
	private final CStringProperty      localGitHubVersion = new CStringProperty();
	private final CStringProperty      sqlVersion         = new CStringProperty();
	private final CStringProperty      description        = new CStringProperty();
	private final ObjectProperty<Node> graphicNode        = new SimpleObjectProperty<>();
	private       FileState            lastFileState      = NORMAL;
	private       FileState            fileState          = NORMAL;
	private       StringProperty       monacoStringProperty;
	private final Integer              fileId;
	private       String               gistId;
	private       String               lastLocalSave;
	private       Timer                sqlSaveTimer       = new Timer();
	private       Timer                descriptionSaveTimer;

	public GistFile(Integer fileId, String gistId, String filename, String content, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.fileName.setValue(filename);
		this.liveVersion.setValue(content);
		this.fileId = fileId;
		if(isDirty) fileState = DIRTY;
		this.description.setValue(Action.getFileDescription(this));
		this.description.addListener(descriptionChangeListener);
		this.sqlVersion.setValue(content);
		lastLocalSave = content;
		refreshGraphicNode();
	}

	public void refreshGraphicNode() {
		Platform.runLater(() -> {
//			WindowManager.handleButtons();
			graphicNode.set(getIcon());
			if (!fileState.equals(lastFileState)) {
				lastFileState = fileState;
				WindowManager.refreshTreeIcons();
			}
		});
	}

	private ImageView getIcon() {
		return switch(fileState) {
			case DIRTY -> TreeIcons.getDirtyIcon();
			case CONFLICT -> TreeIcons.getConflictIcon();
			case NORMAL -> TreeIcons.getFileIcon();
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

	ChangeListener<String> codeEditorChangeListener = (observable, oldValue, newValue) -> {
		if(!newValue.equals(oldValue)) {
			if (Status.comparingLocalDataWithGitHub()) {
				if (preCheckTimer != null) preCheckTimer.cancel();
				preCheckTimer = new Timer();
				preCheckTimer.schedule(commitLater(newValue), 2000);
			}
			else {
				liveVersion.setValue(newValue);
			}
			if (liveVersion.isEqualTo(gitHubVersion)) {
				fileState = NORMAL;
				refreshGraphicNode();
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
					liveVersion.setValue(lastLocalSave);
				}
				else {
					liveVersion.setValue(newContent);
				}
				refreshGraphicNode();
			}
		};
	}

	private GistFile getThis() {return this;}

	private TimerTask localFileSave() {
		return new TimerTask() {
			@Override public void run() {
				if (liveVersion.notEqualTo(lastLocalSave)) {
					if (!Status.comparingLocalDataWithGitHub() && !fileState.equals(CONFLICT)) {
						if(liveVersion.notEqualTo(gitHubVersion.get())) fileState = DIRTY;
						lastLocalSave = liveVersion.get();
						Action.localFileSave(fileId, liveVersion.get(), isDirty());
						refreshGraphicNode();
					}
				}
			}
		};
	}

	public void unbindAll() {
		if(monacoStringProperty != null) monacoStringProperty.removeListener(codeEditorChangeListener);
		sqlSaveTimer.cancel();
	}

	public void setContentListener(StringProperty stringProperty) {
		stringProperty.setValue(liveVersion.get());
		stringProperty.removeListener(codeEditorChangeListener);
		stringProperty.addListener(codeEditorChangeListener);
		monacoStringProperty = stringProperty;
		sqlSaveTimer = new Timer();
		sqlSaveTimer.scheduleAtFixedRate(localFileSave(), 1000, 650);
	}

	public void addedToTree() {
		refreshGraphicNode();
	}

	public void reCheckWithGitHub() {
		gitHubVersion.setValue(Action.getGitHubFileContent(gistId, fileName.name()));
		if(fileState.equals(NORMAL)) {
			if (gitHubVersion.notEqualTo(liveVersion.get())) {
				fileState = CONFLICT;
			}
		}
	}

	private Timer compareTimer;
	public void compareWithGitHub(long queNumber) {
		new Thread(() -> {
			while(Status.comparingLocalDataWithGitHub()) Action.sleep(100);
			compareTimer = new Timer();
			compareTimer.schedule(compareWithGitHub(),10 * queNumber);
		}).start();
	}

	private TimerTask compareWithGitHub() {
		return new TimerTask() {
			@Override public void run() {
			gitHubVersion.setValue(Action.getGitHubFileContent(gistId, fileName.get()));
			localGitHubVersion.setValue(Action.getLocalGitHubVersion(fileId));
			if(gitHubVersion.notEqualTo(localGitHubVersion.get())) {
				fileState = CONFLICT;
				refreshGraphicNode();
			}
			}
		};
	}


	/**
		Public setters
	 */

	public void setDescription(String description) {
		this.description.setValue(description);
	}

	public void undo() {
		CodeEditor.get().getEditor().getDocument().setText(gitHubVersion.get());
		fileState = NORMAL;
		refreshGraphicNode();
	}

	public void setName(String newFilename) {
		String oldFilename = fileName.getValue();
		CodeEditor.setLanguage(getFileExtension());
		Action.renameFile(gistId, fileId, oldFilename, newFilename, liveVersion.getValue());
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

	public String getLiveVersion() {
		return liveVersion.get();
	}

	public String getGitHubVersion() {
		return gitHubVersion.get();
	}

	public boolean isDirty() {
		return fileState.equals(DIRTY);
	}

	public boolean isInConflict() {return fileState.equals(CONFLICT);}

	public boolean isAlertable() {
		return !fileState.equals(NORMAL);
	}

	public String getFilename() {
		return fileName.name();
	}

	public String getGistId() {
		return gistId;
	}

	public Integer getFileId()     {return fileId;}

	public String getLanguage() {
		return getFileExtension();
	}

	public ObjectProperty<Node> getGraphicNode() {return graphicNode;}

	public CStringProperty getNameProperty() {
		return fileName;
	}

	/**
		SQL Actions
	 */

	public void resolveConflict(Type choice) {
		if (choice.equals(Type.GITHUB)) {
			CodeEditor.get().getEditor().getDocument().setText(gitHubVersion.get());
		}
		else {
			Action.updateGistFile(fileId, getGistId(), getFilename(), liveVersion.get(), false);
		}
		CodeEditor.get().setDisable(false);
		refreshGraphicNode();
		fileState = NORMAL;
	}

	public boolean flushDirtyData() {
		if (!fileState.equals(CONFLICT)) {
			boolean dirty = LiveSettings.isOffline();
			if (Action.updateGistFile(fileId, getGistId(), getFilename(), liveVersion.get(), dirty)) {
				gitHubVersion.setValue(liveVersion.get());
				sqlVersion.setValue(liveVersion.get());
				refreshGraphicNode();
				fileState = NORMAL;
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return fileName.name();
	}
}
