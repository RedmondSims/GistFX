package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.Main;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.cryptology.Crypto;
import com.redmondsims.gistfx.enums.OS;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings.DataSource;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.*;
import java.util.*;


class SQLite {


	private final DataSource GITHUB = DataSource.GITHUB;
	private final DataSource LOCAL  = DataSource.LOCAL;
	private       File       sqliteFile;
	private       Connection conn;


	/**
	 *	Local File operations for SQLite
	 */

	public void setConnection() {
		setSQLFile();
		String  sqlScriptPath = Objects.requireNonNull(Main.class.getResource("SQLite/GistFXCreateSchema.sql")).toExternalForm();
		File    sqlScriptFile = new File(sqlScriptPath.replaceFirst("file:", ""));
		boolean createSchema  = !sqliteFile.exists();
		String  connString    = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
		try {
			conn = DriverManager.getConnection(connString);
			conn.setSchema("GistFX");
			conn.setAutoCommit(true);
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.err.println("\n\nSQLite.setDatabaseConnection(): " + e.getMessage() + "\n\n");
			String error = "\n\nThe SQLite library failed to create the database file.\n\tMake sure your Access Control Lists are permissive for the folder that GistFX executes from.\n\nRun program from a command prompt to see the full stack trace.\n\nExiting...";
			System.err.println(error);
			Platform.runLater(() -> {
				CustomAlert.showWarning(error);
				System.exit(101);
			});
		}
		if (createSchema) {
			String schemaStatements = Action.loadTextFile(sqlScriptFile);
			if (!createSchema(schemaStatements)) {
				deleteFile(sqliteFile);
				String error = "\n\nThe SQLite library failed to create the schema.\n\tMake sure your Access Control Lists are permissive for the folder that GistFX executes from.\n\nRun program from a command prompt to see the full stack trace.\n\nExiting...";
				System.err.println(error);
				Platform.runLater(() -> {
					CustomAlert.showWarning(error);
					System.exit(101);
				});
			}
			LiveSettings.setDataSource(GITHUB);
		}
		else {
			boolean tablesHaveData = gistsHasData() && gistFilesHasData();
			if (!tablesHaveData) {
				LiveSettings.setDataSource(GITHUB);
			}
			else {
				LiveSettings.setDataSource(LOCAL);
			}
		}
	}

