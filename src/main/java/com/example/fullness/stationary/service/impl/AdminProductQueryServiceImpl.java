package com.example.fullness.stationary.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.repository.ProductCategoryRepository;
import com.example.fullness.stationary.repository.ProductRepository;
import com.example.fullness.stationary.service.AdminProductQueryService;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link AdminProductQueryService} の実装。
 * 商品・商品カテゴリの参照（検索・一覧取得）のみを責務とする。
 */
@Slf4j
@Service
public class AdminProductQueryServiceImpl implements AdminProductQueryService {

    /** 1ページあたりの表示件数。 */
    private static final int PAGE_SIZE = 10;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * 商品カテゴリの一覧を取得する。
     */
    @Override
    public List<ProductCategory> getAllCategories() {
        try {
            List<ProductCategory> categories = productCategoryRepository.findAll();
            return (categories != null) ? categories : new ArrayList<>();
        } catch (Exception e) {
            throw new AdminBusinessException(messageSource.getMessage("product.info.failed", null, Locale.JAPAN));
        }
    }

    /**
     * カテゴリIDからカテゴリ名を取得する。
     */
    @Override
    public String getCategoryName(Integer categoryId) {
        try {
            if (categoryId == null || categoryId == 0) {
                return null;
            }
            ProductCategory category = productCategoryRepository.findById(categoryId);
            return (category != null) ? category.getName() : null;
        } catch (Exception e) {
            throw new AdminBusinessException(messageSource.getMessage("product.info.failed", null, Locale.JAPAN));
        }
    }

    /**
     * 商品IDから商品情報を取得する。
     */
    @Override
    public Product getProductById(Integer id) {
        try {
            if (id == null) {
                return null;
            }
            return productRepository.findById(id);
        } catch (Exception e) {
            throw new AdminBusinessException(messageSource.getMessage("product.info.failed", null, Locale.JAPAN));
        }
    }

    /**
     * 全商品を検索し、指定ページの結果を返す。
     */
    @Override
    public List<Product> searchAllProducts(int page) {
        int currentPage = Math.max(page, 1);
        int offset = (currentPage - 1) * PAGE_SIZE;

        List<Product> products = productRepository.findAllWithPaging(offset, PAGE_SIZE);
        return (products != null) ? products : new ArrayList<>();
    }

    /**
     * 指定カテゴリの商品を検索し、指定ページの結果を返す。
     * categoryId が null または 0 の場合は全商品検索にフォールバックする。
     */
    @Override
    public List<Product> searchProductsByCategory(Integer categoryId, int page) {
        if (categoryId == null || categoryId == 0) {
            return searchAllProducts(page);
        }

        int currentPage = Math.max(page, 1);
        int offset = (currentPage - 1) * PAGE_SIZE;

        List<Product> products = productRepository.findByCategoryWithPaging(categoryId, offset, PAGE_SIZE);
        return (products != null) ? products : new ArrayList<>();
    }

    /**
     * 全商品の総件数を返す。
     */
    @Override
    public int countAllProducts() {
        return productRepository.countAll();
    }

    /**
     * 指定カテゴリの商品総件数を返す。
     */
    @Override
    public int countProductsByCategory(Integer categoryId) {
        if (categoryId == null || categoryId == 0) {
            return countAllProducts();
        }
        return productRepository.countByCategory(categoryId);
    }
}
