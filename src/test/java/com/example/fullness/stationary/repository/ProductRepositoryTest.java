package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Sql(scripts = {
        "classpath:sql/clear.sql",
        "classpath:sql/data.sql"
}, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}
