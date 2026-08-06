package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fullness.stationary.form.LoginForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 管理画面のログイン画面表示とログイン処理を担当するコントローラー。
 * 入力チェックとロック判定を行い、必要に応じてメッセージを画面へ表示する。
 */
@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    /**
     * ログイン画面を表示する。
     * セッションに保存された各種メッセージを読み取り、画面へ反映する。
     *
     * @param model   画面へ値を渡す Model
     * @param request HTTP リクエスト（セッション情報の取得に使用）
     * @return ログイン画面ビュー名
     */
    @GetMapping("/login")
    public String showAdminLogin(Model model, HttpServletRequest request) {
        handleLoginMessages(request, model);
        return "admin/login";
    }

    /**
     * セッションに保存されたログイン関連メッセージを読み取り、Model に設定する。
     * 読み取ったメッセージは重複表示を防ぐため削除する。
     *
     * @param request HTTP リクエスト（セッション情報の取得に使用）
     * @param model   画面へ値を渡す Model
     */

    private void handleLoginMessages(HttpServletRequest request, Model model) {

        // 既存セッションを取得（新規作成はしない）
        HttpSession session = request.getSession(false);

        // ログインフォーム（画面表示用）を生成
        LoginForm loginForm = new LoginForm();

        if (session != null) {

            // セッションタイムアウト時のメッセージを表示
            if (session.getAttribute("timeoutFlag") != null) {
                model.addAttribute("timeoutMessage", "セッションが切れました。再度ログインしてください");
                session.removeAttribute("timeoutFlag"); // 再表示防止
            }

            // 認証失敗時のエラーメッセージを表示
            String errorMsg = (String) session.getAttribute("loginErrorMessage");
            if (errorMsg != null) {
                model.addAttribute("securityErrorMessage", errorMsg);
                session.removeAttribute("loginErrorMessage"); // 再表示防止
            }

            // 前回入力したユーザー名をフォームに復元
            String savedName = (String) session.getAttribute("loginName");
            if (savedName != null) {
                loginForm.setName(savedName);
                session.removeAttribute("loginName"); // 再表示防止
            }
        }

        // ログインフォームを画面へ渡す
        model.addAttribute("loginForm", loginForm);
    }
}
