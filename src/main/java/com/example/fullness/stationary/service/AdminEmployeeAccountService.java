package com.example.fullness.stationary.service;

import java.util.List;

import com.example.fullness.stationary.entity.EmployeeAccount;

/**
 * サービスインターフェース
 */
public interface AdminEmployeeAccountService {

    /**
     * 社員アカウント登録するrepositoryのメソッドを呼び出し、自動採番されたアカウントIDを返却
     * 
     * @param employeeAccount 登録する社員アカウント
     * @return 自動採番されたアカウントID(serial値)
     */
    public int addEmployeeAccount(EmployeeAccount employeeAccount);

    /**
     * 社員アカウントのない社員の情報一覧を取得
     * 
     * @return 社員情報と紐づいた社員アカウントのリスト
     */
    public List<EmployeeAccount> getEmployeeNameWithEmployeeAccount();

    /**
     * 指定された社員IDと紐づくアカウントを取得
     * 
     * @param id 社員ID
     * @return 社員アカウント(該当がない場合はnull)
     */
    public EmployeeAccount getEmployeeAccountWithEmployeeId(int id);

    /**
     * 指定されたアカウント名と紐づく社員アカウントを取得
     * 
     * @param accountName 取得対象のアカウント名
     * @return 社員アカウント(該当がない場合はnull)
     */
    public boolean getAccountName(String accountName);

    /**
     * 指定されたアカウントIDに紐づく社員アカウントと社員情報を取得
     * 
     * @param id アカウントID
     * @return 社員情報と紐づいた社員アカウント(該当がない場合はnull)
     */
    public EmployeeAccount getEmployeeNameWithEmployeeAccountId(int id);

    public boolean getNotHasEmployeeAccount(int id);

}
