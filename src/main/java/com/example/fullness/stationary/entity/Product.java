package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 商品を表すエンティティクラス。
 * 商品ID、名前、価格、カテゴリ、在庫、画像 URL などを保持する。
 */
@Data
public class Product implements Serializable {
    Integer id;
    Integer productCategoryId;
    String name;
    Integer price;
    String imageUrl;
    Integer deleteFlag;

    private ProductStock productStock;
    // Join用
    private String categoryName;

    public boolean isDeleted() {
        return deleteFlag != null && deleteFlag == 1;
    }
}
