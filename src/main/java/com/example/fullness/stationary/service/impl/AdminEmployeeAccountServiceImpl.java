package com.example.fullness.stationary.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

/**
 * サービス実装クラス
 */
@Service
public class AdminEmployeeAccountServiceImpl implements AdminEmployeeAccountService {

    @Autowired
    EmployeeAccountRepository employeeAccountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MessageSource messageSource;

    @Override
    public int addEmployeeAccount(EmployeeAccount employeeAccount) {
        try {
            // パスワードのハッシュ化
            String encodePassword = passwordEncoder.encode(employeeAccount.getPassword());
            employeeAccount.setPassword(encodePassword);
            employeeAccountRepository.insertEmployeeAccount(employeeAccount);
            int accountId = employeeAccount.getId();
            return accountId;
        } catch (Exception e) {
            throw new AdminBusinessException(
                    messageSource.getMessage("insert.failed", null, Locale.JAPAN));
        }

    }

    @Override
    public List<EmployeeAccount> getEmployeeNameWithEmployeeAccount() {
        try {
            return employeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
        } catch (Exception e) {
            throw new AdminBusinessException(messageSource.getMessage("employee.account.emsg9", null, Locale.JAPAN));
        }
    }

    @Override
    public EmployeeAccount getEmployeeAccountWithEmployeeId(int id) {
        return employeeAccountRepository.selectNotHasEmployeeAccount(id);

    }

    @Override
    public boolean getAccountName(String accountName) {
        try {
            EmployeeAccount employeeAccount = new EmployeeAccount();
            employeeAccount = employeeAccountRepository.selectAccountName(accountName);
            if (employeeAccount != null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new AdminBusinessException(
                    messageSource.getMessage("insert.failed", null, Locale.JAPAN));
        }

    }

    @Override
    public EmployeeAccount getEmployeeNameWithEmployeeAccountId(int id) {
        return employeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(id);

    }

    @Override
    public boolean getNotHasEmployeeAccount(int id) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount = employeeAccountRepository.selectNotHasEmployeeAccount(id);
        if (employeeAccount == null) {
            return false;
        } else {
            return true;
        }
    }

}
