package com.example.fullness.stationary.service;

import com.example.fullness.stationary.FullnessStationaryApplication;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.impl.AdminProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * `AdminProductServiceImpl` を `@SpringBootTest` コンテキストで検証するテスト。
 * リポジトリはモック化して DB を使わずに動作確認を行う。
 */
@SpringBootTest(classes = FullnessStationaryApplication.class, properties = "spring.autoconfigure.exclude=org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
class AdminProductServiceImplSpringBootTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private AdminProductServiceImpl productServiceImpl;

    @BeforeEach
    void setUp() {
        // no-op: モックは各テストで設定する
    }

    @Test
    void getAllCategories_returnsEmptyListWhenRepositoryReturnsNull() {
        when(productCategoryRepository.findAll()).thenReturn(null);

        List<ProductCategory> result = productServiceImpl.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productCategoryRepository).findAll();
    }

    @Test
    void getAllCategories_returnsCategoriesWhenPresent() {
        ProductCategory cat = new ProductCategory();
        cat.setId(1);
        cat.setName("文具");
        when(productCategoryRepository.findAll()).thenReturn(List.of(cat));

        List<ProductCategory> result = productServiceImpl.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("文具", result.get(0).getName());
        verify(productCategoryRepository).findAll();
    }

    @Test
    void getAllCategories_handlesException_returnsEmptyList() {
        when(productCategoryRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        List<ProductCategory> result = productServiceImpl.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productCategoryRepository).findAll();
    }

    @Test
    void getCategoryName_returnsNameWhenExists() {
        ProductCategory cat = new ProductCategory();
        cat.setId(2);
        cat.setName("文房具");
        when(productCategoryRepository.findById(2)).thenReturn(cat);

        String name = productServiceImpl.getCategoryName(2);

        assertEquals("文房具", name);
    }

    @Test
    void getCategoryName_returnsNullForNullOrZeroId() {
        assertNull(productServiceImpl.getCategoryName(null));
        assertNull(productServiceImpl.getCategoryName(0));
        verifyNoInteractions(productCategoryRepository);
    }

    @Test
    void getCategoryName_handlesException_returnsNull() {
        when(productCategoryRepository.findById(99)).thenThrow(new RuntimeException("boom"));
        assertNull(productServiceImpl.getCategoryName(99));
        verify(productCategoryRepository).findById(99);
    }

    @Test
    void searchAllProductsAndSetModel_returnsTrueAndSetsPagingForFoundProducts() {
        Model model = mock(Model.class);
        Product p = new Product();
        p.setId(1);
        p.setName("ペン");
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(List.of(p));
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productServiceImpl.searchAllProductsAndSetModel(1, model);

        assertTrue(result);
        verify(productRepository).findAllWithPaging(0, 10);
        verify(productRepository).countAll();
        verify(model).addAttribute("productList", List.of(p));
        verify(model).addAttribute("currentPage", 1);
        verify(model).addAttribute("totalPages", 1);
        verify(model).addAttribute("totalCount", 1);
        verify(model).addAttribute("hasPrevious", false);
        verify(model).addAttribute("hasNext", false);
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchAllProductsAndSetModel_usesPageOneWhenPageLessThanOne() {
        Model model = mock(Model.class);
        Product p = new Product();
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(List.of(p));
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productServiceImpl.searchAllProductsAndSetModel(-3, model);

        assertTrue(result);
        verify(productRepository).findAllWithPaging(0, 10);
    }

    @Test
    void searchAllProductsAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse() {
        Model model = mock(Model.class);
        when(productRepository.findAllWithPaging(0, 10)).thenThrow(new RuntimeException("db"));

        boolean result = productServiceImpl.searchAllProductsAndSetModel(1, model);

        assertFalse(result);
        verify(model).addAttribute(startsWith("errorMessage"), any());
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchProductsByCategoryAndSetModel_returnsFalseWhenCategoryProductsNotFound() {
        Model model = mock(Model.class);
        ProductCategory cat = new ProductCategory();
        cat.setId(2);
        cat.setName("文具");
        when(productRepository.findByCategoryWithPaging(2, 0, 10)).thenReturn(Collections.emptyList());
        when(productCategoryRepository.findById(2)).thenReturn(cat);

        boolean result = productServiceImpl.searchProductsByCategoryAndSetModel(2, 1, model);

        assertFalse(result);
        verify(productRepository).findByCategoryWithPaging(2, 0, 10);
        verify(model).addAttribute("infoMessage", "該当する商品情報がありません");
        verify(model).addAttribute("selectedCategoryId", 2);
        verify(model).addAttribute("selectedCategoryName", "文具");
    }

    @Test
    void searchProductsByCategoryAndSetModel_returnsTrueAndSetsPagingForCategoryProducts() {
        Model model = mock(Model.class);
        ProductCategory cat = new ProductCategory();
        cat.setId(2);
        cat.setName("文具");
        Product p = new Product();
        p.setId(10);
        when(productRepository.findByCategoryWithPaging(2, 0, 10)).thenReturn(List.of(p));
        when(productRepository.countByCategory(2)).thenReturn(1);
        when(productCategoryRepository.findById(2)).thenReturn(cat);

        boolean result = productServiceImpl.searchProductsByCategoryAndSetModel(2, 1, model);

        assertTrue(result);
        verify(productRepository).findByCategoryWithPaging(2, 0, 10);
        verify(productRepository).countByCategory(2);
        verify(model).addAttribute("selectedCategoryName", "文具");
        verify(model).addAttribute("totalCount", 1);
    }

    @Test
    void searchProductsByCategoryAndSetModel_handlesRepositoryException_setsErrorAndReturnsFalse() {
        Model model = mock(Model.class);
        when(productRepository.findByCategoryWithPaging(2, 0, 10)).thenThrow(new RuntimeException("boom"));

        boolean result = productServiceImpl.searchProductsByCategoryAndSetModel(2, 1, model);

        assertFalse(result);
        verify(model).addAttribute(startsWith("errorMessage"), any());
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchAllProductsAndSetModel_returnsFalseWhenNoProductsFound() {
        Model model = mock(Model.class);
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(Collections.emptyList());

        boolean result = productServiceImpl.searchAllProductsAndSetModel(1, model);

        assertFalse(result);
        verify(productRepository).findAllWithPaging(0, 10);
        verify(model).addAttribute("infoMessage", "該当する商品情報がありません");
        verify(model).addAttribute("searched", true);
    }

    @Test
    void searchProductsByCategoryAndSetModel_redirectsToAllWhenCategoryIsZero() {
        Model model = mock(Model.class);
        List<Product> products = List.of(new Product());
        when(productRepository.findAllWithPaging(0, 10)).thenReturn(products);
        when(productRepository.countAll()).thenReturn(1);

        boolean result = productServiceImpl.searchProductsByCategoryAndSetModel(0, 1, model);

        assertTrue(result);
        verify(productRepository).findAllWithPaging(0, 10);
        verify(productRepository).countAll();
    }
}
