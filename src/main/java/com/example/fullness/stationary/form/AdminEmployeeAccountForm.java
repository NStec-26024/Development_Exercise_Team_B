package com.example.fullness.stationary.form;

import java.io.Serializable;
import java.util.Locale;

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
    @NotNull(message = "{employee.account.emsg1}")
    private Integer employeeId;

    /**
     * アカウント名
     * <ul>
     * <li>必須入力</li>
     * <li>文字数制限: 5文字以上 20文字以下</li>
     * <li>使用可能文字: 半角英数字のみ</li>
     * </ul>
     */
    @NotBlank(message = "{account.name.null}")
    @Pattern(regexp = "^$|^.{5,20}$", message = "{employee.account.emsg3}")
    @Pattern(regexp = "^$|^.{0,4}$|^.{21,}$|^[0-9a-zA-Z]{5,20}$", message = "{employee.account.emsg4}")
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
    @NotBlank(message = "{account.password.null}")
    @Pattern(regexp = "^$|^.{5,20}$", message = "{employee.account.emsg7}")
    @Pattern(regexp = "^$|^.{0,4}$|^.{21,}$|^[0-9a-zA-Z]{5,20}$", message = "{employee.account.emsg8}")
    private String password;

}