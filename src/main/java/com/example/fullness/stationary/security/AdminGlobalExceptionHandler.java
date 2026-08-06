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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import com.example.fullness.stationary.form.LoginForm;

/**
 * 管理画面で発生した例外を共通処理するハンドラー。
 * DB 接続エラーなどを検知し、適切なメッセージを画面へ渡す。
 * 認証関連の例外は Spring Security に委譲する。
 *
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

        // 認証関連の例外は Spring Security に委譲するため再スロー
        if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
            throw ex;
        }

        // 例外発生箇所と種類をログ出力
        log.info("例外検知: URL={}, type={}", request.getRequestURI(), ex.getClass().getSimpleName());

        String errorMessage = ex.getMessage();
        boolean isDisplayErrorPage = false;

        try {
            // DB 接続エラーの場合は専用メッセージを表示する
            if (ex instanceof org.springframework.jdbc.CannotGetJdbcConnectionException ||
                    ex.getCause() instanceof org.springframework.jdbc.CannotGetJdbcConnectionException) {

                isDisplayErrorPage = true;
                errorMessage = messageSource.getMessage(
                        "com.example.fullness.stationary.security.db_error", null, Locale.JAPAN);

                // メッセージが空の場合は汎用システムエラー文言を設定
            } else if (errorMessage == null || errorMessage.isBlank()) {

                errorMessage = messageSource.getMessage(
                        "com.example.fullness.stationary.security.system_error", null, Locale.JAPAN);
            }

            // メッセージキーが存在しない場合のフォールバック
        } catch (NoSuchMessageException e) {
            errorMessage = "システムエラーが発生しました。";
        }

        // 画面にエラーメッセージを渡す
        model.addAttribute("errorMessage", errorMessage);

        // DB エラーの場合は専用エラー画面へ遷移
        if (isDisplayErrorPage) {
            return "admin/error";
        }

        // ログイン画面へ戻すため、前回入力したユーザー名を復元する
        HttpSession session = request.getSession(false);
        LoginForm loginForm = new LoginForm();

        if (session != null) {
            // セッションに保存されていたログイン名をフォームにセット
            String savedName = (String) session.getAttribute("loginName");
            if (savedName != null) {
                loginForm.setName(savedName);
            }
        }

        // ログイン画面にフォームを渡す
        model.addAttribute("loginForm", loginForm);

        return "admin/login";
    }
}