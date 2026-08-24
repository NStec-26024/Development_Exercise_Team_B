package com.example.fullness.stationary.security;

import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.form.AdminLoginForm;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

/**
 * 管理画面で発生した例外を共通処理するハンドラー。
 */
@Slf4j
@ControllerAdvice
public class AdminGlobalExceptionHandler {

    private final MessageSource messageSource;

    /**
     * メッセージ取得に使用する MessageSource を受け取る。
     *
     * @param messageSource メッセージソース
     */
    public AdminGlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 静的リソース（画像・favicon等）が見つからない場合の専用処理。404を返し、
     * リクエストURLが{@code /images/**}配下の場合のみWARNログを出力する。
     *
     * @param request 発生元の HTTP リクエスト
     * @param ex      捕捉した例外
     */
    // @ControllerAdviceは複数ハンドラーがあれば最も型が近いものを優先するため、
    // このメソッドを{@link #handleAllExceptions}より具体的な型で分けておくことで、
    // 静的リソース404が管理者ログイン画面への遷移に巻き取られてしまうのを防いでいる。
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleResourceNotFound(HttpServletRequest request, NoResourceFoundException ex) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/images/")) {
            // DBに登録された商品画像URLに対応する実ファイルが存在しないデータ不整合の
            // 兆候であるため、運用上見逃さないようログに残す（favicon.ico等の通常の404は対象外）
            log.warn("商品画像ファイルが見つかりません（DBのimage_urlに対応する実ファイルが無い可能性）: URL={}", uri);
        }
    }

    private boolean isDbError(Throwable t) {
        Throwable cur = t;
        while (cur != null) {

            if (cur instanceof org.springframework.jdbc.CannotGetJdbcConnectionException ||
                    cur instanceof org.springframework.transaction.CannotCreateTransactionException) {
                return true;
            }

            String cn = cur.getClass().getName();
            if (cn.contains("JDBCConnectionException") ||
                    cn.contains("CommunicationsException") ||
                    cn.contains("SQLException") ||
                    cn.contains("SQLTimeoutException")) {
                return true;
            }

            cur = cur.getCause();
        }
        return false;
    }

    /**
     * 管理画面で発生した例外を共通処理する。
     * 認証関連の例外は Spring Security に委譲し、それ以外はメッセージを設定して
     * エラー画面またはログイン画面へ遷移させる。
     *
     * @param request 発生元の HTTP リクエスト
     * @param ex      捕捉した例外
     * @param model   画面へ値を渡すためのモデル
     * @return 遷移先ビュー名（admin/error または admin/login）
     * @throws Exception 認証関連例外の場合は再スローされる
     */
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(HttpServletRequest request, Exception ex, Model model,
            RedirectAttributes redirectAttributes) throws Exception {

        // 認証関連の例外は Spring Security に委譲
        if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
            throw ex;
        }

        log.info("例外検知: URL={}, type={}", request.getRequestURI(), ex.getClass().getSimpleName());

        String errorMessage;

        if (isDbError(ex)) {
            errorMessage = messageSource.getMessage(
                    "com.example.fullness.stationary.security.db_error",
                    null,
                    Locale.JAPAN);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/admin/error";
        }

        // その他の例外
        try {
            errorMessage = messageSource.getMessage(
                    "com.example.fullness.stationary.security.system_error",
                    null,
                    Locale.JAPAN);
        } catch (NoSuchMessageException e) {
            errorMessage = "システムエラーが発生しました。";
        }

        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        // ログイン画面へ戻すための初期化処理
        HttpSession session = request.getSession(false);
        AdminLoginForm adminLoginForm = new AdminLoginForm();

        if (session != null) {
            String loginError = (String) session.getAttribute("loginErrorMessage");
            if (loginError != null) {
                redirectAttributes.addFlashAttribute("securityErrorMessage", loginError);
                session.removeAttribute("loginErrorMessage");
            }

            String savedUsername = (String) session.getAttribute("loginUsername");
            if (savedUsername != null) {
                adminLoginForm.setName(savedUsername);
                session.removeAttribute("loginUsername");
            }
        }

        redirectAttributes.addFlashAttribute("adminLoginForm", adminLoginForm);
        redirectAttributes.addFlashAttribute(
                BindingResult.MODEL_KEY_PREFIX + "adminLoginForm",
                new BeanPropertyBindingResult(adminLoginForm, "adminLoginForm"));

        return "redirect:/admin/error";
    }

    @ExceptionHandler(AdminBusinessException.class)
    public String dbHaldler(HttpServletRequest request, AdminBusinessException ex, Model model,
            RedirectAttributes redirectAttributes) {

        String errorMessage = ex.getMessage();
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/admin/error";
    }
}