package com.example.fullness.stationary.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.fullness.stationary.service.impl.AdminLoginAttemptServiceImpl;

public class AdminLoginAttemptServiceTest {

    private AdminLoginAttemptServiceImpl service;

    // テスト側で定義（必須）
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 10;

    @BeforeEach
    void setup() {
        service = new AdminLoginAttemptServiceImpl();
        ReflectionTestUtils.setField(service, "MAX_ATTEMPTS", MAX_ATTEMPTS);
        ReflectionTestUtils.setField(service, "LOCK_MINUTES", LOCK_MINUTES);
        service.loginSucceeded("yamadatarou1001");
    }

    // ============================================================
    // loginSucceeded のテスト
    // ============================================================

    @Test
    void loginSucceededTest_case01_Ok() {
        String account = "yamadatarou1001";

        // ロック状態にする（3回失敗）
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            service.loginFailed(account);
        }
        assertTrue(service.isBlocked(account));

        // 成功 → ロック解除
        service.loginSucceeded(account);

        assertFalse(service.isBlocked(account));
    }

    // ============================================================
    // loginFailed のテスト
    // ============================================================

    @Test
    void loginFailedTest_case01_Ok() {
        String account = "yamadatarou1001";

        // 1〜(MAX_ATTEMPTS - 1)回失敗 → ロックされない
        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            service.loginFailed(account);
        }
        assertFalse(service.isBlocked(account));
    }

    @Test
    void loginFailedTest_case02_Ok() {
        String account = "yamadatarou1001";

        // MAX_ATTEMPTS-1回失敗 → ロックされない
        for (int i = 1; i <= MAX_ATTEMPTS - 1; i++) {
            service.loginFailed(account);
            assertFalse(service.isBlocked(account));
        }

        // MAX_ATTEMPTS回目でロック
        service.loginFailed(account);
        assertTrue(service.isBlocked(account));
    }

    // ============================================================
    // isBlocked のテスト
    // ============================================================

    @Test
    void isBlockedTest_case01_Ok() {
        // 失敗情報なし → false
        assertFalse(service.isBlocked("yamadatarou1001"));
    }

    @Test
    void isBlockedTest_case02_Ok() {
        String account = "yamadatarou1001";

        // ロック状態にする
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            service.loginFailed(account);
        }

        assertTrue(service.isBlocked(account));
    }

    @Test
    void isBlockedTest_case03_Ok() {
        String account = "yamadatarou1001";

        // ロック状態にする
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            service.loginFailed(account);
        }
        assertTrue(service.isBlocked(account));

        // 成功 → ロック解除
        service.loginSucceeded(account);

        assertFalse(service.isBlocked(account));
    }
}
