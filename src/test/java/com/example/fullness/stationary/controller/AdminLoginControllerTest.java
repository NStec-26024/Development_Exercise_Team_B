package com.example.fullness.stationary.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("未ログイン状態でログイン画面にアクセスした場合、通常通りログインHTMLを表示すること")
    void loginPage_ShouldShowLoginView_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @Test
    @DisplayName("ログインエラー情報がセッションからモデルに移され、セッションから削除されること")
    void loginPage_ShouldExposeSecurityErrorMessageFromSession() throws Exception {
        mockMvc.perform(get("/admin/login").sessionAttr("LOGIN_ERROR_MSG", "認証に失敗しました"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attribute("securityErrorMessage", "認証に失敗しました"))
                .andExpect(request().sessionAttributeDoesNotExist("LOGIN_ERROR_MSG"));
    }

    @Test
    @WithMockUser(username = "admin_user", roles = { "ADMIN" })
    @DisplayName("ログイン済みユーザーがログイン画面にアクセスすると/adminにリダイレクトされること")
    void loginPage_ShouldRedirectToAdmin_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("POST /admin/loginで正しい入力を送信すると、ログイン処理用URLへフォワードされること")
    void loginProcess_ShouldForwardToLoginAuth_WhenFormIsValid() throws Exception {
        mockMvc.perform(post("/admin/login")
                .param("name", "yamadatarou1001")
                .param("password", "yamadapassword1001")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/admin/login-auth"));
    }

    @Test
    @DisplayName("POST /admin/loginで不正な入力を送信すると、再度ログイン画面が表示されること")
    void loginProcess_ShouldReturnLoginView_WhenValidationFails() throws Exception {
        mockMvc.perform(post("/admin/login")
                .param("name", "")
                .param("password", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attributeHasFieldErrors("loginForm", "name", "password"));
    }
}