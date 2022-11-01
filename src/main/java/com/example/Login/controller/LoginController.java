package com.example.Login.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Login.entity.Session;
import com.example.Login.entity.User;
import com.example.Login.services.LoginService;
import com.example.Login.utils.UsersNotFoundException;

@RestController
@RequestMapping("/api")
public class LoginController {

	private final LoginService loginService;
	
	@Autowired
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}
	
	@PostMapping(value = "/auth/login",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> login(@RequestBody User user) {
		try {
			String token = loginService.login(user);
			return ResponseEntity.ok().body("Token: " + token);
		} catch (UsersNotFoundException e) {
			return ResponseEntity.badRequest().body("No user record found.");
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body("Email Address is already in used.");
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
		}
	}
	
	//require authentication
	@PostMapping("/hello")
	public ResponseEntity<String> sayHello() {
		//Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//return ResponseEntity.ok().body("Hello, " + authentication.getName());
		return ResponseEntity.ok().body("Hello, ");
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
	
	@GetMapping("/session/all")
	public ResponseEntity<List<Session>> listAllSession() {
		return ResponseEntity.ok(loginService.getAllSession());
	}
	
	@PostMapping("/user/delete")
	public ResponseEntity<String> deleteUser(@RequestBody User user) {
		loginService.deleteUser(user);
		return ResponseEntity.ok().body(user.getEmail() + " is deleted.");
	}
}
