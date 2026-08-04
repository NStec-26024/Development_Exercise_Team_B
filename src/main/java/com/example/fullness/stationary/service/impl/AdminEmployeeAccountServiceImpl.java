package com.example.fullness.stationary.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.AdminEmployeeAccountRepository;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

@Service
public class AdminEmployeeAccountServiceImpl implements AdminEmployeeAccountService{

    @Autowired
    AdminEmployeeAccountRepository adminEmployeeAccountRepository;

    @Autowired
    EmployeeAccount employeeAccount;

    @Override
    public void insertEmployeeAccount(EmployeeAccount employeeAccount){
        adminEmployeeAccountRepository.insertEmployeeAccount(employeeAccount);
    }

    @Override
    public List<Employee> selectEmployeeNameWithEmployeeAccount(){
       return adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
    }

    @Override
    public Employee selectNotHasEmployeeAccount(int id){
        return adminEmployeeAccountRepository.selectNotHasEmployeeAccount(id);

    }
    
    @Override
    public boolean selectAccountName(String accountName){
        employeeAccount=adminEmployeeAccountRepository.selectAccountName(accountName);
        if(employeeAccount != null){
            return false;
        }else{
            return true;
        }
    }
    

}
