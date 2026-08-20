package com.example.fullness.stationary.service;

/**
 * 商品修正（BP009〜011, UC012）の業務ロジックを担うサービスの契約。
 */
public interface AdminProductModificationService {

    /**
     * 商品を修正します（product・product_stock を同一トランザクションで更新します）。
     * 画像が指定されなかった場合は既存の画像ファイルを維持します。
     *
     * @param id               修正対象の商品ID
     * @param name             商品名
     * @param price            単価
     * @param stock            在庫数
     * @param categoryId       カテゴリID
     * @param imageBytes       新しい画像データ（未選択の場合は null）
     * @param originalFileName アップロードされた元のファイル名（拡張子判定に使用）
     */
    void updateProduct(Integer id, String name, int price, int stock, Integer categoryId,
            byte[] imageBytes, String originalFileName);
}
