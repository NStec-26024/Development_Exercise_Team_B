package com.example.fullness.stationary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.ProductCategory;

@SpringBootTest
@Transactional
@Sql(scripts = {
        "classpath:sql/clear.sql",
        "classpath:sql/data.sql"
}, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@DisplayName("ProductCategoryRepository テスト")
class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository repository;

    // ============================================================
    // 正常系：全カテゴリを取得できること
    // ============================================================
    @Test
    @DisplayName("selectAll - 全カテゴリを取得できる")
    void selectAllTest_case01_Ok() {

        // 実行
        List<ProductCategory> result = repository.selectAll();

        // 検証：結果が null ではない
        assertThat(result).isNotNull();

        // 検証：複数のカテゴリが取得できている(カテゴリが3件取得できているか)
        assertThat(result).isNotEmpty().hasSize(3);

        // 検証：各カテゴリの内容が正しい
        assertThat(result.get(0)).extracting("id", "name")
                .containsExactly(1, "文具");
        assertThat(result.get(1)).extracting("id", "name")
                .containsExactly(2, "雑貨");
        assertThat(result.get(2)).extracting("id", "name")
                .containsExactly(3, "パソコン周辺機器");
    }

    // ============================================================
    // 正常系：ID を指定してカテゴリを取得できること
    // ============================================================
    @Test
    @DisplayName("selectById - ID で特定のカテゴリを取得できる")
    void selectByIdTest_case01_Ok() {

        // 実行
        ProductCategory result = repository.selectById(1);

        // 検証：null ではない
        assertThat(result).isNotNull();

        // 検証：id / name が正しい
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("文具");
    }

    // ============================================================
    // 正常系：複数のカテゴリから正しいカテゴリが取得できること
    // ============================================================
    @Test
    @DisplayName("selectById - 複数カテゴリから正しいカテゴリを取得できる")
    void selectByIdTest_case02_Ok() {

        // 実行
        ProductCategory result = repository.selectById(3);

        // 検証：null ではない
        assertThat(result).isNotNull();

        // 検証：id / name が正しい
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("パソコン周辺機器");
    }

    // ============================================================
    // 異常系：存在しない ID の場合 null が返ること
    // ============================================================
    @Test
    @DisplayName("selectById - 存在しない ID の場合は null が返る")
    void selectByIdTest_case03_Ok() {

        // 実行
        ProductCategory result = repository.selectById(99999);

        // 検証：null が返る
        assertThat(result).isNull();
    }

    // ============================================================
    // 異常系：null ID を渡した場合 null が返ること
    // ============================================================
    @Test
    @DisplayName("selectById - null ID を渡した場合は null が返る")
    void selectByIdTest_case04_Ok() {

        // 実行
        ProductCategory result = repository.selectById(null);

        // 検証：null が返る
        assertThat(result).isNull();
    }
}
