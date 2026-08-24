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
    @NotBlank(message = "{product.name.null}")
    @Pattern(regexp = "^.{2,20}$", message = "{product.emsg1}")
    private String name;

    /** 単価（必須、半角数字のみ。上限チェックはValidator側） */
    @NotBlank(message = "{product.price.null}")
    @Pattern(regexp = "^[0-9]+$", message = "{product.price.right}")
    private String price;

    /** 在庫数（必須、半角数字のみ。上限チェックはValidator側） */
    @NotBlank(message = "{product.emsg5}")
    @Pattern(regexp = "^[0-9]+$", message = "{product.emsg6}")
    private String stock;

    /** カテゴリID（必須） */
    @NotNull(message = "{product.category.choose}")
    private Integer categoryId;

    /** 確認画面表示用のカテゴリ名（コントローラー側でcategoryIdから解決して設定） */
    private String categoryName;

    private String imagePath;

    /**
     * アップロードされた商品画像。
     * 新規登録時は必須、修正時は未選択なら既存画像を維持する
     */
    private MultipartFile image;
}
