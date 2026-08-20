package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.service.AdminProductService;
import com.example.fullness.stationary.FullnessStationaryApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * `AdminProductController` を `@SpringBootTest` コンテキストで検証するテスト。
 * <p>
 * 実際の DB は使用せず、`AdminProductService` をモック化して動作を検証します。
 */
@SpringBootTest(classes = FullnessStationaryApplication.class, properties = "spring.autoconfigure.exclude=org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
@AutoConfigureMockMvc
class AdminProductControllerSpringBootTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminProductService productService;

    @Test
    void productSearchPage_searchesByCategoryParam() throws Exception {
        ProductCategory category = new ProductCategory();
        category.setId(2);
        category.setName("文具");

        when(productService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/admin/product").param("categoryId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/search"));

        verify(productService).searchProductsByCategoryAndSetModel(eq(2), eq(1), any());
    }

    @Test
    void productSearchPage_usesCategoryParamWhenProvided() throws Exception {
        ProductCategory category = new ProductCategory();
        category.setId(3);
        category.setName("文房具");

        when(productService.getAllCategories()).thenReturn(List.of(category));

        // category query param should take precedence
        mockMvc.perform(get("/admin/product").param("category", "3").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/search"));

        verify(productService).searchProductsByCategoryAndSetModel(eq(3), eq(2), any());
    }

    @Test
    void productSearchPage_withInvalidCategoryString_callsAllProducts() throws Exception {
        when(productService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/admin/product").param("category", "abc"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/search"));

        verify(productService).searchAllProductsAndSetModel(eq(1), any());
    }

    @Test
    void editProduct_redirectsToUpdateUrl() throws Exception {
        mockMvc.perform(get("/admin/product/edit/5").param("category", "2").param("page", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    String loc = result.getResponse().getRedirectedUrl();
                    assert loc != null && loc.contains("/admin/product/update/5");
                });
    }

    @Test
    void deleteProduct_redirectsToDeleteConfirmUrl() throws Exception {
        mockMvc.perform(get("/admin/product/delete/7").param("category", "1").param("page", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    String loc = result.getResponse().getRedirectedUrl();
                    assert loc != null && loc.contains("/admin/product/delete/7");
                });
    }

    @Test
    void addProduct_redirectsToRegisterWithCategory() throws Exception {
        mockMvc.perform(get("/admin/product/add").param("category", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> {
                    String loc = result.getResponse().getRedirectedUrl();
                    assert loc != null && loc.contains("/admin/product/register?category=4");
                });
    }
}
