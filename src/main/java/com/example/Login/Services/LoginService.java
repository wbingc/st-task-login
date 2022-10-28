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
import com.example.Login.Repository.UserRepository;
import com.example.Login.utils.UsersNotFoundException;
import com.example.Login.utils.Utils;

@Service
public class LoginService {

	final Logger LOGGER = LogManager.getLogger(getClass());
	
	private UserRepository userRepo;
	
	@Autowired
	public LoginService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	/***
	 * Insert user into database.
	 * @author wbing
	 * @param User user
	 * @return RedisUser user
	 */
	public User register(User user) {
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userRepo.findById(user.getEmail());
		if(!result.isEmpty()) throw new IllegalStateException("Email Address is already in used.");
		
		UUID uuid = UUID.randomUUID();
		user.setUuid(uuid.toString());
		
		try {
			String hash = Utils.digest(uuid.toString().concat(user.getPassword()));
			user.setPassword(hash);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to compute hash of password.");
		}
		LOGGER.info("Registering User : " + user.getEmail());
		
		return userRepo.save(user);
	}
	
	/***
	 * Retrieve a single User record based on email address provided.
	 * @author wbing
	 * @param String email
	 * @return RedisUser user
	 * @throws UsersNotFoundException 
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
	 * @throws UsersNotFoundException 
	 */
	public List<User> getAllUsers() throws UsersNotFoundException {
		LOGGER.debug("Retrieving users from database.");
		List<User> result = (List<User>) userRepo.findAll();
		return result;
	}
	
	/***
	 * Update User record. 
	 * @author wbing
	 * @param User user
	 * @return RedisUser user
	 * @throws UsersNotFoundException 
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
	
	/***
	 * Verify if login user exist, if so, validate the password, else throw an exception.
	 * @author wbing
	 * @param User user
	 * @return RedisUser token
	 * @throws UsersNotFoundException
	 */
	public String login(User user) throws UsersNotFoundException {
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userRepo.findById(user.getEmail());
		result.orElseThrow(UsersNotFoundException::new);
		
		boolean valid = Utils.validate(result.get().getPassword(), result.get().getUuid(), user.getPassword());
		if(!valid) throw new IllegalStateException("Credentials Mismatch.");
		
		return result.get().getUuid().toString();
	}
	
	/***
	 * Testing endpoint that validates token before returning the result
	 * @author wbing
	 * @param User
	 * @return String str
	 * @throws UsersNotFoundException
	 */
	public String sayHello(User user) throws UsersNotFoundException {
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");
		
		//check if user exist
		Optional<User> result = userRepo.findById(user.getEmail());
		result.orElseThrow(UsersNotFoundException::new);
				
		boolean valid = Utils.validate(result.get().getPassword(), result.get().getUuid(), user.getPassword());
		if(!valid) throw new IllegalStateException("Invalid Token.");
		else
			return "Hello World";
	}
}
