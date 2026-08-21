package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginForm {
    /** アカウント名（必須） */
    @NotBlank(message = "メールアドレスを入力してください")
    private String mealadress;

    /** パスワード（必須） */
    @NotBlank(message = "パスワードを入力してください")
    private String password;
}
