package com.example.fullness.stationary.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 商品を表すエンティティクラス。
 * 商品ID、名前、価格、カテゴリ、在庫、画像 URL などを保持する。
 */
@Data
public class Product implements Serializable {
    private Integer id;
    private String name;
    private Integer price;
    private Integer categoryId;
    private String imageUrl;

    private ProductStock productStock;
}
