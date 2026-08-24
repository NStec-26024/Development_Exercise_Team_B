package com.example.fullness.stationary.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.AdminProductService;

@Service("productService")
public class AdminProductServiceImpl implements AdminProductService {
    /**
     * 1ページあたりの表示件数。
     */
    private static final int PAGE_SIZE = 10;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * 商品サービスの具体実装です。
     * <p>
     * {@link AdminProductService} で定義された契約を実体化し、
     * {@link ProductRepository} と {@link ProductCategoryRepository} を利用して
     * 商品検索・カテゴリ検索・ページング情報の付与を行います。
     */
    public AdminProductServiceImpl() {
        // Spring による自動生成用コンストラクタ
    }

    /**
     * 全カテゴリを取得します。
     *
     * @return 取得したカテゴリのリスト。リポジトリが null を返すか例外発生時は空リストを返す。
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
     *
     * @param categoryId カテゴリID（null または 0 の場合は null を返す）
     * @return カテゴリ名。該当なしまたは例外時は null。
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
     *
     * <p>
     * 指定ページのオフセットと件数を計算して商品を取得し、結果があれば
     * ページング情報を `model` に追加する。商品が見つからない場合は情報メッセージを
     * `model` に設定して false を返す。例外発生時は `errorMessage` を設定して false を返す。
     *
     * @param page  表示するページ番号（1 始まり。1 未満なら 1 として扱う）
     * @param model Spring の `Model`（結果・メッセージを格納）
     * @return 商品が存在してページング情報を設定した場合は true、そうでなければ false
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
                model.addAttribute("infoMessage", messageSource.getMessage("product.emsg12", null, Locale.JAPAN));
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
            throw new AdminBusinessException(messageSource.getMessage("product.info.failed", null, Locale.JAPAN));
        }
    }

    /**
     * ★ カテゴリ別商品を検索してModelに設定
     *
     * <p>
     * `categoryId` が null または 0 の場合は全商品検索にフォールバックする。カテゴリ別に
     * 商品を取得し、結果に応じて `model` にページング情報または情報メッセージを設定する。
     * 例外時は `errorMessage` を設定して false を返す。
     *
     * @param categoryId 対象カテゴリの ID（0 は全カテゴリ扱い）
     * @param page       表示するページ番号（1 始まり）
     * @param model      Spring の `Model`（結果・メッセージを格納）
     * @return 商品が存在してページング情報を設定した場合は true、そうでなければ false
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
                model.addAttribute("infoMessage", messageSource.getMessage("product.emsg12", null, Locale.JAPAN));
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
            throw new AdminBusinessException(messageSource.getMessage("product.info.failed", null, Locale.JAPAN));
        }

    }

    /**
     * ★ ページング情報をModelに設定
     *
     * <p>
     * 与えられた商品リストおよび総件数から総ページ数を計算し、ページング関連の属性を
     * `model` に追加するユーティリティメソッド。
     *
     * @param model        Spring の `Model`（属性を設定）
     * @param products     現在ページの表示商品リスト
     * @param currentPage  現在のページ番号
     * @param totalCount   総件数
     * @param categoryId   選択カテゴリID（カテゴリ検索時）、ない場合は null
     * @param categoryName 選択カテゴリ名（カテゴリ検索時）、ない場合は null
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
