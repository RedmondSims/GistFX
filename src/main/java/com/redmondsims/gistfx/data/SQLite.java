package com.redmondsims.gistfx.data;

import com.redmondsims.gistfx.Launcher;
import com.redmondsims.gistfx.alerts.CustomAlert;
import com.redmondsims.gistfx.cryptology.Crypto;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.preferences.LiveSettings;
import com.redmondsims.gistfx.preferences.UISettings.DataSource;
import com.redmondsims.gistfx.utils.Resources;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;


class SQLite {


	private final DataSource GITHUB = DataSource.GITHUB;
	private final DataSource LOCAL      = DataSource.LOCAL;
	private final File       sqliteFile = Resources.getSQLiteFile();
	private       Connection conn;


	/**
	 *	Local File operations for SQLite
	 */

	public void setConnection() {
		boolean createSchema  = !sqliteFile.exists();
		String  connString    = "jdbc:sqlite:" + sqliteFile.getAbsolutePath();
		try {
			if (conn == null) {
				conn = DriverManager.getConnection(connString);
				conn.setSchema("GistFX");
				conn.setAutoCommit(true);
			}
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
			if (!createSchema()) {
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
		LiveSettings.setDataSource(hasData() ? LOCAL : GITHUB);
	}

	public void deleteDatabaseFile() {
		deleteFile(sqliteFile);
	}

	private void deleteFile(File file) {
		try {
			if(conn != null)
				conn.close();
			if(file.exists())
				FileUtils.forceDelete(file);
		}
		catch (Exception e) {
			Action.error(e);
		}
	}

	private boolean createSchema() {
		boolean result = true;
		try {
			InputStream sqlScriptResourceStream = Objects.requireNonNull(Launcher.class.getResourceAsStream("SQLite/GistFXCreateSchema.sql"));
			String schemaStatements = IOUtils.toString(sqlScriptResourceStream, StandardCharsets.UTF_8);
			schemaStatements = schemaStatements.replaceAll("(.)(\\n)", "$1 ");
			schemaStatements = schemaStatements.replaceAll("CREATE TABLE", "CREATE TABLE IF NOT EXISTS");
			schemaStatements = schemaStatements.replaceAll("(\\()(\\s+)", "$1");
			schemaStatements = schemaStatements.replaceAll("\\n\\n", "\n");
			schemaStatements = schemaStatements.replaceAll(" {2,}", " ");
			schemaStatements = schemaStatements.replaceAll(" \\)", ")");
			String[] createStatements = schemaStatements.split("\n");
			Statement stmt = conn.createStatement();
			for (String SQL : createStatements) {
				stmt.executeUpdate(SQL);
			}
		}
		catch (Exception e) {Action.error(e);}
		return result;
	}

	/**
	 *	ENCRYPTED database methods
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
		catch (Exception e) {Action.error(e);}
	}

	public void deleteGist(String gistId) {
		String sqlGistFiles     = "DELETE FROM GistFiles WHERE gistId = ?";
		String sqlGists         = "DELETE FROM Gists WHERE gistId = ?";
		try {
			PreparedStatement pst = conn.prepareStatement(sqlGistFiles);
			pst.setString(1,gistId);
			pst.executeUpdate();
			pst = conn.prepareStatement(sqlGists);
			pst.setString(1,gistId);
			pst.executeUpdate();
			pst.close();
			Action.deleteGistMetadata(gistId);
		}
		catch (Exception e) {Action.error(e);}

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
		catch (Exception e) {Action.error(e);}

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
				if (name.equals("")) name = Action.getGistName(gistId);
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
		catch (Exception e) {Action.error(e);}
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
		catch (Exception e) {Action.error(e);}

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
		catch (Exception e) {Action.error(e);}

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
		catch (Exception e) {Action.error(e);}


	}

	/**
	 *  Gist File Actions
	 */

	private List<GistFile> getFileMap(String gistId) {
		List<GistFile> fileList = new ArrayList<>();
		String                 SQL = "SELECT fileID, fileName, content, dirty FROM GistFiles WHERE gistId = ?";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1,gistId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Integer  fileId   = rs.getInt(1);
				String   filename = Crypto.decryptWithSessionKey(rs.getString(2));
				String   content  = Crypto.decryptWithSessionKey(rs.getString(3));
				boolean  dirty    = rs.getBoolean(4);
				fileList.add(new GistFile(fileId, gistId, filename, content, dirty));
			}
			rs.close();
		}
		catch (Exception e) {Action.error(e);}

		return fileList;
	}

