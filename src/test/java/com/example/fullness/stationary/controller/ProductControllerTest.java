package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getAdminProductWithCategoryIdParam_searchesByCategory() throws Exception {
        ProductCategory category = new ProductCategory();
        category.setId(2);
        category.setName("文具");

        when(productService.getAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/admin/product").param("categoryId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/search"));

        verify(productService).searchProductsByCategoryAndSetModel(eq(2), eq(1), any());
    }
}
