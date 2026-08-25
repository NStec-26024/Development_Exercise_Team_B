package com.example.fullness.stationary.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.service.AdminProductCategoryService;

@Service
public class AdminProductCategoryServiceImpl implements AdminProductCategoryService {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Override
    public int add(ProductCategory productCategory) {
        try {
            productCategoryRepository.insert(productCategory);
            int productId = productCategory.getId();
            return productId;
        } catch (Exception e) {
            throw new AdminBusinessException("登録処理に失敗しました。管理者に連絡してください");
        }
    }

    @Override
    public ProductCategory getById(int id) {
        return productCategoryRepository.selectById(id);

    }

    @Override
    public boolean existName(String categoryName) {
        try {
            ProductCategory productCategory = productCategoryRepository.selectByName(categoryName);
            if (productCategory != null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new AdminBusinessException("登録処理に失敗しました。管理者に連絡してください");
        }

    }
}
