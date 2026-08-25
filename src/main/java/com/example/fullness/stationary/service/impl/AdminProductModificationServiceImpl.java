package com.example.fullness.stationary.service.impl;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductStockRepository;
import com.example.fullness.stationary.service.AdminProductModificationService;

/**
 * {@link AdminProductModificationService} の実装。
 */
@Service
@Transactional
public class AdminProductModificationServiceImpl implements AdminProductModificationService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * {@inheritDoc}
     * 画像が指定されなかった場合は既存の画像ファイルを維持する。
     */
    @Override
    public void updateProduct(Integer id, String name, int price, int stock, Integer categoryId,
            byte[] imageBytes, String originalFileName) {

        Product current = productRepository.selectById(id);
        if (current == null) {
            throw new AdminBusinessException(
                    messageSource.getMessage("com.example.fullness.stationary.product.not_found", null,
                            Locale.JAPAN));
        }

        String imageFileName = current.getImageUrl();

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setCategoryId(categoryId);
        product.setImageUrl(imageFileName);

        productRepository.update(product);
        productStockRepository.updateByProductId(id, stock);
    }
}
