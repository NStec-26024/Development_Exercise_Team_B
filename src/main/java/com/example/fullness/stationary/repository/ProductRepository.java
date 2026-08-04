package com.example.fullness.stationary.repository;

import com.example.fullness.stationary.entity.Product;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品情報を永続化層から取得するための Mapper インターフェース。
 * MyBatis による SQL 実装を前提とする。
 * UC12（商品修正）で必要な参照・更新機能のみを保持する。
 */
@Mapper
public interface ProductRepository {

        /** 全商品取得（削除済み除く）ページング対応 */
        List<Product> findAllWithPaging(
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        /** カテゴリ別商品取得（削除済み除く）ページング対応 */
        List<Product> findByCategoryWithPaging(
                        @Param("categoryId") Integer categoryId,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

        /** 全商品件数取得（削除済み除く） */
        int countAll();

        /** カテゴリ別商品件数取得（削除済み除く） */
        int countByCategory(@Param("categoryId") Integer categoryId);

        /** 商品ID検索（削除済み除く） */
        Product findById(@Param("id") Integer id);

        /** 商品情報更新（商品名・単価・カテゴリ・画像URL） */
        int update(Product product);

        int deleteById(@Param("id") Integer id);
}
