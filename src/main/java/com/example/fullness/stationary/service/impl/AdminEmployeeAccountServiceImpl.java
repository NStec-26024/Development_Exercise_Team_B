package com.example.fullness.stationary.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public int addEmployeeAccount(EmployeeAccount employeeAccount) {

        // パスワードのハッシュ化
        String encodePassword = passwordEncoder.encode(employeeAccount.getPassword());
        employeeAccount.setPassword(encodePassword);
        adminEmployeeAccountRepository.insertEmployeeAccount(employeeAccount);
        int accountId = employeeAccount.getId();
        return accountId;

    }

    @Override
    public List<EmployeeAccount> getEmployeeNameWithEmployeeAccount() {
        return adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
    }

    @Override
    public EmployeeAccount getEmployeeAccountWithEmployeeId(int id) {
        return adminEmployeeAccountRepository.selectNotHasEmployeeAccount(id);

    }

    @Override
    public boolean getAccountName(String accountName) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount = adminEmployeeAccountRepository.selectAccountName(accountName);
        if (employeeAccount != null) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public EmployeeAccount getEmployeeNameWithEmployeeAccountId(int id) {
        return adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(id);

    }

}
