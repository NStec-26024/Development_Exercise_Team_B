package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.FullnessStationaryApplication;
import com.example.fullness.stationary.entity.Product;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FullnessStationaryApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.enabled=false"
})
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(scripts = {"/repository-schema.sql", "/repository-data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@MapperScan("com.example.fullness.stationary.repository")
class ProductRepositoryMybatisTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findAllWithPaging_returnsPagedProducts() {
        List<Product> products = productRepository.findAllWithPaging(0, 10);

        assertThat(products).hasSize(3);
        assertThat(products.get(0).getId()).isEqualTo(10);
        assertThat(products.get(1).getId()).isEqualTo(20);
        assertThat(products.get(2).getId()).isEqualTo(30);
    }

    @Test
    void findByCategoryWithPaging_returnsCategoryProducts() {
        List<Product> products = productRepository.findByCategoryWithPaging(1, 0, 10);

        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getCategoryId).containsOnly(1);
    }

    @Test
    void countAll_returnsTotalCount() {
        int count = productRepository.countAll();

        assertThat(count).isEqualTo(3);
    }

    @Test
    void countByCategory_returnsCategoryCount() {
        int count = productRepository.countByCategory(1);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void findById_returnsProductWhenExists() {
        Product product = productRepository.findById(10);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("ペン");
    }

    @Test
    void findById_returnsNullWhenMissing() {
        Product product = productRepository.findById(99);

        assertThat(product).isNull();
    }

    @Test
    void deleteById_marksProductAsDeletedAndReturnsCount() {
        int updated = productRepository.deleteById(20);

        assertThat(updated).isEqualTo(1);
        Integer deletedFlag = jdbcTemplate.queryForObject("SELECT delete_flg FROM product WHERE id = ?", Integer.class, 20);
        assertThat(deletedFlag).isEqualTo(1);
    }
}
