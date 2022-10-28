package com.example.Login.Repository;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import com.example.Login.Entity.User;

//https://www.baeldung.com/spring-data-redis-tutorial

@EnableRedisRepositories
public interface UserRepository extends CrudRepository<User, String> {

}
