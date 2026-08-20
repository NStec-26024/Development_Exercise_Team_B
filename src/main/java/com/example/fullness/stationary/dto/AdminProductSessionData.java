package com.example.fullness.stationary.dto;

/**
 * 入力〜確認画面間でセッションに保持する商品登録・修正データ。
 * 画像データはバイト配列として保持する。
 */
public class AdminProductSessionData {

    /** null の場合は新規登録、値がある場合は当該IDの修正。 */
    public Integer targetId;

    public String name;
    public int price;
    public int stock;
    public Integer categoryId;

    /** 新規アップロードされた画像データ（修正時に未選択なら null）。 */
    public byte[] imageBytes;

    /** 元のファイル名（拡張子・Content-Type判定用）。 */
    public String imageFileName;

    /** 修正時、画像未変更の場合に維持する既存の保存済みファイル名。 */
    public String existingImageUrl;
}
