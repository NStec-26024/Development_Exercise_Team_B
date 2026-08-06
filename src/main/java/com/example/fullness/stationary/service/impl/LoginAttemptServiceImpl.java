package com.example.fullness.stationary.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.fullness.stationary.service.LoginAttemptService;

/**
 * ログイン失敗回数の管理とアカウントロック判定を行うサービス。
 * 一定回数の失敗でアカウントを一定時間ロックし、成功時には状態をリセットする。
 */
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 10;

    // 失敗回数
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    // ロック開始時刻
    private final Map<String, LocalDateTime> lockTimeCache = new ConcurrentHashMap<>();

    /** ログイン失敗時 */
    public void loginFailed(String username) {

        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);

        // ロック開始
        if (attempts >= MAX_ATTEMPTS) {
            lockTimeCache.put(username, LocalDateTime.now());
        }
    }

    /** ログイン成功時（ロック解除） */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockTimeCache.remove(username);
    }

    /** ロック判定（メモリ管理 + 自動解除） */
    public boolean isBlocked(String username) {

        // 失敗回数が閾値未満 → ロックされていない
        if (attemptsCache.getOrDefault(username, 0) < MAX_ATTEMPTS) {
            return false;
        }

        // ロック開始時刻がない → ロックされていない
        LocalDateTime lockTime = lockTimeCache.get(username);
        if (lockTime == null) {
            return false;
        }

        // ロック解除判定
        LocalDateTime unlockTime = lockTime.plusMinutes(LOCK_MINUTES);
        if (LocalDateTime.now().isAfter(unlockTime)) {
            // 自動解除
            attemptsCache.remove(username);
            lockTimeCache.remove(username);
            return false;
        }

        // ロック中
        return true;
    }
}
