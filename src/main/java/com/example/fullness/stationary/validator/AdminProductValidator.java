package com.example.fullness.stationary.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.fullness.stationary.form.AdminProductForm;

@Component
public class AdminProductValidator implements Validator {

    @Autowired
    private AdminProductPriceValidator priceValidator;

    @Autowired
    private AdminProductStockValidator stockValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return AdminProductForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        AdminProductForm form = (AdminProductForm) target;

        priceValidator.validate(form, errors);
        stockValidator.validate(form, errors);
    }
}
