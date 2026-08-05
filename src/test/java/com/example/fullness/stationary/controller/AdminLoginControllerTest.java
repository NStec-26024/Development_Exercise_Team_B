package com.example.fullness.stationary.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.fullness.stationary.service.LoginAttemptService;

public class AdminLoginControllerTest {

    private MockMvc mockMvc;
    private LoginAttemptService loginAttemptService;
    private MessageSource messageSource;

    @BeforeEach
    void setup() {
        loginAttemptService = mock(LoginAttemptService.class);
        messageSource = mock(MessageSource.class);

        when(messageSource.getMessage(anyString(), any(), eq(Locale.JAPAN)))
                .thenReturn("MSG");

        AdminLoginController controller = new AdminLoginController(loginAttemptService, messageSource);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // =========================================================
    // loginPage() のテスト
    // =========================================================

    // ---------------------------------------------------------
    // GET /admin/login → loginForm がセットされる
    // ---------------------------------------------------------
    @Test
    void loginPageTest_case01_Ok() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    // ---------------------------------------------------------
    // GET /admin/login → loginName の復元
    // ---------------------------------------------------------
    @Test
    void loginPageTest_case02_Ok() throws Exception {
        mockMvc.perform(get("/admin/login").sessionAttr("loginName", "takumi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("loginForm",
                        org.hamcrest.Matchers.hasProperty("name",
                                org.hamcrest.Matchers.is("takumi"))));
    }

    // ---------------------------------------------------------
    // GET /admin/login → timeoutFlag の表示
    // ---------------------------------------------------------
    @Test
    void loginPageTest_case03_Ok() throws Exception {
        mockMvc.perform(get("/admin/login").sessionAttr("timeoutFlag", true))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("timeoutMessage",
                        "セッションが切れました。再度ログインしてください"));
    }

    // ---------------------------------------------------------
    // GET /admin/login → logoutFlag の表示
    // ---------------------------------------------------------
    @Test
    void loginPageTest_case04_Ok() throws Exception {
        mockMvc.perform(get("/admin/login").sessionAttr("logoutFlag", true))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("logoutMessage", "ログアウトしました。"));
    }

    // ---------------------------------------------------------
    // GET /admin/login → loginErrorMessage の表示
    // ---------------------------------------------------------
    @Test
    void loginPageTest_case05_Ok() throws Exception {
        mockMvc.perform(get("/admin/login").sessionAttr("loginErrorMessage", "MSG"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("securityErrorMessage", "MSG"));
    }

    // =========================================================
    // loginProcess() のテスト
    // =========================================================

    // ---------------------------------------------------------
    // POST /admin/login → バリデーションエラー（name 空）
    // ---------------------------------------------------------
    @Test
    void loginProcessTest_case01_Ok() throws Exception {
        mockMvc.perform(post("/admin/login")
                .param("name", "")
                .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    // ---------------------------------------------------------
    // POST /admin/login → ロック中
    // ---------------------------------------------------------
    @Test
    void loginProcessTest_case02_Ok() throws Exception {
        when(loginAttemptService.isBlocked("takumi")).thenReturn(true);

        mockMvc.perform(post("/admin/login")
                .param("name", "takumi")
                .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("securityErrorMessage", "MSG"))
                .andExpect(model().attributeExists("loginForm"));
    }

    // ---------------------------------------------------------
    // POST /admin/login → ロックしていない → 認証処理へフォワード
    // ---------------------------------------------------------
    @Test
    void loginProcessTest_case03_Ok() throws Exception {
        when(loginAttemptService.isBlocked("takumi")).thenReturn(false);

        mockMvc.perform(post("/admin/login")
                .param("name", "takumi")
                .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/admin/login-auth"));
    }
}
