package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.fullness.stationary.entity.ProductCategory;

@Mapper
public interface ProductCategoryRepository {

    /**
     * 全カテゴリを取得
     * 
     * @return カテゴリリスト
     */
    List<ProductCategory> findAll();

    /**
     * IDでカテゴリを取得
     * 
     * @param id カテゴリID
     * @return カテゴリ情報
     */
    ProductCategory findById(@Param("id") Integer id);

    /**
     * カテゴリに紐づく商品の件数を取得
     */

}
