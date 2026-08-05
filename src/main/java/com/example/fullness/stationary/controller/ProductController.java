package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ProductController {

    @Autowired
    private ProductService productService;

    private Integer parseCategoryId(String category) {
        if (category == null || category.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.valueOf(category.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 商品検索画面表示（GET）
     */
    @GetMapping("/product")
    public String productSearchPage(
            @RequestParam(name = "category", required = false, defaultValue = "") String category,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {

        Integer categoryId = parseCategoryId(category);
        System.out.println("=== GET /admin/product ===");
        System.out.println("categoryId: " + categoryId + ", page: " + page);

        // カテゴリ一覧を取得
        List<ProductCategory> categories = productService.getAllCategories();
        System.out.println("categories size: " + categories.size());
        model.addAttribute("categories", categories);

        // 検索条件に応じて商品を検索
        if (categoryId == null || categoryId == 0) {
            productService.searchAllProductsAndSetModel(page, model);
        } else {
            productService.searchProductsByCategoryAndSetModel(categoryId, page, model);
        }

        return "admin/product-search";
    }

    /**
     * カテゴリ検索（POST）
     */
    @PostMapping("/product")
    public String searchProductsByCategory(
            @RequestParam(name = "category", required = false, defaultValue = "") String category,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {

        Integer categoryId = parseCategoryId(category);
        System.out.println("=== POST /admin/product ===");
        System.out.println("categoryId: " + categoryId);
        System.out.println("page: " + page);

        // カテゴリ一覧を取得
        List<ProductCategory> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);

        // ★ categoryId が 0 なら全商品、それ以外はカテゴリ別
        if (categoryId == null || categoryId == 0) {
            System.out.println("検索モード: 全商品");
            productService.searchAllProductsAndSetModel(page, model);
        } else {
            System.out.println("検索モード: カテゴリID=" + categoryId);
            productService.searchProductsByCategoryAndSetModel(categoryId, page, model);
        }

        return "admin/product-search";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(
            @PathVariable Integer id,
            @RequestParam(name = "category", required = false, defaultValue = "0") Integer categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        return "redirect:/admin/product/update/" + id
                + "?category=" + categoryId
                + "&page=" + page;
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(
            @PathVariable Integer id,
            @RequestParam(name = "category", required = false, defaultValue = "0") Integer categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        return "redirect:/admin/product/delete/" + id
                + "?category=" + categoryId
                + "&page=" + page;
    }

    @GetMapping("/product/add")
    public String addProduct(
            @RequestParam(name = "category", required = false, defaultValue = "") String category) {

        Integer categoryId = parseCategoryId(category);
        return "redirect:/admin/product/register?category=" + (categoryId != null ? categoryId : 0);
    }
}