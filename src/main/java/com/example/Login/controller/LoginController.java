package com.example.Login.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

import com.example.Login.entity.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.Login.services.LoginService;
import com.example.Login.utils.UsersNotFoundException;

@RestController
@RequestMapping("/api")
public class LoginController {

	@Autowired
	private LoginService loginService;

	final Logger LOGGER = LogManager.getLogger();

	@PostMapping(value = "/auth/login",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> login(@RequestBody UserDTO user) {
		try {
			String token = loginService.login(user);
			return ResponseEntity.ok().body("Token: " + token);
		} catch (UsersNotFoundException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("No user record found.");
		} catch (IllegalStateException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping(value = "/auth/fakelogin",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fakeLogin(@RequestBody UserDTO user) {
		return ResponseEntity.ok().body(UUID.randomUUID().toString());
	}
	
	@PostMapping(value = "/auth/signup",
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> register(@RequestBody UserDTO user) {
		try {
			UserDTO result = loginService.register(user);
			return ResponseEntity.ok().body("User: " + result.getEmail() + " registered.");
		} catch (IllegalArgumentException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (SQLIntegrityConstraintViolationException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Email Address is already in used.");
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@PostMapping(value = "/auth/signup/all",
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> registerAll(@RequestBody List<UserDTO> list) {
		try {
			loginService.saveAll(list);
			return ResponseEntity.ok().body("Users registered.");
		} catch (IllegalArgumentException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (SQLIntegrityConstraintViolationException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Email Address is already in used.");
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to register user.");
		}
	}
	
	//require authentication
	@PostMapping("/hello")
	public ResponseEntity<String> sayHello() {
		return ResponseEntity.ok().body("Hello, " +
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	@GetMapping("/user/{email}")
	public ResponseEntity<UserDTO> getByEmail(@PathVariable("email") String email) {
		try {
			UserDTO result = loginService.getUser(email);
			return ResponseEntity.ok().body(result);
		} catch (UsersNotFoundException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/user/all") 
	public ResponseEntity<List<UserDTO>> listAll() {
		try {
			return ResponseEntity.ok().body(loginService.getAllUsers());
		} catch (UsersNotFoundException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/user/token")
	public ResponseEntity<List<String>> listAllSession() {
		try {
			return ResponseEntity.ok().body(loginService.getAllSession());
		}
		catch (UsersNotFoundException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/reset",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updatePassword(@RequestBody UserDTO obj) {
		try{
			loginService.updatePassword(
					SecurityContextHolder.getContext().getAuthentication().getName(), obj);
			return ResponseEntity.ok().body("Password successfully reset.");
		} catch (IllegalArgumentException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (UsersNotFoundException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("No user record found.");
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@PutMapping(value = "/update",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateUser(@RequestBody UserDTO user) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		try{
			loginService.updateUser(authentication.getName(), user);
			return ResponseEntity.ok().body("Successfully updated user details for \"" +
					authentication.getName() + "\".");
		}
		catch (Exception e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@PutMapping(value = "/update/all",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateAll(@RequestBody List<UserDTO> list) {
		try{
			loginService.updateAll(list);
			return ResponseEntity.ok().body("Successfully updated user details.");
		}
		catch (IllegalArgumentException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@PutMapping("/refresh/{email}")
	public ResponseEntity<String> refreshToken(@PathVariable String email) {
		try {
			String token = loginService.refreshToken(email);
			return ResponseEntity.ok().body("New Token: " + token);
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@DeleteMapping("/delete/{email}")
	public ResponseEntity<String> deleteUser(@PathVariable String email) {
		try {
			loginService.deleteUser(email);
			return ResponseEntity.ok().body(email + " is deleted.");
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@DeleteMapping(value = "/delete/all",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteAll(@RequestBody List<UserDTO> list) {
		try {
			loginService.deleteAll(list);
			return ResponseEntity.ok().body("Users are deleted.");
		} catch (IllegalArgumentException e) {
			LOGGER.debug(e.getMessage());
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}
}
