package com.example.fullness.stationary.validator;

import com.example.fullness.stationary.service.AdminEmployeeAccountService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueAccountNameValidator implements ConstraintValidator<UniqueAccountName, String> {

    @Autowired
    private AdminEmployeeAccountService adminEmployeeAccountService; // Serviceを注入

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return true;
        }

        if (!adminEmployeeAccountService.getAccountName(name)) {

            return false;
        }

        return true;
    }
}