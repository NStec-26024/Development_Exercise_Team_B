package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductDetail implements Serializable {
    private int id;
    private String productName;
    private int price;
    private int stockQuantity;
    private String imageUrl;
    private String categoryName;
}
