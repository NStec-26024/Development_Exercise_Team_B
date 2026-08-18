package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.EmployeeAccount;

@Mapper
public interface EmployeeAccountRepository {

    /**
     * 指定したユーザー名のパスワードを返す。
     *
     * @param name ユーザー名
     * @return パスワード（存在しない場合は null）
     */
    EmployeeAccount findByName(String name);
}
