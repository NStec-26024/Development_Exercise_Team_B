package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.FullnessStationaryApplication;
import com.example.fullness.stationary.entity.ProductCategory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
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
@Sql(scripts = { "/repository-schema.sql", "/repository-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@MapperScan("com.example.fullness.stationary.repository")
class ProductCategoryRepositoryMybatisTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    void findAll_returnsAllCategories() {
        List<ProductCategory> categories = productCategoryRepository.findAll();

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(ProductCategory::getName).containsExactly("文具", "文房具");
    }

    @Test
    void findById_returnsCategoryWhenExists() {
        ProductCategory category = productCategoryRepository.findById(1);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("文具");
    }

    @Test
    void findById_returnsNullWhenMissing() {
        ProductCategory category = productCategoryRepository.findById(99);

        assertThat(category).isNull();
    }
}