	public void saveFile(int fileId, String content, boolean dirty) {
		String SQL = "UPDATE GistFiles SET content = ?, dirty = ? WHERE fileId = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1, Crypto.encryptWithSessionKey(content));
			pst.setBoolean(2, dirty);
			pst.setInt(3, fileId);
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
		catch (Exception e) {Action.error(e);}

	}

	public int newSQLFile(String gistId, String filename, String content) {
		String eFilename = Crypto.encryptWithSessionKey(filename);
		String eContent  = Crypto.encryptWithSessionKey(content);
		int    fileId    = 0;
		String SQL       = "INSERT INTO GistFiles (gistId, fileName, content, gitHubVersion) VALUES (?,?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, gistId);
			pst.setString(2, eFilename);
			pst.setString(3, eContent);
			pst.setString(4, eContent);
			pst.executeUpdate();
			ResultSet rs = getLastIDResultSet();
			assert rs != null;
			rs.next();
			fileId = rs.getInt(1);
			pst.close();
		}
		catch (Exception e) {Action.error(e);}

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
		catch (Exception e) {Action.error(e);}

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
		catch (Exception e) {Action.error(e);}

	}

	public void updateGitHubVersion(int fileId, String gitHubContent) {
		String eContent = Crypto.encryptWithSessionKey(gitHubContent);
		String SQL = "UPDATE GistFiles SET gitHubVersion = ? WHERE fileID = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL);
			pst.setString(1,eContent);
			pst.setInt(2, fileId);
			pst.executeUpdate();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}

	}

	public String getLocalGitHubVersion(int fileId) {
		String localGitHubVersion = "";
		String SQL = "SELECT gitHubVersion FROM GistFiles WHERE fileId = ?;";
		try {
			PreparedStatement pst = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1,fileId);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				localGitHubVersion = Crypto.decryptWithSessionKey(rs.getString(1));
			}
			rs.close();
			pst.close();
		}
		catch (SQLException sqe) {sqe.printStackTrace();}
		return localGitHubVersion;
	}

	/**
	 *	NON-encrypted database methods
	 */

	public boolean hasData() {
		return gistsHaveRecords() && gistFilesHaveRecords();
	}

	private boolean gistsHaveRecords() {
		String  SQL     = "SELECT count(*) FROM Gists;";
		return hasRecords(SQL);
	}

	private boolean gistFilesHaveRecords() {
		String  SQL     = "SELECT count(*) FROM GistFiles;";
		return hasRecords(SQL);
	}

	private boolean hasRecords(String SQL) {
		boolean hasData = false;
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			if (rs.next()) {
				hasData = rs.getInt(1) > 0;
			}
			rs.close();
		}
		catch (Exception e) {Action.error(e);}

		return hasData;
	}

	private ResultSet getLastIDResultSet() {
		try {
			return conn.prepareStatement("select last_insert_rowid();").executeQuery();
		}
		catch (Exception e) {Action.error(e);}

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
		catch (Exception e) {Action.error(e);}

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
		catch (Exception e) {Action.error(e);}

		return false;
	}

	public void cleanDatabase() {
		String cleanGists   = "DELETE FROM Gists WHERE gistId IS NOT NULL;";
		String cleanFiles   = "DELETE FROM GistFiles WHERE fileId <> 0;";
		String cleanMetadata= "DELETE FROM Metadata WHERE id <> 0;";
		String resetAutoIncrementFiles = "UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = 'GistFiles';";
		try {
			conn.createStatement().executeUpdate(cleanGists);
			conn.createStatement().executeUpdate(cleanFiles);
			conn.createStatement().executeUpdate(cleanMetadata);
			conn.createStatement().executeUpdate(resetAutoIncrementFiles);
		}
		catch (Exception e) {Action.error(e);}

	}

	public void deleteMetadata() {
		String SQL = "DELETE FROM Metadata;";
		try {
			conn.createStatement().executeUpdate(SQL);
		}
		catch (Exception e) {Action.error(e);}
	}
	
	public String getMD2() {
		String SQL = "SELECT jsonString FROM Metadata WHERE id = 2;";
		String response = "";
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQL);
			if (rs.next()) {
				response = rs.getString(1);
			}
			rs.close();
		}
		catch (Exception e) {Action.error(e);}

		return response;
	}

	public boolean reEncryptData(String oldPassword, String newPassword) {
		List<GistRecord>     gistRecordList = new ArrayList<>();
		List<GistFileRecord> fileList       = new ArrayList<>();
		String               SQLGist        = "SELECT * FROM Gists;";
		String               SQLFile        = "SELECT * FROM GistFiles;";
		Crypto.setTempSessionKey(oldPassword);
		boolean response = true;
		try {
			ResultSet rs = conn.createStatement().executeQuery(SQLGist);
			while (rs.next()) {
				String  gistId      = Crypto.decryptWithSessionKey(rs.getString("gistId"));
				String  description = Crypto.decryptWithSessionKey(rs.getString("description"));
				boolean isPublic    = rs.getBoolean("isPublic");
				String  url         = Crypto.decryptWithSessionKey(rs.getString("url"));
				gistRecordList.add(new GistRecord(gistId, description, isPublic, url));
			}
			rs = conn.createStatement().executeQuery(SQLFile);
			while (rs.next()) {
				int     fileId     = rs.getInt("fileId");
				String  gistId     = Crypto.decryptWithSessionKey(rs.getString("gistId"));
				String  filename   = Crypto.decryptWithSessionKey(rs.getString("filename"));
				String  content    = Crypto.decryptWithSessionKey(rs.getString("content"));
				boolean dirty      = rs.getBoolean("dirty");
				fileList.add(new GistFileRecord(fileId, gistId, filename, content, dirty));
			}
			rs.close();
			conn.createStatement().executeQuery("DELETE FROM Gists");
			conn.createStatement().executeQuery("DELETE FROM GistFiles");
		}
		catch (Exception e) {Action.error(e);}



		String SQLAddGist = "INSERT INTO Gists (gistId, description, isPublic, url) VALUES (?,?,?,?)";
		String SQLAddGistFile = "INSERT INTO GistFiles (fileId, gistId, filename, content, dirty) VALUES (?,?,?,?,?)";
		Crypto.setTempSessionKey(newPassword);
		try {
			PreparedStatement pst = conn.prepareStatement(SQLAddGist);
			for(GistRecord gistRecord : gistRecordList) {
				pst.setString(1, Crypto.encryptWithSessionKey(gistRecord.gistId()));
				pst.setString(2, Crypto.encryptWithSessionKey(gistRecord.description()));
				pst.setBoolean(3,gistRecord.isPublic());
				pst.setString(4, Crypto.encryptWithSessionKey(gistRecord.url()));
				pst.executeUpdate();
			}
			pst = conn.prepareStatement(SQLAddGistFile);
			for(GistFileRecord fileRecord : fileList) {
				pst.setInt(1,fileRecord.fileId());
				pst.setString(2, Crypto.encryptWithSessionKey(fileRecord.gistId()));
				pst.setString(3, Crypto.encryptWithSessionKey(fileRecord.filename()));
				pst.setString(4, Crypto.encryptWithSessionKey(fileRecord.content()));
				pst.setBoolean(5,fileRecord.dirty());
				pst.executeUpdate();
			}
			pst.close();
		}
		catch (Exception e) {Action.error(e);}

		return response;
	}

	public void deleteAllLocalData() {
		try {
			conn.createStatement().executeUpdate("DELETE FROM Gists WHERE gistId <> 'a';");
			conn.createStatement().executeUpdate("DELETE FROM GistFiles WHERE fileId <> 0;");
			conn.createStatement().executeUpdate("DELETE FROM Metadata WHERE id <> 0;");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
