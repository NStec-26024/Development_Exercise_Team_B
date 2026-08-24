package com.example.fullness.stationary.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.entity.ProductStock;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.helper.ProductHelper;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductStockRepository;
import com.example.fullness.stationary.service.AdminProductRegistrationService;

@Service
@Transactional
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
        Product product = productHelper.formToEntity(adminProductRegistrationForm);

        productRepository.insertProduct(product);

        ProductStock stock = new ProductStock();
        stock.setProductId(product.getId());
        stock.setQuantity(adminProductRegistrationForm.getStock());
        productStockRepository.insertStock(stock);

        productStockRepository.insertStock(stock);
    }

}
