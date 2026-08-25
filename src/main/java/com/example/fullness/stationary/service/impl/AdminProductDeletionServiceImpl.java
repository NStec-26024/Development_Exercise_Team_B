package com.example.fullness.stationary.service.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.AdminProductDeletionService;

@Service
@Transactional
public class AdminProductDeletionServiceImpl implements AdminProductDeletionService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MessageSource messageSource;

    // 商品を論理削除する
    @Override
    public void deleteProduct(Integer id) {
        Product current = productRepository.selectById(id);
        if (current == null) {
            throw new AdminBusinessException(
                    messageSource.getMessage(
                            "com.example.fullness.stationary.product.not_found",
                            (Object[]) null,
                            Locale.JAPAN));
        }

        productRepository.deleteById(id);
    }
}
