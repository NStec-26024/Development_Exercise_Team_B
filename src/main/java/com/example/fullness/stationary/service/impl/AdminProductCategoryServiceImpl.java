package com.example.fullness.stationary.service.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.service.AdminProductCategoryService;

@Service
public class AdminProductCategoryServiceImpl implements AdminProductCategoryService {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    MessageSource messageSource;

    @Override
    public int add(ProductCategory productCategory) {
        try {
            productCategoryRepository.insert(productCategory);
            int productId = productCategory.getId();
            return productId;
        } catch (Exception e) {
            throw new AdminBusinessException(messageSource.getMessage("insert.failed", null, Locale.JAPAN));
        }
    }

    @Override
    public ProductCategory getById(int id) {
        try {
            return productCategoryRepository.selectById(id);
        } catch (Exception e) {
            throw new AdminBusinessException(messageSource.getMessage("insert.failed", null, Locale.JAPAN));
        }

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
            throw new AdminBusinessException(messageSource.getMessage("insert.failed", null, Locale.JAPAN));
        }

    }
}
