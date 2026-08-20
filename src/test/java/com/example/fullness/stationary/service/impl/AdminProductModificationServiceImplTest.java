package com.example.fullness.stationary.service.impl;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminProductModificationServiceImplTest {

    @InjectMocks
    private AdminProductModificationServiceImpl service;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        reset(productRepository, productStockRepository, messageSource);
    }

    /**
     * 1. 正常系：商品が存在する場合、Product と ProductStock が正しく更新される
     */
    @Test
    void updateProductTest_case01_Ok() {

        Integer id = 1;

        // 既存商品を返すようにモック設定
        Product existing = new Product();
        existing.setId(id);
        existing.setName("旧商品名");
        existing.setPrice(100);
        existing.setCategoryId(1);
        existing.setImageUrl("old.png");

        when(productRepository.findById(id)).thenReturn(existing);

        // 実行
        service.updateProduct(id, "新商品名", 999, 50, 2, null, null);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).update(captor.capture());
        // verify(productRepository).update(new Product(1, ""));

        Product updated = captor.getValue();

        assertEquals(id, updated.getId());
        assertEquals("新商品名", updated.getName());
        assertEquals(999, updated.getPrice());
        assertEquals(2, updated.getCategoryId());
        assertEquals("old.png", updated.getImageUrl());

        // 在庫更新の検証
        verify(productStockRepository).updateByProductId(id, 50);
    }

    /**
     * 2. 異常系：商品が存在しない場合、AdminBusinessException が送出される
     */
    @Test
    void updateProductTest_case02_Ok() {

        Integer nonExistId = 999;

        when(productRepository.findById(nonExistId)).thenReturn(null);
        when(messageSource.getMessage(
                eq("com.example.fullness.stationary.product.not_found"),
                any(),
                eq(Locale.JAPAN))).thenReturn("入力情報が見つかりません。再度入力してください");

        assertThrows(AdminBusinessException.class,
                () -> service.updateProduct(nonExistId, "name", 100, 10, 1, null, null));

        verify(productRepository, never()).update(any());
        verify(productStockRepository, never()).updateByProductId(any(), any());
    }
}
