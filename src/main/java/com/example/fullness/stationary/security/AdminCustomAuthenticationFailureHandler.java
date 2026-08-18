package com.example.fullness.stationary.security;

import java.io.IOException;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.example.fullness.stationary.service.AdminLoginAttemptService;

/**
 * ログイン認証失敗時の処理をカスタムするハンドラー。
 * 入力されたアカウント名をセッションに保存し、ロック中かどうかを次の2通りで判定する。
 *
 * 1. {@link LockedException}が発生した場合（既存アカウントのロック状態を
 * {@code DaoAuthenticationProvider}が事前チェックした結果）
 * 2. それ以外の例外でも{@link AdminLoginAttemptService#isBlocked}がtrueの場合
 * （存在しないアカウント名でロックに達している場合）
 *
 * ロック中と判定した場合はロックメッセージを設定し失敗回数を加算しない。
 * それ以外の認証失敗はアカウント名の有無を区別せずbad_credentialsメッセージを設定し、
 * 失敗回数を加算する。
 */
@Component
public class AdminCustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AdminLoginAttemptService adminLoginAttemptServiceImpl;
    private final MessageSource messageSource;

    /**
     * @param adminLoginAttemptServiceImpl ログイン失敗回数とロック状態を管理するサービス
     * @param messageSource                メッセージ取得に使用する MessageSource
     */
    public AdminCustomAuthenticationFailureHandler(AdminLoginAttemptService adminLoginAttemptServiceImpl,
            MessageSource messageSource) {
        this.adminLoginAttemptServiceImpl = adminLoginAttemptServiceImpl;
        this.messageSource = messageSource;
        setDefaultFailureUrl("/admin/login");
    }

    private boolean isDbError(Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof CannotGetJdbcConnectionException
                    || cur instanceof DataAccessResourceFailureException) {
                return true;
            }

            String cn = cur.getClass().getName();
            if (cn.contains("JDBCConnectionException") || cn.contains("CommunicationsException")
                    || cn.contains("SQLException") || cn.contains("SQLTimeoutException")) {
                return true;
            }

            cur = cur.getCause();
        }
        return false;
    }

    /**
     * 認証失敗時に呼び出される処理。
     * アカウント名の保存を行い、ロック中でなければ失敗回数を加算した上で
     * bad_credentials メッセージを設定する。ロック中の場合はロックメッセージを設定し、
     * 失敗回数は加算しない。
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

        // ログインフォームの name を取得（AdminSecurityConfig#usernameParameter と一致させる）
        String accountName = request.getParameter("name");

        // セッションを必ず作成（アカウント名保存のため）
        HttpSession session = request.getSession(true);

        // 認証処理中に DB 接続エラーが発生した場合はエラー画面へ遷移させる
        // リクエスト属性や例外チェーン全体を調べる（handler のみで対処）
        boolean dbErrorDetected = false;

        // サーブレットエラー属性に例外があればチェック
        Object servletEx = request.getAttribute("javax.servlet.error.exception");
        if (servletEx instanceof Throwable) {
            if (isDbError((Throwable) servletEx)) {
                dbErrorDetected = true;
            }
        }

        // 直接渡された exception チェーンをチェック
        if (!dbErrorDetected && exception != null) {
            if (isDbError(exception)) {
                dbErrorDetected = true;
            } else if (exception instanceof AuthenticationServiceException && exception.getCause() != null
                    && isDbError(exception.getCause())) {
                dbErrorDetected = true;
            }
        }

        if (dbErrorDetected) {
            String dbErrorMsg = messageSource.getMessage(
                    "com.example.fullness.stationary.security.db_error",
                    null,
                    Locale.JAPAN);
            session.setAttribute("errorMessage", dbErrorMsg);
            response.sendRedirect("/admin/error");
            return;
        }

        // 入力したユーザー名を保存（パスワードは保存しない）
        session.setAttribute("loginUsername", accountName);

        boolean locked = exception instanceof LockedException
                || (accountName != null && !accountName.isBlank()
                        && adminLoginAttemptServiceImpl.isBlocked(accountName));

        if (locked) {
            // ロック中の試行はパスワード照合まで到達していないため、失敗回数は加算しない
            session.setAttribute("loginErrorMessage",
                    messageSource.getMessage(
                            "com.example.fullness.stationary.security.locked",
                            null,
                            Locale.JAPAN));
        } else {
            // アカウント未登録・パスワード不一致を区別せず同一メッセージを表示する
            session.setAttribute("loginErrorMessage",
                    messageSource.getMessage(
                            "com.example.fullness.stationary.security.bad_credentials",
                            null,
                            Locale.JAPAN));

            if (accountName != null && !accountName.isBlank()) {
                // 失敗回数を加算（ロック判定は LoginAttemptService が行う）
                adminLoginAttemptServiceImpl.loginFailed(accountName);
            }
        }

        // ログイン画面へリダイレクト
        response.sendRedirect("/admin/login");
    }

}
