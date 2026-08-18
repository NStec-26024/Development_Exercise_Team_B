package com.example.fullness.stationary.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class AdminLoginAttemptServiceImplTest {

    private AdminLoginAttemptServiceImpl service;

    @BeforeEach
    void setup() {
        service = new AdminLoginAttemptServiceImpl();

        // @Value の値をテスト用にセット
        ReflectionTestUtils.setField(service, "MAX_ATTEMPTS", 5);
        ReflectionTestUtils.setField(service, "LOCK_MINUTES", 10);
    }

    // ============================================================
    // case01: 失敗回数が閾値未満 → ロックされない
    // ============================================================
    @Test
    void isBlockedTest_case01_Ok() {
        service.loginFailed("yamadatarou1001"); // 1回目
        service.loginFailed("yamadatarou1001"); // 2回目
        service.loginFailed("yamadatarou1001"); // 3回目
        service.loginFailed("yamadatarou1001"); // 4回目

        assertFalse(service.isBlocked("yamadatarou1001"));
    }

    // ============================================================
    // case02: 閾値に達した瞬間 → ロックされる
    // ============================================================
    @Test
    void isBlockedTest_case02_Ok() {
        service.loginFailed("yamadatarou1001"); // 1回目
        service.loginFailed("yamadatarou1001"); // 2回目
        service.loginFailed("yamadatarou1001"); // 3回目
        service.loginFailed("yamadatarou1001"); // 4回目
        service.loginFailed("yamadatarou1001"); // 5回目 → ロック開始

        assertTrue(service.isBlocked("yamadatarou1001"));
    }

    // ============================================================
    // case03: ロック開始後、ロック時間内 → ロック継続
    // ============================================================
    @Test
    void isBlockedTest_case03_Ok() {
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001"); // ロック開始

        assertTrue(service.isBlocked("yamadatarou1001"));
    }

    // ============================================================
    // case04: ロック時間を過ぎたら自動解除される
    // ============================================================
    @Test
    void isBlockedTest_case04_Ok() {

        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001"); // ロック開始

        // ロック開始時刻を過去に書き換える（ロック解除をテストするため）
        LocalDateTime past = LocalDateTime.now().minusMinutes(20);

        Map<String, LocalDateTime> mutableMap = new java.util.concurrent.ConcurrentHashMap<>();
        mutableMap.put("yamadatarou1001", past);

        ReflectionTestUtils.setField(service, "lockTimeCache", mutableMap);

        assertFalse(service.isBlocked("yamadatarou1001"));
    }

    // ============================================================
    // case05: loginSucceeded で状態がリセットされる
    // ============================================================
    @Test
    void loginSucceededTest_case05_Ok() {

        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001");
        service.loginFailed("yamadatarou1001"); // ロック開始

        assertTrue(service.isBlocked("yamadatarou1001"));

        service.loginSucceeded("yamadatarou1001");

        assertFalse(service.isBlocked("yamadatarou1001"));
    }

    // ============================================================
    // case06: username が null / blank の場合は何もしない
    // ============================================================
    @Test
    void loginFailedTest_case06_Ok() {

        service.loginFailed(null);
        service.loginFailed("");
        service.loginFailed("   ");

        assertFalse(service.isBlocked("yamadatarou1001")); // 何も起きていない
    }
}
