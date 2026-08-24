package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.dto.AdminProductSessionData;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.AdminProductForm;
import com.example.fullness.stationary.service.AdminProductQueryService;
import com.example.fullness.stationary.service.SessionService;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;

/**
 * 商品編集フォーム画面の表示および入力内容のセッション保存を担当する Controller。
 *
 * この Controller の責務は以下のとおり。
 * - 商品編集フォームの初期表示（GET）
 * - 入力内容のセッション保存（POST）
 * - 商品が存在しない場合のエラーメッセージ設定と一覧画面へのリダイレクト
 *
 */
@Controller
@RequestMapping("/admin/product/edit")
public class AdminProductEditFormController {

    @Autowired
    private AdminProductQueryService productQueryService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SessionService sessionService;

    /**
     * 商品編集フォームの初期表示処理。
     *
     * 主な処理内容：
     * - 商品 ID に対応する商品情報を取得する
     * - 商品が存在しない場合はエラーメッセージを設定し一覧画面へリダイレクトする
     * - セッションに保持されている編集途中データがあればフォームに反映する
     * - 商品情報をフォームに設定し、編集画面を表示する
     *
     * @param id                 商品 ID（パスパラメータ）
     * @param session            HTTP セッション
     * @param model              画面へ渡すモデル
     * @param redirectAttributes リダイレクト時のメッセージ設定
     * @return 編集フォーム画面、または一覧画面へのリダイレクト
     */
    @GetMapping("/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Product product = productQueryService.getProductById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    messageSource.getMessage("com.example.fullness.stationary.product.not_found", null, Locale.JAPAN));
            return "redirect:/admin/product";
        }

        AdminProductForm form = new AdminProductForm();
        form.setId(id);

        AdminProductSessionData data = sessionService.get(session);

        // 編集途中データがある場合はセッション内容を優先
        if (data != null && id.equals(data.targetId)) {
            form.setName(data.name);
            form.setPrice(String.valueOf(data.price));
            form.setStock(String.valueOf(data.stock));
            form.setCategoryId(data.categoryId);
            form.setImagePath(data.existingImageUrl);
        } else {
            // 商品情報をフォームに設定
            form.setName(product.getName());
            form.setPrice(String.valueOf(product.getPrice()));
            int stock = product.getProductStock() != null ? product.getProductStock().getQuantity() : 0;
            form.setStock(String.valueOf(stock));
            form.setCategoryId(product.getCategoryId());
            form.setImagePath(product.getImageUrl());
        }

        model.addAttribute("form", form);
        model.addAttribute("categories", productQueryService.getAllCategories());
        model.addAttribute("productId", id);
        model.addAttribute("currentImageUrl", product.getImageUrl());

        return "admin/product/edit_form";
    }

    /**
     * 商品編集フォームの入力内容をセッションへ保存し、確認画面へ遷移する。
     *
     * 主な処理内容：
     * - 商品 ID の存在チェック
     * - フォーム入力値（name, price, stock, categoryId）のパース
     * - 画像がアップロードされている場合はバイト配列として保持
     * - 編集内容をセッションへ保存
     * - 確認画面へリダイレクト
     *
     * @param id                 商品 ID
     * @param form               編集フォーム入力内容
     * @param session            HTTP セッション
     * @param model              画面モデル
     * @param redirectAttributes リダイレクト時のメッセージ設定
     * @return 編集確認画面へのリダイレクト
     */
    @PostMapping("/{id}")
    public String submitEditForm(
            @PathVariable Integer id,
            @ModelAttribute("form") AdminProductForm form,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Product current = productQueryService.getProductById(id);

        if (current == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    messageSource.getMessage("com.example.fullness.stationary.product.not_found", null, Locale.JAPAN));
            return "redirect:/admin/product";
        }

        int price = 0;
        int stock = 0;

        try {
            if (form.getPrice() != null && !form.getPrice().isBlank()) {
                price = Integer.parseInt(form.getPrice());
            }
        } catch (Exception ignored) {
        }

        try {
            if (form.getStock() != null && !form.getStock().isBlank()) {
                stock = Integer.parseInt(form.getStock());
            }
        } catch (Exception ignored) {
        }

        AdminProductSessionData data = new AdminProductSessionData();
        data.targetId = id;
        data.name = form.getName();
        data.price = price;
        data.stock = stock;
        data.categoryId = form.getCategoryId();

        // 画像処理
        if (form.getImage() != null && !form.getImage().isEmpty()) {
            try {
                data.imageBytes = form.getImage().getBytes();
                data.imageFileName = form.getImage().getOriginalFilename();
            } catch (IOException e) {
            }
        }

        data.existingImageUrl = current.getImageUrl();

        sessionService.save(session, data);

        return "redirect:/admin/product/edit/confirm";
    }
}
