package com.example.fullness.stationary.service;

/**
 * ログイン失敗回数の管理とアカウントロック判定を行うサービスのインターフェイス。
 */
public interface LoginAttemptService {

    /**
     * ログイン失敗時に失敗回数を加算し、必要に応じてロックを設定する。
     *
     * @param username 対象ユーザー名
     */
    void loginFailed(String username);

    /**
     * ログイン成功時に失敗回数とロック状態をリセットする。
     *
     * @param username 対象ユーザー名
     */
    void loginSucceeded(String username);

    /**
     * アカウントがロック中かどうかを判定する。
     *
     * @param username 対象ユーザー名
     * @return ロック中なら true
     */
    boolean isBlocked(String username);
}
