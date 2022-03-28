package com.redmondsims.gistfx.networking;

import com.redmondsims.gistfx.cryptology.Crypto;
import com.redmondsims.gistfx.data.Action;
import com.redmondsims.gistfx.enums.TreeType;
import com.redmondsims.gistfx.gist.Gist;
import com.redmondsims.gistfx.gist.GistFile;
import com.redmondsims.gistfx.gist.GistManager;
import java.util.ArrayList;
import java.util.List;

import static com.redmondsims.gistfx.enums.TreeType.*;

public class PayloadBuilder {


	public PayloadBuilder(String senderName, List<Gist> gistList, String categoryName, String password) {
		String passwordHash = "";
		if (password != null) {
			if (!password.isEmpty()) {
				this.password = password;
				passwordHash = Crypto.hashPassword(this.password);
				this.usePassword = true;
			}
		}
		this.senderName   = Crypto.encryptData(senderName);
		this.categoryName = encrypt(categoryName);
		this.title        = Crypto.encryptData(categoryName);
		for(Gist gist : gistList) {
			addGist(gist);
		}
		payload = new Payload(this.title, this.senderName, this.gistRecordList, null, this.categoryName, this.usePassword, passwordHash, CATEGORY);
	}

	public PayloadBuilder(String senderName, Gist gist, String password) {
		String passwordHash = "";
		if (password != null) {
			if (!password.isEmpty()) {
				this.password = password;
				passwordHash = Crypto.hashPassword(this.password);
				this.usePassword = true;
			}
		}
		this.senderName = Crypto.encryptData(senderName);
		this.title = Crypto.encryptData(gist.getName());
		addGist(gist);
		payload = new Payload(this.title, this.senderName, this.gistRecordList, null, this.categoryName, this.usePassword, passwordHash, GIST);
	}

	public PayloadBuilder(String senderName, GistFile gistFile, String password) {
		String passwordHash = "";
		if (password != null) {
			if (!password.isEmpty()) {
				this.password = password;
				passwordHash = Crypto.hashPassword(this.password);
				this.usePassword = true;
			}
		}
		this.senderName = Crypto.encryptData(senderName);
		String filename    = encrypt(gistFile.getFilename());
		String content     = encrypt(gistFile.getContent());
		String     description = encrypt(gistFile.getDescription());
		FileRecord fileRecord  = new FileRecord(filename, content, description);
		this.title = Crypto.encryptData(gistFile.getFilename());
		payload = new Payload(this.title, this.senderName, null, fileRecord, null, this.usePassword, passwordHash, FILE);
	}

	public PayloadBuilder(Payload payload) {
		this.payload      = payload;
		this.senderName   = payload.senderName();
		this.title        = payload.title();
		this.categoryName = payload.categoryName();
		this.usePassword  = payload.usePassword();
		this.passwordHash = payload.passwordHash();
		this.type         = payload.type();
	}

	private final List<GistRecord> gistRecordList = new ArrayList<>();
	private final String           title;
	private final String            senderName;
	private final Payload           payload;
	private       String            categoryName;
	private       String            password;
	private       String            passwordHash;
	private       boolean           usePassword     = false;
	private       TreeType          type;

	public String getSenderName() {
		return Crypto.decryptData(senderName);
	}

	public String getTitle() {
		return Crypto.decryptData(title);
	}

	public boolean passwordValid(String password) {
		if(Crypto.validatePassword(password, passwordHash)) {
			this.password = password;
			return true;
		}
		return false;
	}

	public boolean usePassword() {
		return usePassword;
	}

	public TreeType getType() {
		return type;
	}

	private void addGist(Gist gist) {
		String gistDescription = encrypt(gist.getDescription());
		String           gistName       = encrypt(gist.getName());
		List<FileRecord> fileRecordList = new ArrayList<>();
		for(GistFile file : gist.getFiles()) {
			String filename        = encrypt(file.getFilename());
			String content         = encrypt(file.getContent());
			String fileDescription = encrypt(file.getDescription());
			fileRecordList.add(new FileRecord(filename, content, fileDescription));
		}
		GistRecord gistRecord = new GistRecord(gistName, gistDescription, fileRecordList);
		gistRecordList.add(gistRecord);
	}

	public FileRecord getGistPayloadFile() {
		String filename = decrypt(payload.fileRecord().filename());
		String content = decrypt(payload.fileRecord().content());
		String description = decrypt(payload.fileRecord().description());
		return new FileRecord(filename, content, description);
	}

	public List<String> createGists(String password) {
		this.password = password;
		return createGists();
	}

	public List<String> createGists() {
		List<String> gistIdList = new ArrayList<>();
		String categoryName = decrypt(payload.categoryName());
		String gistId = "";
		if(!categoryName.isEmpty()) {
			Action.addCategoryName(categoryName);
		}
		String filename;
		String content;
		String fileDescription;
		boolean createNewGist;
		String gistName;
		String gistDescription;
		for (GistRecord gistPayLoad : payload.gistRecordList()) {
			gistName = decrypt(gistPayLoad.gistName());
			gistDescription = decrypt(gistPayLoad.gistDescription());
			createNewGist = true;
			for (FileRecord fileRecord : gistPayLoad.fileRecordList()) {
				filename = decrypt(fileRecord.filename());
				content = decrypt(fileRecord.content());
				fileDescription = decrypt(fileRecord.description());
				if(createNewGist) {
					gistId        = GistManager.createNewGhGist(gistName, gistDescription, filename, content, fileDescription, false);
					createNewGist = false;
					gistIdList.add(gistId);
					continue;
				}
				GistManager.addNewFile(gistId, filename, content, fileDescription);
			}
			if(!categoryName.isEmpty()) {
				Action.mapCategoryNameToGist(gistId,categoryName);
			}
		}
		return gistIdList;
	}

	private String encrypt(String data) {
		if (usePassword) {
			return Crypto.encryptWithPassword(data,password);
		}
		else {
			return Crypto.encryptData(data);
		}
	}

	private String decrypt(String data) {
		if (usePassword) {
			return Crypto.decryptWithPassword(data,password);
		}
		else {
			return Crypto.decryptData(data);
		}
	}

	public Payload getPayload() {
		return payload;
	}

}
