package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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

    /**
     * ログイン画面を表示する。
     * セッションに保存された timeoutMessage、loginErrorMessage、loginUsername を読み取って
     * 画面へ反映し、読み取った値はセッションから削除する。
     *
     * @param model   画面へ値を渡す Model
     * @param request HTTP リクエスト（セッション情報の取得に使用）
     * @return ログイン画面ビュー名
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpServletRequest request) {
        // 認証済みユーザーが直接 /admin/login にアクセスした場合は管理トップへリダイレクト
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin";
        }

        HttpSession session = request.getSession(false);
        AdminLoginForm adminLoginForm = new AdminLoginForm();

        model.addAttribute("timeoutMessage", popSessionAttribute(session, "timeoutMessage"));
        model.addAttribute("securityErrorMessage", popSessionAttribute(session, "loginErrorMessage"));

        String savedUsername = popSessionAttribute(session, "loginUsername");
        if (savedUsername != null) {
            adminLoginForm.setName(savedUsername);
        }

        model.addAttribute("adminLoginForm", adminLoginForm);
        return "admin/login";
    }

    /**
     * セッションから値を取り出し、取り出した値は即座にセッションから削除する。
     *
     * @param session 対象セッション（null の場合は未取得として扱う）
     * @param key     取得する属性名
     * @return 保存されていた値。存在しない場合は null
     */
    // ログイン画面のメッセージ（タイムアウト・認証エラー等）を再読み込み時に重複表示させないための一度限り読み取り。
    private String popSessionAttribute(HttpSession session, String key) {
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(key);
        if (value == null) {
            return null;
        }

        session.removeAttribute(key);
        return String.valueOf(value);
    }
}
