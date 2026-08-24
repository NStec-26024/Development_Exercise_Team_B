package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品情報を永続化層から取得・更新するための Mapper インターフェース。
 * MyBatis による SQL 実装を前提とする。
 */
@Mapper
public interface ProductRepository {

        /**
         * 全商品取得（削除済み除く）ページング対応
         * 
         * @param offset オフセット
         * @param limit  取得件数
         * @return 商品リスト
         */
        List<Product> selectAllWithPaging(
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        /**
         * カテゴリ別商品取得（削除済み除く）ページング対応
         * 
         * @param categoryId カテゴリID
         * @param offset     オフセット
         * @param limit      取得件数
         * @return 商品リスト
         */
        List<Product> selectByCategoryWithPaging(
                        @Param("categoryId") Integer categoryId,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        /**
         * 全商品件数取得（削除済み除く）
         * 
         * @return 商品件数
         */
        int countAll();

        /**
         * カテゴリ別商品件数取得（削除済み除く）
         * 
         * @param categoryId カテゴリID
         * @return 商品件数
         */
        int countByCategory(@Param("categoryId") Integer categoryId);

        /**
         * 商品ID検索（削除済み除く）
         * 
         * @param id 商品ID
         * @return 商品情報
         */
        Product selectById(@Param("id") Integer id);

        /**
         * 商品論理削除
         * 
         * @param id 商品ID
         * @return 更新件数
         */
        int deleteById(@Param("id") Integer id);
}
