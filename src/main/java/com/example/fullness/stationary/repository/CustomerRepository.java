package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.Customer;

@Mapper
public interface CustomerRepository {
    Customer findByMailAddress(String mailAddress);
}
