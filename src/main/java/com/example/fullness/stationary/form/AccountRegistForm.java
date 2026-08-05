package com.example.fullness.stationary.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountRegistForm implements Serializable {

    private Integer id;

    @NotNull(message = "社員名を選択してください")
    private Integer employeeId;

    @NotNull(message = "アカウント名を入力してください")
    @Size(min = 5, max = 20, message = "アカウント名は5～20文字で入力してください")
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "アカウント名は半角英数字で入力してください")
    private String name;

    @NotNull(message = "パスワードを入力してください")
    @Size(min = 5, max = 20, message = "パスワードは5～20文字で入力してください")
    @Pattern(regexp = "[0-9a-zA-Z]+", message = "パスワードは半角英数字で入力してください")
    private String password;

}
