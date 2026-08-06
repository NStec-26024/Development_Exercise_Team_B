package com.example.fullness.stationary.service;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ProductService の振る舞いを検証するためのユニットテストクラス。
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private Model model;

    @InjectMocks
    private ProductService productService;

    private ProductCategory sampleCategory;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleCategory = new ProductCategory();
        sampleCategory.setId(1);
        sampleCategory.setName("文具");

        sampleProduct = new Product();
        sampleProduct.setId(1);
        sampleProduct.setName("テストペン");
        sampleProduct.setPrice(100);
        sampleProduct.setImageUrl("black_pen.jpg");
        sampleProduct.setCategoryId(1);
        sampleProduct.setStock(10);
    }

    @Test
    void getAllCategories_returnsCategoriesWhenPresent() {
        List<ProductCategory> categories = List.of(sampleCategory);
        when(productCategoryRepository.findAll()).thenReturn(categories);

        List<ProductCategory> result = productService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(categories, result);
        verify(productCategoryRepository).findAll();
    }

    @Test
    void getAllCategories_returnsEmptyListWhenRepositoryReturnsNull() {
        when(productCategoryRepository.findAll()).thenReturn(null);

        List<ProductCategory> result = productService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productCategoryRepository).findAll();
    }

    @Test
    void getCategoryName_returnsNameWhenCategoryExists() {
        when(productCategoryRepository.findById(eq(1))).thenReturn(sampleCategory);

        String result = productService.getCategoryName(1);

        assertEquals("文具", result);
    }

    @Test
    void getCategoryName_returnsNullForNullOrZeroId() {
        assertNull(productService.getCategoryName(null));
        assertNull(productService.getCategoryName(0));
        verifyNoInteractions(productCategoryRepository);
    }

    @Test
    void searchAllProductsAndSetModel_returnsFalseWhenNoProductsFound() {
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(Collections.emptyList());

        boolean result = productService.searchAllProductsAndSetModel(1, model);

        assertFalse(result);
        verify(productRepository).findAllWithPaging(0, 10);
        verify(model).addAttribute("infoMessage", "該当する商品情報がありません");
        verify(model).addAttribute("searched", true);
        verify(model).addAttribute("selectedCategoryId", 0);
    }

    @Test
    void searchAllProductsAndSetModel_returnsTrueAndSetsPagingForFoundProducts() {
        List<Product> products = List.of(sampleProduct);
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(products);
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productService.searchAllProductsAndSetModel(1, model);

        assertTrue(result);
        verify(productRepository).findAllWithPaging(0, 10);
        verify(productRepository).countAll();
        verify(model).addAttribute("productList", products);
        verify(model).addAttribute("currentPage", 1);
        verify(model).addAttribute("totalPages", 1);
        verify(model).addAttribute("totalCount", 1);
        verify(model).addAttribute("hasPrevious", false);
        verify(model).addAttribute("hasNext", false);
        verify(model).addAttribute("selectedCategoryId", 0);
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchAllProductsAndSetModel_usesPageOneWhenPageIsLessThanOne() {
        List<Product> products = List.of(sampleProduct);
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(products);
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productService.searchAllProductsAndSetModel(-5, model);

        assertTrue(result);
        verify(productRepository).findAllWithPaging(0, 10);
    }

    @Test
    void searchProductsByCategoryAndSetModel_returnsFalseWhenCategoryProductsNotFound() {
        when(productRepository.findByCategoryWithPaging(2, 0, 10)).thenReturn(Collections.emptyList());
        when(productCategoryRepository.findById(2)).thenReturn(sampleCategory);

        boolean result = productService.searchProductsByCategoryAndSetModel(2, 1, model);

        assertFalse(result);
        verify(productRepository).findByCategoryWithPaging(2, 0, 10);
        verify(model).addAttribute("infoMessage", "該当する商品情報がありません");
        verify(model).addAttribute("searched", true);
        verify(model).addAttribute("selectedCategoryId", 2);
        verify(model).addAttribute("selectedCategoryName", "文具");
    }

    @Test
    void searchProductsByCategoryAndSetModel_returnsTrueAndSetsPagingForCategoryProducts() {
        List<Product> products = List.of(sampleProduct);
        when(productRepository.findByCategoryWithPaging(2, 0, 10)).thenReturn(products);
        when(productRepository.countByCategory(2)).thenReturn(1);
        when(productCategoryRepository.findById(2)).thenReturn(sampleCategory);

        boolean result = productService.searchProductsByCategoryAndSetModel(2, 1, model);

        assertTrue(result);
        verify(productRepository).findByCategoryWithPaging(2, 0, 10);
        verify(productRepository).countByCategory(2);
        verify(model).addAttribute("productList", products);
        verify(model).addAttribute("currentPage", 1);
        verify(model).addAttribute("totalPages", 1);
        verify(model).addAttribute("totalCount", 1);
        verify(model).addAttribute("hasPrevious", false);
        verify(model).addAttribute("hasNext", false);
        verify(model).addAttribute("selectedCategoryId", 2);
        verify(model).addAttribute("selectedCategoryName", "文具");
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchProductsByCategoryAndSetModel_redirectsToAllProductsWhenCategoryIdIsZero() {
        List<Product> products = List.of(sampleProduct);
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(products);
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productService.searchProductsByCategoryAndSetModel(0, 1, model);

        assertTrue(result);
        verify(productRepository).findAllWithPaging(0, 10);
        verify(productRepository).countAll();
    }

    @Test
    void getAllCategories_handlesRepositoryException_returnsEmptyList() {
        when(productCategoryRepository.findAll()).thenThrow(new RuntimeException("DB fail"));

        List<ProductCategory> result = productService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productCategoryRepository).findAll();
    }

    @Test
    void getCategoryName_handlesRepositoryException_returnsNull() {
        when(productCategoryRepository.findById(eq(99))).thenThrow(new RuntimeException("DB fail"));

        String result = productService.getCategoryName(99);

        assertNull(result);
        verify(productCategoryRepository).findById(99);
    }

    @Test
    void searchAllProductsAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse() {
        when(productRepository.findAllWithPaging(0, 10)).thenThrow(new RuntimeException("DB fail"));

        boolean result = productService.searchAllProductsAndSetModel(1, model);

        assertFalse(result);
        verify(productRepository).findAllWithPaging(0, 10);
        verify(model).addAttribute(eq("errorMessage"), any());
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchProductsByCategoryAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse() {
        when(productRepository.findByCategoryWithPaging(2, 0, 10)).thenThrow(new RuntimeException("DB fail"));

        boolean result = productService.searchProductsByCategoryAndSetModel(2, 1, model);

        assertFalse(result);
        verify(productRepository).findByCategoryWithPaging(2, 0, 10);
        verify(model).addAttribute(eq("errorMessage"), any());
        verify(model).addAttribute("searched", true);
    }
}
