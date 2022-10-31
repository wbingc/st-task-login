package com.example.Login.Entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("Session")
@Data
@NoArgsConstructor
public class Session implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Indexed
	private String token;
	
	@Id
	private String email;
}
