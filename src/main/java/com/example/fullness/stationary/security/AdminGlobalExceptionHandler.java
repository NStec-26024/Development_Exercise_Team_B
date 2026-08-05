package com.example.fullness.stationary.security;

import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import com.example.fullness.stationary.form.LoginForm;

/**
 * 管理画面で発生した例外を共通処理するハンドラー。
 * 404 や DB 接続エラーなどを検知し、適切なメッセージを画面へ渡す。
 * 認証関連の例外は Spring Security に委譲する。
 */
@Slf4j
@ControllerAdvice
public class AdminGlobalExceptionHandler {

    private MessageSource messageSource;

    public AdminGlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 管理画面の例外を処理し、エラー画面またはログイン画面へ遷移する。
     *
     * @param request HTTP リクエスト
     * @param ex      発生した例外
     * @param model   画面へのデータ受け渡し
     * @return 遷移先ビュー名
     * @throws Exception 認証関連例外は再スロー
     */
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(HttpServletRequest request, Exception ex, Model model) throws Exception {

        if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
            throw ex;
        }

        log.info("例外検知: URL={}, type={}", request.getRequestURI(), ex.getClass().getSimpleName());

        String errorMessage = ex.getMessage();
        boolean isDisplayErrorPage = false;

        try {
            if (ex instanceof NoHandlerFoundException ||
                    (ex.getMessage() != null && ex.getMessage().contains("No static resource"))) {
                isDisplayErrorPage = true;
                errorMessage = "お探しのページは見つかりませんでした。";
            } else if (ex instanceof org.springframework.jdbc.CannotGetJdbcConnectionException ||
                    ex.getCause() instanceof org.springframework.jdbc.CannotGetJdbcConnectionException) {
                isDisplayErrorPage = true;
                errorMessage = messageSource.getMessage(
                        "com.example.fullness.stationary.security.db_error", null, Locale.JAPAN);
            } else if (errorMessage == null || errorMessage.isBlank()) {
                errorMessage = messageSource.getMessage(
                        "com.example.fullness.stationary.security.system_error", null, Locale.JAPAN);
            }
        } catch (NoSuchMessageException e) {
            errorMessage = "システムエラーが発生しました。";
        }

        model.addAttribute("errorMessage", errorMessage);

        if (isDisplayErrorPage) {
            return "admin/error";
        }

        HttpSession session = request.getSession(false);
        LoginForm loginForm = new LoginForm();
        if (session != null) {
            String savedName = (String) session.getAttribute("loginName");
            if (savedName != null) {
                loginForm.setName(savedName);
            }
        }

        model.addAttribute("loginForm", loginForm);
        return "admin/login";
    }
}
