package com.example.fullness.stationary.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import com.example.fullness.stationary.service.AdminLoginAttemptService;

/**
 * 管理画面のユーザー認証に利用される UserDetailsService の実装。
 *
 * employee_account テーブルから管理者ユーザー情報（name / password）を取得し、
 * Spring Security の認証処理で利用される UserDetails を生成する。
 *
 * 本クラスはロック判定ロジックを持たず、ロック状態は
 * {@link AdminLoginAttemptService} の判定結果を accountLocked() に反映するのみ。
 * アカウントが存在しない場合は、ロック状態に関係なく常に
 * {@link UsernameNotFoundException} を送出する。
 */
@Service
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    /** ユーザー情報を取得するためのリポジトリ */
    @Autowired
    private EmployeeAccountRepository employeeAccountRepository;

    /** ログイン失敗回数とロック状態を管理するサービス */
    @Autowired
    private AdminLoginAttemptService adminLoginAttemptServiceImpl;

    /**
     * 指定されたユーザー名のユーザー情報を読み込み、UserDetails を返す。
     *
     * アカウントがロック中の場合は accountLocked(true) を設定し、
     * DaoAuthenticationProvider の事前チェック（isAccountNonLocked）により
     * パスワード照合前に LockedException が送出されるようにする。
     *
     * ユーザー名が存在しない場合は UsernameNotFoundException を送出し、
     * 認証失敗として Spring Security の FailureHandler に処理が委譲される。
     *
     * @param username 認証対象のユーザー名
     * @return 認証に利用する UserDetails
     * @throws UsernameNotFoundException ユーザー名が存在しない場合
     */

    @Override
    public UserDetails loadUserByUsername(String username) {

        EmployeeAccount employeeAccount = employeeAccountRepository.selectByName(username);

        if (employeeAccount == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        boolean locked = adminLoginAttemptServiceImpl.isBlocked(username);

        return User.withUsername(employeeAccount.getName())
                .password(employeeAccount.getPassword())
                .accountLocked(locked)
                .roles("ADMIN")
                .build();
    }
}
