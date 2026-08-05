package com.example.fullness.stationary.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fullness.stationary.form.LoginForm;
import com.example.fullness.stationary.service.LoginAttemptService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 管理画面のログイン画面表示とログイン処理を担当するコントローラー。
 * 入力チェックとロック判定を行い、必要に応じてメッセージを画面へ表示する。
 */
@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    private final LoginAttemptService loginAttemptService;
    private final MessageSource messageSource;

    public AdminLoginController(LoginAttemptService loginAttemptService, MessageSource messageSource) {
        this.loginAttemptService = loginAttemptService;
        this.messageSource = messageSource;
    }

    /**
     * ログイン画面を表示する。
     * セッションに保存された各種メッセージを読み取り、画面へ反映する。
     *
     * @param model   画面へ値を渡す Model
     * @param request HTTP リクエスト（セッション情報の取得に使用）
     * @return ログイン画面ビュー名
     */
    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {
        handleLoginMessages(request, model);
        return "admin/login";
    }

    /**
     * ログインフォームの入力チェックとロック判定を行う。
     * 問題なければ認証処理（/admin/login-auth）へフォワードする。
     *
     * @param loginForm 入力されたログインフォーム
     * @param result    入力チェック結果（バリデーション）
     * @param model     画面へ値を渡す Model
     * @param request   HTTP リクエスト（セッションへのメッセージ設定に使用）
     * @return ログイン画面、または認証処理へのフォワード
     */
    @PostMapping("/login")
    public String loginProcess(@Validated LoginForm loginForm, BindingResult result, Model model,
            HttpServletRequest request) {

        if (result.hasErrors()) {
            model.addAttribute("loginForm", loginForm);
            return "admin/login";
        }

        String accountName = loginForm.getName();
        if (accountName != null && !accountName.isBlank() && loginAttemptService.isBlocked(accountName)) {
            request.getSession(true).setAttribute(
                    "loginErrorMessage",
                    messageSource.getMessage("com.example.fullness.stationary.security.locked", null, Locale.JAPAN));
            handleLoginMessages(request, model);
            return "admin/login";
        }

        return "forward:/admin/login-auth";
    }

    /**
     * セッションに保存されたログイン関連メッセージを読み取り、Model に設定する。
     * 読み取ったメッセージは重複表示を防ぐため削除する。
     *
     * @param request HTTP リクエスト（セッション情報の取得に使用）
     * @param model   画面へ値を渡す Model
     */
    private void handleLoginMessages(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        LoginForm loginForm = new LoginForm();

        if (session != null) {

            if (session.getAttribute("timeoutFlag") != null) {
                model.addAttribute("timeoutMessage", "セッションが切れました。再度ログインしてください");
                session.removeAttribute("timeoutFlag");
            }

            if (session.getAttribute("logoutFlag") != null) {
                model.addAttribute("logoutMessage", "ログアウトしました。");
                session.removeAttribute("logoutFlag");
            }

            String errorMsg = (String) session.getAttribute("loginErrorMessage");
            if (errorMsg != null) {
                model.addAttribute("securityErrorMessage", errorMsg);
                session.removeAttribute("loginErrorMessage");
            }

            String savedName = (String) session.getAttribute("loginName");
            if (savedName != null) {
                loginForm.setName(savedName);
                session.removeAttribute("loginName");
            }
        }

        model.addAttribute("loginForm", loginForm);
    }
}
