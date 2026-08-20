package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.ProductCategory;

@Mapper
public interface ProductCategoryRepository {

    public int insertProductCategory(ProductCategory productCategory);

    public ProductCategory selectCategoryNameWithCategoryId(int id);

    public ProductCategory selectCategoryName(String name);

}
