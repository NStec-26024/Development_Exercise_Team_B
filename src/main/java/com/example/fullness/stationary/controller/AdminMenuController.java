package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 管理画面トップメニューを表示するコントローラー。
 * /admin へのアクセス時に管理メニュー画面を返す。
 */
@Controller
@RequestMapping("/admin")
public class AdminMenuController {

    /**
     * 管理メニュー画面を表示する。
     *
     * @return 管理メニュー画面のビュー名
     */
    @GetMapping
    public String adminRoot() {
        return "admin/menu";
    }
}