	private void setSQLFile() {
		File corePath;
		if (LiveSettings.getOS().equals(OS.MAC)) {
			corePath = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "GistFX").toFile();
		}
		else if(LiveSettings.getOS().equals(OS.WINDOWS)) {
			corePath = Paths.get(System.getProperty("user.home"),"AppData","Local","GistFX").toFile();
		}
		else {
			corePath = Paths.get(System.getProperty("user.home"),".gistfx").toFile();
		}
		if(!corePath.exists()) corePath.mkdir();
		sqliteFile = new File(corePath, "Database.sqlite");
	}

	public void deleteDatabaseFile() {
		setSQLFile();
		deleteFile(sqliteFile);
	}

	private void deleteFile(File file) {
		try {
			FileUtils.forceDelete(file);
		}
		catch (IOException e) {
			System.err.println("SQLite.delete(File): " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *	ENCRYPTED database methods
	 */

	private boolean createSchema(String schemaStatements) {
		boolean result = true;
		schemaStatements = schemaStatements.replaceAll("(.)(\\n)", "$1 ");
		schemaStatements = schemaStatements.replaceAll("CREATE TABLE", "CREATE TABLE IF NOT EXISTS");
		schemaStatements = schemaStatements.replaceAll("(\\()(\\s+)", "$1");
		schemaStatements = schemaStatements.replaceAll("\\n\\n", "\n");
		schemaStatements = schemaStatements.replaceAll(" {2,}", " ");
		schemaStatements = schemaStatements.replaceAll(" \\)", ")");
		String[] createStatements = schemaStatements.split("\n");
		try {
			Statement stmt = conn.createStatement();
			for (String SQL : createStatements) {
				stmt.executeUpdate(SQL);
			}
		}
		catch (SQLException sqe) {
			result = false;
			System.err.println("*** SQLite.createSchema ***\n" + sqe.getMessage());
			sqe.printStackTrace();
		}
		return result;
	}

	/**
	 *  Gist only actions
	 */

	public void addGist(Gist gist) {
		String  gistId       = gist.getGistId();
		String  eDescription = Crypto.encryptWithSessionKey(gist.getDescription());
		boolean isPublic     = gist.isPublic();
		String  eUrl         = Crypto.encryptWithSessionKey(gist.getURL());
		try {
			String            SQL = "INSERT INTO Gists (gistId, description, isPublic, url) VALUES (?,?,?,?)";
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1, gistId);
			pst.setString(2, eDescription);
			pst.setBoolean(3, isPublic);
			pst.setString(4, eUrl);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	public void deleteGist(String gistId) {
		String SQL    = "DELETE FROM Gists WHERE gistId = ?";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1, gistId);
			pst.executeUpdate();
			pst.close();
			Action.removeJsonName(gistId);
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	public void updateGistDescription(Gist gist) {
		String gistId       = gist.getGistId();
		String eDescription = Crypto.encryptWithSessionKey(gist.getDescription());
		String SQL          = "UPDATE Gists SET description = ? WHERE gistId = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1, eDescription);
			pst.setString(2, gistId);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	public Map<String, Gist> getGistMap() {
		Map<String, Gist> map = new HashMap<>();
		String            SQL = "SELECT gistId, description, isPublic, url FROM Gists;";
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			while (rs.next()) {
				String  gistId      = rs.getString(1);
				String  name        = Action.getGistName(gistId);
				String  description = Crypto.decryptWithSessionKey(rs.getString(2));
				if (name.equals("")) name = description;
				boolean isPublic    = rs.getBoolean(3);
				String  url         = Crypto.decryptWithSessionKey(rs.getString(4));
				Gist    gist        = new Gist(gistId, name, description, isPublic, url);
				map.put(gistId, gist);
			}
			rs.close();
			for (Gist gist : map.values()) {
				gist.addFiles(getFileMap(gist.getGistId()));
			}
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return map;
	}

	public String getMetadata() {
		String response = "";
		String SQL = "SELECT jsonString FROM Metadata WHERE id = 1;";
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			if (rs.next()) {
				response = rs.getString(1);
			}
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return response;
	}

	private boolean hasMetadata() {
		boolean response = false;
		String SQL = "SELECT Count(*) FROM Metadata;";
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			if (rs.next()) {
				response = rs.getInt(1) > 0;
			}
			rs.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return response;
	}

	protected void saveMetadata(String jsonString) {
		String INSERT = "INSERT INTO Metadata (jsonString, id) VALUES (?,?)";
		String UPDATE = "UPDATE Metadata SET jsonString = ? WHERE id = ?;";
		try {
			PreparedStatement pst;
			if (hasMetadata()) {
				pst = conn.prepareStatement(UPDATE);
			}
			else {
				pst = conn.prepareStatement(INSERT);
			}
			pst.setString(1,jsonString);
			pst.setInt(2,1);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}

	}

	/**
	 *  Gist File Actions
	 */

	private List<GistFile> getFileMap(String gistId) {
		List<GistFile> fileList = new ArrayList<>();
		String                 SQL = "SELECT fileID, fileName, content, uploadDate, dirty FROM GistFiles WHERE gistId = ?";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1,gistId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Integer  fileId   = rs.getInt(1);
				String   filename = Crypto.decryptWithSessionKey(rs.getString(2));
				String   content  = Crypto.decryptWithSessionKey(rs.getString(3));
				Date uploadDate = rs.getDate(4);
				boolean  dirty    = rs.getBoolean(5);
				fileList.add(new GistFile(fileId, gistId, filename, content, uploadDate, dirty));
			}
			rs.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return fileList;
	}

	public void saveFile(GistFile file) {
		Integer fileId   = file.getFileId();
		String  eContent = Crypto.encryptWithSessionKey(file.getContent());
		Date uploadDate = file.getUploadDate();
		boolean dirty    = file.isDirty();
		String  SQL      = "UPDATE GistFiles SET content = ?, dirty = ?, uploadDate = ? WHERE fileId = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1, eContent);
			pst.setBoolean(2, dirty);
			pst.setDate(3,uploadDate);
			pst.setInt(4, fileId);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	public void renameFile(Integer fileId, String newFilename) {
		String  eFilename = Crypto.encryptWithSessionKey(newFilename);
		String  SQL       = "UPDATE GistFiles SET fileName = ? WHERE fileId = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1, eFilename);
			pst.setInt(2, fileId);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	public int newSQLFile(String gistId, String filename, String content, Date uploadDate) {
		String eFilename = Crypto.encryptWithSessionKey(filename);
		String eContent  = Crypto.encryptWithSessionKey(content);
		int    fileId    = 0;
		String SQL       = "INSERT INTO GistFiles (gistId, fileName, content, uploadDate) VALUES (?,?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, gistId);
			pst.setString(2, eFilename);
			pst.setString(3, eContent);
			pst.setDate(4,uploadDate);
			pst.executeUpdate();
			ResultSet rs = getLastIDResultSet();
			assert rs != null;
			rs.next();
			fileId = rs.getInt(1);
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return fileId;
	}

	public void deleteGistFile(GistFile file) {
		Integer fileId = file.getFileId();
		String  SQL    = "DELETE FROM GistFiles WHERE fileId = ?";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setInt(1, fileId);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	public void changeGistId(GistFile file, String gistId) {
		Integer fileId = file.getFileId();
		String SQL = "UPDATE GistFiles SET gistId = ? WHERE fileId = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1,gistId);
			pst.setInt(2,fileId);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	/**
	 *	NON-encrypted database methods
	 */

	private boolean gistsHasData() {
		String  SQL     = "SELECT count(*) FROM Gists;";
		return hasData(SQL);
	}

	private boolean gistFilesHasData() {
		String  SQL     = "SELECT count(*) FROM GistFiles;";
		return hasData(SQL);
	}

	private boolean hasData(String SQL) {
		boolean hasData = false;
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			if (rs.next()) {
				hasData = rs.getInt(1) > 0;
			}
			rs.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return hasData;
	}

	private ResultSet getLastIDResultSet() {
		try {
			return conn.prepareStatement("select last_insert_rowid();").executeQuery();
		}
		catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return null;
	}

	public void setDirtyFile(Integer fileId, boolean dirty) {
		String SQL = "UPDATE GistFiles SET dirty = ? WHERE fileId = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setBoolean(1, dirty);
			pst.setInt(2, fileId);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
	}

	public boolean fileIsDirty (Integer fileId) {
		String SQL = "SELECT dirty FROM GistFiles WHERE fileID = ?";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1,fileId);
			ResultSet rs = pst.executeQuery();
			boolean isDirty = false;
			if (rs.next()) {
				isDirty = rs.getBoolean(1);
			}
			rs.close();
			pst.close();
			return isDirty;
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return false;
	}

	public void cleanDatabase() {
		String cleanGists   = "DELETE FROM Gists WHERE gistId IS NOT NULL;";
		String resetAutoIncrementFiles = "UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = 'GistFiles';";
		try {
			conn.createStatement().executeUpdate(cleanGists);
			conn.createStatement().executeUpdate(resetAutoIncrementFiles);
		}
		catch (SQLException throwables) {throwables.printStackTrace();}
	}

}
