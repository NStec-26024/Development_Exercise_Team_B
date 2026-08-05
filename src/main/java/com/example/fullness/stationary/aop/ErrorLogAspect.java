package com.example.fullness.stationary.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * システム内の例外発生を検知し、エラーログを一元記録するアスペクトクラス。
 * 
 */
@Slf4j
@Aspect
@Component
public class ErrorLogAspect {

    /**
     * <p>
     * 対象：com.example.fullness.stationary パッケージ配下の、
     * 「Controller」または「Service」で終わるクラスのすべてのメソッド
     * </p>
     * 
     * @param joinPoint 発生元のメソッド情報
     * @param ex        発生した例外オブジェクト
     *                  コントローラーおよびサービス配下で例外が発生した際、自動的にエラーログを記録します。
     */

    @AfterThrowing(pointcut = "execution(* com.example.fullness.stationary..*Controller.*(..)) || " +
            "execution(* com.example.fullness.stationary..*Service.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        // 例外が発生した「クラス名」と「メソッド名」を取得
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();

        // エラーログに詳細情報（スタックトレース含む）を記録
        log.error("【システムエラー】発生場所: {}.{}() | エラー内容: {}",
                className, methodName, ex.getMessage(), ex);
    }
}