package com.example.Login.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class Session implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	private String token;
	private String email;
}
