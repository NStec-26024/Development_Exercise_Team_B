package com.example.fullness.stationary.repository;

import om.e

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@Sql(scripts = {
        "/repository-schema.sql",
        "/repository-data.sql"
}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    void findAll_returnsAllCategories() {
        List<ProductCategory> categories = productCategoryRepository.selectAll();

        assertThat(categories).hasSize(3);
        assertThat(categories).extracting(ProductCategory::getName).containsExactly("文具", "雑貨", "パソコン周辺機器");
    }

    @Test
    void findById_returnsCategoryWhenExists() {
        ProductCategory category = productCategoryRepository.selectById(1);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("文具");
    }


void findById_returnsNullWhenMissing() {
        ProductCategory category = productCategoryRepository.selectById(99);

        assertThat(category).isNull();
    }
}
