package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminErrorController {

    @GetMapping("/admin/error")
    public String showErrorPage(HttpSession session, Model model) {

        // failureHandler がセットしたエラーメッセージを取得
        String errorMessage = (String) session.getAttribute("errorMessage");

        // 画面へ渡す
        model.addAttribute("errorMessage", errorMessage);

        return "admin/error"; // admin/error.html を表示
    }
}
