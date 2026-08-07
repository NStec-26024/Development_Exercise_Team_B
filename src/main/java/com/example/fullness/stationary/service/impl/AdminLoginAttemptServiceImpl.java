package com.example.fullness.stationary.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.example.fullness.stationary.service.AdminLoginAttemptService;

/**
 * ログイン失敗回数の管理とアカウントロック判定を行うサービス。
 * 一定回数の失敗でアカウントを一定時間ロックし、成功時には状態をリセットする。
 */
@Service
public class AdminLoginAttemptServiceImpl implements AdminLoginAttemptService {

    @Value("${login.max-attempts}")
    private int MAX_ATTEMPTS;

    @Value("${login.lock-minutes}")
    private int LOCK_MINUTES;

    // 失敗回数
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    // ロック開始時刻
    private final Map<String, LocalDateTime> lockTimeCache = new ConcurrentHashMap<>();

    /**
     * ログイン失敗時に失敗回数を加算し、必要に応じてロックを開始する。
     *
     * @param username ログインを試行したユーザー名
     */
    @Override
    public void loginFailed(String username) {

        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);

        // ロック開始
        if (attempts >= MAX_ATTEMPTS) {
            lockTimeCache.put(username, LocalDateTime.now());
        }
    }

    /**
     * ログイン成功時に失敗回数とロック状態をリセットする。
     *
     * @param username ログインに成功したユーザー名
     */
    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        lockTimeCache.remove(username);
    }

    /**
     * アカウントがロック中かどうかを判定する。
     * ロック時間が過ぎていれば自動的に解除する。
     *
     * @param username 判定対象のユーザー名
     * @return ロック中なら true、ロックされていなければ false
     */
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
