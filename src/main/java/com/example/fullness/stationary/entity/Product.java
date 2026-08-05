package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Product implements Serializable {
    private Integer id;
    private String name;
    private Integer price;
    private Integer categoryId;
    private Integer stock;
    private String imageUrl;
    private String description;

    // ★ カテゴリ名を追加（JOIN時に使用）
    private String categoryName;
}