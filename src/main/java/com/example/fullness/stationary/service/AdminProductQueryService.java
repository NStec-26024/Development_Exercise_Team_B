package com.example.fullness.stationary.service;

import java.util.List;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;

/**
 * 商品・商品カテゴリの参照（検索・一覧取得）を担うサービスの契約。
 * UC12（商品修正）に必要な参照機能のみを保持する。
 */
public interface AdminProductQueryService {

    List<ProductCategory> getAllCategories();

    String getCategoryName(Integer categoryId);

    Product getProductById(Integer id);

    List<Product> searchAllProducts(int page);

    List<Product> searchProductsByCategory(Integer categoryId, int page);

    int countAllProducts();

    int countProductsByCategory(Integer categoryId);
}
