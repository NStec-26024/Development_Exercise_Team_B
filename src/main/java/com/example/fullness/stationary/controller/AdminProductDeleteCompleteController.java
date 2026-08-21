package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/product/delete")
public class AdminProductDeleteCompleteController {
    private static final String SESSION_KEY_DELETE_COMPLETED = "productDeleteCompleted";

    // 商品削除の完了画面を表示する
    // 削除操作を行っていない直接アクセスは検索画面へ戻す。
    // productNameはAdminProductDleteCofirmCotrollerからのリダイレクト時にflash属性として渡される
    @GetMapping("/complete")
    public String showDeleteComplete(HttpSession session, Model model) {
        Object completed = session.getAttribute(SESSION_KEY_DELETE_COMPLETED);
        session.removeAttribute(SESSION_KEY_DELETE_COMPLETED);
        if (!Boolean.TRUE.equals(completed)) {
            return "redirect:/admin/product";
        }
        return "admin/product/delete_complete";
    }

}
