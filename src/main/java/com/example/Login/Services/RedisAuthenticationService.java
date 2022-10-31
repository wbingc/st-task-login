package com.example.Login.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import com.example.Login.Entity.Session;
import com.example.Login.Repository.SessionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.google.common.net.HttpHeaders;

@Service
public class RedisAuthenticationService {
	
	final Logger LOGGER = LogManager.getLogger(getClass());
	
	private static final String BEARER_PREFIX = "Bearer ";
	private final SessionRepository sessionRepo;
	
	private enum Role {
		USER
	}

	@Autowired
	public RedisAuthenticationService(SessionRepository sessionRepo) {
		this.sessionRepo = sessionRepo;
	}
	
	/***
	 * Returns the newly created Authentication obj
	 * @param  request HttpServletRequest
	 * @return Optional<Authentication> authentication
	 */
	public Optional<Authentication> authenticate(HttpServletRequest request) {
		LOGGER.debug("In progress for authentication.");
		return extractToken(request).flatMap(this::lookup);
	}
	
	/***
	 * Creates an authentication obj from token
	 * @param token String token
	 * @return Optional<Authentication> authentication
	 */
	private Optional<Authentication> lookup(String token) {
		LOGGER.info("Lookup : " + token);
		Optional<Session> session = sessionRepo.findByToken(token);
		if(session.isPresent()) {
			LOGGER.debug("Creating authentication for : " + session.get().getEmail());
			Authentication authentication = create(session.get().getEmail());
			return Optional.of(authentication);
		}
		return Optional.empty();
	}
	
	/***
	 * To extract token string value from http header
	 * @param request HttpServletRequest
	 * @return Optional<String> token
	 */
	private Optional<String> extractToken(HttpServletRequest request) {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		LOGGER.debug("What's in my header : " + authorization);
		if(authorization != null) {
			if(authorization.startsWith(BEARER_PREFIX)) {
				String token = authorization.substring(BEARER_PREFIX.length()).trim();
				if(!token.isBlank()) return Optional.of(token);
			}
		}
		return Optional.empty();
	}
	
	/***
	 * Create a new Authentication obj.
	 * @param email String email
	 * @return authentication Authentication authentication
	 */
	private Authentication create(String email) {
		if(email.isEmpty()) email = "N/A";
		List<GrantedAuthority> authorities = Stream.of(Role.USER)
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
				.collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(email, "N/A", authorities);
	}

}
