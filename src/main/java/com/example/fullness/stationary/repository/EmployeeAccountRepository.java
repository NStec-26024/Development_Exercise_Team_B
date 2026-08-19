package com.example.fullness.stationary.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.fullness.stationary.entity.EmployeeAccount;

/**
 * 社員アカウント情報に関するデータベースアクセスを制御するマッパーインターフェース
 */
@Mapper
public interface EmployeeAccountRepository {

    /**
     * 新しい社員アカウントをデータベースに登録
     * 
     * @param employeeAccount 登録する社員アカウント
     * @return 挿入された行数（通常は1）
     */
    public int insertEmployeeAccount(EmployeeAccount employeeAccount);

    /**
     * 社員アカウントのない社員の情報一覧を取得
     * 
     * @return 社員情報と紐づいた社員アカウントのリスト
     */
    public List<EmployeeAccount> selectEmployeeNameWithEmployeeAccount();

    /**
     * 指定された社員IDと紐づく社員アカウントのない社員を取得
     * 
     * @param id 社員ID
     * @return 社員アカウント(該当がない場合はnull)
     */
    public EmployeeAccount selectNotHasEmployeeAccount(int id);

    /**
     * 指定されたアカウント名と紐づく社員アカウントを取得
     * 
     * @param accountName 取得対象のアカウント名
     * @return 社員アカウント(該当がない場合はnull)
     */
    public EmployeeAccount selectAccountName(String accountName);

    /**
     * 指定されたアカウントIDに紐づく社員アカウントと社員情報を取得
     * 
     * @param id アカウントID
     * @return 社員情報と紐づいた社員アカウント(該当がない場合はnull)
     */
    public EmployeeAccount selectEmployeeNameWithEmployeeAccountId(int id);

}
