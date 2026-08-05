package com.example.fullness.stationary.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * ログイン失敗回数の管理とアカウントロック判定を行うサービス。
 * 一定回数の失敗でアカウントを一定時間ロックし、成功時には状態をリセットする。
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 10;

    private final Map<String, LoginFailureInfo> failures = new ConcurrentHashMap<>();

    /**
     * ログイン成功時に失敗情報をリセットする。
     *
     * @param accountName アカウント名
     */
    public void loginSucceeded(String accountName) {
        if (accountName == null || accountName.isBlank()) {
            return;
        }
        failures.remove(accountName);
    }

    /**
     * ログイン失敗時に失敗回数を加算し、必要であればロックを設定する。
     *
     * @param accountName アカウント名
     */
    public void loginFailed(String accountName) {
        if (accountName == null || accountName.isBlank()) {
            return;
        }

        failures.compute(accountName, (key, info) -> {
            if (info == null) {
                info = new LoginFailureInfo(0, null);
            }
            int nextFailCount = info.failCount + 1;
            LocalDateTime lockedUntil = info.lockedUntil;

            if (nextFailCount >= MAX_ATTEMPTS) {
                lockedUntil = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
            }

            return new LoginFailureInfo(nextFailCount, lockedUntil);
        });
    }

    /**
     * アカウントがロック中かどうかを判定する。
     *
     * @param accountName アカウント名
     * @return ロック中なら true
     */
    public boolean isBlocked(String accountName) {
        if (accountName == null || accountName.isBlank()) {
            return false;
        }

        LoginFailureInfo info = failures.get(accountName);
        if (info == null || info.lockedUntil == null) {
            return false;
        }

        if (info.lockedUntil.isAfter(LocalDateTime.now())) {
            return true;
        }

        failures.remove(accountName);
        return false;
    }

    /**
     * ログイン失敗回数とロック解除時刻を保持する内部クラス。
     */
    private static class LoginFailureInfo {
        private final int failCount;
        private final LocalDateTime lockedUntil;

        LoginFailureInfo(int failCount, LocalDateTime lockedUntil) {
            this.failCount = failCount;
            this.lockedUntil = lockedUntil;
        }
    }
}
