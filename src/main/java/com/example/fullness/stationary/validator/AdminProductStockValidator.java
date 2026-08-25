package com.example.fullness.stationary.validator;

import java.math.BigInteger;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.example.fullness.stationary.form.AdminProductForm;

@Component
public class AdminProductStockValidator {

    public void validate(AdminProductForm form, Errors errors) {

        if (!errors.hasFieldErrors("stock")) {
            BigInteger stock = new BigInteger(form.getStock());
            if (stock.compareTo(BigInteger.valueOf(1000)) > 0) {
                errors.rejectValue("stock", null, "在庫数は1000個以下で入力してください");
            }
        }
    }
}
