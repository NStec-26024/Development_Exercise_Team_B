package com.example.fullness.stationary.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.service.AdminProductCategoryService;

@Service
@Transactional
public class AdminProductCategoryServiceImpl implements AdminProductCategoryService {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Override
    public int addProductCategory(ProductCategory productCategory) {
        productCategoryRepository.insertProductCategory(productCategory);
        int productId = productCategory.getId();
        return productId;
    }

    @Override
    public String getCategoryNameWithCategoryId(int id) {
        ProductCategory productCategory = productCategoryRepository.selectCategoryNameWithCategoryId(id);
        return productCategory.getName();
    }

    @Override
    public boolean getCategoryName(String categoryName) {
        ProductCategory productCategory = productCategoryRepository.selectCategoryName(categoryName);
        if (productCategory != null) {
            return false;
        } else {
            return true;
        }

    }
}
