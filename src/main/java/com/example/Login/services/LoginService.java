package com.example.Login.services;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Login.entity.Session;
import com.example.Login.entity.User;
import com.example.Login.mapper.SessionMapper;
import com.example.Login.mapper.UserMapper;
import com.example.Login.utils.UsersNotFoundException;
import com.example.Login.utils.Utils;

@Service
public class LoginService {

	final Logger LOGGER = LogManager.getLogger(getClass());
	private final UserMapper userMapper;
	private final SessionMapper sessionMapper;
	
	@Autowired
	public LoginService(UserMapper userRepo, SessionMapper sessionRepo) {
		this.userMapper = userRepo;
		this.sessionMapper = sessionRepo;
	}
	
	/***
	 * Insert user into database.
	 * @author wbing
	 * @param  user User user
	 * @return User user
	 */
	public User register(User user) {
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userMapper.findByEmail(user.getEmail());
		if(result.isPresent()) throw new IllegalStateException("Email Address is already in used.");
		
		UUID uuid = UUID.randomUUID();
		Session session = new Session();
		session.setToken(uuid.toString());
		session.setEmail(user.getEmail());

		try {
			String hash = Utils.digest(user.getPassword());
			user.setPassword(hash);
		} 
		catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}

		LOGGER.info("Registering User : " + user.getEmail());
		userMapper.save(user);
		sessionMapper.save(session);

		return user;
	}
	
	/***
	 * Retrieve a single User record based on email address provided.
	 * @author wbing
	 * @param email String email
	 * @return RedisUser user
	 * @throws UsersNotFoundException UserNotFoundException
	 */
	public User getUser(String email) throws UsersNotFoundException  {
		LOGGER.debug("Retrieving user from database.");
		Optional<User> result = userMapper.findByEmail(email);
		return result
				.orElseThrow(UsersNotFoundException::new);
	}
	
	/***
	 * Retrieve all User records from database.
	 * @author wbing
	 * @return List<RedisUser> users
	 * @throws UsersNotFoundException UserNotFoundException
	 */
	public List<User> getAllUsers() throws UsersNotFoundException {
		LOGGER.debug("Retrieving users from database.");
		return userMapper.findAll();
	}
	
	/**
	 * Serve as test function for me to delete wrongly registered accounts.
	 * @author wbing
	 * @param user User user
	 */
	public void deleteUser(User user) {
		LOGGER.debug("Deleting user from database.");
		userMapper.deleteByEmail(user.getEmail());
		sessionMapper.deleteByEmail(user.getEmail());
	}
	
	/***
	 * Verify if login user exist, if so, validate the password, else throw an exception.
	 * @author wbing
	 * @param user User user
	 * @return RedisUser token
	 * @throws UsersNotFoundException userRepo.findAll();
	 */
	public String login(User user) throws UsersNotFoundException {
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userMapper.findByEmail(user.getEmail());
		result.orElseThrow(UsersNotFoundException::new);

		boolean valid = Utils.validate(result.get().getPassword(), user.getPassword());
		if(!valid) throw new IllegalStateException("Credentials Mismatch.");
		
		//retrieve token
		Optional<Session> sResult = sessionMapper.findByEmail(user.getEmail());
		sResult.orElseThrow(UsersNotFoundException::new);
		
		return sResult.get().getToken();
	}
	
	
	/***
	 * Retrieve all Session records from database.
	 * @author wbing
	 * @return List<Session> List of sessions
	 */
	public List<Session> getAllSession() {
		LOGGER.debug("Retrieving sessions from database.");
		return sessionMapper.findAll();
	}
}
