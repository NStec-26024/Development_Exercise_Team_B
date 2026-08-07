package com.example.fullness.stationary.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueAccountNameValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueAccountName {

    String message() default "このアカウント名は既に使用されています";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
