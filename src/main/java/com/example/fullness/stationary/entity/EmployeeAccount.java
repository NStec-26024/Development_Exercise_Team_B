package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class EmployeeAccount implements Serializable {

    private int id;

    private int employeeId;

    private String name;

    private String password;

}
