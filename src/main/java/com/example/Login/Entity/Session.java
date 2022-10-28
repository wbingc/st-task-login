package com.example.Login.Entity;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Session")
public class Session implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String token;
	
	@Id
	private String email;
	
	public Session() {}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return new String("Session [ Token: \"" + this.token + "\", Email : \"" + this.email + "\"]");
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, token);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		return Objects.equals(email, other.email) && Objects.equals(token, other.token);
	}
}