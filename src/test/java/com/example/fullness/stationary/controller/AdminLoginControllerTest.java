package com.example.fullness.stationary.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdminLoginControllerTest {

        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
                MessageSource messageSource = new StaticMessageSource();
                mockMvc = MockMvcBuilders.standaloneSetup(new AdminLoginController(messageSource))
                                .build();
        }

        // 入力（GET が正常に受け付けられる）
        @Test
        void showLoginPageTest_case01_Ok() throws Exception {
                mockMvc.perform(get("/admin/login"))
                                .andExpect(status().isOk());
        }

        // 処理（timeoutMessage が Model に設定される）
        @Test
        void showLoginPageTest_case02_Ok() throws Exception {
                mockMvc.perform(get("/admin/login")
                                .sessionAttr("timeoutMessage", "セッションが切れました"))
                                .andExpect(model().attribute("timeoutMessage", "セッションが切れました"))
                                .andExpect(view().name("admin/login"));
        }

        // 処理（loginErrorMessage が Model に設定される）
        @Test
        void showLoginPageTest_case03_Ok() throws Exception {
                mockMvc.perform(get("/admin/login")
                                .sessionAttr("loginErrorMessage", "ログインエラー"))
                                .andExpect(model().attribute("securityErrorMessage", "ログインエラー"))
                                .andExpect(view().name("admin/login"));
        }

        // 処理（loginName が LoginForm に設定される）
        @Test
        void showLoginPageTest_case04_Ok() throws Exception {
                mockMvc.perform(get("/admin/login")
                                .sessionAttr("loginName", "yamadatarou1001"))
                                .andExpect(model().attributeExists("loginForm"))
                                .andExpect(model().attribute("loginForm",
                                                org.hamcrest.Matchers.hasProperty("name",
                                                                org.hamcrest.Matchers.is("yamadatarou1001"))))
                                .andExpect(view().name("admin/login"));
        }

        // 出力（ビュー名と loginForm が必ず存在する）
        @Test
        void showLoginPageTest_case05_Ok() throws Exception {
                mockMvc.perform(get("/admin/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/login"))
                                .andExpect(model().attributeExists("loginForm"));
        }
}
