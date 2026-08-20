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

import com.example.fullness.stationary.entity.Product;

@SpringBootTest
@Transactional
@Sql(scripts = {
        "classpath:sql/clear.sql",
        "classpath:sql/data.sql"
}, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
@DisplayName("ProductRepository テスト（仕様書準拠）")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    // ============================================================
    // 正常系：全商品取得（削除済み除く）
    // ============================================================
    @Test
    @DisplayName("findAllWithPaging - 商品リストが返る")
    void findAllWithPaging_case01_Ok() {

        List<Product> result = repository.findAllWithPaging(0, 10);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();

        // 仕様書の例：PDS001 が返るケース
        assertThat(result.get(0).getName()).isEqualTo("マーカー(青)");
    }

    // ============================================================
    // 異常系：全商品取得（空リスト）
    // ============================================================
    @Test
    @DisplayName("findAllWithPaging - 空リストが返る")
    void findAllWithPaging_case02_Empty() {

        List<Product> result = repository.findAllWithPaging(999, 10);

        assertThat(result).isEmpty();
    }

    // ============================================================
    // 正常系：カテゴリ別商品取得（削除済み除く）
    // ============================================================
    @Test
    @DisplayName("findByCategoryWithPaging - カテゴリの商品が返る")
    void findByCategoryWithPaging_case01_Ok() {

        List<Product> result = repository.findByCategoryWithPaging(1, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("マーカー(青)");
    }

    // ============================================================
    // 異常系：カテゴリ別商品取得（空リスト）
    // ============================================================
    @Test
    @DisplayName("findByCategoryWithPaging - 空リストが返る")
    void findByCategoryWithPaging_case02_Empty() {

        List<Product> result = repository.findByCategoryWithPaging(999, 0, 10);

        assertThat(result).isEmpty();
    }

    // ============================================================
    // 正常系：全商品件数取得（削除済み除く）
    // ============================================================
    @Test
    @DisplayName("countAll - 総件数が返る")
    void countAll_case01_Ok() {

        int count = repository.countAll();

        assertThat(count).isEqualTo(28); // 仕様書の totalCount に合わせる
    }

    // ============================================================
    // 正常系：カテゴリ別商品件数取得
    // ============================================================
    @Test
    @DisplayName("countByCategory - カテゴリ件数が返る")
    void countByCategory_case01_Ok() {

        int count = repository.countByCategory(1);

        assertThat(count).isEqualTo(16); // データセットに合わせる
    }

    // ============================================================
    // 異常系：カテゴリ別商品件数（0件）
    // ============================================================
    @Test
    @DisplayName("countByCategory - 0件が返る")
    void countByCategory_case02_Zero() {

        int count = repository.countByCategory(999);

        assertThat(count).isEqualTo(0);
    }

    // ============================================================
    // 正常系：ID検索
    // ============================================================
    @Test
    @DisplayName("findById - 商品が返る")
    void findById_case01_Ok() {

        Product result = repository.findById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("マーカー(青)");
    }

    // ============================================================
    // 異常系：ID検索（存在しない ID）
    // ============================================================
    @Test
    @DisplayName("findById - null が返る（存在しない ID）")
    void findById_case02_NotFound() {

        Product result = repository.findById(99999);

        assertThat(result).isNull();
    }

    // ============================================================
    // 異常系：ID検索（null）
    // ============================================================
    @Test
    @DisplayName("findById - null が返る（ID = null）")
    void findById_case03_Null() {

        Product result = repository.findById(null);

        assertThat(result).isNull();
    }

}
