package com.example.Login.Repository;

import java.util.Optional;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import com.example.Login.Entity.Session;

@EnableRedisRepositories
public interface SessionRepository extends CrudRepository<Session, String>{
	Optional<Session> findByToken(String token);
}
