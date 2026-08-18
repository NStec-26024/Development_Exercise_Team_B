package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.ProductCategory;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 商品カテゴリ情報を取得するための Mapper インターフェース。
 * UC12（商品修正）で必要な参照機能のみを保持する。
 */
@Mapper
public interface ProductCategoryRepository {

    public int insert(ProductCategory productCategory);

    public ProductCategory selectById(int id);

    public ProductCategory selectByName(String name);

    /** 全カテゴリを取得 */
    List<ProductCategory> findAll();

    /** IDでカテゴリを取得 */
    ProductCategory findById(@Param("id") Integer id);
}
