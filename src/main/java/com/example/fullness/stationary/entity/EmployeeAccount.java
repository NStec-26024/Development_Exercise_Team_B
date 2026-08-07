package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class EmployeeAccount implements Serializable {
    Integer id;
    Integer employeeId;
    String name;
    String password;
}
