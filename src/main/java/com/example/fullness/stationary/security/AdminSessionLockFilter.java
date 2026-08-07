package com.example.fullness.stationary.security;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.fullness.stationary.service.AdminLoginAttemptService;

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
public class AdminSessionLockFilter extends OncePerRequestFilter {

    private final AdminLoginAttemptService adminLoginAttemptServiceImpl;
    private final MessageSource messageSource;

    /**
     * ロック判定に必要なサービスとメッセージソースを受け取る。
     *
     * @param adminLoginAttemptServiceImpl ログイン失敗回数とロック状態を管理するサービス
     * @param messageSource                メッセージ取得に使用する MessageSource
     */
    @Autowired
    public AdminSessionLockFilter(AdminLoginAttemptService adminLoginAttemptServiceImpl, MessageSource messageSource) {
        this.adminLoginAttemptServiceImpl = adminLoginAttemptServiceImpl;
        this.messageSource = messageSource;
    }

    /**
     * ログイン処理前にアカウントがロックされているかを判定する。
     * ロック中の場合はエラーメッセージとユーザー名をセッションに保存し、
     * ログイン画面へリダイレクトする。
     *
     * @param request     HTTP リクエスト
     * @param response    HTTP レスポンス
     * @param filterChain 次のフィルターへ処理を渡すチェーン
     * @throws ServletException フィルター処理中のサーブレット例外
     * @throws IOException      リダイレクト時の入出力例外
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // ロック判定は login-auth（POST）を行う
        if (uri.equals("/admin/login-auth")) {

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

            // ロック判定
            if (accountName != null && adminLoginAttemptServiceImpl.isBlocked(accountName)) {

                HttpSession session = request.getSession(true);

                // ロックメッセージ（Handler が拾う）
                session.setAttribute(
                        "loginErrorMessage",
                        messageSource.getMessage(
                                "com.example.fullness.stationary.security.locked",
                                null,
                                Locale.JAPAN));

                // 前回入力したユーザー名を保存（Handler が復元する）
                session.setAttribute("loginName", accountName);

                // ロック時は login へリダイレクト
                response.sendRedirect("/admin/login");
                return;
            }
        }

        // ロックされていない場合は次のフィルターへ処理を渡す
        filterChain.doFilter(request, response);
    }
}
