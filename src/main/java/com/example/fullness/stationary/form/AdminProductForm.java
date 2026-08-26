package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品登録・修正画面（BP012/BP009）の入力値を保持するフォーム。
 * 価格・在庫数は数値ではなく文字列として受け取り、桁数・形式チェックをこのフォームで行う。
 */
@Data
public class AdminProductForm {
    private Integer id;

    /** 商品名（必須、2〜20文字） */
    @NotBlank(message = "商品名を入力してください")
    @Pattern(regexp = "^.{2,20}$", message = "商品名は2～20文字で入力してください")
    private String name;

    /** 単価（必須、半角数字のみ。上限チェックはValidator側） */
    @NotBlank(message = "価格を入力してください")
    @Pattern(regexp = "^[0-9]+$", message = "正しい価格形式で入力してください")
    private String price;

    /** 在庫数（必須、半角数字のみ。上限チェックはValidator側） */
    @NotBlank(message = "在庫数を入力してください")
    @Pattern(regexp = "^[0-9]+$", message = "正しい在庫数形式で入力してください")
    private String stock;

    /** カテゴリID（必須） */
    @NotNull(message = "カテゴリを選択してください")
    private Integer categoryId;

    /** 確認画面表示用のカテゴリ名（コントローラー側でcategoryIdから解決して設定） */
    private String categoryName;

    private String imagePath;

    /**
     * アップロードされた商品画像。
     * 新規登録時は必須、修正時は未選択なら既存画像を維持する
     */
    private MultipartFile image;

    /** 新規アップロードされた画像データ（修正時に未選択なら null）。 */
    public byte[] imageBytes;

    /** 元のファイル名（拡張子・Content-Type判定用）。 */
    public String imageFileName;

    private String base64Image;
}
