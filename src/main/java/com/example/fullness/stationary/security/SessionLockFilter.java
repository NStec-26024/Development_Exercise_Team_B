package com.example.fullness.stationary.security;

import java.io.IOException;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.fullness.stationary.service.LoginAttemptService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

/**
 * ログイン前にアカウントがロックされているかを判定するフィルター。
 * ロック中の場合は認証処理へ進ませず、ログイン画面へフォワードする。
 */
@Slf4j
public class SessionLockFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;
    private final MessageSource messageSource;

    public SessionLockFilter(LoginAttemptService loginAttemptService, MessageSource messageSource) {
        this.loginAttemptService = loginAttemptService;
        this.messageSource = messageSource;
    }

    /**
     * ログイン画面（/admin/login）およびログイン処理（/admin/login-auth）へのアクセス時に
     * アカウントのロック状態を確認するフィルター処理。
     * ロック中の場合は認証処理へ進ませず、エラーメッセージを設定してログイン画面へフォワードする。
     * ロックされていない場合は通常通りフィルターチェーンを継続する。
     *
     * @param request     クライアントからの HTTP リクエスト（アカウント名の取得に使用）
     * @param response    レスポンスオブジェクト（フォワード時に使用）
     * @param filterChain 次のフィルターへ処理を渡すためのチェーン
     */

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        log.warn("[DEBUG] SessionLockFilter 起動: path={}", path);

        if (path.equals("/admin/login") || path.equals("/admin/login-auth")) {

            String accountName = request.getParameter("name");

            if (accountName != null && !accountName.isBlank() && loginAttemptService.isBlocked(accountName)) {

                HttpSession session = request.getSession(true);

                session.setAttribute(
                        "loginErrorMessage",
                        messageSource.getMessage(
                                "com.example.fullness.stationary.security.locked",
                                null,
                                Locale.JAPAN));

                request.getRequestDispatcher("/admin/login").forward(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
