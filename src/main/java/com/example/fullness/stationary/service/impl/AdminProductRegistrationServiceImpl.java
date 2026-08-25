package com.example.fullness.stationary.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductStock;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.helper.ProductHelper;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductStockRepository;
import com.example.fullness.stationary.service.AdminProductRegistrationService;

@Service
public class AdminProductRegistrationServiceImpl implements AdminProductRegistrationService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductStockRepository productStockRepository;

    @Autowired
    ProductHelper productHelper;

    @Override
    public void addProduct(AdminProductRegistrationForm adminProductRegistrationForm) {
        try {
            Product product = productHelper.formToEntity(adminProductRegistrationForm);

            productRepository.insertProduct(product);

            ProductStock stock = new ProductStock();
            stock.setProductId(product.getId());
            stock.setQuantity(adminProductRegistrationForm.getStock());
            productStockRepository.insertStock(stock);
        } catch (Exception e) {
            throw new AdminBusinessException("登録処理に失敗しました。管理者に連絡してください");

        }
    }

}
