package com.example.fullness.stationary.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;

@Service
public interface AdminProductRegistrationService {
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
    public ProductCategory getCategoryNameById(Integer categoryId);

    /**
     * 社員アカウント登録するrepositoryのメソッドを呼び出し、自動採番されたアカウントIDを返却
     * 
     * @param product 登録する商品
     * @return 自動採番されたアカウントID(serial値)
     */
    @Transactional
    public void addProduct(AdminProductRegistrationForm adminProductRegistrationForm);

    /**
     * 商品IDから商品を取得
     * 
     * @param id
     * @return 商品IDに紐づいた商品
     */
    public Product getById(int id);

}
