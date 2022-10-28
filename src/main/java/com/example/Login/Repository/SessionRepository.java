package com.example.Login.Repository;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import com.example.Login.Entity.Session;

@EnableRedisRepositories
public interface SessionRepository extends CrudRepository<Session, String>{

}
