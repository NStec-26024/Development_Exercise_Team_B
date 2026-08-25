package com.example.fullness.stationary.exception;

import java.io.IOException;

/**
 * 管理画面の業務例外を表すクラス。
 * 業務上のエラーが発生した際に投げられる。
 */
public class AdminIOException extends IOException {

    /**
     * エラーメッセージを指定して例外を生成する。
     */
    public AdminIOException(String msg) {
        super(msg);
    }
}
