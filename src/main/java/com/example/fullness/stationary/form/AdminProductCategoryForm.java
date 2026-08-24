package com.example.fullness.stationary.form;

import java.io.Serializable;

import com.example.fullness.stationary.validator.UniqueProductCategoryName;
import com.example.fullness.stationary.validator.ValidatorGroup1;
import com.example.fullness.stationary.validator.ValidatorGroup2;
import com.example.fullness.stationary.validator.ValidatorGroup3;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminProductCategoryForm implements Serializable {

    private Integer id;

    @NotBlank(groups = ValidatorGroup1.class, message = "{category.emsg2}")
    @Size(groups = ValidatorGroup2.class, min = 1, max = 30, message = "{category.emsg3}")
    @UniqueProductCategoryName(groups = ValidatorGroup3.class)
    private String name;

}
