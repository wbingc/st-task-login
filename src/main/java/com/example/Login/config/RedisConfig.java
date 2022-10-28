package com.example.Login.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	
	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private int port;
	
	@Value("${spring.redis.password}")
	private String password;
	
	@Bean
	JedisConnectionFactory jedisConnectFactory() { //define for jedis client
		RedisStandaloneConfiguration rsc = new RedisStandaloneConfiguration();
		rsc.setHostName(host);
		rsc.setPort(port);
		rsc.setPassword(RedisPassword.of(password));
		return new JedisConnectionFactory(rsc);
	}
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectFactory());
		return template;
	}
	
	//For session
	@Bean(name="sessionTemplate")
	public RedisTemplate<String,String> redisSessionTemplate() {
		RedisTemplate<String,String> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectFactory());
		template.setDefaultSerializer(new StringRedisSerializer());
		return template;
	}

}
