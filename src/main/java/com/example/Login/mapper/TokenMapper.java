package com.example.Login.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface TokenMapper {

    Optional<String> findByEmail(String email);
    void updateToken(@Param("email") String email, @Param("token") String token);
    void deleteToken(String email);
}
