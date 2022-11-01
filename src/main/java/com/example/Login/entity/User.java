package com.example.Login.entity;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Serializable{

	@Serial
	private static final long serialVersionUID = 1L;

	private String email;
	private String password;
}
