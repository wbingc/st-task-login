package com.example.Login.Services;

import java.security.NoSuchAlgorithmException;
import java.util.List;

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
	
	private final UserRepository userRepo;
	
	@Autowired
	public LoginService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public User register(User user) throws IllegalArgumentException {
		return userRepo.register(user);
	}
	
	public List<User> listUsers() throws UsersNotFoundException {
		return userRepo.findAll();
	}
	
	public User getUser(String email) throws UsersNotFoundException {
		return userRepo.findUserByEmail(email);
	}
	
	//return token to authenticated user
	public String login(User user) throws UsersNotFoundException, IllegalStateException {
		User result = userRepo.findUserByEmail(user.getEmail());
		
		try {
			String loginPw = Utils.digest(result.getUuid().toString() + user.getPassword());
			LOGGER.info("Verifying User : " + result.getEmail());
			
			if(!result.getPassword().equals(loginPw)) {
				throw new IllegalStateException("Credentials Mismatch.");
			}
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Unable to hash password.");
		}
		return result.getUuid().toString();
	}
}
