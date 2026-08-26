package com.example.fullness.stationary.form;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminProductRegistrationForm implements Serializable {

    /**
     * 商品カテゴリ名
     */
    private String categoryName;

    /**
     * 商品名
     */
    @NotBlank(message = "商品名を入力してください")
    @Pattern(regexp = "^$|^.{2,20}$", message = "商品名は2～20文字で入力してください")
    private String name;

    /**
     * 単価
     */
    @NotNull(message = "価格を入力してください")
    @Min(value = 0, message = "価格は100万円以下で入力してください")
    @Max(value = 1000000, message = "価格は100万円以下で入力してください")
    private Integer price;

    /**
     * 在庫数
     */
    @NotNull(message = "在庫数を入力してください")
    @Min(value = 0, message = "在庫数は1000個以下で入力してください")
    @Max(value = 1000, message = "在庫数は1000個以下で入力してください")
    private Integer stock;

    /**
     * 商品カテゴリID
     */
    @NotNull(message = "カテゴリを選択してください")
    private Integer categoryId;

    /**
     * 画像URL
     */
    private MultipartFile image;

}
