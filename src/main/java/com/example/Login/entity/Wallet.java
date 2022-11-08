package com.example.Login.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Wallet {

    private int id;
    private String email;
    private float balance;
}