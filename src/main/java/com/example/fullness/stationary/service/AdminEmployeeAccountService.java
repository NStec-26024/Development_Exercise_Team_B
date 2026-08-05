package com.example.fullness.stationary.service;

import java.util.List;

import com.example.fullness.stationary.entity.EmployeeAccount;

public interface AdminEmployeeAccountService {

    public int insertEmployeeAccount(EmployeeAccount employeeAccount);

    public List<EmployeeAccount> selectEmployeeNameWithEmployeeAccount();

    public EmployeeAccount selectNotHasEmployeeAccount(int id);

    public boolean selectAccountName(String accountName);

    public EmployeeAccount selectEmployeeNameWithEmployeeAccountId(int id);

}
