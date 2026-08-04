package com.example.fullness.stationary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;

import lombok.Data;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepositroy;
    /* 1ページあたりの表示件数 */
    private static final int PAGE_SIZE = 10;

    /*
     * 
     * カテゴリ別商品一覧取得とページング情報をModelに設定
     * 
     * @param categoryId カテゴリID
     * 
     * @param page ページ番号
     * 
     * @param model モデル
     * 
     * @return 商品が存在する場合true
     */
    public boolean searchProductsAndSetModel(
            Integer categoryId, int page, Model model) {
        if (categoryId == null) {
            throw new IllegalArgumentException("カテゴリを選択してください");
        }
        // ページ番号が1未満の場合は1にする
        if (page < 1) {
            page = 1;
        }

        int offset = (page - 1) * PAGE_SIZE;

        // 商品一覧取得
        List<Product> products = productRepository.findByCategoryWithPaging(categoryId, offset, offset);

        // 商品が存在しない場合
        if (products == null || products.isEmpty()) {
            return false;
        }

        int totalCount = productRepository.countByCategory(categoryId);

        // 総ページ数計算
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        // ページング情報計算
        boolean hasPrevious = page > 1;
        boolean hasNext = page < totalPages;
        model.addAttribute("productList", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("hasPrevious", hasPrevious);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("serched", true);

        return true;
    }

    // 全カテゴリ取得
    public List<ProductCategory> getAllCategories() {
        return productCategoryRepositroy.findAll();
    }

}