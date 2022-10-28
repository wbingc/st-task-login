package com.example.Login.Services;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Login.Entity.Session;
import com.example.Login.Entity.User;
import com.example.Login.Repository.SessionRepository;
import com.example.Login.Repository.UserRepository;
import com.example.Login.utils.UsersNotFoundException;
import com.example.Login.utils.Utils;

@Service
public class LoginService {

	final Logger LOGGER = LogManager.getLogger(getClass());
	private final UserRepository userRepo;
	private final SessionRepository sessionRepo;
	
	@Autowired
	public LoginService(UserRepository userRepo, SessionRepository sessionRepo) {
		this.userRepo = userRepo;
		this.sessionRepo = sessionRepo;
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
		Optional<User> result = userRepo.findById(user.getEmail());
		if(result.isPresent()) throw new IllegalStateException("Email Address is already in used.");
		
		UUID uuid = UUID.randomUUID();
		Session session = new Session();
		session.setToken(uuid.toString());
		session.setEmail(user.getEmail());
		sessionRepo.save(session);
		
		try {
			String hash = Utils.digest(user.getPassword());
			user.setPassword(hash);
		} 
		catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}

		LOGGER.info("Registering User : " + user.getEmail());
		
		return userRepo.save(user);
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
		Optional<User> result = userRepo.findById(email);
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
		return (List<User>) userRepo.findAll();
	}
	
	/***
	 * Update User record. 
	 * @author wbing
	 * @param  user User user
	 * @return RedisUser user
	 * @throws UsersNotFoundException userRepo.findAll();
	 */
	public User updateUser(User user) throws UsersNotFoundException {
		LOGGER.debug("Updating users from database.");
		
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userRepo.findById(user.getEmail());
		result.orElseThrow(UsersNotFoundException::new);
		
		result.get().setEmail(user.getEmail());
		return userRepo.save(result.get());
	}
	
	/**
	 * Serve as test function for me to delete wrongly registered accounts.
	 * @author wbing
	 * @param user User user
	 */
	public void deleteUser(User user) {
		LOGGER.debug("Deleting user from database.");
		userRepo.delete(user);
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
		Optional<User> result = userRepo.findById(user.getEmail());
		result.orElseThrow(UsersNotFoundException::new);
		
		boolean valid = Utils.validate(result.get().getPassword(), user.getPassword());
		if(!valid) throw new IllegalStateException("Credentials Mismatch.");
		
		//retrieve token
		Optional<Session> sResult = sessionRepo.findById(user.getEmail());
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
		return (List<Session>) sessionRepo.findAll();
	}
}
