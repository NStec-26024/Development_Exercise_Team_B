package com.example.fullness.stationary.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.service.AdminProductDeletionService;
import com.example.fullness.stationary.service.AdminProductQueryService;

import jakarta.servlet.http.HttpSession;

/**
 * 商品削除確認画面（BP007, UC013）を担当するコントローラクラス。
 * 「完了」押下時に、はじめて論理削除（delete_flag更新）を行う。
 */
@Controller
@RequestMapping("/admin/product/delete")
public class AdminProductDeleteConfirmController {

    private static final String SESSION_KEY_DELETE_COMPLETED = "productDeleteCompleted";

    @Autowired
    private AdminProductQueryService productQueryService;

    @Autowired
    private AdminProductDeletionService productDeletionService;

    @Autowired
    private MessageSource messageSource;

    // 削除する商品を表示する
    @GetMapping("/{id}")
    public String showDeleteProduct(
            @PathVariable("id") Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Product product = productQueryService.getProductById(id);

            if (product == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        messageSource.getMessage(
                                "com.example.fullness.stationary.product.not_found",
                                null,
                                Locale.JAPAN));
                return "redirect:/admin/product";
            }

            model.addAttribute("product", product);
            return "admin/product/delete_confirm";

        } catch (DataAccessException e) {
            // DBエラーの場合はエラー画面へ
            return "error";
        }
    }

    /**
     * 商品削除を確定する（論理削除）。
     */
    @PostMapping("/{id}")
    public String completeDelete(
            @PathVariable("id") Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Product product = productQueryService.getProductById(id);

            productDeletionService.deleteProduct(id);

            session.setAttribute(SESSION_KEY_DELETE_COMPLETED, true);
            redirectAttributes.addFlashAttribute(
                    "productName",
                    product != null ? product.getName() : null);

            return "redirect:/admin/product/delete/complete";

        } catch (AdminBusinessException e) {
            // 業務エラーの場合
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/product";

        } catch (DataAccessException e) {
            // DBエラーの場合
            return "error";
        }
    }

}
