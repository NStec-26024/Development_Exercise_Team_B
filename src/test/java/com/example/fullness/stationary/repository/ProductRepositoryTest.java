package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Product;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MybatisTest
@Sql(scripts = {
        "/repository-schema.sql",
        "/repository-data.sql"
}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void selectAllWithPaging_returnsPagedProducts() {
        List<Product> products = productRepository.selectAllWithPaging(0, 10);

        assertThat(products).hasSize(10);
        assertThat(products.get(0).getId()).isEqualTo(1);
        assertThat(products.get(1).getId()).isEqualTo(2);
        assertThat(products.get(2).getId()).isEqualTo(3);
    }

    @Test
    void selectByCategoryWithPaging_returnsCategoryProducts() {
        List<Product> products = productRepository.selectByCategoryWithPaging(1, 0, 10);

        assertThat(products).hasSize(10);
        assertThat(products).extracting(Product::getCategoryId).containsOnly(1);
    }

    @Test
    void countAll_returnsTotalCount() {
        int count = productRepository.countAll();

        assertThat(count).isEqualTo(28);
    }

    @Test
    void countByCategory_returnsCategoryCount() {
        int count = productRepository.countByCategory(1);

        assertThat(count).isEqualTo(16);
    }

    @Test
    void selectById_returnsProductWhenExists() {
        Product product = productRepository.selectById(10);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("油性ボールペン(赤)");
    }

    @Test
    void selectById_returnsNullWhenMissing() {
        Product product = productRepository.selectById(99);

        assertThat(product).isNull();
    }

    @Test
    void deleteById_softDeletion() {
        // Arrange
        Integer id = 1;

        Integer beforeDeleteFlg = jdbcTemplate.queryForObject(
                "SELECT delete_flg FROM product WHERE id = ?",
                Integer.class,
                id);

        // Act
        productRepository.deleteById(id);

        // Assert
        Integer afterDeleteFlg = jdbcTemplate.queryForObject(
                "SELECT delete_flg FROM product WHERE id = ?",
                Integer.class,
                id);

        assertEquals(0, beforeDeleteFlg);
        assertEquals(1, afterDeleteFlg);
    }
}
