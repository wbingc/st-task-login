package com.example.Login.services;

import com.example.Login.entity.User;
import com.example.Login.mapper.UserMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SessionAuthenticationService {

	final Logger LOGGER = LogManager.getLogger(getClass());
	private static final String BEARER_PREFIX = "Bearer ";

	@Autowired
	private UserMapper userMapper;

	private enum Role {
		USER
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
		Optional<User> result = userMapper.findByToken(token);
		if(result.isPresent()) {
			LOGGER.debug("Creating authentication for : " + result.get().getEmail());
			Authentication authentication = create(result.get().getEmail());
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
