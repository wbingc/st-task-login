package com.example.Login.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Service
public class RedisAuthenticationService {
	
	final Logger LOGGER = LogManager.getLogger(getClass());
	
	private static final String BEARER_PREFIX = "Bearer ";
	
	private enum Role {
		USER
	}

}
