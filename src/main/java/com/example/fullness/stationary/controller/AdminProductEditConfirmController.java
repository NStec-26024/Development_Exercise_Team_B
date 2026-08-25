package com.example.fullness.stationary.controller;

import java.io.IOError;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.exception.AdminIOException;
import com.example.fullness.stationary.form.AdminProductForm;
import com.example.fullness.stationary.helper.AdminProductHelper;
import com.example.fullness.stationary.service.AdminProductModificationService;
import com.example.fullness.stationary.service.AdminProductQueryService;

import jakarta.servlet.http.HttpSession;

/**
 * 商品編集内容の確認画面表示および更新処理を担当する Controller。
 *
 * - 編集フォームで入力された内容を確認画面に表示する（GET）
 * - 確認画面から更新処理を実行する（POST）
 * - セッションに保持された編集内容が存在しない場合は一覧画面へリダイレクトする
 *
 */
@Controller
@RequestMapping("/admin/product/edit/confirm")
public class AdminProductEditConfirmController {

    @Autowired
    private AdminProductHelper adminProductHelper;

    @Autowired
    private AdminProductQueryService productQueryService;

    @Autowired
    private AdminProductModificationService adminProductModificationService;

    /**
     * 商品編集内容の確認画面を表示する。
     *
     * 主な処理内容：
     * - セッションから編集内容を取得する
     * - 編集内容が存在しない場合は一覧画面へリダイレクトする
     * - 編集内容をフォームオブジェクトに詰めて画面へ渡す
     *
     * @param session HTTP セッション
     * @param model   画面へ渡すモデル
     * @return 編集確認画面、または一覧画面へのリダイレクト
     */
    @GetMapping
    public String showConfirmPage(HttpSession session, Model model) {

        AdminProductForm form = (AdminProductForm) session.getAttribute("adminProductForm");

        if (form == null) {
            return "redirect:/admin/product";
        }

        String categoryName = productQueryService.getCategoryName(form.getCategoryId());
        form.setCategoryName(categoryName);

        // ★ 画像表示用のURLを生成
        String imageUrl;

        if (form.getImageBytes() != null && form.getImageBytes().length > 0) {
            String base64 = java.util.Base64.getEncoder().encodeToString(form.getImageBytes());
            imageUrl = "data:image/*;base64," + base64;
        } else {
            // 既存画像を使う
            imageUrl = "/images/" + form.getImagePath();
        }

        model.addAttribute("form", form);
        return "admin/product/edit_confirm";
    }

    /**
     * 商品編集内容を更新処理サービスへ渡し、更新を実行する。
     *
     * 主な処理内容：
     * - セッションから編集内容を取得する
     * - 編集内容が存在しない場合は一覧画面へリダイレクトする
     * - 更新処理サービスを呼び出して商品情報を更新する
     * - 完了画面に表示するメッセージを設定する
     * - セッション内容を破棄する
     * - 完了画面へリダイレクトする
     *
     * @param session            HTTP セッション
     * @param redirectAttributes 完了画面へ渡すメッセージ
     * @return 完了画面へのリダイレクト
     */
    @PostMapping
    public String executeUpdate(
            HttpSession session,
            RedirectAttributes redirectAttributes) throws IOException {

        AdminProductForm form = (AdminProductForm) session.getAttribute("adminProductForm");

        if (form == null) {
            return "redirect:/admin/product";
        }

        Product product = adminProductHelper.fromToEntity(form);

        if (form.getImageBytes() != null && form.getImageBytes().length > 0) {

            // 保存先ディレクトリ
            String uploadDir = "src/main/resources/static/images/";

            // 新しいファイル名（元のファイル名）
            String fileName = form.getImageFileName();

            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths.get(uploadDir + fileName),
                        form.getImageBytes());
            } catch (IOException e) {
                throw new AdminIOException("test");
            }

            product.setImageUrl(fileName);

        } else {
            product.setImageUrl(form.getImagePath());
        }

        adminProductModificationService.updateProduct(product);

        redirectAttributes.addFlashAttribute("completed", true);
        redirectAttributes.addFlashAttribute("productName", form.getName());

        session.removeAttribute("adminProductForm");

        return "redirect:/admin/product/edit/complete";
    }
}
