package com.example.fullness.stationary.form;

import java.io.Serializable;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminProductCategoryForm implements Serializable {

    private Integer id;

    @NotBlank(message = "カテゴリ名は入力してください")
    @Size(min = 1, max = 30, message = "カテゴリ名は1~30文字で入力してください")

    private String name;

}
