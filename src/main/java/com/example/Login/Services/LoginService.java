package com.example.Login.Services;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Login.Entity.User;
import com.example.Login.Repository.UserMapper;
import com.example.Login.utils.UsersNotFoundException;
import com.example.Login.utils.Utils;

@Service
public class LoginService {
	
	final Logger LOGGER = LogManager.getLogger(getClass());
	
	private UserMapper userMapper;
	
	@Autowired
	public LoginService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
	
	/***
	 * Insert User record into database.
	 * @author Insert User record into database.
	 * @param User user
	 * @return User user
	 */
	public User register(User user) {
		LOGGER.debug("Inserting record into database.");
		
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userMapper.getUser(user.getEmail());
		result.orElseThrow(IllegalStateException::new);
		
		UUID uuid = UUID.randomUUID();
		user.setUuid(uuid.toString());
		
		try {
			String hash = Utils.digest(result.get().getUuid().concat(result.get().getPassword()));
			user.setPassword(hash);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}
		return userMapper.insertUser(user);
	}

	/***
	 * Retrieve a single User record based on email address provided.
	 * @author wbing
	 * @param String email
	 * @return User user
	 * @throws UsersNotFoundException 
	 */
	public User getUser(String email) throws UsersNotFoundException  {
		LOGGER.debug("Retrieving user from database.");
		Optional<User> result = userMapper.getUser(email);
		return result
				.orElseThrow(UsersNotFoundException::new);
	}
	
	/***
	 * Retrieve all User records from database.
	 * @author wbing
	 * @return List<User> users
	 * @throws UsersNotFoundException 
	 */
	public List<User> getAllUsers() throws UsersNotFoundException {
		LOGGER.debug("Retrieving users from database.");
		Optional<List<User>> result = userMapper.getAllUser();
		return result
				.orElseThrow(UsersNotFoundException::new);
	}
	
	/***
	 * Update User record. 
	 * @author wbing
	 * @param User user
	 * @return User user
	 * @throws UsersNotFoundException 
	 */
	public User updateUser(User user) throws UsersNotFoundException {
		LOGGER.debug("Updating users from database.");
		Optional<User> result = userMapper.updateUser(user);
		return result
				.orElseThrow(UsersNotFoundException::new);
	}
	
	/***
	 * Verify if login user exist, if so, validate the password, else throw an exception.
	 * @author wbing
	 * @param User user
	 * @return String token
	 * @throws UsersNotFoundException
	 */
	public String login(User user) throws UsersNotFoundException {
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userMapper.getUser(user.getEmail());
		result.orElseThrow(UsersNotFoundException::new);
		
		try {
			LOGGER.info("Verifying User : " + result.get().getEmail());
			String loginPw = Utils.digest(result.get().getUuid().concat(result.get().getPassword()));
			
			if(!result.get().getPassword().equals(loginPw))
				throw new IllegalStateException("Credentials Mismatch.");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}
		
		return result.get().getUuid().toString();
	}
}
