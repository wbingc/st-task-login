package com.example.Login.mapper;

import com.example.Login.entity.Wallet;

import java.util.Optional;

public interface WalletMapper {

    Optional<Wallet> findByEmail(String email);
    void update(Wallet wallet);
    void delete(String email);
}
