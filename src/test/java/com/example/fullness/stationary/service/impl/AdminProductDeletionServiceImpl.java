package com.example.fullness.stationary.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class AdminProductDeletionServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private AdminProductDeletionServiceImpl deletionService;

    /**
     * 商品が存在する場合、
     * deleteById() が呼ばれることを確認する。
     */
    @Test
    void deleteProduct_deletesProductWhenExists() {

        // Arrange
        Integer productId = 1;

        Product product = new Product();
        product.setId(productId);
        product.setName("マーカー(青)");

        when(productRepository.selectById(productId))
                .thenReturn(product);

        // Act & Assert
        assertDoesNotThrow(() -> {
            deletionService.deleteProduct(productId);
        });

        // selectByIdで商品を確認
        verify(productRepository).selectById(productId);

        // 論理削除処理が呼ばれる
        verify(productRepository).deleteById(productId);
    }

    /**
     * 商品が存在しない場合、
     * AdminBusinessException が発生することを確認する。
     */
    @Test
    void deleteProduct_throwsExceptionWhenProductNotFound() {

        // Arrange
        Integer productId = 999;

        when(productRepository.selectById(productId))
                .thenReturn(null);

        when(messageSource.getMessage(
                eq("com.example.fullness.stationary.product.not_found"),
                isNull(),
                eq(Locale.JAPAN)))
                .thenReturn("商品が見つかりません");

        // Act & Assert
        assertThrows(
                AdminBusinessException.class,
                () -> deletionService.deleteProduct(productId));

        // 商品検索は行われる
        verify(productRepository).selectById(productId);

        // 商品が存在しないのでdeleteByIdは呼ばれない
        verify(productRepository, never()).deleteById(any());
    }

    /**
     * 商品が存在しない場合、
     * MessageSourceからエラーメッセージを取得することを確認する。
     */
    @Test
    void deleteProduct_getsNotFoundMessageWhenProductNotFound() {

        // Arrange
        Integer productId = 999;

        when(productRepository.selectById(productId))
                .thenReturn(null);

        when(messageSource.getMessage(
                eq("com.example.fullness.stationary.product.not_found"),
                isNull(),
                eq(Locale.JAPAN)))
                .thenReturn("商品が見つかりません");

        // Act
        assertThrows(
                AdminBusinessException.class,
                () -> deletionService.deleteProduct(productId));

        // エラーメッセージ取得を確認
        verify(messageSource).getMessage(
                "com.example.fullness.stationary.product.not_found",
                null,
                Locale.JAPAN);
    }
}
