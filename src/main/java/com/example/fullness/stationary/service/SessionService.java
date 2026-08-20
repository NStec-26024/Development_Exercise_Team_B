package com.example.fullness.stationary.service;

import com.example.fullness.stationary.dto.AdminProductSessionData;

import jakarta.servlet.http.HttpSession;

/**
 * 商品編集処理で使用するセッションデータを管理するサービスのインターフェイス。
 *
 * - 商品編集途中の入力内容をセッションへ保存する
 * - セッションから編集途中データを取得する
 * - 編集完了後にセッションデータを削除する
 */
public interface SessionService {

    /**
     * 編集途中の入力内容をセッションへ保存する。
     *
     * @param session HTTP セッション
     * @param data    編集途中データ
     */
    void save(HttpSession session, AdminProductSessionData data);

    /**
     * セッションから編集途中データを取得する。
     * データが存在しない場合は null を返す。
     *
     * @param session HTTP セッション
     * @return 編集途中データ、または null
     */
    AdminProductSessionData get(HttpSession session);

    /**
     * セッションに保存されている編集途中データを削除する。
     *
     * @param session HTTP セッション
     */
    void clear(HttpSession session);
}
