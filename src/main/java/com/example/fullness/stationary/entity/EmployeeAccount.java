package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class EmployeeAccount implements Serializable {

    private Integer id;

    private Integer employeeId;

    private String name;

    private String password;

    private Employee employee;

}
