package com.example.Login.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

//https://www.danvega.dev/blog/2022/09/06/spring-security-jwt/
// https://github.com/danvega/

@Configuration
public class SecurityConfig {
	
	@Value("${jwt.password}")
	private String password;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.mvcMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
						.mvcMatchers(HttpMethod.GET, "/api/test/hello").authenticated()
						)
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
	
	@Bean
	public JwtDecoder decoder() throws NoSuchAlgorithmException, InvalidKeySpecException {
		return NimbusJwtDecoder.withSecretKey(getKeyFromPassword(password)).build();
	}
	
	private SecretKey getKeyFromPassword(String uuid) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String salt = "1234";
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(
				uuid.toCharArray(),
				salt.getBytes(),
				65536,
				256);
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}
	
	/***
	@Bean
	public JwtEncoder encoder() throws NoSuchAlgorithmException, InvalidKeySpecException {
		JWK jwk = new OctetSequenceKey.Builder(getKeyFromPassword(password)).build(); //generate token with uuid
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}
	***/
}
