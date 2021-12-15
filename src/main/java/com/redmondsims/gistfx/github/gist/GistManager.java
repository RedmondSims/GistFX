package com.redmondsims.gistfx.github.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.ui.GistWindow;
import com.redmondsims.gistfx.ui.preferences.AppSettings;
import com.redmondsims.gistfx.ui.preferences.LiveSettings;
import com.redmondsims.gistfx.ui.preferences.UISettings;
import javafx.application.Platform;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.io.IOException;
import java.util.*;

/*
 *  This class is used mainly by GistWindow but in some cases also by GistFile.
 * It manages all of the Gist and GistFile objects that are instantiated so that
 * GistWindow must gain access to those objects through GistManager. Then if any
 * access needs to happen to GitHub or SQLite, GistManager uses the Action class
 * which is the front end for all lower level data classes.
 *
 * Other classes interact with Action as well, but minimally.
 */

public class GistManager {

	private static final UISettings.DataSource LOCAL = UISettings.DataSource.LOCAL;
	private static       Map<String, Gist>     gistMap;
	private static GistWindow gistWindow;

	public static void startFromGit(Map<String, GHGist> ghGistMap, boolean fromReload) {
		Action.cleanDatabase();
		Action.loadNameMapIntoDatabase(); //This restores the names that have been assigned to the Gists
		Map<String, Gist> gistMap      = new HashMap<>();
		String            gistId, name, description, filename, url, content;
		String            jsonGistName = Action.getJSonGistName();
		boolean           isPublic;
		int               fileId;
		List<GistFile>    fileList;
		for (GHGist ghGist : ghGistMap.values()) {
			gistId      = ghGist.getGistId();
			name        = Action.getGistName(ghGist); //This returns the name from the Json file which is now in the NameMap table.
			description = ghGist.getDescription();
			isPublic    = ghGist.isPublic();
			url         = ghGist.getHtmlUrl().toString();
			fileList    = new ArrayList<>();
			if (!description.equals(jsonGistName)) {
				for (GHGistFile file : ghGist.getFiles().values()) {
					filename = file.getFileName();
					content  = file.getContent();
					fileId   = Action.newSQLFile(gistId, filename, content);
					GistFile gistFile = new GistFile(file, fileId, gistId,false);
					fileList.add(gistFile);
				}
				Gist gist = new Gist(gistId, name, description, isPublic, url);
				Action.addGistToSQL(gist);
				gist.addFiles(fileList);
				gistMap.put(gistId, gist);
			}
		}
		GistManager.setGistMap(gistMap);
		Platform.runLater(() -> new GistWindow().showMainWindow(fromReload));
		if (AppSettings.getFirstRun()) {
			AppSettings.setFirstRun(false);
		}
		AppSettings.setLoadSource(LOCAL);
		LiveSettings.dataSource = LOCAL;
	}

	public static void startFromDatabase() {
		Action.loadBestNameMap();
		Map<String, Gist> gistMap = Action.getGistMap();
		Platform.runLater(() -> new GistWindow().showMainWindow(false));
		GistManager.setGistMap(gistMap);
	}

	public static Collection<Gist> getGists() {return gistMap.values();}

	public static void setGistMap(Map<String, Gist> map) {
		gistMap = map;
	}

	public static String addNewGist(String name, String description, String filename, String content, boolean isPublic) {
		String newGistId = "";
		GHGist ghGist    = Action.addGistToGitHub(description, filename, content, isPublic);
		if (ghGist != null) {
			newGist(ghGist, name);
			newGistId = ghGist.getGistId();
		}
		return newGistId;
	}

	public static GistFile addNewFile(String gistId, String filename, String content) {
		GistFile file = null;
		GHGistFile ghGistFile = Action.addFileToGist(gistId, filename, content);
		if (ghGistFile != null) {
			file = newFile(ghGistFile,gistId,filename,content);
			getGist(gistId).addFile(file);
		}
		return file;
	}

	public static boolean deleteFile(GistFile file) {
		{
			String gistId = file.getGistId();
			gistMap.get(gistId).deleteFile(file.getFilename());
			return Action.delete(file);
		}
	}

	public static boolean deleteGist(Gist gist) {return Action.delete(gist);}

