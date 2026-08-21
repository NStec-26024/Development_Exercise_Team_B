package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.Product;

@Mapper
public interface ProductRepository {

    /**
     * 新商品をデータベースに登録
     * 
     * @param product 登録する商品
     * @return 挿入された行数（通常は1）
     */
    public int insertProduct(Product product);

    /**
     * 商品IDから商品を取得
     * 
     * @param id
     * @return 商品IDに紐づいた商品
     */
    public Product selectById(int id);

    public void insertStock(Product product);

    public void insertCategory(Product product);

}
