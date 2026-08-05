package com.example.fullness.stationary.security;

import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import com.example.fullness.stationary.form.LoginForm;

@ControllerAdvice
public class AdminGlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AdminGlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public AdminGlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(HttpServletRequest request, Exception ex, Model model) throws Exception {
        if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
            throw ex;
        }

        logger.error("システムエラーが発生しました。URL={}; message={}", request.getRequestURI(), ex.getMessage(), ex);

        String errorMessage;
        try {
            errorMessage = messageSource.getMessage(
                    "com.example.fullness.stationary.security.system_error",
                    null,
                    Locale.JAPAN);
        } catch (NoSuchMessageException e) {
            errorMessage = "システムエラーが発生しました。";
        }

        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("loginForm", new LoginForm());

        return "admin/login";
    }
}
