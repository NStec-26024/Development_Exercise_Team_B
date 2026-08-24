package com.example.fullness.stationary.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.entity.ProductStock;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.helper.ProductHelper;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductStockRepository;

@ExtendWith(MockitoExtension.class)
public class AdminProductRegistrationServiceImplTest {

    @InjectMocks
    private AdminProductRegistrationServiceImpl service;

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @Mock
    ProductStockRepository productStockRepository;

    @Mock
    ProductHelper productHelper;

    private ProductCategory productCategory;
    private Product product;
    private ProductStock productStock;

    @BeforeEach
    void setUp() {
        product = new Product();

    }

    @Test
    @DisplayName("addProduct: 商品と在庫の情報が正常に登録できること")
    void testAddProduct_Success() {
        // 入力フォームの準備
        AdminProductRegistrationForm form = new AdminProductRegistrationForm();
        form.setStock(50);

        // ヘルパーから変換されるエンティティの準備（IDが1として登録される想定）
        Product mockProduct = new Product();
        mockProduct.setId(100);

        // モックの振る舞いを定義
        when(productHelper.formToEntity(form)).thenReturn(mockProduct);

        // テスト対象メソッドの実行
        service.addProduct(form);

        // 検証：各リポジトリやヘルパーが意図した回数・引数で呼ばれたか
        verify(productHelper, times(1)).formToEntity(form);
        verify(productRepository, times(1)).insertProduct(mockProduct);

        // 実装コード内で「productStockRepository.insertStock(stock)」が2回重複して呼ばれているため、times(2)で検証しています
        verify(productStockRepository, times(2)).insertStock(any(ProductStock.class));
    }

}
