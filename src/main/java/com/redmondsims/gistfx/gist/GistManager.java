package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.ui.LoginWindow;
import com.redmondsims.gistfx.utils.SceneOne;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

	public static void startFromGit(Map<String, GHGist> ghGistMap, State launchState) {
		Action.cleanDatabase();
		Action.loadNameMapIntoDatabase(); //This restores the names that have been assigned to the Gists
		gistMap      = new HashMap<>();
		for (GHGist ghGist : ghGistMap.values()) {
			addGist(ghGist);
		}
		LoginWindow.updateProcess("Loading GUI");
		WindowManager.newGistWindow(launchState);
		if (AppSettings.getFirstRun()) {
			AppSettings.setFirstRun(false);
		}
		AppSettings.setDataSource(LOCAL);
		LiveSettings.applyAppSettings();
	}

	private static void addGist(GHGist ghGist) {
		String         filename, content;
		int            fileId;
		List<GistFile> fileList;
		String  gistId      = ghGist.getGistId();
		String  name        = Action.getGistName(ghGist); //This returns the name from the NameMap table or the local JSon file if either exists, otherwise it gets back the gist description
		String  description = ghGist.getDescription();
		boolean isPublic    = ghGist.isPublic();
		String  url         = ghGist.getHtmlUrl().toString();
		Gist    gist        = new Gist(gistId, name, description, isPublic, url);
		Action.addGistToSQL(gist);
		fileList    = new ArrayList<>();
		for (GHGistFile file : ghGist.getFiles().values()) {
			filename = file.getFileName();
			content  = file.getContent();
			fileId   = Action.newSQLFile(gistId, filename, content);
			Date uploadDate = new Date(System.currentTimeMillis());
			GistFile gistFile = new GistFile(fileId,gistId,filename,content,uploadDate,false);
			fileList.add(gistFile);
		}
		gist.addFiles(fileList);
		gistMap.put(gistId, gist);
	}

	public static void startFromDatabase() {
		Action.loadBestNameMap();
		gistMap = Action.getGistMap();
		SceneOne.close();
		WindowManager.newGistWindow(State.LOCAL);
	}

	public static Collection<Gist> getGists() {return gistMap.values();}

	public static String addNewGist(String name, String description, String filename, String content, boolean isPublic) {
		GHGist ghGist = Action.addGistToGitHub(description, filename, content, isPublic);
		if (ghGist != null) {
			addGist(ghGist);
			gistMap.get(ghGist.getGistId()).setName(name);
			Action.addToNameMap(ghGist.getGistId(), name);
			return ghGist.getGistId();
		}
		return "";
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
		Date uploadDate = new Date(System.currentTimeMillis());
		newFileList.add(new GistFile(fileId,newGistId,filename,content,uploadDate,false));
		String url     = ghGist.getHtmlUrl().toString();
		Gist   newGist = new Gist(newGistId, name, description, isPublic, url);
		for (int x = 1; x < files.length; x++) { //We used zero to create the new Gist
			filename = files[x].getFilename();
			content  = files[x].getContent();
			fileId   = Action.newSQLFile(newGistId, filename, content);
			uploadDate = new Date(System.currentTimeMillis());
			newFileList.add(new GistFile(fileId,newGistId, filename, content, uploadDate, false));
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
					file.refreshFileFlag();
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

	public static List<String> getFilenamesFor(String gistId) {
		LinkedList<String> list = new LinkedList<>();
		for (GistFile file : getGist(gistId).getFiles()) {
			list.addLast(file.getFilename());
		}
		return list;
	}

	private static void newGist(GHGist ghGist, String name, String filename, String content) {
		String gistId = ghGist.getGistId();
		Gist gist = new Gist(
				gistId,
				name,
				ghGist.getDescription(),
				ghGist.isPublic(),
				ghGist.getHtmlUrl().toString());
		Action.addGistToSQL(gist);
		int fileId   = Action.newSQLFile(gistId, filename, content);
		Date uploadDate = new Date(System.currentTimeMillis());
		gist.addFile(new GistFile(fileId, gistId, filename, content, uploadDate, false));
		gistMap.put(gistId, gist);
	}

	private static GistFile newFile(GHGistFile ghGistFile,String gistId, String filename, String content) {
		int      fileId = Action.newSQLFile(gistId, filename, content);
		Date uploadDate = new Date(System.currentTimeMillis());
		GistFile file   = new GistFile(fileId,gistId,filename,content,uploadDate,false);
		gistMap.get(gistId).addFile(file);
		return file;
	}

	private static void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
