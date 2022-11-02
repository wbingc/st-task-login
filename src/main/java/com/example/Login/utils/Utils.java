package com.example.Login.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

//https://www.tutorialspoint.com/java_cryptography/java_cryptography_message_digest.htm
//bcrypt: https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt

@Component
public class Utils {
	
	final static Logger LOGGER = LogManager.getLogger();
	
	/***
	 * Supporting function to generate a hash for Input String
	 * @author wbing
	 * @param plaintext String
	 * @return digest String
	 * @throws NoSuchAlgorithmException NoSuchAlgorithm
	 */
	public static String digest(String plaintext) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(plaintext.getBytes());
		byte[] digest = md.digest();
		
		StringBuilder hexString = new StringBuilder();

		for (byte b : digest) {
			hexString.append(Integer.toHexString(0xFF & b));
		}
		return hexString.toString();
	}
	
	/***
	 * Validate Stored Password Hash with Input Password
	 * @author wbing
	 * @param oldPw String
	 * @param newPw String
	 * @return result boolean
	 */
	public static boolean validatePw(String oldPw, String newPw) {
		try {
			String loginPw = Utils.digest(newPw);
			if(!oldPw.equals(loginPw)) return false;
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}
		
		return true;
	}

	/***
	 * Supporting Function: When resetting password, the newly input password cant be the same as existing password
	 * @author wbing
	 * @param oldPw String
	 * @param newPw String
	 * @return result boolean
	 */
	public static boolean isSame(String oldPw, String newPw) {
		try {
			String newHash = Utils.digest(newPw);
			return newHash.equals(oldPw);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}
		return false;
	}
}
