package com.example.fullness.stationary.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.fullness.stationary.service.AdminProductCategoryService;

public class UniqueProductCategoryNameValidator implements ConstraintValidator<UniqueProductCategoryName, String> {

    @Autowired
    private AdminProductCategoryService adminProductCategoryService; // Serviceを注入

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return true;
        }

        if (!adminProductCategoryService.existName(name)) {

            return false;
        }

        return true;
    }
}