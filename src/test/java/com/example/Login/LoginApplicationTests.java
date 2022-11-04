package com.example.Login;

import com.example.Login.entity.UserDTO;
import com.example.Login.services.LoginService;
import com.example.Login.utils.UsersNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class LoginApplicationTests {

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Autowired
	private LoginService loginService;

	@Autowired
	private RestTemplate restTemplate;

	private MockRestServiceServer mockServer;
	private ObjectMapper mapper = new ObjectMapper();
	private String url = "http://localhost:8081/api";

	@Test
	void contextLoads() {
	}

	@BeforeEach
	void init() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}
	@BeforeTransaction
	void logBeforeDb() {
		LOGGER.info("Entering a transaction.");
	}
	@AfterTransaction
	void logAfterDb(){
		LOGGER.info("Exited a transaction.");
	}

	UserDTO baseline() {
		return new UserDTO()
				.setId(85)
				.setEmail("testing@gmail.com")
				.setName("Eason")
				.setPassword("3ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4")
				.setStatus("ACTIVE")
				.setToken("63baea08-9782-499c-bb7b-2d070b41bb79")
				.setBalance(0);
	}

	@Test
	void testListingOfSingleUser() throws URISyntaxException, JsonProcessingException, UsersNotFoundException {
		mockServer.expect(ExpectedCount.once(),
				MockRestRequestMatchers.requestTo(new URI(url+"/user/testing@gmail.com")))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(MockRestResponseCreators.withSuccess(mapper.writeValueAsString(baseline()), MediaType.APPLICATION_JSON));

		UserDTO result = loginService.getUser("testing@gmail.com");
		mockServer.verify();
		Assertions.assertEquals(baseline(),result);

	}
	@Test
	void testUpdateOfUser() throws URISyntaxException, JsonProcessingException, UsersNotFoundException {
		UserDTO user = new UserDTO()
				.setId(90)
				.setEmail("mock@gmail.com")
				.setName("Mock")
				.setPassword("3ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4")
				.setStatus("ACTIVE")
				.setToken("63baea08-9782-499c-bb7b-2d070b41bb79")
				.setBalance(30f);

		mockServer.expect(ExpectedCount.once(),
				MockRestRequestMatchers.requestTo(new URI(url+"/update")))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(MockRestResponseCreators.withSuccess(mapper.writeValueAsString(user), MediaType.APPLICATION_JSON));

		loginService.updateUser("testing@gmail.com", user);
		UserDTO result = loginService.getUser(user.getEmail());

		mockServer.verify();
		Assertions.assertEquals(user,result);

	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


}
