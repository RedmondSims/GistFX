package com.redmondsims.gistfx.cryptology;

import com.redmondsims.gistfx.enums.Type;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

public class Crypto implements Serializable {

	@Serial private static final long serialVersionUID = 4L;

	//public static DateTimeFormatter fmtMS = DateTimeFormatter.("mm:ss");

	private static final String       KEY     = "G0zpVjraWhg3;Z^,KFQ$q6I8OBTx(t:IfH.NY)^Z^6{VLNm]BQYsNC]{";
	private static final CharSequence SEED    = "BGP&+gG,uY)41#:SlG&po4dsFPrW2:R5*&^_W+fuYCfw9;XMC&kG)a>WgH99Q^z{BRa0Q8(xyQ}H]41&W:zU";
	private static final Base64       base64  = new Base64(true);
	private static final String       token   = "3IHWMyZuW∂5is&7bUkj6";
	private static final String       json    = "mQ8UaX&9UNSakIWugdH3";
	private static final String       data    = "uRcRiA99n5ZR15fS9QWt";
	private static final String       jsonKey = encryptCommon(json, KEY);
	private static final String       dataKey = encryptCommon(data, KEY);
	private static       String       sessionKey;

	private static String encryptCommon(String msg, String userKey) {
		String response = "";
		try {
			if (msg != null) {
				if(!msg.isEmpty()) {
					SecretKeySpec keySpec = new SecretKeySpec(userKey.getBytes(), "Blowfish");
					Cipher        cipher  = Cipher.getInstance("Blowfish");
					cipher.init(Cipher.ENCRYPT_MODE, keySpec);
					response = base64.encodeToString(cipher.doFinal(msg.getBytes()));
				}
			}
		}
		catch (Exception e) {
			System.err.println("Crypto.encryptCommon: " + e.getMessage());
			System.out.println("msg: " + msg + "\n\tkey: " + userKey);
		}
		return response;
	}

	private static String decryptCommon(String encMsg, String userKey) {
		String response = "";
		try {
			if(encMsg != null) {
				if (!encMsg.isEmpty()) {
					byte[]        encryptedData = Base64.decodeBase64(encMsg);
					SecretKeySpec keySpec       = new SecretKeySpec(userKey.getBytes(), "Blowfish");
					Cipher        cipher        = Cipher.getInstance("Blowfish");
					cipher.init(Cipher.DECRYPT_MODE, keySpec);
					byte[] decrypted = cipher.doFinal(encryptedData);
					response = new String(decrypted);
				}
			}
		}
		catch (Exception e) {
			System.err.println("Crypto.decryptCommon: " + e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	public static void setSessionKey(String userPassword) {
		if (userPassword.isEmpty()) {sessionKey = encryptCommon(token, KEY);}
		else {sessionKey = encryptCommon(userPassword, KEY);}
	}

	public static String encrypData(String data) {
		return encryptCommon(data,dataKey);
	}

	public static String decrypData(String encData) {
		return decryptCommon(encData,dataKey);
	}

	public static String encryptJson(String message) {return encryptCommon(message, jsonKey);}

	public static String decryptJson(String cipher) {return decryptCommon(cipher, jsonKey);}

	public static String encryptWithSessionKey(String message) {
		return encryptCommon(message, sessionKey);
	}

	public static String encryptWithPassword(String message, String password){
		String key = encryptCommon(password,KEY);
		return encryptCommon(message,key);
	}

	public static String decryptWithPassword(String encodedMessage, String password){
		String key = encryptCommon(password,KEY);
		return decryptCommon(encodedMessage,key);
	}

	public static String decryptWithSessionKey(String cipher) {
		return decryptCommon(cipher, sessionKey);
	}

	public static String randomText(int size, Type type) {
		String        soup      = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		if (type.equals(Type.DIRTY)) soup += "!)@(#*$&%^<>?,./:;}{][=+-_";
		int           max       = soup.length();
		char[]        soupArray = soup.toCharArray();
		StringBuilder sb        = new StringBuilder();
		for (int x = 0; x < size; x++) {
			Random random = new Random();
			int    y      = random.nextInt(max);
			sb.append(soupArray[y]);
		}
		return sb.toString();
	}

	//Password Hashing

	public static String hashPassword(String password) {
		Pbkdf2PasswordEncoder pwe = new Pbkdf2PasswordEncoder(SEED, 24, 75000, 4096);
		return pwe.encode(password);
	}

	public static boolean validatePassword(String password, String hash) {
		Pbkdf2PasswordEncoder pwe = new Pbkdf2PasswordEncoder(SEED, 24, 75000, 4096);
		return pwe.matches(password, hash);
	}

}
