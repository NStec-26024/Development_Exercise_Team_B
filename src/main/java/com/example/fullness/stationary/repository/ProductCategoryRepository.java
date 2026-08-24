package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.fullness.stationary.entity.ProductCategory;

/**
 * 商品カテゴリ情報を取得するための Mapper インターフェース。
 */
@Mapper
public interface ProductCategoryRepository {

    /**
     * 全カテゴリを取得
     * 
     * @return カテゴリリスト
     */
    List<ProductCategory> selectAll();

    /**
     * IDでカテゴリを取得
     * 
     * @param id カテゴリID
     * @return カテゴリ情報
     */
    ProductCategory selectById(@Param("id") Integer id);

    public ProductCategory selectByName(String name);

}
