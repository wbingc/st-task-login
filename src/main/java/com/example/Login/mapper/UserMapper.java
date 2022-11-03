package com.example.Login.mapper;

import com.example.Login.entity.User;
import com.example.Login.entity.UserDTO;
import com.example.Login.entity.Wallet;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface UserMapper {
    //Optional<User> findByEmail(String email);
    Optional<UserDTO> findByEmail(String email);
    Optional<User> findByToken(String token);
    List<User> findAll();
    void save(User user);
    void saveAll(List<User> users);
    void saveUserWithWallet(User user);
    void saveAllUserWithWallet(List<User> users);
    void deleteByEmail(String email);
    void deleteAll(List<User> users);
    void updateUser(@Param("user") User obj, @Param("email") String email);
    void updateAll(List<User> users);
}
