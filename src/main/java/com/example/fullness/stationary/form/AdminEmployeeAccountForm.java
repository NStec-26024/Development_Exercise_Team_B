package com.example.fullness.stationary.form;

import java.io.Serializable;

import com.example.fullness.stationary.validator.UniqueAccountName;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 社員アカウント登録画面からの入力値を保持し、バリデーションの設定をしたフォームクラス
 */
@Data
public class AdminEmployeeAccountForm implements Serializable {

    /** アカウントID（自動採番） */
    private Integer id;

    /**
     * 社員ID
     * <ul>
     * <li>必須入力（未選択は不可）</li>
     * </ul>
     */
    @NotNull(message = "社員名を選択してください")
    private Integer employeeId;

    /**
     * アカウント名
     * <ul>
     * <li>必須入力</li>
     * <li>文字数制限: 5文字以上 20文字以下</li>
     * <li>使用可能文字: 半角英数字のみ</li>
     * </ul>
     */
    @NotBlank(message = "アカウント名を入力してください")
    @Pattern(regexp = "^$|^.{5,20}$", message = "アカウント名は5～20文字で入力してください")
    @Pattern(regexp = "^$|^.{0,4}$|^.{21,}$|^[0-9a-zA-Z]{5,20}$", message = "アカウント名は半角英数字で入力してください")
    @UniqueAccountName
    private String name;

    /**
     * ログインパスワード（平文）
     * <ul>
     * <li>必須入力</li>
     * <li>文字数制限: 5文字以上 20文字以下</li>
     * <li>使用可能文字: 半角英数字のみ</li>
     * </ul>
     */
    @NotBlank(message = "パスワードを入力してください")
    @Pattern(regexp = "^$|^.{5,20}$", message = "パスワードは5～20文字で入力してください")
    @Pattern(regexp = "^$|^.{0,4}$|^.{21,}$|^[0-9a-zA-Z]{5,20}$", message = "パスワードは半角英数字で入力してください")
    private String password;

}