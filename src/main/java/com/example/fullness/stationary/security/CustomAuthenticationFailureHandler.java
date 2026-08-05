package com.example.fullness.stationary.security;

import java.io.IOException;
import java.util.Locale;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.fullness.stationary.service.LoginAttemptService;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final MessageSource messageSource;
    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationFailureHandler(MessageSource messageSource, LoginAttemptService loginAttemptService) {
        this.messageSource = messageSource;
        this.loginAttemptService = loginAttemptService;
        // ログイン画面へ遷移するように設定
        setDefaultFailureUrl("/admin/login");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("name");
        AuthenticationException targetException = exception;

        if (username != null) {
            if (loginAttemptService.isBlocked(username)) {
                // すでにロック状態のユーザーがアクセスしてきた場合
                targetException = new LockedException("Account is locked");
            } else {
                // 通常のパスワードミスなどの場合、カウントを進める
                loginAttemptService.loginFailed(username);

                // 今回のミスで5回目に達した場合、例外を LockedException に差し替える
                if (loginAttemptService.isBlocked(username)) {
                    targetException = new LockedException("Account is locked");
                }
            }
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("loginErrorMessage") == null) {
            String msgKey = "com.example.fullness.stationary.security.bad_credentials";

            if (targetException instanceof LockedException) {
                // ロック時用のエラーメッセージキーに分岐
                msgKey = "com.example.fullness.stationary.security.locked";
            } else if (targetException.getCause() instanceof CannotGetJdbcConnectionException) {
                msgKey = "com.example.fullness.stationary.security.db_error";
            }

            String errorMessage = messageSource.getMessage(msgKey, null, Locale.JAPAN);
            session.setAttribute("loginErrorMessage", errorMessage);
        }

        // setDefaultFailureUrl("/admin/login") に基づいてログイン画面に遷移します
        super.onAuthenticationFailure(request, response, targetException);
    }
}
