package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Product implements Serializable {
    Integer id;
    Integer productCategoryId;
    String name;
    Integer price;
    String imageUrl;
    Integer deleteFlag;

    // Join用
    private String categoryName;

    public boolean isDeleted() {
        return deleteFlag != null && deleteFlag == 1;
    }
}
