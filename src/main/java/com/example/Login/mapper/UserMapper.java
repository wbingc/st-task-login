package com.example.Login.mapper;

import com.example.Login.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserMapper {
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void save(User user);
    void deleteByEmail(String email);
}
