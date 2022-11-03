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
    List<UserDTO> findAll();
    void save(User user);
    void saveAll(List<User> users);
    void saveUserWithWallet(UserDTO user);
    void saveAllUserWithWallet(List<UserDTO> users);
    void deleteByEmail(String email);
    void deleteAll(List<UserDTO> users);
    void updateUser(@Param("user") UserDTO obj, @Param("email") String email);
    void updateAll(List<UserDTO> users);
}
