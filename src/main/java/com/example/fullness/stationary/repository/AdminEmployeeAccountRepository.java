package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;

@Mapper
public interface AdminEmployeeAccountRepository {

    public int insertEmployeeAccount(EmployeeAccount employeeAccount);

    public List<Employee> selectEmployeeNameWithEmployeeAccount();

    public Employee selectNotHasEmployeeAccount(int id);

    public EmployeeAccount selectAccountName(String accountName);

}
