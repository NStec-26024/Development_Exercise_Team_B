package com.example.fullness.stationary.validator;

import java.math.BigInteger;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.example.fullness.stationary.form.AdminProductForm;

@Component
public class AdminProductPriceValidator {

    public void validate(AdminProductForm form, Errors errors) {

        if (!errors.hasFieldErrors("price")) {
            BigInteger price = new BigInteger(form.getPrice());
            if (price.compareTo(BigInteger.valueOf(1000000)) > 0) {
                errors.rejectValue("price", null, "価格は100万円以下で入力してください");
            }
        }
    }
}
