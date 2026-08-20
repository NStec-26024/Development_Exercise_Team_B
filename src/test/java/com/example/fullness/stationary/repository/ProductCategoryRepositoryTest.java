package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void InsertProductCategoryTest_case1() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("衣料品");
        productCategoryRepository.insertProductCategory(productCategory);
        int actual = productCategory.getId();
        assertEquals(4, actual);
    }

    @Test
    public void selectCategoryNameTest_OK_case2() {
        ProductCategory productCategory = productCategoryRepository.selectCategoryName("文具");
        assertEquals(1, productCategory.getId());
        assertEquals("文具", productCategory.getName());

    }

    @Test
    public void selectAccountNameTest_null_case3() {
        ProductCategory productCategory = productCategoryRepository.selectCategoryName("衣料品");
        assertEquals(null, productCategory);

    }

    @Test
    public void selectCategoryNameWithProductCategoryIdTest_OK_case4() {
        ProductCategory productCategory = productCategoryRepository.selectCategoryNameWithCategoryId(1);
        assertEquals("文具", productCategory.getName());

    }

    @Test
    public void selectCategoryNameWithProductCategoryIdTest_null_case5() {
        ProductCategory productCategory = productCategoryRepository.selectCategoryNameWithCategoryId(4);
        assertEquals(null, productCategory);

    }
}
