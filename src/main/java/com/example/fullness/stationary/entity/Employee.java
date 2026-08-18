package com.example.fullness.stationary.entity;

import lombok.Data;

/**
 * 社員を表すエンティティクラス。
 */
@Data
public class Employee {
    private Integer id;
    private Integer departmentId;
    private String name;
    private String nameKana;
}
