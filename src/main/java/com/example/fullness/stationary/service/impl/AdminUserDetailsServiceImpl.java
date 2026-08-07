package com.example.fullness.stationary.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;

/**
 * 管理画面のユーザー認証に利用される UserDetailsService の実装。
 *
 * employee_account テーブルからユーザー情報（name と password）を取得し、
 * Spring Security の認証処理で利用される UserDetails を生成する。
 *
 * 本サービスはユーザー情報の読み込みのみを担当する。
 * 
 * ユーザー名が存在しない場合は UsernameNotFoundException を送出し、
 * 認証失敗として Spring Security の FailureHandler に処理が委譲される。
 */
@Service
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeAccountRepository employeeAccountRepository;

    /**
     * employee_account テーブルへアクセスするリポジトリを受け取る。
     *
     * @param employeeAccountRepository ユーザー情報を取得するためのリポジトリ
     */
    public AdminUserDetailsServiceImpl(EmployeeAccountRepository employeeAccountRepository) {
        this.employeeAccountRepository = employeeAccountRepository;
    }

    /**
     * 指定されたユーザー名のユーザー情報を読み込み、UserDetails を返す。
     *
     * ユーザー名が存在しない場合は UsernameNotFoundException を送出し、
     * Spring Security の認証失敗処理に制御が移る。
     *
     * @param username 認証対象のユーザー名
     * @return 認証に利用する UserDetails
     * @throws UsernameNotFoundException ユーザー名が存在しない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) {

        EmployeeAccount employeeAccount = employeeAccountRepository.findByName(username);

        if (employeeAccount == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return User.withUsername(employeeAccount.getName())
                .password(employeeAccount.getPassword())
                .roles("ADMIN")
                .build();
    }
}
