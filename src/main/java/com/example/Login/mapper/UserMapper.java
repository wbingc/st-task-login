package com.example.Login.mapper;

import com.example.Login.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface UserMapper {
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
    List<User> findAll();
    void save(User user);
    void saveAll(List<User> user);
    void deleteByEmail(String email);
    void updateUser(@Param("user") User obj, @Param("email") String email);
    void updateAll(List<User> users);
}
