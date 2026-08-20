package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 商品カテゴリを表すエンティティクラス。
 */
@Data
public class ProductCategory implements Serializable {
    private int id;
    private String name;
}
