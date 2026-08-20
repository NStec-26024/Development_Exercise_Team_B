package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.ProductCategory;

@Mapper
public interface ProductCategoryRepository {

    public int insert(ProductCategory productCategory);

    public ProductCategory selectById(int id);

    public ProductCategory selectByName(String name);

}
