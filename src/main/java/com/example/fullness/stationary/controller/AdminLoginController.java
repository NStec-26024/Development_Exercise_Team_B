package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.fullness.stationary.form.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    // GETアクセス時、およびフォワード時の両方に対応する共通処理
    private void handleLoginMessages(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // タイムアウト判定
            if (session.getAttribute("timeoutFlag") != null) {
                model.addAttribute("timeoutMessage", "セッションが切れました。再度ログインしてください");
                session.removeAttribute("timeoutFlag");
            }
            // ログアウト判定
            if (session.getAttribute("logoutFlag") != null) {
                model.addAttribute("logoutMessage", "ログアウトしました。");
                session.removeAttribute("logoutFlag");
            }
            // セキュリティエラー（ロックなど）判定
            String errorMsg = (String) session.getAttribute("loginErrorMessage");
            if (errorMsg != null) {
                model.addAttribute("securityErrorMessage", errorMsg);
                session.removeAttribute("loginErrorMessage");
            }
        }
        model.addAttribute("loginForm", new LoginForm());
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {
        handleLoginMessages(request, model);
        return "admin/login";
    }

    @PostMapping("/login")
    public String loginProcess(@Validated LoginForm loginForm, BindingResult result, Model model,
            HttpServletRequest request) {
        // SecurityからフォワードされてきたPOST通信をここで検知
        HttpSession session = request.getSession(false);
        if (session != null
                && (session.getAttribute("timeoutFlag") != null || session.getAttribute("logoutFlag") != null)) {
            handleLoginMessages(request, model);
            return "admin/login";
        }

        // 通常のバリデーションチェック
        if (result.hasErrors()) {
            model.addAttribute("loginForm", loginForm);
            return "admin/login";
        }
        return "forward:/admin/login-auth";
    }
}
