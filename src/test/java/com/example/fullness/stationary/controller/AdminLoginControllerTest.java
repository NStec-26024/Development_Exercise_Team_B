package com.example.fullness.stationary.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ConcurrentModel;

import jakarta.servlet.http.HttpSession;

public class AdminLoginControllerTest {

        private MockMvc mockMvc;

        @BeforeEach
        void setup() {
                mockMvc = MockMvcBuilders.standaloneSetup(new AdminLoginController())
                                .build();
        }

        // ============================================================
        // GET が正常に受け付けられる
        // ============================================================
        @Test
        void showLoginPageTest_case01_Ok() throws Exception {
                mockMvc.perform(get("/admin/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/login"))
                                .andExpect(model().attributeExists("adminLoginForm"));
        }

        // ============================================================
        // timeoutMessage が Model に設定される
        // ============================================================
        @Test
        void showLoginPageTest_case02_Ok() throws Exception {
                mockMvc.perform(get("/admin/login")
                                .sessionAttr("timeoutMessage", "セッションが切れました"))
                                .andExpect(model().attribute("timeoutMessage", "セッションが切れました"))
                                .andExpect(view().name("admin/login"));
        }

        // ============================================================
        // loginErrorMessage が Model に設定される
        // ============================================================
        @Test
        void showLoginPageTest_case03_Ok() throws Exception {
                mockMvc.perform(get("/admin/login")
                                .sessionAttr("loginErrorMessage", "ログインエラー"))
                                .andExpect(model().attribute("securityErrorMessage", "ログインエラー"))
                                .andExpect(view().name("admin/login"));
        }

        // ============================================================
        // loginUsername が adminLoginForm に設定される
        // ============================================================
        @Test
        void showLoginPageTest_case04_Ok() throws Exception {
                mockMvc.perform(get("/admin/login")
                                .sessionAttr("loginUsername", "yamadatarou1001"))
                                .andExpect(model().attributeExists("adminLoginForm"))
                                .andExpect(model().attribute("adminLoginForm",
                                                org.hamcrest.Matchers.hasProperty("name",
                                                                org.hamcrest.Matchers.is("yamadatarou1001"))))
                                .andExpect(view().name("admin/login"));
        }

        // ============================================================
        // adminLoginForm が必ず存在する
        // ============================================================
        @Test
        void showLoginPageTest_case05_Ok() throws Exception {
                mockMvc.perform(get("/admin/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/login"))
                                .andExpect(model().attributeExists("adminLoginForm"));
        }

        // ============================================================
        // 内部処理：popSessionAttribute が値を削除していることを確認する
        // ============================================================
        @Test
        void PopSessionAttributeTest_case06_Ok() {

                AdminLoginController controller = new AdminLoginController();
                MockHttpServletRequest request = new MockHttpServletRequest();
                HttpSession session = request.getSession(true);

                // セッションに値をセット
                session.setAttribute("timeoutMessage", "セッションがきれました");

                ConcurrentModel model = new ConcurrentModel();

                // コントローラを直接呼び出し（内部処理テスト）
                controller.showLoginPage(model, request);

                // Model に値が入っている（外部仕様）
                assertEquals("セッションがきれました", model.getAttribute("timeoutMessage"));

                // セッションから削除されている（内部仕様）
                assertNull(session.getAttribute("timeoutMessage"));
        }
}
