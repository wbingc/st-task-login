package com.example.Login.mapper;

import com.example.Login.entity.Session;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SessionMapper {

    Optional<Session> findByEmail(String email);
    Optional<Session> findByToken(String token);
    List<Session> findAll();
    void save(Session sessions);
    void deleteByEmail(String email);
    void deleteByToken(String token);
}
