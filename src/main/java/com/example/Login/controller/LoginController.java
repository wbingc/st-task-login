package com.example.Login.controller;

import java.util.List;
import java.util.UUID;

import com.example.Login.entity.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.Login.entity.User;
import com.example.Login.services.LoginService;
import com.example.Login.utils.UsersNotFoundException;

@RestController
@RequestMapping("/api")
public class LoginController {

	@Autowired
	private LoginService loginService;
	
	@PostMapping(value = "/auth/login",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> login(@RequestBody User user) {
		try {
			String token = loginService.login(user);
			return ResponseEntity.ok().body("Token: " + token);
		} catch (UsersNotFoundException e) {
			return ResponseEntity.badRequest().body("No user record found.");
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping(value = "/auth/fakelogin",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fakeLogin(@RequestBody User user) {
		return ResponseEntity.ok().body(UUID.randomUUID().toString());
	}
	
	@PostMapping(value = "/auth/signup",
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> register(@RequestBody User user) {
		try {
			User result = loginService.register(user);
			return ResponseEntity.ok().body("User: " + result.getEmail() + " registered.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Email Address is already in used.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Unable to register.");
		}
	}
	
	//require authentication
	@PostMapping("/hello")
	public ResponseEntity<String> sayHello() {
		return ResponseEntity.ok().body("Hello, " +
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	@GetMapping("/user/{email}")
	public ResponseEntity<User> getByEmail(@PathVariable("email") String email) {
		try {
			User result = loginService.getUser(email);
			return ResponseEntity.ok().body(result);
		} catch (UsersNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/user/all") 
	public ResponseEntity<List<User>> listAll() {
		try {
			return ResponseEntity.ok(loginService.getAllUsers());
		} catch (UsersNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/user/token")
	public ResponseEntity<List<String>> listAllSession() {
		try {
			return ResponseEntity.ok().body(loginService.getAllSession());
		}
		catch (UsersNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/reset")
	public ResponseEntity<String> updatePassword(@RequestBody UserDTO obj) {
		try{
			loginService.updatePassword(
					SecurityContextHolder.getContext().getAuthentication().getName(), obj);
			return ResponseEntity.ok().body("Password successfully reset.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (UsersNotFoundException e) {
			return ResponseEntity.badRequest().body("No user record found.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateStatus(@RequestBody UserDTO user) {
		try{
			loginService.updateUser(
					SecurityContextHolder.getContext().getAuthentication().getName(),
					user);
			return ResponseEntity.ok().body("Successfully updated user details for \""
				+ SecurityContextHolder.getContext().getAuthentication().getName() + "\".");
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Unable to process request.");
		}
	}

	@PutMapping("/refresh/{email}")
	public ResponseEntity<String> refreshToken(@PathVariable String email) {
		String token = loginService.refreshToken(email);
		return ResponseEntity.ok().body("New Token: " + token);
	}

	@DeleteMapping("/delete/{email}")
	public ResponseEntity<String> deleteUser(@PathVariable String email) {
		loginService.deleteUser(email);
		return ResponseEntity.ok().body(email + " is deleted.");
	}
}
