package com.example.fullness.stationary.form;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminProductRegistrationForm implements Serializable {

    /**
     * 商品ID
     */
    private Integer id;

    /**
     * 商品カテゴリ名
     */
    private String productCategoryName;

    /**
     * 商品名
     */
    @NotNull(message = "商品名を入力してください")
    @Size(min = 2, max = 20, message = "商品名は2～20文字で入力してください")
    private String ProductName;

    /**
     * 単価
     */
    @NotNull(message = "価格を入力してください")
    @Pattern(regexp = "[0-9]+", message = "正しい価格形式で入力してください")
    @Size(min = 0, max = 1000000, message = "価格は100万円以下で入力してください")
    private Integer price;

    /**
     * 在庫数
     */
    @NotNull(message = "在庫数を入力してください")
    @Pattern(regexp = "[0-9]+", message = "正しい在庫数形式で入力してください")
    @Size(min = 0, max = 1000, message = "在庫数は1000個以下で入力してください")
    private Integer quantity;

    /**
     * 商品カテゴリID
     */
    @NotNull(message = "カテゴリを選択してください")
    private Integer productCategoryId;

    /**
     * 画像URL
     */
    private MultipartFile imageFile;

}
