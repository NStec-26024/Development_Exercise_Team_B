package com.example.fullness.stationary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.ProductService;

@Controller
@RequestMapping("/admin/product")
public class PropductController {
    @Autowired
    private ProductService productService;

    /**
     * BP006: 商品検索画面表示（初期表示）
     * URL: /admin/product
     */
    @GetMapping("/product")
    public String productSearchPage(Model model) {
        // カテゴリマスタから全カテゴリ一覧を取得
        List<ProductCategory> categories = productService.getAllCategories();
        model.addAttribute("categories", categories);

        return "admin/product-search";// これ絶対違う！！！！！！！！！！！！！！！！！！！！！！
    }

    /**
     * カテゴリ検索ボタン押下時
     * 選択されたカテゴリの商品をDBから検索し、検索結果を一覧表示
     */
    @PostMapping("/product")
    public String searchProducts(
            @RequestParam(name = "category", required = false) Integer categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {
        try {
            List<ProductCategory> categories = productService.getAllCategories();
            model.addAttribute("categories", categories);

            // 選択されたカテゴリIDを保持
            model.addAttribute("selectedCategoryId", categoryId);

            // 商品検索とページング情報設定
            boolean hasPrevious = productService.searchProductsAndSetModel(categoryId, page, model);

            return "admin/product";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "商品データ取得エラー：" + e.getMessage());

            List<ProductCategory> categories = productService.getAllCategories();
            model.addAttribute("categories", categories);
            return "admin/product";
        }

    }

    // 修正
    @GetMapping("/product/edit/{productId}")
    public String editProduct() {
        return "edit";
    }

    // 削除
    @GetMapping("delete/{productId}")
    public String deleteProduct() {
        return "delete";
    }

    // 追加
    @GetMapping("add")
    public String addProduct() {
        return "add";
    }

}
