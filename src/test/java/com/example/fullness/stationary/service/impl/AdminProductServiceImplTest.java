package com.example.fullness.stationary.service.impl;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.impl.AdminProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * `AdminProductServiceImpl` を Mockito で検証する純粋な単体テスト。
 * `ProductRepository` と `ProductCategoryRepository` はモック化し、
 * 依存先の呼び出しと戻り値に基づくサービス動作を確認する。
 */
@ExtendWith(MockitoExtension.class)
class AdminProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        // Mockito がモックを初期化するため、特別なセットアップは不要
    }

    @Test
    void getAllCategories_returnsEmptyListWhenRepositoryReturnsNull() {
        when(productCategoryRepository.selectAll()).thenReturn(null);

        List<ProductCategory> result = productService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productCategoryRepository).selectAll();
    }

    @Test
    void getAllCategories_returnsCategoriesWhenPresent() {
        ProductCategory category = new ProductCategory();
        category.setId(1);
        category.setName("文具");
        when(productCategoryRepository.selectAll()).thenReturn(List.of(category));

        List<ProductCategory> result = productService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("文具", result.get(0).getName());
        verify(productCategoryRepository).selectAll();
    }

    @Test
    void getAllCategories_handlesException_returnsEmptyList() {
        when(productCategoryRepository.selectAll()).thenThrow(new RuntimeException("DB error"));

        List<ProductCategory> result = productService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productCategoryRepository).selectAll();
    }

    @Test
    void getCategoryName_returnsNameWhenExists() {
        ProductCategory category = new ProductCategory();
        category.setId(2);
        category.setName("文房具");
        when(productCategoryRepository.selectById(2)).thenReturn(category);

        String name = productService.getCategoryName(2);

        assertEquals("文房具", name);
        verify(productCategoryRepository).selectById(2);
    }

    @Test
    void getCategoryName_returnsNullForNullOrZeroId() {
        assertNull(productService.getCategoryName(null));
        assertNull(productService.getCategoryName(0));

        verifyNoInteractions(productCategoryRepository);
    }

    @Test
    void getCategoryName_handlesException_returnsNull() {
        when(productCategoryRepository.selectById(99)).thenThrow(new RuntimeException("boom"));

        assertNull(productService.getCategoryName(99));
        verify(productCategoryRepository).selectById(99);
    }

    @Test
    void searchAllProductsAndSetModel_returnsFalseWhenNoProductsFound() {
        when(productRepository.selectAllWithPaging(0, 10)).thenReturn(Collections.emptyList());

        boolean result = productService.searchAllProductsAndSetModel(1, model);

        assertFalse(result);
        verify(productRepository).selectAllWithPaging(0, 10);
        verify(model).addAttribute("infoMessage", "該当する商品情報がありません");
        verify(model).addAttribute("searched", true);
        verify(model).addAttribute("selectedCategoryId", 0);
    }

    @Test
    void searchAllProductsAndSetModel_returnsTrueAndSetsPagingForFoundProducts() {
        Product product = new Product();
        product.setId(1);
        product.setName("ペン");
        when(productRepository.selectAllWithPaging(0, 10)).thenReturn(List.of(product));
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productService.searchAllProductsAndSetModel(1, model);

        assertTrue(result);
        verify(productRepository).selectAllWithPaging(0, 10);
        verify(productRepository).countAll();
        verify(model).addAttribute("productList", List.of(product));
        verify(model).addAttribute("currentPage", 1);
        verify(model).addAttribute("totalPages", 1);
        verify(model).addAttribute("totalCount", 1);
        verify(model).addAttribute("hasPrevious", false);
        verify(model).addAttribute("hasNext", false);
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchAllProductsAndSetModel_usesPageOneWhenPageLessThanOne() {
        Product product = new Product();
        when(productRepository.selectAllWithPaging(0, 10)).thenReturn(List.of(product));
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productService.searchAllProductsAndSetModel(-3, model);

        assertTrue(result);
        verify(productRepository).selectAllWithPaging(0, 10);
        verify(productRepository).countAll();
    }

    @Test
    void searchAllProductsAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse() {
        when(productRepository.selectAllWithPaging(0, 10)).thenThrow(new RuntimeException("db"));

        boolean result = productService.searchAllProductsAndSetModel(1, model);

        assertFalse(result);
        verify(model).addAttribute(startsWith("errorMessage"), any());
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchProductsByCategoryAndSetModel_returnsFalseWhenCategoryProductsNotFound() {
        ProductCategory category = new ProductCategory();
        category.setId(2);
        category.setName("文具");
        when(productRepository.selectByCategoryWithPaging(2, 0, 10)).thenReturn(Collections.emptyList());
        when(productCategoryRepository.selectById(2)).thenReturn(category);

        boolean result = productService.searchProductsByCategoryAndSetModel(2, 1, model);

        assertFalse(result);
        verify(productRepository).selectByCategoryWithPaging(2, 0, 10);
        verify(model).addAttribute("infoMessage", "該当する商品情報がありません");
        verify(model).addAttribute("searched", true);
        verify(model).addAttribute("selectedCategoryId", 2);
        verify(model).addAttribute("selectedCategoryName", "文具");
    }

    @Test
    void searchProductsByCategoryAndSetModel_returnsTrueAndSetsPagingForCategoryProducts() {
        ProductCategory category = new ProductCategory();
        category.setId(2);
        category.setName("文具");
        Product product = new Product();
        product.setId(10);
        when(productRepository.selectByCategoryWithPaging(2, 0, 10)).thenReturn(List.of(product));
        when(productRepository.countByCategory(2)).thenReturn(1);
        when(productCategoryRepository.selectById(2)).thenReturn(category);

        boolean result = productService.searchProductsByCategoryAndSetModel(2, 1, model);

        assertTrue(result);
        verify(productRepository).selectByCategoryWithPaging(2, 0, 10);
        verify(productRepository).countByCategory(2);
        verify(model).addAttribute("productList", List.of(product));
        verify(model).addAttribute("currentPage", 1);
        verify(model).addAttribute("totalPages", 1);
        verify(model).addAttribute("totalCount", 1);
        verify(model).addAttribute("selectedCategoryId", 2);
        verify(model).addAttribute("selectedCategoryName", "文具");
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchProductsByCategoryAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse() {
        when(productRepository.selectByCategoryWithPaging(2, 0, 10)).thenThrow(new RuntimeException("boom"));

        boolean result = productService.searchProductsByCategoryAndSetModel(2, 1, model);

        assertFalse(result);
        verify(model).addAttribute(startsWith("errorMessage"), any());
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchProductsByCategoryAndSetModel_treatsZeroCategoryAsAllProducts() {
        Product product = new Product();
        when(productRepository.selectAllWithPaging(0, 10)).thenReturn(List.of(product));
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productService.searchProductsByCategoryAndSetModel(0, 1, model);

        assertTrue(result);
        verify(productRepository).selectAllWithPaging(0, 10);
        verify(productRepository).countAll();
    }
}
