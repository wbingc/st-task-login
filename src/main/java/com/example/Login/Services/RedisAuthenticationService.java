package com.example.Login.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
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
	
	@Autowired
	@Qualifier("sessionTemplate") private RedisTemplate<String, String> redis;
	
	private enum Role {
		USER,
		ADMIN
	}
	
	/***
	 * Returns the newly created Authentication obj
	 * @param HttpServletRequest request
	 * @return Optional<Authentication> authentication
	 */
	public Optional<Authentication> authenticate(HttpServletRequest request) {
		return extractToken(request).flatMap(this::lookup);
	}
	
	/***
	 * Creates an authentication obj from token
	 * @param String token
	 * @return Optional<Authentication> authentication
	 */
	private Optional<Authentication> lookup(String token) {
		String email = this.redis.opsForValue().get(token);
		if(email != null) {
			Authentication authentication = create(email, Role.USER);
			return Optional.of(authentication);
		}
		return Optional.empty();
	}
	
	/***
	 * To extract token string value from http header
	 * @param HttpServletRequest request
	 * @return Optional<String> token
	 */
	private static Optional<String> extractToken(HttpServletRequest request) {
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
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
	 * @param String email
	 * @param Role roles
	 * @return Authentication authentication
	 */
	private static Authentication create(String email, Role roles) {
		String name = email;
		if(email == null) name = "N/A";
		
		List<GrantedAuthority> authorities = Stream.of(roles)
				.distinct()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
				.collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(name, "N/A", authorities);
	}

}
