package com.example.fullness.stationary.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 顧客を表すエンティティクラス。
 */
@Data
public class Customer {
    private Integer id;
    private String name;
    private String nameKana;
    private String address1;
    private String address2;
    private String phoneNumber;
    private String mailAddress;
    private String username;
    private String password;
    private LocalDateTime registerDate;
}
