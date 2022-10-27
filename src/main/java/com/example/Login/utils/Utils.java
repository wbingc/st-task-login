package com.example.Login.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

//https://www.tutorialspoint.com/java_cryptography/java_cryptography_message_digest.htm
//bcrypt: https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt

@Component
public class Utils {
	
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
}
