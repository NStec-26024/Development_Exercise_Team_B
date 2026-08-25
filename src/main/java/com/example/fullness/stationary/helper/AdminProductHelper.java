package com.example.fullness.stationary.helper;

import org.springframework.stereotype.Component;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductStock;
import com.example.fullness.stationary.form.AdminProductForm;

@Component
public class AdminProductHelper {

    /**
     * Product → AdminProductForm 変換
     */
    public AdminProductForm fromToForm(Product product) {

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

    /**
     * 編集フォームから Product エンティティを生成する。
     *
     * @param form 編集フォーム
     * @return Product エンティティ
     */
    public Product fromToEntity(AdminProductForm form) {

        Product product = new Product();
        ProductStock stock = new ProductStock();

        product.setId(form.getId());
        product.setName(form.getName());
        product.setPrice(Integer.parseInt(form.getPrice()));
        product.setCategoryId(form.getCategoryId());
        product.setImageUrl(form.getImagePath());
        stock.setQuantity(Integer.parseInt(form.getStock()));
        product.setProductStock(stock);

        return product;
    }
}
