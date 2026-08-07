package com.example.fullness.stationary.controller;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fullness.stationary.form.AdminLoginForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 管理画面のログイン画面表示を担当するコントローラー。
 * セッションに保存されたメッセージや前回入力したユーザー名を読み取り、
 * ログイン画面へ反映する。
 */
@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    private final MessageSource messageSource;

    /**
     * 管理画面のログイン画面表示を担当するコントローラー。
     * メッセージソースを受け取り、画面表示時のメッセージ取得に利用する。
     *
     * @param messageSource メッセージ取得に使用する MessageSource
     */
    public AdminLoginController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * ログイン画面を表示する。
     * セッションに保存された timeoutMessage、loginErrorMessage、loginName を読み取り、
     * 必要に応じて画面へ反映する。読み取ったメッセージは重複表示を防ぐため削除する。
     *
     * @param model   画面へ値を渡す Model
     * @param request HTTP リクエスト（セッション情報の取得に使用）
     * @return ログイン画面ビュー名
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        AdminLoginForm adminLoginForm = new AdminLoginForm();

        if (session != null) {

            Object timeoutMessage = session.getAttribute("timeoutMessage");
            if (timeoutMessage != null) {
                model.addAttribute("timeoutMessage", timeoutMessage);
                session.removeAttribute("timeoutMessage");
            }

            Object errorMsg = session.getAttribute("loginErrorMessage");
            if (errorMsg != null) {
                model.addAttribute("securityErrorMessage", errorMsg);
                session.removeAttribute("loginErrorMessage");
            }

            Object savedName = session.getAttribute("loginName");
            if (savedName != null) {
                adminLoginForm.setName(String.valueOf(savedName));
                session.removeAttribute("loginName");
            }
        }

        model.addAttribute("adminLoginForm", adminLoginForm);
        return "admin/login";
    }
}
