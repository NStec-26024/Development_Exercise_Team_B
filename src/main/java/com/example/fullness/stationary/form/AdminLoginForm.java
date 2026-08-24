package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理画面ログインの入力値（アカウント名・パスワード）を保持するフォーム。
 * 必須チェックは Jakarta Validation により行われる。
 */
@Data
public class AdminLoginForm {

    /** アカウント名（必須） */
    @NotBlank(message = "{account.name.null}")
    private String name;

    /** パスワード（必須） */
    @NotBlank(message = "{account.password.null}")
    private String password;
}
