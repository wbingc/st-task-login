package com.example.Login.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.Login.Entity.User;


@Repository
public interface UserRepository extends CrudRepository<User, String> {

}
