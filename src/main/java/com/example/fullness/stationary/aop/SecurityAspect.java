package com.example.fullness.stationary.aop;

import java.util.Locale;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {
    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    private final JdbcTemplate jdbcTemplate;
    private final MessageSource messageSource;

    public SecurityAspect(JdbcTemplate jdbcTemplate, MessageSource messageSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.messageSource = messageSource;
    }

    @Before("execution(* com.example.fullness.stationary.config.CustomAuthenticationFailureHandler.onAuthenticationFailure(..))")
    public void auditLoginFailure(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = (HttpServletRequest) args[0];
        AuthenticationException exception = (AuthenticationException) args[2];

        String msgKey = "com.example.fullness.stationary.security.bad_credentials";

        if (exception instanceof LockedException) {
            msgKey = "com.example.fullness.stationary.security.locked";
        } else if (exception.getCause() instanceof CannotGetJdbcConnectionException) {
            logger.error("データベース接続エラー");
            msgKey = "com.example.fullness.stationary.security.db_error";
        }

        String translatedMsg = messageSource.getMessage(msgKey, null, Locale.JAPAN);
        request.getSession().setAttribute("LOGIN_ERROR_MSG", translatedMsg);
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }

    @Around("execution(* com.example.fullness.stationary.controller.AdminLoginController.loginPage(..))")
    public Object checkAlreadyLoggedIn(ProceedingJoinPoint joinPoint) throws Throwable {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {

            return "redirect:/admin";
        }

        return joinPoint.proceed();
    }
}
