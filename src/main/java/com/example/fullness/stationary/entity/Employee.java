package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class Employee implements Serializable {

    private int id;

    private int departmentId;

    private String name;

    private String nameKana;

}
