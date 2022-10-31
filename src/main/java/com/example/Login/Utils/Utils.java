package com.example.Login.Utils;

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
	 * @param plaintext
	 * @return String digest
	 * @throws NoSuchAlgorithmException
	 */
	public static String digest(String plaintext) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(plaintext.getBytes());
		byte[] digest = md.digest();
		
		StringBuffer hexString = new StringBuffer();
		
		for(int i=0; i<digest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & digest[i]));
		}
		return hexString.toString();
	}
	
	/***
	 * Validate Stored Password Hash with Input Password
	 * @author wbing
	 * @param originalPassword
	 * @param password
	 * @return boolean result
	 */
	public static boolean validate(String originalPassword, String password) {		
		try {
			String loginPw = Utils.digest(password);
			
			if(!originalPassword.equals(loginPw))
				return false;
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}
		
		return true;
	}
}
