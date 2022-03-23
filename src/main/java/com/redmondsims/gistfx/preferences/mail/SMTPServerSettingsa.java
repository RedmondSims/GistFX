package com.redmondsims.gistfx.preferences.mail;

import com.redmondsims.gistfx.cryptology.Crypto;

public class SMTPServerSettingsa {

	public SMTPServerSettingsa(String smtpServer, String name, String userName, String password, Integer portTLS, Integer portSSL, boolean requireSSL, boolean requireTLS, boolean requireAuthentication, boolean requireSecureConnection) {
		this.smtpServer              = smtpServer;
		this.name                    = name;
		this.userName                = userName;
		this.password                = Crypto.encryptWithSessionKey(password);
		this.portTLS                 = portTLS;
		this.portSSL                 = portSSL;
		this.requireSSL              = requireSSL;
		this.requireTLS              = requireTLS;
		this.requireAuthentication   = requireAuthentication;
		this.requireSecureConnection = requireSecureConnection;
	}

	private final String  smtpServer;
	private final String  name;
	private final String  userName;
	private final String  password;
	private final Integer portTLS;
	private final Integer portSSL;
	private final boolean requireSSL;
	private final boolean requireTLS;
	private final boolean requireAuthentication;
	private final boolean requireSecureConnection;


	public String getSmtpServer() {
		return smtpServer;
	}

	public String getName() {
		return name;
	}

	public String getSenderEmail() {
		return userName;
	}

	public String getPassword() {
		return Crypto.decryptWithSessionKey(password);
	}

	public Integer getPortTLS() {
		return portTLS;
	}

	public Integer getPortSSL() {
		return portSSL;
	}

	public boolean isRequireSSL() {
		return requireSSL;
	}

	public boolean isRequireTLS() {
		return requireTLS;
	}

	public boolean isRequireAuthentication() {
		return requireAuthentication;
	}

	public boolean isRequireSecureConnection() {
		return requireSecureConnection;
	}
}
