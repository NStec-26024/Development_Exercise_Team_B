package com.example.fullness.stationary.controller;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.service.AdminProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理画面向けの商品操作を担当するコントローラクラス。
 *
 * <p>
 * 商品検索画面の表示、カテゴリによる検索条件の受け渡し、各種リダイレクト（編集・削除・追加）を提供します。
 */
@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private AdminProductService productService;

    /**
     * リクエストパラメータのカテゴリID文字列を解析して Integer に変換します。
     * 空文字または変換不可な値は 0 を返し、コントローラ側で「全カテゴリ扱い」として扱います。
     *
     * @param category カテゴリIDを表す文字列（nullable）
     * @return 変換後のカテゴリID（不正な場合は 0）
     */
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
     * 商品検索画面を表示するエンドポイント（GET）。
     *
     * <p>
     * クエリパラメータ `category` と `page` を受け取り、カテゴリ一覧をモデルに追加した上で
     * `ProductService` による検索処理を呼び出す。検索結果は `Model` に格納され、ビュー
     * `admin/product/search` を返す。
     *
     * @param category カテゴリID の文字列（省略可）
     * @param page     表示するページ番号（省略時は 1）
     * @param model    結果を格納する Spring の Model
     * @return 表示するテンプレート名（admin/product/search）
     */
    @GetMapping("/product")
    public String productSearchPage(
            @RequestParam(name = "category", required = false, defaultValue = "") String category,
            @RequestParam(name = "categoryId", required = false, defaultValue = "") String categoryIdParam,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {

        String selectedCategory = (category == null || category.trim().isEmpty())
                ? categoryIdParam
                : category;
        Integer categoryId = parseCategoryId(selectedCategory);
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

        return "admin/product/search";
    }

    /**
     * 編集ページへのリダイレクトを行うエンドポイント。
     *
     * @param id         編集対象の商品ID
     * @param categoryId 検索時のカテゴリID（リダイレクト先に付与）
     * @param page       検索時のページ番号（リダイレクト先に付与）
     * @return 編集画面へのリダイレクト URL
     */
    @GetMapping("/product/edit/{id}")
    public String editProduct(
            @PathVariable Integer id,
            @RequestParam(name = "category", required = false, defaultValue = "0") Integer categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        return "redirect:/admin/product/update/" + id
                + "?category=" + categoryId
                + "&page=" + page;
    }

    /**
     * 削除確認ページへのリダイレクトを行うエンドポイント。
     *
     * @param id         削除対象の商品ID
     * @param categoryId 検索時のカテゴリID（リダイレクト先に付与）
     * @param page       検索時のページ番号（リダイレクト先に付与）
     * @return 削除確認画面へのリダイレクト URL
     */
    @GetMapping("/product/delete/{id}")
    public String deleteProduct(
            @PathVariable Integer id,
            @RequestParam(name = "category", required = false, defaultValue = "0") Integer categoryId,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        return "redirect:/admin/product/delete/" + id
                + "?category=" + categoryId
                + "&page=" + page;
    }

    /**
     * 追加画面へのリダイレクトを行うエンドポイント。
     *
     * @param category カテゴリID の文字列（省略可）。指定がある場合はリダイレクト URL に付与する。
     * @return 追加画面へのリダイレクト URL
     */
    @GetMapping("/product/add")
    public String addProduct(
            @RequestParam(name = "category", required = false, defaultValue = "") String category) {

        Integer categoryId = parseCategoryId(category);
        return "redirect:/admin/product/register?category=" + (categoryId != null ? categoryId : 0);
    }
}