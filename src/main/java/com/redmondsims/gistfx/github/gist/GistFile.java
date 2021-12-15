package com.redmondsims.gistfx.github.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.ui.CodeEditor;
import com.redmondsims.gistfx.ui.alerts.CustomAlert;
import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.github.GHGistFile;

import java.util.Timer;
import java.util.TimerTask;

public class GistFile {

	private final StringProperty       filename      = new SimpleStringProperty();
	private final StringProperty       content       = new SimpleStringProperty();
	private final StringProperty       languageInfo  = new SimpleStringProperty();
	private final BooleanProperty      dirty         = new SimpleBooleanProperty(false);
	private final ObjectProperty<Node> graphicNode   = new SimpleObjectProperty<>();
	private final Integer              fileId;
	private       String               language;
	private       String               type;
	private       int                  size;
	private final String               gistId;
	private       String               lastLocalSave = "";
	private       String               oldFilename;
	private       long                 startTime     = System.currentTimeMillis();
	private       Timer                sqlSaveTimer  = new Timer();
	private boolean undoFile = false;

	public GistFile(String gistId, String filename, String content, Integer fileId, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.filename.setValue(filename);
		this.content.setValue(content);
		this.fileId = fileId;
		this.dirty.setValue(isDirty);
		init();
	}

	public GistFile(Integer fileId, String gistId, String filename, String content, boolean isDirty) { //used for new files being added to Gist
		this.gistId = gistId;
		this.filename.setValue(filename);
		this.content.setValue(content);
		this.fileId = fileId;
		this.dirty.setValue(isDirty);
		init();
	}

	public GistFile(GHGistFile ghGistFile, Integer fileId, String gistId, boolean isDirty) {
		this.gistId = gistId;
		this.filename.setValue(ghGistFile.getFileName());
		this.content.setValue(ghGistFile.getContent());
		this.language = ghGistFile.getLanguage();
		this.type = ghGistFile.getType();
		this.size = ghGistFile.getSize();
		this.fileId = fileId;
		this.dirty.setValue(isDirty);
		init();
	}

	private void checkSetFileFlag() {
		dirty.setValue(Action.fileIsDirty(fileId));
		Platform.runLater(() -> {
			if (dirty.getValue().equals(true) && LiveSettings.flagDirtyFiles) {
				Platform.runLater(() -> graphicNode.set(LiveSettings.getDirtyFlag()));
			}
			else {
				Platform.runLater(() -> graphicNode.set(null));
			}
		});
	}

	private void init() {
		dirty.addListener((observable, oldValue, newValue) -> {
			if (newValue != null && oldValue != null) {
				if (!oldValue.equals(newValue)) {
					Action.setDirtyFile(fileId, newValue);
					GistManager.handleButtons();
					checkSetFileFlag();
				}
			}
		});
	}

	public void refreshDirtyFlag() {
		checkSetFileFlag();
	}

	/*
		Private getters
	 */

	private String getFileExtension() {
		boolean noDot = !filename.getValue().contains(".");
		if (noDot) {
			return "";
		}
		return FilenameUtils.getExtension(filename.getValue());
	}

	/*
		Property binders and related
	 */

	ChangeListener<String> contentChangeListener = (observable, oldValue, newValue) -> {
		if(!newValue.equals(oldValue)) {
			startTime = System.currentTimeMillis();
			sqlSaveTimer.cancel();
			sqlSaveTimer = new Timer();
			sqlSaveTimer.scheduleAtFixedRate(sqlSave(),1000,100);
		}
	};

	private TimerTask sqlSave() {
		return new TimerTask() {
			@Override public void run() {
				if ((System.currentTimeMillis() - startTime) > 800) {
					if (!content.getValue().equals(lastLocalSave)) {
						lastLocalSave = content.getValue();
						if (!undoFile) dirty.setValue(true);
						undoFile = false;
						saveDataToSQL();
						sqlSaveTimer.cancel();
					}
				}
			}
		};
	}

	public void unbindAll() {
		content.unbind();
		languageInfo.setValue("");
		languageInfo.unbind();
		content.removeListener(contentChangeListener);
	}

	public void bindContentTo(StringProperty stringProperty) {
		stringProperty.setValue(content.getValue());
		content.bind(stringProperty);
		content.addListener(contentChangeListener);
	}

	public void addedToTree() {
		checkSetFileFlag();
	}

	/*
		Public setters
	 */

	public void undo() {
		String lastContent = Action.getGistFileContent(gistId,filename.getValue());
		if (lastContent == null) {
			CustomAlert.showWarning("There was a problem downloading from GitHub");
			return;
		}
		undoFile = true;
		CodeEditor.get().getEditor().getDocument().setText(lastContent);
		Action.save(this,false);
		dirty.setValue(false);
	}

	public void setActiveWith(StringProperty filenameProperty, StringProperty languageInfoProperty) {
		GistManager.unBindFileObjects();
		CodeEditor.bindDocumentTo(this);
		filenameProperty.bind(filename);
		languageInfoProperty.bind(this.languageInfo);
		CodeEditor.setLanguage(getFileExtension());
		languageInfo.setValue("Detected language: " + CodeEditor.getLanguage());
	}

	public void renameFile(String newFilename) {
		this.oldFilename = filename.getValue();
		filename.setValue(newFilename);
		CodeEditor.setLanguage(getFileExtension());
		Action.renameFile(this);
	}

	/*
		Public getters
	 */

	public String getContent() {
		return content.getValue();
	}

	public boolean isDirty() {
		return dirty.getValue();
	}

	public String getFilename() {
		return filename.getValue();
	}

	public String getGistId() {
		return gistId;
	}

	public Integer getFileId()     {return fileId;}

	public String getNewFilename() {return filename.getValue();}

	public String getOldFilename() {return oldFilename;}

	public String getToolTip() {
		return "Language: " + this.language + "\n" +
						 "Type: " + this.type + "\n" +
						 "Bytes: " + this.size;
	}

	public ObjectProperty<Node> getFlagNode() {return graphicNode;}

	/*
		SQL Actions
	 */

	private void saveDataToSQL() {
		Action.save(this, false);
	}

	private boolean updateGitHub() {
		return Action.save(this, true);
	}

	public boolean flushDirtyData() {
		boolean response = false;
		if (updateGitHub()) {
			saveDataToSQL();
			dirty.setValue(false);
			lastLocalSave = content.getValue();
			response         = true;
		}
		return response;
	}

	public void delete() {
		Action.delete(this);
	}

	@Override
	public String toString() {
		return filename.getValue();
	}
}
