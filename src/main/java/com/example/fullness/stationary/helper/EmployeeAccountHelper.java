package com.example.fullness.stationary.helper;

import org.springframework.stereotype.Component;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AdminEmployeeAccountForm;

/**
 * FormからEntityに変換するHelperクラス
 */
@Component
public class EmployeeAccountHelper {

    /**
     * 入力データが入ったFormとハッシュ化されたパスワードを基に社員アカウントEntityを生成
     * 
     * @param adminEmployeeAccountForm 入力データが入ったForm
     * @param encodePassword           ハッシュ化されたパスワード
     * @return 社員アカウントEntity
     */
    public EmployeeAccount formToEntity(AdminEmployeeAccountForm adminEmployeeAccountForm, String encodePassword) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount.setEmployeeId(adminEmployeeAccountForm.getEmployeeId());
        employeeAccount.setName(adminEmployeeAccountForm.getName());
        employeeAccount.setPassword(encodePassword);

        return employeeAccount;
    }

    /**
     * 入力データが入ったFormを基に社員アカウントEntityを生成
     * 
     * @param adminEmployeeAccountForm 入力データが入ったForm
     * @return 社員アカウントEntity
     */
    public EmployeeAccount formToEntity(AdminEmployeeAccountForm adminEmployeeAccountForm) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount.setEmployeeId(adminEmployeeAccountForm.getEmployeeId());
        employeeAccount.setName(adminEmployeeAccountForm.getName());
        employeeAccount.setPassword(adminEmployeeAccountForm.getPassword());

        return employeeAccount;
    }

}
