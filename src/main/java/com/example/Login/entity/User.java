package com.example.Login.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class User {

	private int id;
	private String email;
	private String name;
	private String password;
	private String status;
	private String token;
}
