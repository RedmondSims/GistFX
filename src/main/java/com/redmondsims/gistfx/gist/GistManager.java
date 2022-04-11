package com.redmondsims.gistfx.gist;

import com.redmondsims.gistfx.cryptology.Crypto;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.Names;
import com.redmondsims.gistfx.enums.Source;
import com.redmondsims.gistfx.enums.State;
import com.redmondsims.gistfx.enums.Type;
import com.redmondsims.gistfx.preferences.AppSettings;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings;
import com.redmondsims.gistfx.utils.Status;
import org.kohsuke.github.GHGist;
import org.kohsuke.github.GHGistFile;

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

	public static void startFromGit(Map<String, GHGist> ghGistMap, Source launchSource) {
		Status.setState(State.LOADING);
		Action.wipeSQLAndMetaData();
		gistMap      = new HashMap<>();
		for (GHGist ghGist : ghGistMap.values()) {
			if (!ghGist.getDescription().equals(Names.GITHUB_METADATA.Name())) {
				addNewGistFromGitHub(ghGist);
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

	public static void startEmpty() {
		WindowManager.newGistWindow(Source.BLANK);
	}

	public static String createNewGhGist(String gistName, String gistDescription, String filename, String content, String fileDescription, boolean isPublic) {
		GHGist ghGist = Action.getNewGist(gistDescription, filename, content, isPublic);
		if(ghGist != null) {
			String gistId = ghGist.getGistId();
			Action.setGistName(gistId, gistName);
			if (!ghGist.getDescription().equals(Names.GITHUB_METADATA.Name())) {
				addNewGistFromGitHub(ghGist);
			}
			setFileDescription(gistId, filename, fileDescription);
			return gistId;
		}
		return "";
	}

	public static void addNewGistFromGitHub(GHGist ghGist) {
		if(ghGist != null) {
			String  gistId      = ghGist.getGistId();
			String  name        = Action.getGistName(ghGist); //This returns the name from the NameMap table or the local JSon file if either exists, otherwise it gets back the gist description
			String  description = ghGist.getDescription();
			boolean isPublic    = ghGist.isPublic();
			String  url         = ghGist.getHtmlUrl().toString();
			Gist    gist        = new Gist(gistId, name, description, isPublic, url);
			Action.addGistToSQL(gist);
			List<GistFile> fileList = new ArrayList<>();
			for (GHGistFile ghGistFile : ghGist.getFiles().values()) {
				GistFile gistFile = newGistFileAddSQL(gistId,ghGistFile);
				fileList.add(gistFile);
			}
			gist.addFiles(fileList);
			gistMap.putIfAbsent(gistId,gist);
		}
	}

	public static void addFileToGist(String gistId, GHGistFile ghGistFile) {
		Gist gist = gistMap.get(gistId);
		if (gist != null) {
			gistMap.get(gistId).getFiles().add(newGistFileAddSQL(gistId, ghGistFile));
		}
	}

	public static Collection<Gist> getGists() {return gistMap.values();}

	public static String addNewGistToGitHub(String name, String description, String filename, String content, boolean isPublic) {
		GHGist ghGist = Action.addGistToGitHub(description, filename, content, isPublic);
		String gistId;
		String url;
		if (ghGist != null) {
			gistId = ghGist.getGistId();
			url    = ghGist.getHtmlUrl().toString();
		}
		else {
			gistId = "Offline" + Crypto.randomText(15, Type.STANDARD);
			url = "";
		}
		Gist   gist   = new Gist(gistId, name, description, isPublic, url);
		Action.addGistToSQL(gist);
		Action.setGistName(gistId, name);
		gistMap.put(gistId,gist);
		int fileId = Action.addFileToSQL(gistId,filename,content);
		GistFile gistFile = new GistFile(fileId,gistId,filename,content,false);
		gist.addFile(gistFile);
		return gistId;
	}

	public static GistFile addNewFile(String gistId, String filename, String content, String fileDescription) {
		GistFile   file       = null;
		GHGistFile ghGistFile = Action.addFileToGitHub(gistId, filename, content);
		if(ghGistFile != null) {
			file = newGistFileAddSQL(gistId, ghGistFile);
		}
		else {
			file = newGistFileAddSQL(gistId, filename, content);
		}
		getGist(gistId).addFile(file);
		setFileDescription(gistId, filename, fileDescription);
		return file;
	}

	private static void setFileDescription (String gistId, String filename, String description) {
		if (description != null) {
			if(!description.isEmpty()) {
				Action.setFileDescription(gistId,filename,description);
			}
		}
	}

	private static GistFile newGistFileAddSQL(String gistId, GHGistFile ghGistFile) {
		if(ghGistFile != null) {
			String filename   = ghGistFile.getFileName();
			String content    = ghGistFile.getContent();
			int    fileId     = Action.addFileToSQL(gistId, filename, content);
			return new GistFile(fileId, gistId, filename, content, false);
		}
		return null;
	}

	private static GistFile newGistFileAddSQL(String gistId, String filename, String content) {
		int fileId = Action.addFileToSQL(gistId, filename, content);
		return new GistFile(fileId, gistId, filename, content, false);
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
		if(file != null) {
			String gistId = file.getGistId();
			gistMap.get(gistId).deleteFile(file.getFilename());
			Action.delete(file);
		}
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
		String content  = files[0].getLiveVersion();
		GHGist ghGist   = Action.addGistToGitHub(description, filename, content, isPublic);
		if (ghGist == null) return null;
		String newGistId = ghGist.getGistId();
		int    fileId    = Action.addFileToSQL(newGistId, filename, content);
		newFileList.add(new GistFile(fileId,newGistId,filename,content,false));
		String url     = ghGist.getHtmlUrl().toString();
		Gist   newGist = new Gist(newGistId, name, description, isPublic, url);
		for (int x = 1; x < files.length; x++) { //We used zero to create the new Gist
			filename = files[x].getFilename();
			content  = files[x].getLiveVersion();
			fileId   = Action.addFileToSQL(newGistId, filename, content);
			newFileList.add(new GistFile(fileId,newGistId, filename, content, false));
			try {
				ghGist.update().addFile(filename, content).update();
			}
			catch (Exception e) {Action.error(e);}
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

	public static Map<String,Gist> getGistMap() {
		return gistMap;
	}

	public static List<String> getGistFilenames(String gistId) {
		List<String> list = new ArrayList<>();
		Gist gist = gistMap.get(gistId);
		for(GistFile gistFile : gist.getFiles()) {
			list.add(gistFile.getFilename());
		}
		list.sort(Comparator.comparing(String::toString));
		return list;
	}

	public static Map<String,GistFile> getGistFileMap(String gistId) {
		Map<String,GistFile> newMap = new HashMap<>();
		Gist gist = gistMap.get(gistId);
		for(GistFile gistFile : gist.getFiles()) {
			newMap.put(gistFile.getFilename(),gistFile);
		}
		return newMap;
	}
	public static void deleteLocalGist(String gistId) {
		gistMap.remove(gistId);
		Action.deleteLocalGist(gistId);
	}


}
