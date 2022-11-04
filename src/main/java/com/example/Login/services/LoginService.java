package com.example.Login.services;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.Login.entity.UserDTO;
import com.example.Login.utils.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Login.mapper.UserMapper;
import com.example.Login.utils.UsersNotFoundException;
import com.example.Login.utils.Utils;
import org.springframework.transaction.annotation.Transactional;

@Service
//@Transactional(readOnly = true)
public class LoginService {

	final Logger LOGGER = LogManager.getLogger(getClass());
	@Autowired
	private UserMapper userMapper;

	/***
	 * Verify if login user exist, if so, validate the password, else throw an exception.
	 * @author wbing
	 * @param user User user
	 * @return RedisUser token
	 * @throws UsersNotFoundException userRepo.findAll();
	 */
	public String login(UserDTO user) throws UsersNotFoundException {
		if(user == null) throw new IllegalArgumentException("Invalid Argument.");

		//check if user exist
		Optional<UserDTO> result = userMapper.findByEmail(user.getEmail());
		result.orElseThrow(UsersNotFoundException::new);
		if(!Utils.validatePw(result.get().getPassword(), user.getPassword()))
			throw new IllegalStateException("Credentials Mismatch.");
		return result.get().getToken();
	}

	/***
	 * Insert user into database.
	 * @author wbing
	 * @param  user User user
	 * @return User user
	 */
	@Transactional
	public UserDTO register(UserDTO user) throws SQLIntegrityConstraintViolationException {
		if(user == null) 
			throw new IllegalArgumentException("Invalid Argument.");

		try {
			UUID uuid = UUID.randomUUID();
			String hash = Utils.digest(user.getPassword());
			user.setToken(uuid.toString()).setStatus(Status.ACTIVE.toString()).setPassword(hash);
		} 
		catch (NoSuchAlgorithmException e) {
			LOGGER.debug(e.getMessage());
			LOGGER.error("Unable to compute hash of password.");
		}
		LOGGER.debug("Registering User : " + user.getEmail());
		//userMapper.save(user);
		userMapper.saveUserWithWallet(user);

		//test for db rollback
		Utils.makeError();
		return user;
	}

	/***
	 * Register a list of users define by JSON array
	 * @author wbing
	 * @param list List<User>
	 */
	@Transactional
	public void saveAll(List<UserDTO> list) throws SQLIntegrityConstraintViolationException {
		if(list == null || list.isEmpty())
			throw new IllegalArgumentException("Invalid Argument.");

		list.forEach(user -> {
			try {
				UUID uuid = UUID.randomUUID();
				String hash = Utils.digest(user.getPassword());
				user.setToken(uuid.toString()).setStatus(Status.ACTIVE.toString()).setPassword(hash);
				LOGGER.debug("implement updates: " + user.toString());
			} catch (NoSuchAlgorithmException e) {
				LOGGER.debug(e.getMessage());
				LOGGER.error("Unable to compute hash of password.");
			}
		});
		LOGGER.debug("Registering: " + list.toString());
		//userMapper.saveAll(list);
		userMapper.saveAllUserWithWallet(list);
		//test for db rollback
		Utils.makeError();
	}
	
	/***
	 * Retrieve a single User record based on email address provided.
	 * @author wbing
	 * @param email String email
	 * @return RedisUser user
	 * @throws UsersNotFoundException UserNotFoundException
	 */
	public UserDTO getUser(String email) throws UsersNotFoundException  {
		LOGGER.debug("Retrieving user from database.");
		Optional<UserDTO> result = userMapper.findByEmail(email);
		return result
				.orElseThrow(UsersNotFoundException::new);
	}
	
	/***
	 * Retrieve all User records from database.
	 * @author wbing
	 * @return List<RedisUser> users
	 * @throws UsersNotFoundException UserNotFoundException
	 */
	public List<UserDTO> getAllUsers() throws UsersNotFoundException {
		LOGGER.debug("Retrieving users from database.");
		return userMapper.findAll();
	}

	/***
	 * Retrieve all Session records from database.
	 * @author wbing
	 * @return List<Session> List of sessions
	 */
	public List<String> getAllSession() throws UsersNotFoundException {
		LOGGER.debug("Retrieving sessions from database.");
		List<UserDTO> resultSet = userMapper.findAll();
		if(resultSet.isEmpty()) throw new UsersNotFoundException("There are no user in records.");
		return resultSet.stream()
				.map(UserDTO::getToken)
				.collect(Collectors.toList());
	}

	/***
	 * Checks if the newly input password is the same as existing password, if not permit the reset.
	 * @author wbing
	 * @param email String
	 * @param obj UserDTO
	 * @throws UsersNotFoundException userNotFoundException
	 */
	@Transactional
	public void updatePassword(String email, UserDTO obj) throws UsersNotFoundException {
		LOGGER.debug("Resetting user password.");
		Optional<UserDTO> result = userMapper.findByEmail(email);
		result.orElseThrow(UsersNotFoundException::new);
		if(Utils.isSame(result.get().getPassword(), obj.getPassword()))
			throw new IllegalArgumentException("Cannot re-use password.");

		try {
			userMapper.updateUser(new UserDTO().setPassword(Utils.digest(obj.getPassword())), email);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.debug(e.getMessage());
			LOGGER.error("Unable to compute hash of password.");
		}
	}

	/***
	 * Update User information into database.
	 * @author wbing
	 * @param email String
	 * @param obj UserDTO
	 */
	@Transactional
	public void updateUser(String email, UserDTO obj) {
		LOGGER.debug("Updating user information.");
		userMapper.updateUser(obj, email);
		//test for db rollback
		Utils.makeError();
	}

	/***
	 * Batch update on User Table
	 * @author wbing
	 * @param list List<User>
	 */
	@Transactional
	public void updateAll(List<UserDTO> list) {
		if(list == null || list.isEmpty())
			throw new IllegalArgumentException("Invalid Argument.");

		LOGGER.debug("Batch update on user information.");
		LOGGER.debug(list.toString());
		userMapper.updateAll(list);
		//test for db rollback
		Utils.makeError();
	}

	/***
	 * Refreshes token for registered users
	 * @author wbing
	 * @param email String
	 * @return uuid String
	 */
	@Transactional
	public String refreshToken(String email) {
		LOGGER.debug("Refreshing token for : " + email);
		String uuid = UUID.randomUUID().toString();
		userMapper.updateUser(new UserDTO().setToken(uuid),email);
		return uuid;
	}
	
	/**
	 * Serve as test function for me to delete wrongly registered accounts.
	 * @author wbing
	 * @param email String
	 */
	@Transactional
	public void deleteUser(String email) {
		LOGGER.debug("Deleting user from database.");
		userMapper.deleteByEmail(email);
		//test for db rollback
		Utils.makeError();
	}

	@Transactional
	public void deleteAll(List<UserDTO> list) {
		LOGGER.debug("Deleting a list of users from database.");
		LOGGER.debug(list.toString());
		userMapper.deleteAll(list);
		//test for db rollback
		Utils.makeError();
	}
}
