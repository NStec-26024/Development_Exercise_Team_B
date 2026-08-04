package com.example.fullness.stationary.service;

import java.util.List;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;

public interface AdminEmployeeAccountService {

    public void insertEmployeeAccount(EmployeeAccount employeeAccount);

    public List<Employee> selectEmployeeNameWithEmployeeAccount();

    public Employee selectNotHasEmployeeAccount(int id);

}
