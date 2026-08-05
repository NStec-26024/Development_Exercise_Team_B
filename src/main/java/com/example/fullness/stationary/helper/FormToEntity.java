package com.example.fullness.stationary.helper;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AccountRegistForm;

public class FormToEntity {

    public EmployeeAccount formToEntity(AccountRegistForm form, String encodePassword) {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount.setEmployeeId(form.getEmployeeId());
        employeeAccount.setName(form.getName());
        employeeAccount.setPassword(encodePassword);

        return employeeAccount;
    }

}
