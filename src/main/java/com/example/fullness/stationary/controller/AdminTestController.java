package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminTestController {

    @GetMapping("/test-error")
    public String causeError() {
        // ⭐ わざと未定義のエラー（NullPointerExceptionなど）を発生させる
        throw new RuntimeException("テスト用のシステムエラーです！");
    }
}