package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.fullness.stationary.entity.Product;

@Mapper
public interface ProductRepository {

    List<Product> findByCategoryWithPaging(
            @Param("categoryId") Integer categoryId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    int countByCategory(@Param("categoryId") Integer categoryId);

    int deleteById(@Param("id") Integer id);
}
