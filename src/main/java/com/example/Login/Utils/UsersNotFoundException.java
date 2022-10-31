package com.example.Login.Utils;

public class UsersNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UsersNotFoundException() {}
	
	public UsersNotFoundException(String message) {
		super(message);
	}

}
