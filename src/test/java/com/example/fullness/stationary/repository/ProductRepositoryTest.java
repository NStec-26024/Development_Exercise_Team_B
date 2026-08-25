package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Product;

import org.junit.jupiter.api.DisplayName;
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

    // === HEAD ブランチのテスト ===

    @Test
    @DisplayName("selectAllWithPaging - 商品リストが返る")
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

    // === development ブランチのテスト ===

    /**
     * 1. 正常系：更新件数と更新後の値を DB 直接参照で検証する
     */
    @Test
    void update_case01_Ok() {

        // --- Arrange ---
        Integer id = 1;
        Product p = new Product();
        p.setId(id);
        p.setName("新しい商品名");
        p.setPrice(999);
        p.setCategoryId(2);
        p.setImageUrl("new.png");

        int updatedCount = productRepository.update(p);

        assertThat(updatedCount)
                .as("更新件数が 1 件であること")
                .isEqualTo(1);

        var updated = jdbcTemplate.queryForMap(
                "SELECT name, price, product_category_id, image_url FROM product WHERE id = ?",
                id);

        assertThat(updated)
                .as("更新後のレコードが正しく反映されていること")
                .containsEntry("name", "新しい商品名")
                .containsEntry("price", 999)
                .containsEntry("product_category_id", 2)
                .containsEntry("image_url", "new.png");
    }

    /**
     * 2. 異常系：存在しない ID の場合、更新件数が 0 件である
     */
    @Test
    void update_case02_Ok() {

        Product p = new Product();
        p.setId(999); // data.sql に存在しない ID
        p.setName("新しい商品名");

        int updatedCount = productRepository.update(p);

        assertThat(updatedCount).isEqualTo(0);
    }

    // ============================================================
    // 異常系：ID検索（存在しない ID）
    // ============================================================
    @Test
    @DisplayName("selectById - null が返る（存在しない ID）")
    void selectById_case02_NotFound() {

        Product result = productRepository.selectById(99999);

        assertThat(result).isNull();
    }

    // ============================================================
    // 異常系：ID検索（null）
    // ============================================================
    @Test
    @DisplayName("selectById - null が返る（ID = null）")
    void selectById_case03_Null() {

        Product result = productRepository.selectById(null);

        assertThat(result).isNull();
    }

    // ============================================================
    // 正常系：商品登録
    // ============================================================
    @Test
    @Sql(scripts = { "/repository-schema.sql",
            "/repository-data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

    void testInsertProduct() {

        Product inputProduct = new Product();
        inputProduct.setName("くまのぬいぐるみ<>限定</>");
        inputProduct.setPrice(2300);

        int count = productRepository.insertProduct(inputProduct);

        // 挿入された行数が1
        assertThat(count).isEqualTo(1);

        // 自動採番されたIDが29か
        assertThat(inputProduct.getId()).isEqualTo(29);

    }

}
