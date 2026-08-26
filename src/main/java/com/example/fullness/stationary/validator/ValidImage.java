package com.example.fullness.stationary.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageFileValidator.class) // バリデーションを実行するクラスを指定
@Target({ ElementType.FIELD, ElementType.PARAMETER }) // フィールドと引数に付与可能にする
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    // エラー時のデフォルトメッセージ
    String message() default "画像ファイル（JPEG, PNG, WebP）のみアップロード可能です。PDFなどの形式は許可されていません。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
