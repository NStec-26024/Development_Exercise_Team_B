package com.example.fullness.stationary.helper;

import org.springframework.stereotype.Component;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.AdminProductForm;

@Component
public class AdminProductFormHelper {

    /**
     * Product → AdminProductForm 変換
     */
    public AdminProductForm fromProduct(Product product) {

        AdminProductForm form = new AdminProductForm();

        form.setId(product.getId());
        form.setName(product.getName());
        form.setPrice(String.valueOf(product.getPrice()));

        int stock = (product.getProductStock() != null)
                ? product.getProductStock().getQuantity()
                : 0;
        form.setStock(String.valueOf(stock));

        form.setCategoryId(product.getCategoryId());
        form.setImagePath(product.getImageUrl());

        return form;
    }
}
