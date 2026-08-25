package com.example.fullness.stationary.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductStock;
import com.example.fullness.stationary.form.AdminProductForm;

@Component
public class AdminProductEntityHelper {

    /**
     * 編集フォームから Product エンティティを生成する。
     *
     * @param form 編集フォーム
     * @return Product エンティティ
     */
    public Product toProduct(AdminProductForm form) {

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
