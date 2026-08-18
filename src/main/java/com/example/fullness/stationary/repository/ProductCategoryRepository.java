package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.fullness.stationary.entity.ProductCategory;

/**
 * 商品カテゴリ情報を取得するための Mapper インターフェース。
 * UC12（商品修正）で必要な参照機能のみを保持する。
 */
@Mapper
public interface ProductCategoryRepository {

    /** 全カテゴリを取得 */
    List<ProductCategory> findAll();

    /** IDでカテゴリを取得 */
    ProductCategory findById(@Param("id") Integer id);
}
