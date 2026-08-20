package com.example.fullness.stationary.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class ProductStockRepositoryTest {

    @Autowired
    private ProductStockRepository productStockRepository;

    @Test
    void updateByProductId_case01_Ok() {

        Integer productId = 1; // data.sql に存在する ID
        Integer newQuantity = 50;

        // 実行
        int updatedCount = productStockRepository.updateByProductId(productId, newQuantity);

        // 更新件数の検証
        assertThat(updatedCount).isEqualTo(1);

    }

    @Test
    void updateByProductId_case02_Ok() {

        Integer nonExistId = 999; // data.sql に存在しない ID
        Integer newQuantity = 50;

        // 実行
        int updatedCount = productStockRepository.updateByProductId(nonExistId, newQuantity);

        // 更新件数が 0 であること
        assertThat(updatedCount).isEqualTo(0);
    }
}
