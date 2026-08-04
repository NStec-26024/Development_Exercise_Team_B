package com.example.fullness.stationary.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;

@Repository
public interface AdminEmployeeAccountRepository {

    public int insertEmployeeAccount(EmployeeAccount employeeAccount);

    public List<Employee> selectEmployeeNameWithEmployeeAccount();

    public Employee selectNotHasEmployeeAccount(int id);

    public EmployeeAccount selectAccountName(String accountName);


}
