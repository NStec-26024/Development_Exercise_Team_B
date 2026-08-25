package com.example.fullness.stationary.service.impl;

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
import com.example.fullness.stationary.entity.ProductStock;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.helper.ProductHelper;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductStockRepository;

@ExtendWith(MockitoExtension.class)
public class AdminProductRegistrationServiceImplTest {

    @InjectMocks
    private AdminProductRegistrationServiceImpl adminProductRegistrationServiceImpl;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private ProductHelper productHelper;

    private AdminProductRegistrationForm inputForm;
    private Product inputProduct;
    private ProductStock productStock;

    @BeforeEach
    void setUp() {
        inputForm = new AdminProductRegistrationForm();
        inputForm.setName("くまのぬいぐるみ<>限定</>");
        inputForm.setPrice(2300);
        inputForm.setStock(900);
        inputForm.setCategoryId(2);

        inputProduct = new Product();
        inputProduct.setId(4);
        inputProduct.setName("くまのぬいぐるみ<>限定</>");
        inputProduct.setPrice(2300);

        productStock = new ProductStock();
        productStock.setProductId(inputProduct.getId());
        productStock.setQuantity(900);

        inputProduct.setProductStock(productStock);

    }

    @Test
    @DisplayName("addProduct: 商品と在庫の情報が正常に登録できること")
    void testAddProductOk_case1() {
        when(productHelper.formToEntity(inputForm)).thenReturn(inputProduct);
        when(productRepository.insertProduct(inputProduct)).thenReturn(1);
        when(productStockRepository.insertStock(inputProduct.getProductStock())).thenReturn(1);

        adminProductRegistrationServiceImpl.addProduct(inputForm);

        verify(productHelper, times(1)).formToEntity(inputForm);
        verify(productRepository, times(1)).insertProduct(inputProduct);
        verify(productStockRepository, times(1)).insertStock(inputProduct.getProductStock());

    }

}
