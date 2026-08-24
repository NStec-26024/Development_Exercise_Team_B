package com.example.fullness.stationary.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;

@ExtendWith(MockitoExtension.class)
public class AdminProductCategoryServiceImplTest {

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    AdminProductCategoryServiceImpl adminProductCategoryService;

    private ProductCategory productCategory;

    @BeforeEach
    void setUp() {
        productCategory = new ProductCategory();
        productCategory.setId(4);
        productCategory.setName("衣料品");
    }

    @Test
    public void addTest_OK_case1() {

        when(productCategoryRepository.insert(productCategory))
                .thenAnswer(invocation -> {
                    ProductCategory arg = invocation.getArgument(0);
                    arg.setId(5);
                    return 1;
                });

        int actual = adminProductCategoryService.add(productCategory);

        assertEquals(5, actual);
        verify(productCategoryRepository, times(1)).insert(productCategory);
    }

    @Test
    public void addTest_NG_case2() {
        when(productCategoryRepository.insert(productCategory))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminProductCategoryService.add(productCategory);
        });

    }

    @Test
    public void existNameTest_OK_case03() {

        when(productCategoryRepository.selectByName("衣料品"))
                .thenReturn(productCategory);

        boolean actual = adminProductCategoryService.existName("衣料品");

        assertFalse(actual);
        verify(productCategoryRepository, times(1)).selectByName("衣料品");

    }

    @Test
    public void existNameTest_OK_case04() {

        when(productCategoryRepository.selectByName("衣料品"))
                .thenReturn(null);

        boolean actual = adminProductCategoryService.existName("衣料品");

        assertTrue(actual);
        verify(productCategoryRepository, times(1)).selectByName("衣料品");

    }

    @Test
    public void existNameTest_NG_case05() {

        when(productCategoryRepository.selectByName("衣料品"))
                .thenThrow(new DataRetrievalFailureException("e"));

        assertThrows(DataRetrievalFailureException.class, () -> {
            adminProductCategoryService.existName("衣料品");
        });

    }

    @Test
    public void getByIdTest_OK_case6() {

        when(productCategoryRepository.selectById(4))
                .thenReturn(productCategory);

        ProductCategory actual = adminProductCategoryService
                .getById(4);

        assertEquals(productCategory, actual);
        verify(productCategoryRepository, times(1)).selectById(4);
        assertInstanceOf(ProductCategory.class, productCategory);

    }

    @Test
    public void getByIdTest_NG_case7() {
        when(productCategoryRepository.selectById(4))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminProductCategoryService.getById(4);
        });

    }

}
