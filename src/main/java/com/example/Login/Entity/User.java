package com.example.Login.Entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Users")
@Data
@NoArgsConstructor
public class User implements Serializable{

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	private String email;
	private String password;
}
