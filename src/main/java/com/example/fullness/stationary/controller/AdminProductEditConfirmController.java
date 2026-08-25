package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.AdminProductForm;
import com.example.fullness.stationary.helper.AdminProductEntityHelper;
import com.example.fullness.stationary.service.AdminProductModificationService;
import com.example.fullness.stationary.service.AdminProductQueryService;

import jakarta.servlet.http.HttpSession;

/**
 * 商品編集内容の確認画面表示および更新処理を担当する Controller。
 *
 * <p>
 * 以下の機能を提供する：
 * <ul>
 * <li>編集内容の確認画面表示（GET）</li>
 * <li>戻る／完了ボタンによる遷移制御（POST）</li>
 * <li>セッションに編集内容が存在しない場合は一覧画面へリダイレクト</li>
 * </ul>
 */
@Controller
@RequestMapping("/admin/product/edit/confirm")
public class AdminProductEditConfirmController {

    @Autowired
    private AdminProductEntityHelper adminProductEntityHelper;

    @Autowired
    private AdminProductQueryService productQueryService;

    @Autowired
    private AdminProductModificationService adminProductModificationService;

    /**
     * 編集内容確認画面を表示する。
     */
    @GetMapping
    public String showConfirmPage(HttpSession session, Model model) {

        // --- セッションから編集内容を取得 ---
        AdminProductForm form = (AdminProductForm) session.getAttribute("adminProductForm");
        if (form == null) {
            return "redirect:/admin/product";
        }

        // --- カテゴリ名を補完 ---
        String categoryName = productQueryService.getCategoryName(form.getCategoryId());
        form.setCategoryName(categoryName);

        model.addAttribute("form", form);
        return "admin/product/edit_confirm";
    }

    /**
     * 編集内容を更新する（戻る／完了ボタン）。
     */
    @PostMapping
    public String executeUpdate(
            @RequestParam("action") String action,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // --- セッションから編集内容を取得 ---
        AdminProductForm form = (AdminProductForm) session.getAttribute("adminProductForm");
        if (form == null) {
            return "redirect:/admin/product";
        }

        // --- 戻るボタン ---
        if ("back".equals(action)) {
            return "redirect:/admin/product/edit/" + form.getId();
        }

        // --- 完了ボタン ---
        if ("complete".equals(action)) {

            Product product = adminProductEntityHelper.toProduct(form);

            adminProductModificationService.updateProduct(product);

            redirectAttributes.addFlashAttribute("completed", true);
            redirectAttributes.addFlashAttribute("productName", form.getName());

            // セッション破棄
            session.removeAttribute("adminProductForm");

            return "redirect:/admin/product/edit/complete";
        }

        // --- 想定外の action ---
        return "redirect:/admin/product";
    }

}
