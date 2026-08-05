package com.example.fullness.stationary.service;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setup() {
        service = new LoginAttemptService();
    }

    // ============================================================
    // loginSucceeded のテスト
    // ============================================================

    @Test
    void loginSucceededTest_case01_Ok() {
        String account = "takumi";

        // ロック状態にする
        for (int i = 0; i < 5; i++) {
            service.loginFailed(account);
        }
        assertTrue(service.isBlocked(account));

        // 成功 → ロック解除
        service.loginSucceeded(account);

        assertFalse(service.isBlocked(account));
    }

    @Test
    void loginSucceededTest_case02_Ok() {
        // null / 空文字は何もしない
        service.loginSucceeded(null);
        service.loginSucceeded("");
        service.loginSucceeded(" ");

        assertTrue(true); // 例外が出ないことを確認
    }

    // ============================================================
    // loginFailed のテスト
    // ============================================================

    @Test
    void loginFailedTest_case01_Ok() {
        String account = "takumi";

        // 1〜4回失敗 → ロックされない
        for (int i = 0; i < 4; i++) {
            service.loginFailed(account);
            assertFalse(service.isBlocked(account));
        }
    }

    @Test
    void loginFailedTest_case02_Ok() {
        String account = "takumi";

        // 5回目でロック
        for (int i = 0; i < 4; i++) {
            service.loginFailed(account);
        }
        assertFalse(service.isBlocked(account));

        service.loginFailed(account);
        assertTrue(service.isBlocked(account));
    }

    @Test
    void loginFailedTest_case03_Ok() {
        // null / 空文字は何もしない
        service.loginFailed(null);
        service.loginFailed("");
        service.loginFailed(" ");

        assertTrue(true);
    }

    // ============================================================
    // isBlocked のテスト
    // ============================================================

    @Test
    void isBlockedTest_case01_Ok() {
        // 失敗情報なし → false
        assertFalse(service.isBlocked("takumi"));
    }

    @Test
    void isBlockedTest_case02_Ok() {
        String account = "takumi";

        // ロック状態にする
        for (int i = 0; i < 5; i++) {
            service.loginFailed(account);
        }

        assertTrue(service.isBlocked(account));
    }

    @Test
    void isBlockedTest_case03_Ok() throws Exception {
        String account = "takumi";

        // ロック状態にする
        for (int i = 0; i < 5; i++) {
            service.loginFailed(account);
        }

        // lockedUntil を過去に書き換える（強制解除）
        Field failuresField = service.getClass().getDeclaredField("failures");
        failuresField.setAccessible(true);
        Map<String, ?> map = (Map<String, ?>) failuresField.get(service);

        Object info = map.get(account);
        Field lockedUntilField = info.getClass().getDeclaredField("lockedUntil");
        lockedUntilField.setAccessible(true);
        lockedUntilField.set(info, LocalDateTime.now().minusMinutes(20));

        // 過去 → ロック解除される
        assertFalse(service.isBlocked(account));

        // Map から削除されていること
        assertFalse(map.containsKey(account));
    }

    @Test
    void isBlockedTest_case04_Ok() {
        assertFalse(service.isBlocked(null));
        assertFalse(service.isBlocked(""));
        assertFalse(service.isBlocked(" "));
    }
}
