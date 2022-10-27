package com.example.Login.Repository;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import com.example.Login.Entity.User;
import com.example.Login.utils.UsersNotFoundException;
import com.example.Login.utils.Utils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

//NOW ONLY WRITE TO JSON

@Repository
public class UserRepository {

	final Logger LOGGER = LogManager.getLogger(getClass());
	
	@Value("classpath:users.json")
	private Resource resource;
	private ObjectMapper mapper = new ObjectMapper();
	
	public User register(User user) throws IllegalArgumentException {
		if(isExist(user)) {
			LOGGER.warn("Email address is already in used.");
			throw new IllegalArgumentException("Email address is already in used.");
		}
		
		User newUser = new User();
		newUser.setEmail(user.getEmail());
		UUID uuid = UUID.randomUUID();
		newUser.setUuid(uuid);
		
		//hash
		try {
			String hash = Utils.digest(uuid+user.getPassword());
			newUser.setPassword(hash);
		}
		catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to hash password.");
		}
		
		//persist the data
		write(newUser);
		return newUser;
	}
	
	public User findUserByEmail(String email) throws UsersNotFoundException {
		LOGGER.info("Recieved : " + email);
		List<User> users = fetch();
		User result = null;
		
		try {
			for(User u : users) {
				LOGGER.info("Comparing with : " + u.getEmail());
				if(u.getEmail().equals(email)) result = u;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.warn("There are no users to begin with.");
		}
		
		if(result != null) {
			LOGGER.info("Found User : " + result.getEmail());
			return result;
		}
		else {
			throw new UsersNotFoundException("There is no such user.");
		}
	}
	
	public List<User> findAll() throws UsersNotFoundException{
		List<User> users = fetch();
		if(users != null) return users;
		else {
			throw new UsersNotFoundException("There are no user registered.");
		}
	}
	
	public boolean isExist(User user) {
		try {
			for(User u : fetch()) {
				if(u.getEmail().equals(user.getEmail())) return true;
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.warn("There are no users to begin with.");
		}
		return false;
	}
	
	private List<User> fetch() {
		List<User> users = null;
		
		LOGGER.info("Preparing to fetch data.");
		try {
			users = Arrays.asList(mapper.readValue(resource.getFile(), User[].class));
			LOGGER.info("Fetched" + users.toString());
		} catch (JsonParseException e) {
			LOGGER.error("Unable to parse JSON from file.");
		} catch (JsonMappingException e) {
			LOGGER.error("Unable to map JSON to Java Object.");
		} catch (IOException e) {
			LOGGER.error("Unable to load input file");
		}
		
		if(users == null) return new ArrayList<User>();
		else return users;
	}
	
	private void write(User user) {
		try {
			List<User> existingUsers = fetch();
			ArrayList<User> newList = new ArrayList<User>();
			LOGGER.info("Preparing to write new user: " + user.toString());
			LOGGER.info("Into : " + existingUsers.toString());
			
			newList.addAll(existingUsers);
			newList.add(user);
			
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(resource.getFile(), newList);
			LOGGER.info("Added \"" + user.getEmail() + "\" as new user");
		}
		catch(ArrayIndexOutOfBoundsException e) {
			LOGGER.warn("There are no users to begin with.");
		} catch (JsonGenerationException e) {
			LOGGER.error("Unable to parse JSON from file.");
		} catch (JsonMappingException e) {
			LOGGER.error("Unable to map JSON to Java Object.");
		} catch (IOException e) {
			LOGGER.error("Unable to load input file");
		}
	}
}
