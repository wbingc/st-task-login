package com.example.Login.Config;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.Login.Services.RedisAuthenticationService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	final Logger LOGGER = LogManager.getLogger(getClass());

	@Autowired
	private RedisAuthenticationService redisAuthService;
	
	@Bean
	public AuthenticationManager authManager(UserDetailsService uss) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(uss);
		return new ProviderManager(authProvider);
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
								.mvcMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
								.mvcMatchers(HttpMethod.POST, "/api/hello").authenticated()
								.mvcMatchers(HttpMethod.GET, "/api/user/**").permitAll()
						)
				.addFilterAt(this::authenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
	
	//nopassword only for testing
	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.withUsername("testing")
				.password("{noop}password")
				.authorities("read")
				.build());
		
	}
	
	private void authenticationFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Optional<Authentication> authentication = this.redisAuthService.authenticate((HttpServletRequest)request);
		authentication.ifPresent(
				SecurityContextHolder.getContext()::setAuthentication);
		chain.doFilter(request, response);
	}
}
