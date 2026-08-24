package com.example.fullness.stationary.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    "登録処理に失敗しました。管理者に連絡してください");
        }

    }

    @Override
    public List<EmployeeAccount> getEmployeeNameWithEmployeeAccount() {
        try {
            return employeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
        } catch (Exception e) {
            throw new AdminBusinessException("社員情報の取得に失敗しました");
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
                    "登録処理に失敗しました。管理者に連絡してください");
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
