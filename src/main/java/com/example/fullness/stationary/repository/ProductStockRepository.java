package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.ProductStock;

import org.apache.ibatis.annotations.Param;

/**
 * 商品在庫（product_stock）を取得・更新するための Mapper インターフェース。
 */
@Mapper
public interface ProductStockRepository {

    /**
     * 商品IDに対応する在庫レコードを新規登録する。
     *
     * @param productId 商品ID
     * @param quantity  在庫数
     * @return 登録件数
     */
    int insert(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    /**
     * 商品IDに対応する在庫数を更新する。
     *
     * @param productId 商品ID
     * @param quantity  更新後の在庫数
     * @return 更新件数
     */
    int updateByProductId(@Param("productId") Integer productId, @Param("quantity") Integer quantity);

    /**
     * 新しい在庫情報を登録する(UC010)
     * 
     * @param stock 登録する在庫情報（商品ID、初期在庫数が含まれる）
     */
    public int insertStock(ProductStock stock);
}
