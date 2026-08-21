package com.example.fullness.stationary.entity;

import lombok.Data;

/**
 * 商品在庫を表すエンティティクラス（対応するテーブル名は product_stock）。
 */
@Data
public class ProductStock {
    private Integer id;
    private Integer productId;
    private Integer quantity;
}