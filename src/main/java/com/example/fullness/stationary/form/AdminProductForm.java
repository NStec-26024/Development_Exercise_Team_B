package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品登録・修正画面（BP012/BP009）の入力値を保持するフォーム。
 * 価格・在庫数は数値ではなく文字列として受け取り、桁数・形式チェックまでをこのフォームで行い、
 * 上限値チェックは{@link com.example.fullness.stationary.validator.AdminProductValidator}側で行う。
 */
// 価格・在庫数を文字列で受け取っているのは、「未入力」「形式不正」「上限超過」を
// それぞれ区別したメッセージで表示するため。
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
    private String imagePath;

    /**
     * アップロードされた商品画像。
     * 新規登録時は必須、修正時は未選択なら既存画像を維持する
     */
    private MultipartFile image;
}
