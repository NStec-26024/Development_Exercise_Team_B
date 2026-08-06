package com.example.fullness.stationary.security;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.fullness.stationary.service.impl.LoginAttemptService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

/**
 * ログイン処理前にアカウントのロック状態を判定するフィルター。
 * ロック中の場合は認証処理へ進ませず、ログイン画面へ戻す。
 */
@Slf4j
public class SessionLockFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;
    private final MessageSource messageSource;

    /**
     * @param loginAttemptService ログイン失敗回数とロック状態を管理するサービス
     * @param messageSource       ロック時に表示するメッセージを取得するための MessageSource
     */
    @Autowired
    public SessionLockFilter(LoginAttemptService loginAttemptService, MessageSource messageSource) {
        this.loginAttemptService = loginAttemptService;
        this.messageSource = messageSource;
    }

    /**
     * ログイン画面（/admin/login）およびログイン処理（/admin/login-auth）へのアクセス時に
     * アカウントのロック状態を確認し、ロック中ならログイン画面へ戻す。
     *
     * @param request     クライアントからの HTTP リクエスト
     * @param response    レスポンスオブジェクト
     * @param filterChain 次のフィルターへ処理を渡すチェーン
     * @throws ServletException フィルター処理中の例外
     * @throws IOException      入出力例外
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 現在アクセスしている URI を取得
        String uri = request.getRequestURI();

        // ログイン画面とログイン処理のときだけロック判定を行う
        if (uri.equals("/admin/login") || uri.equals("/admin/login-auth")) {

            // フォームから送られたアカウント名を取得
            String accountName = request.getParameter("name");

            // POST の場合は request.getParameter が null のことがあるためセッションから補完
            if (accountName == null || accountName.isBlank()) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    Object savedName = session.getAttribute("loginName");
                    if (savedName != null) {
                        accountName = savedName.toString();
                    }
                }
            }

            // アカウントがロックされているか判定
            if (accountName != null && loginAttemptService.isBlocked(accountName)) {

                // ロックメッセージをセッションに保存（login画面で表示される）
                HttpSession session = request.getSession(true);
                session.setAttribute(
                        "loginErrorMessage",
                        messageSource.getMessage(
                                "com.example.fullness.stationary.security.locked",
                                null,
                                Locale.JAPAN));

                // POST の場合は forward ではなく redirect を使う
                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    response.sendRedirect("/admin/login");
                    return;
                }

                // GET の場合は forward でログイン画面へ戻す
                request.getRequestDispatcher("/admin/login").forward(request, response);
                return;
            }
        }

        // ロックされていない場合は次のフィルターへ処理を渡す
        filterChain.doFilter(request, response);
    }
}
