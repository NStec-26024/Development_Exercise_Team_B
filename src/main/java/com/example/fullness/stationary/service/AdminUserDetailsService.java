package com.example.fullness.stationary.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 管理画面のユーザー情報を提供するサービス。
 */
public interface AdminUserDetailsService {

    /**
     * DB からユーザー情報を読み込む UserDetailsService を返す。
     *
     * @return UserDetailsService 実装
     */
    UserDetailsService userDetailsService();
}
