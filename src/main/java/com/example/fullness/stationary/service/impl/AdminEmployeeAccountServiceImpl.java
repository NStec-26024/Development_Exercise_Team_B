package com.example.fullness.stationary.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.AdminEmployeeAccountRepository;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

/**
 * サービス実装クラス
 */
@Service
@Transactional
public class AdminEmployeeAccountServiceImpl implements AdminEmployeeAccountService {

    @Autowired
    AdminEmployeeAccountRepository adminEmployeeAccountRepository;

    @Override
    public int insertEmployeeAccount(EmployeeAccount employeeAccount) {
        adminEmployeeAccountRepository.insertEmployeeAccount(employeeAccount);
        int accountId = employeeAccount.getId();
        return accountId;

    }

    @Override
    public List<EmployeeAccount> selectEmployeeNameWithEmployeeAccount() {
        return adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
    }

    @Override
    public EmployeeAccount selectNotHasEmployeeAccount(int id) {
        return adminEmployeeAccountRepository.selectNotHasEmployeeAccount(id);

    }

    @Override
    public boolean selectAccountName(String accountName) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount = adminEmployeeAccountRepository.selectAccountName(accountName);
        if (employeeAccount != null) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public EmployeeAccount selectEmployeeNameWithEmployeeAccountId(int id) {
        return adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(id);

    }

}
