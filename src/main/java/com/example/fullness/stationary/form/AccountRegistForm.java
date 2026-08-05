package com.example.fullness.stationary.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountRegistForm implements Serializable {

    private Integer id;

    @NotNull
    private Integer employeeId;

    @NotNull
    @Size(min = 5, max = 20)
    @Pattern(regexp = "[0-9a-zA-Z]+")
    private String name;

    @NotNull
    @Size(min = 5, max = 20)
    @Pattern(regexp = "[0-9a-zA-Z]+")
    private String password;

}
