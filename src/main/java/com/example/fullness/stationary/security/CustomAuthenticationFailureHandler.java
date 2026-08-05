package com.example.fullness.stationary.security;

import java.io.IOException;
import java.util.Locale;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import com.example.fullness.stationary.service.LoginAttemptService;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * ログイン認証失敗時の処理をカスタムするハンドラー。
 * ログイン失敗回数の記録、ロック判定、エラーメッセージの設定を行う。
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final MessageSource messageSource;
    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationFailureHandler(MessageSource messageSource, LoginAttemptService loginAttemptService) {
        this.messageSource = messageSource;
        this.loginAttemptService = loginAttemptService;
        setDefaultFailureUrl("/admin/login");
    }

    /**
     * 認証失敗時にロック判定・失敗回数加算・メッセージ設定を行い、
     * 必要に応じて LockedException を発生させる。
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession(true);
        String accountName = request.getParameter("name");
        AuthenticationException targetException = exception;

        if (accountName != null && !accountName.isBlank() && loginAttemptService.isBlocked(accountName)) {
            targetException = new LockedException("Account is locked due to multiple failed attempts");
            log.warn("[AUDIT] ロック中ログイン試行: account={}, sessionId={}", accountName, session.getId());
        } else {
            loginAttemptService.loginFailed(accountName);

            if (accountName != null && !accountName.isBlank() && loginAttemptService.isBlocked(accountName)) {
                targetException = new LockedException("Account is locked due to multiple failed attempts");
                log.warn("[AUDIT] アカウントロック発生: account={}, sessionId={}", accountName, session.getId());
            } else {
                log.warn("[AUDIT] ログイン失敗: account={}, sessionId={}", accountName, session.getId());
            }
        }

        if (session.getAttribute("loginErrorMessage") == null) {
            String rawName = request.getParameter("name");
            session.setAttribute("loginName", rawName);
            String msgKey = "com.example.fullness.stationary.security.bad_credentials";

            if (targetException instanceof LockedException) {
                msgKey = "com.example.fullness.stationary.security.locked";
            } else if (targetException.getCause() instanceof CannotGetJdbcConnectionException) {
                msgKey = "com.example.fullness.stationary.security.db_error";
            }

            session.setAttribute("loginErrorMessage",
                    messageSource.getMessage(msgKey, null, Locale.JAPAN));
        }

        super.onAuthenticationFailure(request, response, targetException);
    }
}
