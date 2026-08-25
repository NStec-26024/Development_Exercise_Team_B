package com.example.fullness.stationary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import com.example.fullness.stationary.entity.ProductStock;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/repository-schema.sql",
        "/repository-data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProductStockRepositoryTest {

    @Autowired
    private ProductStockRepository productStockRepository;

    private ProductStock stock;

    @Test
    void testInsertStockOK() {

        stock = new ProductStock();
        stock.setProductId(3);
        stock.setQuantity(1000);

        int count = productStockRepository.insertStock(stock);

        assertThat(count).isEqualTo(1);
        assertThat(stock.getId()).isEqualTo(1);

    }

}
