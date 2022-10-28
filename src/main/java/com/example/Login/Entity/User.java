package com.example.Login.Entity;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Users")
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	private String email;
	
	private String password;
	private String uuid;

	public User() {};

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return new String("User[email:\"" + this.email + "\", password:\"" + this.password + "\"]");
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof User)) return false;
		
		User user = (User) obj;
		return Objects.equals(this.email, user.email) && Objects.equals(this.password, user.password);
		
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.email, this.password);
	}

}
