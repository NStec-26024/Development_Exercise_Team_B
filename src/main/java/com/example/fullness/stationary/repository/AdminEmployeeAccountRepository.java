package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.EmployeeAccount;

@Mapper
public interface AdminEmployeeAccountRepository {

    public int insertEmployeeAccount(EmployeeAccount employeeAccount);

    public List<EmployeeAccount> selectEmployeeNameWithEmployeeAccount();

    public EmployeeAccount selectNotHasEmployeeAccount(int id);

    public EmployeeAccount selectAccountName(String accountName);

    public EmployeeAccount selectEmployeeNameWithEmployeeAccountId(int id);

}
