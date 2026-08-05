package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.example.fullness.stationary.entity.EmployeeAccount;

@Mapper
public interface AdminEmployeeAccountRepository {

    public int insertEmployeeAccount(EmployeeAccount employeeAccount);

    public List<String> selectEmployeeNameWithEmployeeAccount();

    public String selectNotHasEmployeeAccount(int id);

    public EmployeeAccount selectAccountName(String accountName);

    public String selectEmployeeNameWithEmployeeAccountId(int id);

}
