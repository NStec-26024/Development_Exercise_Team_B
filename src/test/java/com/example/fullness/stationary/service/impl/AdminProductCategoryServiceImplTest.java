package com.example.fullness.stationary.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    public void addProductCategoryTest_OK_case1() {

        when(productCategoryRepository.insertProductCategory(productCategory))
                .thenAnswer(invocation -> {
                    ProductCategory arg = invocation.getArgument(0);
                    arg.setId(5);
                    return 1;
                });

        int actual = adminProductCategoryService.addProductCategory(productCategory);

        assertEquals(5, actual);
        verify(productCategoryRepository, times(1)).insertProductCategory(productCategory);
    }

    @Test
    public void addProductCategoryTest_NG_case2() {
        when(productCategoryRepository.insertProductCategory(productCategory))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminProductCategoryService.addProductCategory(productCategory);
        });

    }

    @Test
    public void getCategoryNameTest_OK_case03() {

        when(productCategoryRepository.selectCategoryName("衣料品"))
                .thenReturn(productCategory);

        boolean actual = adminProductCategoryService.getCategoryName("衣料品");

        assertFalse(actual);
        verify(productCategoryRepository, times(1)).selectCategoryName("衣料品");

    }

    @Test
    public void getCategoryNameTest_OK_case04() {

        when(productCategoryRepository.selectCategoryName("衣料品"))
                .thenReturn(null);

        boolean actual = adminProductCategoryService.getCategoryName("衣料品");

        assertTrue(actual);
        verify(productCategoryRepository, times(1)).selectCategoryName("衣料品");

    }

    @Test
    public void getCategoryNameTest_NG_case05() {

        when(productCategoryRepository.selectCategoryName("衣料品"))
                .thenThrow(new DataRetrievalFailureException("e"));

        assertThrows(DataRetrievalFailureException.class, () -> {
            adminProductCategoryService.getCategoryName("衣料品");
        });

    }

    @Test
    public void getProductCategoryNameWithCategoryIdTest_OK_case6() {

        when(productCategoryRepository.selectCategoryNameWithCategoryId(4))
                .thenReturn(productCategory);

        String actual = adminProductCategoryService
                .getCategoryNameWithCategoryId(4);

        assertEquals("衣料品", actual);
        verify(productCategoryRepository, times(1)).selectCategoryNameWithCategoryId(4);

    }

    @Test
    public void getEmployeeNameWithProductCategoryIdTest_NG_case7() {
        when(productCategoryRepository.selectCategoryNameWithCategoryId(4))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminProductCategoryService.getCategoryNameWithCategoryId(4);
        });

    }

}
