package com.example.Login.mapper;

import com.example.Login.entity.UserDTO;
import com.example.Login.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface UserMapper {
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
    List<User> findAll();
    void save(User user);
    void deleteByEmail(String email);
    void updatePassword(@Param("email") String email, @Param("password") String password);
    void updateUser(@Param("user") UserDTO obj, @Param("email") String email);
    void updateToken(@Param("email") String email, @Param("token") String token);
}
