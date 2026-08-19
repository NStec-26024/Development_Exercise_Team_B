package com.example.fullness.stationary.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * コントローラー・サービス層で発生した例外を横断的にログ出力するアスペクト。
 * 発生箇所（クラス名・メソッド名）と例外内容を記録する。
 */
@Slf4j
@Aspect
@Component
public class ErrorLogAspect {

    /**
     * 例外発生時にログを出力する。
     * 対象は Controller / RestController / Service の各メソッド。
     *
     * @param joinPoint 例外が発生したメソッドの情報（クラス名・メソッド名など）
     * @param ex        発生した例外オブジェクト
     */
    @AfterThrowing(pointcut = "execution(* com.example.fullness.stationary..*(..)) && " +
            "(@target(org.springframework.stereotype.Controller) || " +
            "@target(org.springframework.web.bind.annotation.RestController) || " +
            "@target(org.springframework.stereotype.Service))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();

        log.error("【システムエラー】発生場所: {}.{}() | エラー内容: {}",
                className, methodName, ex.getMessage(), ex);

    }
}
