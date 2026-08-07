package com.example.fullness.stationary.security;

import java.io.IOException;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.fullness.stationary.service.AdminLoginAttemptService;

/**
 * ログイン認証失敗時の処理をカスタムするハンドラー。
 * ログイン失敗回数の記録、ロック判定、エラーメッセージの設定を行う。
 */
@Slf4j
@Component
public class AdminCustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private MessageSource messageSource;
    private AdminLoginAttemptService adminLoginAttemptServiceImpl;

    /**
     * @param messageSource                メッセージソース
     * @param adminLoginAttemptServiceImpl ログイン失敗回数とロック状態を管理するサービス
     */
    public AdminCustomAuthenticationFailureHandler(MessageSource messageSource,
            AdminLoginAttemptService adminLoginAttemptServiceImpl) {
        this.messageSource = messageSource;
        this.adminLoginAttemptServiceImpl = adminLoginAttemptServiceImpl;
        setDefaultFailureUrl("/admin/login");
    }

    /**
     * 認証失敗時に呼び出される処理。
     * アカウント名の保存、失敗回数の加算、エラーメッセージ設定を行い、
     * ログイン画面へリダイレクトする。
     *
     * @param request   認証リクエスト
     * @param response  レスポンス
     * @param exception 認証失敗の原因となった例外
     * @throws IOException      リダイレクト時の入出力例外
     * @throws ServletException サーブレット例外
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        // ログインフォームの name を取得
        String accountName = request.getParameter("name");

        // 認証失敗ログ
        log.info("failureHandler が呼ばれました: {}", accountName);

        // セッションを必ず作成（メッセージ保存のため）
        HttpSession session = request.getSession(true);

        // ★ 入力したユーザー名を保存（再表示用）
        session.setAttribute("loginName", accountName);

        // 失敗回数を加算（ロック判定は LoginAttemptService が行う）
        if (accountName != null && !accountName.isBlank()) {
            adminLoginAttemptServiceImpl.loginFailed(accountName);
        }

        // 認証失敗メッセージをセッションに保存（画面で表示される）
        session.setAttribute(
                "loginErrorMessage",
                messageSource.getMessage(
                        "com.example.fullness.stationary.security.bad_credentials",
                        null,
                        Locale.JAPAN));

        // ログイン画面へリダイレクト
        response.sendRedirect("/admin/login");
    }

}
