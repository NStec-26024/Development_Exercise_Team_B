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

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.exception.AdminIOException;
import com.example.fullness.stationary.form.AdminProductForm;
import com.example.fullness.stationary.helper.AdminProductHelper;
import com.example.fullness.stationary.service.AdminProductQueryService;

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
    private AdminProductHelper ProductFormHelper;

    @Autowired
    private MessageSource messageSource;

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
            Model model,
            RedirectAttributes redirectAttributes) {

        Product product = productQueryService.getProductById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    messageSource.getMessage("com.example.fullness.stationary.product.not_found", null, Locale.JAPAN));
            return "redirect:/admin/product";
        }

        AdminProductForm form = ProductFormHelper.fromToForm(product);

        model.addAttribute("form", form);
        model.addAttribute("categories", productQueryService.getAllCategories());
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
            RedirectAttributes redirectAttributes) throws IOException {

        Product current = productQueryService.getProductById(id);
        if (current == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    messageSource.getMessage(
                            "com.example.fullness.stationary.product.not_found",
                            null,
                            Locale.JAPAN));
            return "redirect:/admin/product";
        }

        form.setImagePath(current.getImageUrl());

        try {
            form.setImageBytes(form.getImage().getBytes());
            form.setImageFileName(form.getImage().getOriginalFilename());
        } catch (IOException e) {
            throw new AdminIOException("test");
        }

        session.setAttribute("adminProductForm", form);

        return "redirect:/admin/product/edit/confirm";
    }

}