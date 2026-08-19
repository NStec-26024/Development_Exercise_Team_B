package com.example.fullness.stationary.service;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    // 1ページあたりの表示件数
    private static final int PAGE_SIZE = 10;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    /**
     * ★ 全カテゴリを取得（プルダウン表示用）
     */
    public List<ProductCategory> getAllCategories() {
        try {
            List<ProductCategory> categories = productCategoryRepository.findAll();

            if (categories == null) {
                System.out.println("WARN: categories is null");
                return new ArrayList<>();
            }

            System.out.println("INFO: カテゴリ件数 = " + categories.size());

            // デバッグ：取得したカテゴリを出力
            for (ProductCategory cat : categories) {
                System.out.println("  ID=" + cat.getId() + ", Name=" + cat.getName());
            }

            return categories;

        } catch (Exception e) {
            System.out.println("ERROR in getAllCategories:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * ★ カテゴリIDからカテゴリ名を取得
     */
    public String getCategoryName(Integer categoryId) {
        if (categoryId == null || categoryId == 0) {
            return null;
        }

        try {
            ProductCategory category = productCategoryRepository.findById(categoryId);
            return (category != null) ? category.getName() : null;
        } catch (Exception e) {
            System.out.println("ERROR in getCategoryName:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ★ 全商品を検索してModelに設定（初期表示用）
     */
    public boolean searchAllProductsAndSetModel(int page, Model model) {
        try {
            System.out.println("=== searchAllProductsAndSetModel ===");
            System.out.println("page: " + page);

            // ページ番号のバリデーション
            if (page < 1) {
                page = 1;
            }

            // オフセット計算
            int offset = (page - 1) * PAGE_SIZE;
            System.out.println("offset: " + offset + ", limit: " + PAGE_SIZE);

            // 商品を取得
            List<Product> products = productRepository.findAllWithPaging(offset, PAGE_SIZE);

            if (products == null) {
                System.out.println("ERROR: products is null");
                products = new ArrayList<>();
            }

            System.out.println("取得した商品件数: " + products.size());

            // 商品が見つからない場合
            if (products.isEmpty()) {
                model.addAttribute("infoMessage", "該当する商品情報がありません");
                model.addAttribute("searched", true);
                model.addAttribute("selectedCategoryId", 0);
                return false;
            }

            // 総件数を取得
            int totalCount = productRepository.countAll();
            System.out.println("総商品件数: " + totalCount);

            // ページング情報をModelに設定
            setPagenationInfo(model, products, page, totalCount, 0, null);

            return true;

        } catch (Exception e) {
            System.out.println("ERROR in searchAllProductsAndSetModel:");
            e.printStackTrace();
            model.addAttribute("errorMessage", "商品情報の取得に失敗しました: " + e.getMessage());
            model.addAttribute("searched", true);
            return false;
        }
    }

    /**
     * ★ カテゴリ別商品を検索してModelに設定
     */
    public boolean searchProductsByCategoryAndSetModel(Integer categoryId, int page, Model model) {
        try {
            System.out.println("=== searchProductsByCategoryAndSetModel ===");
            System.out.println("categoryId: " + categoryId + ", page: " + page);

            // categoryId が 0 の場合は全商品検索
            if (categoryId == null || categoryId == 0) {
                System.out.println("categoryId is 0 → 全商品検索へ");
                return searchAllProductsAndSetModel(page, model);
            }

            // ページ番号のバリデーション
            if (page < 1) {
                page = 1;
            }

            // オフセット計算
            int offset = (page - 1) * PAGE_SIZE;
            System.out.println("offset: " + offset + ", limit: " + PAGE_SIZE);

            // カテゴリ別商品を取得
            List<Product> products = productRepository.findByCategoryWithPaging(
                    categoryId, offset, PAGE_SIZE);

            if (products == null) {
                System.out.println("ERROR: products is null");
                products = new ArrayList<>();
            }

            System.out.println("取得した商品件数: " + products.size());

            // カテゴリ名を取得
            String categoryName = getCategoryName(categoryId);
            System.out.println("カテゴリ名: " + categoryName);

            // 商品が見つからない場合
            if (products.isEmpty()) {
                model.addAttribute("infoMessage", "該当する商品情報がありません");
                model.addAttribute("searched", true);
                model.addAttribute("selectedCategoryId", categoryId);
                model.addAttribute("selectedCategoryName", categoryName);
                return false;
            }

            // 総件数を取得
            int totalCount = productRepository.countByCategory(categoryId);
            System.out.println("カテゴリ内総商品件数: " + totalCount);

            // ページング情報をModelに設定
            setPagenationInfo(model, products, page, totalCount, categoryId, categoryName);

            return true;

        } catch (Exception e) {
            System.out.println("ERROR in searchProductsByCategoryAndSetModel:");
            e.printStackTrace();
            model.addAttribute("errorMessage", "商品情報の取得に失敗しました: " + e.getMessage());
            model.addAttribute("searched", true);
            return false;
        }
    }

    /**
     * ★ ページング情報をModelに設定
     */
    private void setPagenationInfo(Model model, List<Product> products,
            int currentPage, int totalCount,
            Integer categoryId, String categoryName) {

        // 総ページ数を計算
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

        System.out.println("=== ページング情報 ===");
        System.out.println("currentPage: " + currentPage);
        System.out.println("totalPages: " + totalPages);
        System.out.println("totalCount: " + totalCount);

        // Modelに設定
        model.addAttribute("productList", products);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("hasPrevious", currentPage > 1);
        model.addAttribute("hasNext", currentPage < totalPages);
        model.addAttribute("selectedCategoryId", categoryId != null ? categoryId : 0);
        model.addAttribute("selectedCategoryName", categoryName);
        model.addAttribute("searched", true);
    }
}