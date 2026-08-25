package com.example.fullness.stationary.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.ProductStock;

@Mapper
public interface ProductStockRepository {
    /**
     * 新しい在庫情報を登録する(UC010)
     * 
     * @param stock 登録する在庫情報（商品ID、初期在庫数が含まれる）
     */
    public int insertStock(ProductStock stock);
}
