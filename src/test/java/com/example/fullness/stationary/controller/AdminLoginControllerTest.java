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

                AdminLoginController controller = new AdminLoginController();

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
                mockMvc.perform(get("/admin/login").sessionAttr("loginName", "yamadatarou1001"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/login"))
                                .andExpect(model().attribute("loginForm",
                                                org.hamcrest.Matchers.hasProperty("name",
                                                                org.hamcrest.Matchers.is("yamadatarou1001"))));
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
        // GET /admin/login → loginErrorMessage の表示
        // ---------------------------------------------------------
        @Test
        void loginPageTest_case04_Ok() throws Exception {
                mockMvc.perform(get("/admin/login").sessionAttr("loginErrorMessage", "MSG"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/login"))
                                .andExpect(model().attribute("securityErrorMessage", "MSG"));
        }

}