	public static Gist setPublicState(Gist oldGist, boolean isPublic) {
		List<GistFile> newFileList = new ArrayList<>();
		String         oldGistId   = oldGist.getGistId();
		String         description = oldGist.getDescription();
		String         name        = oldGist.getName();
		GistFile[]     files       = new GistFile[oldGist.getFiles().size()];
		files = oldGist.getFiles().toArray(files);
		String filename = files[0].getFilename();
		String content  = files[0].getContent();
		GHGist ghGist   = Action.addGistToGitHub(description, filename, content, isPublic);
		if (ghGist == null) return null;
		String newGistId = ghGist.getGistId();
		int    fileId    = Action.newSQLFile(newGistId, filename, content);
		newFileList.add(new GistFile(newGistId, filename, content, fileId, false));
		String url     = ghGist.getHtmlUrl().toString();
		Gist   newGist = new Gist(newGistId, name, description, isPublic, url);
		for (int x = 1; x < files.length; x++) { //We used zero to create the new Gist
			filename = files[x].getFilename();
			content  = files[x].getContent();
			fileId   = Action.newSQLFile(newGistId, filename, content);
			newFileList.add(new GistFile(newGistId, filename, content, fileId, false));
			try {
				ghGist.update().addFile(filename, content).update();
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		newGist.addFiles(newFileList);
		gistMap.remove(oldGistId);
		gistMap.put(newGistId, newGist);

		Action.addGistToSQL(newGist);
		if (!Action.delete(oldGist)) return null;
		return newGist;
	}

	public static void unBindFileObjects() {
		for (Gist gist : getGists()) {
			for (GistFile file : gist.getFiles()) {
				file.unbindAll();
			}
		}
	}

	public static Gist getGist(String gistId) {return gistMap.get(gistId);}

	public static void basicDBLoad() {
		gistMap = Action.getGistMap();
	}

	public static boolean isDirty() {
		boolean dirty = false;
		for (Gist gist : getGists()) {
			for (GistFile file : gist.getFiles()) {
				if (file.isDirty()) {
					dirty = true;
					break;
				}
			}
		}
		return dirty;
	}

	public static void refreshDirtyFileFlags() {
		if (gistMap != null) {
			for (Gist gist : gistMap.values()) {
				for (GistFile file : gist.getFiles()) {
					file.refreshDirtyFlag();
				}
			}
		}
	}

	public static List<GistFile> getUnsavedFiles() {
		List<GistFile> list = new ArrayList<>();
		for (Gist gist : getGists()) {
			for (GistFile file : gist.getFiles()) {
				if (file.isDirty()) {
					list.add(file);
				}
			}
		}
		return list;
	}

	public static Integer getDirtyCount() {
		Integer dirtyCount = 0;
		for (Gist gist : getGists()) {
			for (GistFile file : gist.getFiles()) {
				if (file.isDirty()) {
					dirtyCount++;
				}
			}
		}
		return dirtyCount;
	}

	public static List<String> getFilenamesFor(String gistId) {
		LinkedList<String> list = new LinkedList<>();
		for (GistFile file : getGist(gistId).getFiles()) {
			list.addLast(file.getFilename());
		}
		return list;
	}

	private static void newGist(GHGist ghGist, String name) {
		String gistId = ghGist.getGistId();
		Gist gist = new Gist(
				gistId,
				name, ghGist.getDescription(),
				ghGist.isPublic(),
				ghGist.getHtmlUrl().toString());
		for (GHGistFile ghGistFile : ghGist.getFiles().values()) {
			String filename = ghGistFile.getFileName();
			String content  = ghGistFile.getContent();
			int    fileId   = Action.newSQLFile(gistId, filename, content);
			gist.addFile(new GistFile(fileId, gistId, filename, content, false));
		}
		gistMap.put(gistId, gist);
		Action.addGistToSQL(gist);
	}

	private static GistFile newFile(GHGistFile ghGistFile,String gistId, String filename, String content) {
		int      fileId = Action.newSQLFile(gistId, filename, content);
		GistFile file   = new GistFile(ghGistFile, fileId, gistId,false);
		gistMap.get(gistId).addFile(file);
		return file;
	}

	public static void setGistWindow (GistWindow gistWindow) {
		GistManager.gistWindow = gistWindow;
	}

	public static void setPBStyle(String style) {
		gistWindow.setPBarStyle(style);
	}

	public static void handleButtons() {
		if(gistWindow != null) {
			gistWindow.handleButtonBar();
		}
	}

}
