package com.example.fullness.stationary.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueProductCategoryNameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueProductCategoryName {

    String message() default "入力されたカテゴリ名は既に登録されています";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}