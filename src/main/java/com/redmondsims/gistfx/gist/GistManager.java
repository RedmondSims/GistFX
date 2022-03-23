package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Names;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.utils.Status;
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

	public static void startFromGit(Map<String, GHGist> ghGistMap, Source launchSource) {
		Status.setState(State.LOADING);
		Action.cleanDatabase();
		gistMap      = new HashMap<>();
		for (GHGist ghGist : ghGistMap.values()) {
			if (!ghGist.getDescription().equals(Names.GIST_DATA_DESCRIPTION.Name())) {
				addGistFromGitHub(ghGist);
			}
		}
		WindowManager.newGistWindow(launchSource);
		AppSettings.set().firstRun(false);
		AppSettings.set().dataSource(LOCAL);
		LiveSettings.applyAppSettings();
	}

	public static void startFromDatabase() {
		Status.setState(State.LOADING);
		gistMap = Action.getGistMap();
		WindowManager.newGistWindow(Source.LOCAL);
	}

	public static void startEmpty(){
		WindowManager.newGistWindow(Source.BLANK);
	}

	public static String createNewGhGist(String gistName, String gistDescription, String filename, String content, String fileDescription, boolean isPublic) {
		GHGist ghGist      = Action.getNewGist(gistDescription, filename, content, isPublic);
		String gistId = ghGist.getGistId();
		Action.setGistName(gistId, gistName);
		if (!ghGist.getDescription().equals(Names.GIST_DATA_DESCRIPTION.Name())) {
			addGistFromGitHub(ghGist);
		}
		setFileDescription(gistId,filename,fileDescription);
		return gistId;
	}

	public static void addGistFromGitHub(GHGist ghGist) {
		String  gistId      = ghGist.getGistId();
		String  name        = Action.getGistName(ghGist); //This returns the name from the NameMap table or the local JSon file if either exists, otherwise it gets back the gist description
		String  description = ghGist.getDescription();
		boolean isPublic    = ghGist.isPublic();
		String  url         = ghGist.getHtmlUrl().toString();
		Gist    gist        = new Gist(gistId, name, description, isPublic, url);
		Action.addGistToSQL(gist);
		List<GistFile> fileList = new ArrayList<>();
		for (GHGistFile file : ghGist.getFiles().values()) {
			addFileToList(fileList,gistId,file);
		}
		gist.addFiles(fileList);
		gistMap.put(gistId, gist);
	}

	public static void addFileToList(List<GistFile> fileList, String gistId, GHGistFile ghGistFile) {
		fileList.add(newSQLFile(gistId, ghGistFile));
	}

	public static List<GistFile> getFileList (String gistId) {
		return gistMap.get(gistId).getFiles();
	}

	public static Collection<Gist> getGists() {return gistMap.values();}

	public static String addNewGistToGitHub(String name, String description, String filename, String content, boolean isPublic) {
		GHGist ghGist = Action.addGistToGitHub(description, filename, content, isPublic);
		if (ghGist != null) {
			String gistId = ghGist.getGistId();
			String url    = ghGist.getHtmlUrl().toString();
			Gist   gist   = new Gist(gistId, name, description, isPublic, url);
			Action.addGistToSQL(gist);
			Action.setGistName(gistId, name);
			gistMap.put(gistId,gist);
			Date uploadDate = new Date(System.currentTimeMillis());
			int fileId = Action.addFileToSQL(gistId,filename,content,uploadDate);
			GistFile gistFile = new GistFile(fileId,gistId,filename,content,uploadDate,false);
			gist.addFile(gistFile);
			return gistId;
		}
		return "";
	}

	public static GistFile addNewFile(String gistId, String filename, String content, String fileDescription) {
		GistFile file = null;
		GHGistFile ghGistFile = Action.addFileToGitHub(gistId, filename, content);
		if (ghGistFile != null) {
			file = newSQLFile(gistId,ghGistFile);
			getGist(gistId).addFile(file);
			setFileDescription(gistId,filename,fileDescription);
		}
		return file;
	}

	private static void setFileDescription (String gistId, String filename, String description) {
		if (description != null) {
			if(!description.isEmpty()) {
				Action.setFileDescription(gistId,filename,description);
			}
		}
	}

	private static GistFile newSQLFile( String gistId, GHGistFile ghGistFile) {
		String filename = ghGistFile.getFileName();
		String content = ghGistFile.getContent();
		Date uploadDate = new Date(System.currentTimeMillis());
		int      fileId = Action.addFileToSQL(gistId, filename, content, uploadDate);
		return new GistFile(fileId,gistId,filename,content,uploadDate,false);
	}

	public static GistFile getFile(String gistId, int fileId) {
		return gistMap.get(gistId).getFile(fileId);
	}

	public static GistFile getFile(String gistId, String filename) {
		return gistMap.get(gistId).getFile(filename);
	}

	public static GistFile getFile(String filename) {
		for (Gist gist : gistMap.values()) {
			for (GistFile file : gist.getFiles()) {
				if (file.getFilename().equals(filename)) return file;
			}
		}
		return null;
	}

	public static void deleteFile(GistFile file) {
		String gistId = file.getGistId();
		gistMap.get(gistId).deleteFile(file.getFilename());
		Action.delete(file);
	}

	public static void deleteGist(String gistId) {
			Action.delete(gistId);
			gistMap.remove(gistId);
	}

	public static boolean gistHasFile(String gistId, String filename) {
		return gistMap.get(gistId).getFile(filename) != null;
	}

	public static boolean hasGist(String gistId) {
		return gistMap.containsKey(gistId);
	}

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
		Date uploadDate = new Date(System.currentTimeMillis());
		int    fileId    = Action.addFileToSQL(newGistId, filename, content,uploadDate);
		newFileList.add(new GistFile(fileId,newGistId,filename,content,uploadDate,false));
		String url     = ghGist.getHtmlUrl().toString();
		Gist   newGist = new Gist(newGistId, name, description, isPublic, url);
		for (int x = 1; x < files.length; x++) { //We used zero to create the new Gist
			filename = files[x].getFilename();
			content  = files[x].getContent();
			fileId   = Action.addFileToSQL(newGistId, filename, content,uploadDate);
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
		Action.delete(oldGistId);
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
					file.refreshGraphicNode();
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
		Date uploadDate = new Date(System.currentTimeMillis());
		int fileId   = Action.addFileToSQL(gistId, filename, content,uploadDate);
		gist.addFile(new GistFile(fileId, gistId, filename, content, uploadDate, false));
		gistMap.put(gistId, gist);
	}

	private static void sleep(long milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean filesEqual(String gistId, String filename, String content) {
		GistFile gistFile = gistMap.get(gistId).getFile(filename);
		if (gistFile == null) return false;
		String thisContent = gistFile.getContent();
		return thisContent.equals(content);
	}
}
