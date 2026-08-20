package com.example.fullness.stationary.service;

import com.example.fullness.stationary.dto.AdminProductSessionData;

import jakarta.servlet.http.HttpSession;

public interface SessionService {

    void save(HttpSession session, AdminProductSessionData data);

    AdminProductSessionData get(HttpSession session);

    void clear(HttpSession session);
}
