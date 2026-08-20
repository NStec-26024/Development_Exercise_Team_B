package com.example.fullness.stationary.service.impl;

import org.springframework.stereotype.Service;

import com.example.fullness.stationary.dto.AdminProductSessionData;
import com.example.fullness.stationary.service.SessionService;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionServiceImpl implements SessionService {

    private static final String KEY = "adminProductSessionData";

    public void save(HttpSession session, AdminProductSessionData data) {
        session.setAttribute(KEY, data);
    }

    public AdminProductSessionData get(HttpSession session) {
        Object obj = session.getAttribute(KEY);
        return (obj instanceof AdminProductSessionData) ? (AdminProductSessionData) obj : null;
    }

    public void clear(HttpSession session) {
        session.removeAttribute(KEY);
    }
}
