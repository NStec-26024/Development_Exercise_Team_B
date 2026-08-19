package com.example.fullness.stationary.exception;

/**
 * 管理画面の業務例外を表すクラス。
 * 業務上のエラーが発生した際に投げられる。
 */
public class AdminBusinessException extends RuntimeException {

    /**
     * エラーメッセージを指定して例外を生成する。
     */
    public AdminBusinessException(String msg) {
        super(msg);
    }
}
