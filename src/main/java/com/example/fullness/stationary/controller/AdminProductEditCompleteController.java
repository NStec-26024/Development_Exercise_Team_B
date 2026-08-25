package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fullness.stationary.form.AdminProductForm;

/**
 * 商品修正の完了画面（BP011, UC012）を担当するコントローラクラス。
 * {@link AdminProductEditConfirmController}が設定した完了フラグを経由していない直接アクセスは、検索画面へ差し戻す。
 */
@Controller
@RequestMapping("/admin/product/edit")
public class AdminProductEditCompleteController {
    /**
     * 商品修正の完了画面を表示する。完了操作を経ていない直接アクセスは検索画面へ戻す。
     */
    @GetMapping("/complete")
    public String showEditComplete(Model model) {

        Boolean completed = (Boolean) model.getAttribute("completed");

        if (completed == null || !completed) {
            return "redirect:/admin/product";
        }

        return "admin/product/edit_complete";
    }

}
