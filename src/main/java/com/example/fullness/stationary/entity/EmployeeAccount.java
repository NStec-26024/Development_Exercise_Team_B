package com.example.fullness.stationary.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 社員アカウント情報を保持するエンティティクラス
 * employee_accountテーブルに対応
 */
@Data
public class EmployeeAccount implements Serializable {

    /** 社員アカウントID（主キー） */
    private Integer id;

    /** 社員ID（外部キー：employeeテーブルを参照） */
    private Integer employeeId;

    /** アカウント名 */
    private String name;

    /** パスワード */
    private String password;

    /** 紐づく社員情報（テーブル結合用） */
    private Employee employee;

}
