package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import com.example.fullness.stationary.entity.ProductCategory;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/schema.sql", "/sql/data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProductCategoryRepositoryTest {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Test
    public void InsertTest_case1() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("衣料品");
        productCategoryRepository.insert(productCategory);
        int actual = productCategory.getId();
        assertEquals(4, actual);
        assertInstanceOf(ProductCategory.class, productCategory);
    }

    @Test
    public void selectByNameTest_OK_case2() {
        ProductCategory productCategory = productCategoryRepository.selectByName("文具");
        assertEquals(1, productCategory.getId());
        assertEquals("文具", productCategory.getName());
        assertInstanceOf(ProductCategory.class, productCategory);

    }

    @Test
    public void selectByNameTest_null_case3() {
        ProductCategory productCategory = productCategoryRepository.selectByName("衣料品");
        assertEquals(null, productCategory);
    }

    @Test
    public void selectByIdTest_OK_case4() {
        ProductCategory productCategory = productCategoryRepository.selectById(1);
        assertEquals("文具", productCategory.getName());
        assertInstanceOf(ProductCategory.class, productCategory);

    }

    @Test
    public void selectByIdTest_null_case5() {
        ProductCategory productCategory = productCategoryRepository.selectById(4);
        assertEquals(null, productCategory);
    }
}
