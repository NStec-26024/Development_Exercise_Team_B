package com.example.fullness.stationary.helper;

import org.springframework.stereotype.Component;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AccountRegistForm;

/**
 * FormからEntityに変換するHelperクラス
 */
@Component
public class FormToEntity {

    /**
     * 入力データが入ったFormとハッシュ化されたパスワードを基に社員アカウントEntityを生成
     * 
     * @param accountRegistForm 入力データが入ったForm
     * @param encodePassword    ハッシュ化されたパスワード
     * @return 社員アカウントEntity
     */
    public EmployeeAccount formToEntity(AccountRegistForm accountRegistForm, String encodePassword) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount.setEmployeeId(accountRegistForm.getEmployeeId());
        employeeAccount.setName(accountRegistForm.getName());
        employeeAccount.setPassword(encodePassword);

        return employeeAccount;
    }

    /**
     * 入力データが入ったFormを基に社員アカウントEntityを生成
     * 
     * @param accountRegistForm 入力データが入ったForm
     * @return 社員アカウントEntity
     */
    public EmployeeAccount formToEntity(AccountRegistForm accountRegistForm) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount.setEmployeeId(accountRegistForm.getEmployeeId());
        employeeAccount.setName(accountRegistForm.getName());
        employeeAccount.setPassword(accountRegistForm.getPassword());

        return employeeAccount;
    }

}
