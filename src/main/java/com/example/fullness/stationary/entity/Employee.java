package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 社員の基本情報を保持するエンティティクラス
 * employeeテーブルに対応
 */
@Data
public class Employee implements Serializable {

    /** 社員ID（主キー） */
    private Integer id;

    /** 所属部署ID */
    private Integer departmentId;

    /** 社員氏名（漢字） */
    private String name;

    /** 社員氏名（フリガナ） */
    private String nameKana;

}
