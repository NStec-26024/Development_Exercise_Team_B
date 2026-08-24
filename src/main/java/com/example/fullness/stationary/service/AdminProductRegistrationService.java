package com.example.fullness.stationary.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;

@Service
public interface AdminProductRegistrationService {

    // /**
    // * 指定したカテゴリ ID に対応するカテゴリ名を取得します。
    // *
    // * @param categoryId カテゴリ ID
    // * @return カテゴリ名、もしくは存在しない場合は null
    // */
    // public ProductCategory getCategoryNameById(Integer categoryId);

    /**
     * 社員アカウント登録するrepositoryのメソッドを呼び出し、自動採番されたアカウントIDを返却
     * 
     * @param product 登録する商品
     * @return 自動採番されたアカウントID(serial値)
     */
    @Transactional
    public void addProduct(AdminProductRegistrationForm adminProductRegistrationForm);

}
