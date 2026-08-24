package com.example.fullness.stationary.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.dto.AdminProductSessionData;
import com.example.fullness.stationary.service.SessionService;

import jakarta.servlet.http.HttpSession;

/**
 * 商品編集処理で使用するセッションデータを管理するサービス。
 *
 * このサービスの責務は以下のとおり。
 * - 商品編集途中の入力内容をセッションへ保存する
 * - セッションから編集途中データを取得する
 * - 編集完了後にセッションデータを削除する
 *
 * セッションキーは固定で管理し、Controller や他のサービスが
 * セッションキーを意識せずに利用できるようにしている。
 */
@Service
public class SessionServiceImpl implements SessionService {

    /** セッションに保存するデータのキー名 */
    @Value("${session.key.admin-product}")
    private String KEY;

    /**
     * 編集途中の入力内容をセッションへ保存する。
     *
     * @param session HTTP セッション
     * @param data    編集途中データ
     */
    public void save(HttpSession session, AdminProductSessionData data) {
        session.setAttribute(KEY, data);
    }

    /**
     * セッションから編集途中データを取得する。
     * データが存在しない場合は null を返す。
     *
     * @param session HTTP セッション
     * @return 編集途中データ、または null
     */
    public AdminProductSessionData get(HttpSession session) {
        Object obj = session.getAttribute(KEY);
        return (obj instanceof AdminProductSessionData) ? (AdminProductSessionData) obj : null;
    }

    /**
     * セッションに保存されている編集途中データを削除する。
     *
     * @param session HTTP セッション
     */
    public void clear(HttpSession session) {
        session.removeAttribute(KEY);
    }
}