package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.fullness.stationary.entity.ProductCategory;
import java.util.List;

/**
 * 商品カテゴリ情報を取得するための Mapper インターフェース。
 */
@Mapper
public interface ProductCategoryRepository {

    public int insert(ProductCategory productCategory);

    public ProductCategory selectById(int id);

    public ProductCategory selectByName(String name);

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

}
