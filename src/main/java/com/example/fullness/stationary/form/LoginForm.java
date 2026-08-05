package com.example.fullness.stationary.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginForm {
    @NotBlank(message = "アカウント名を入力してください")
    @Size(min = 5, max = 20, message = "アカウント名を入力してください")
    private String name;

    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 5, max = 20, message = "パスワードを入力してください")
    private String password;
}
