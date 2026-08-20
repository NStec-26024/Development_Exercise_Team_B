package com.example.fullness.stationary.helper;

import org.springframework.stereotype.Component;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.AdminProductCategoryForm;

@Component
public class ProductCategoryHelper {

    /**
     * 入力データが入ったFormを基にプロダクトカテゴリEntityを生成
     * 
     * @param adminProductCategoryForm 入力データが入ったForm
     * @return 社員アカウントEntity
     */
    public ProductCategory formToEntity(AdminProductCategoryForm adminProductCategoryForm) {
        ProductCategory ProductCategory = new ProductCategory();
        ProductCategory.setName(adminProductCategoryForm.getName());

        return ProductCategory;
    }
}
