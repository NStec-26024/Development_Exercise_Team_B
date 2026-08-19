package com.example.fullness.stationary.service;

import java.util.List;

import org.springframework.ui.Model;

import com.example.fullness.stationary.entity.ProductCategory;

/**
 * 商品関連のビジネスロジックを定義するサービスの契約です。
 * <p>
 * このインタフェースは管理画面の商品検索、カテゴリ取得、
 * カテゴリ別検索とページングのためのメソッドを提供します。
 */
public interface AdminProductService {

    /**
     * 全ての商品カテゴリを取得します。
     *
     * @return 取得したカテゴリのリスト
     */
    public List<ProductCategory> getAllCategories();

    /**
     * 指定したカテゴリ ID に対応するカテゴリ名を取得します。
     *
     * @param categoryId カテゴリ ID
     * @return カテゴリ名、もしくは存在しない場合は null
     */
    public String getCategoryName(Integer categoryId);

    /**
     * 全商品を検索し、検索結果とページング情報を Model に設定します。
     *
     * @param page  表示するページ番号
     * @param model Spring の Model
     * @return 商品が存在してページング情報が設定された場合は true、それ以外は false
     */
    public boolean searchAllProductsAndSetModel(int page, Model model);

    /**
     * 指定カテゴリに属する商品を検索し、検索結果とページング情報を Model に設定します。
     *
     * @param categoryId カテゴリ ID
     * @param page       表示するページ番号
     * @param model      Spring の Model
     * @return 商品が存在してページング情報が設定された場合は true、それ以外は false
     */
    public boolean searchProductsByCategoryAndSetModel(Integer categoryId, int page, Model model);

}
