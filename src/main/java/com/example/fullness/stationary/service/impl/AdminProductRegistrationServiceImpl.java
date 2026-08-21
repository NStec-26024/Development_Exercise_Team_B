package com.example.fullness.stationary.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.helper.ProductHelper;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.AdminProductRegistrationService;

@Service
@Transactional
public class AdminProductRegistrationServiceImpl implements AdminProductRegistrationService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductHelper productHelper;

    /**
     * 全カテゴリを取得します。
     *
     * @return 取得したカテゴリのリスト。リポジトリが null を返すか例外発生時は空リストを返す。
     */
    @Override
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
     * カテゴリIDからカテゴリ名を取得
     *
     */
    @Override
    public ProductCategory getCategoryNameById(Integer categoryId) {
        return productCategoryRepository.findById(categoryId);
    }

    @Override
    public int addProduct(AdminProductRegistrationForm adminProductRegistrationForm) {
        Product product = productHelper.formToEntity(adminProductRegistrationForm);

        productRepository.insertProduct(product);
        productRepository.insertCategory(product);
        productRepository.insertStock(product);

        int accountId = product.getId();
        return accountId;
    }

    @Override
    public Product getById(int id) {
        return productRepository.selectById(id);
    }

}
