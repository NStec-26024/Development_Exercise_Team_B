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
    public int add(ProductCategory productCategory) {
        productCategoryRepository.insert(productCategory);
        int productId = productCategory.getId();
        return productId;
    }

    @Override
    public ProductCategory getById(int id) {
        return productCategoryRepository.selectById(id);

    }

    @Override
    public boolean existName(String categoryName) {
        ProductCategory productCategory = productCategoryRepository.selectByName(categoryName);
        if (productCategory != null) {
            return false;
        } else {
            return true;
        }

    }
}
