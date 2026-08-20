package com.example.fullness.stationary.service;

import com.example.fullness.stationary.entity.ProductCategory;

public interface AdminProductCategoryService {

    /**
     * カテゴリ登録するrepositoryのメソッドを呼び出し、自動採番されたアカウントIDを返却
     * 
     * @param ProductCategory 登録するカテゴリ
     * @return 自動採番されたカテゴリID(serial値)
     */
    public int add(ProductCategory productCategory);

    /**
     * 指定されたカテゴリIDと紐づくカテゴリ名を取得
     * 
     * @param id カテゴリID
     * @return カテゴリ(該当がない場合はnull)
     */
    public ProductCategory getById(int id);

    /**
     * 指定されたカテゴリ名の重複チェックを行う
     * 
     * @param accountName 重複チェック対象のカテゴリ名
     * @return カテゴリ名が未登録な場合は {@code true}、重複する場合は {@code false}
     */
    public boolean existName(String categoryName);

}
