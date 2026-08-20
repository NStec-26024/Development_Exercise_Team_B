package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.AdminProductCategoryForm;
import com.example.fullness.stationary.helper.ProductCategoryHelper;
import com.example.fullness.stationary.service.AdminProductCategoryService;
import com.example.fullness.stationary.validator.ValidationOrder;

@Controller
@RequestMapping("/admin/category/add")
public class AdminProductCategoryController {

    // service層でハッシュ化させる(DB登録前)
    // アカウントの存在確認もservice層

    @Autowired
    AdminProductCategoryService adminProductCategoryService;

    @Autowired
    ProductCategoryHelper productCategoryHelper;

    /**
     * リクエスト毎にアカウント登録Formオブジェクトを初期化
     * 
     * @return 初期化されたadminProductCategoryFormオブジェクト
     */
    @ModelAttribute
    public AdminProductCategoryForm setUpForm() {
        return new AdminProductCategoryForm();
    }

    /**
     * アカウント登録の入力画面を表示
     * データベースから登録可能な社員一覧を取得し、セレクトボックス用に画面に引き渡す
     * 
     * @param model 画面へデータを引き渡すためのModelオブジェクト
     * @return 入力画面のテンプレートパス ("admin/account/form")
     */
    @GetMapping
    public String showInput(Model model) {

        return "admin/category/form";
    }

    /**
     * 入力画面からの送信内容をバリデーションチェックし、問題がなければ確認画面へリダイレクト
     * エラーがある場合は入力フォームへ値を保持したまま差し戻し
     * 
     * @param adminProductCategoryForm 入力画面から送信されたフォームオブジェクト
     * @param result                   バリデーションの検証結果
     * @param redirectAttributes       リダイレクト先へデータを引き継ぐためのフラッシュ属性設定オブジェクト
     * @return 成功時は確認画面、失敗時は入力画面へのリダイレクトパス
     */
    @PostMapping("/postconfirm")
    public String validateInput(
            @Validated(ValidationOrder.class) @ModelAttribute("adminProductCategoryForm") AdminProductCategoryForm adminProductCategoryForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("adminProductCategoryForm", adminProductCategoryForm);
        if (result.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + "adminProductCategoryForm", result);
            return "redirect:/admin/category/add";

        } else {

            String name = adminProductCategoryForm.getName();
            redirectAttributes.addFlashAttribute("categoryName", name);

            return "redirect:/admin/category/add/confirm";
        }
    }

    /**
     * アカウント登録の確認画面を表示
     * 不正遷移防止のためフォームデータの存在チェックを行う
     * 
     * @param adminProductCategoryForm /postconfirmから送信されたフォームオブジェクト
     * @param redirectAttributes       不正アクセス時のエラーメッセージ格納オブジェクト
     * @return 確認画面のテンプレートパス、不正遷移時は入力画面へのリダイレクトパス
     */
    @GetMapping("/confirm")
    public String showConfirm(
            @ModelAttribute("adminProductCategoryForm") AdminProductCategoryForm adminProductCategoryForm,
            RedirectAttributes redirectAttributes) {
        /*
         * null:データそのものがない
         * isEmpty:文字数０文字の空っぽ(String)
         */

        if (adminProductCategoryForm.getName() == null || adminProductCategoryForm.getName().isEmpty()) {

            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin/category/add";
        }

        return "admin/category/confirm";
    }

    /**
     * アカウント名の重複チェックを行い、パスワードをハッシュ化した上でデータベースへ登録
     * 成功時は完了画面へリダイレクト、重複エラー時は入力画面へリダイレクト
     * 
     * @param adminProductCategoryForm 確認画面から送信されたフォームオブジェクト
     * @param redirectAttributes       完了画面へ引き渡すデータ格納オブジェクト
     * @return 完了画面へのリダイレクトパス、重複エラー時は入力画面へのリダイレクトパス
     */
    @PostMapping("/postcomplete")
    public String regist(AdminProductCategoryForm adminProductCategoryForm,
            RedirectAttributes redirectAttributes) {
        // 重複チェック
        ProductCategory productCategory = productCategoryHelper.formToEntity(adminProductCategoryForm);
        if (!adminProductCategoryService.existName(productCategory.getName())) {

            redirectAttributes.addFlashAttribute("errorMessages", "入力されたカテゴリ名は既に登録されています");
            redirectAttributes.addFlashAttribute("adminProductCategoryForm", adminProductCategoryForm);
            return "redirect:/admin/category/add";
        }

        // DBへのインサート処理
        int categoryId = adminProductCategoryService
                .add(productCategory);
        productCategory = adminProductCategoryService
                .getById(categoryId);
        redirectAttributes.addFlashAttribute("categoryName", productCategory);

        return "redirect:/admin/category/add/complete";

    }

    /**
     * 確認画面から入力画面へ戻る処理を制御
     * ユーザーがこれまで入力していた内容とともに入力画面へリダイレクト
     * 
     * @param adminProductCategoryForm 入力画面から送信されたフォームオブジェクト
     * @param redirectAttributes       入力画面へ引き渡すデータ格納オブジェクト
     * @return 入力画面へのリダイレクトパス
     */
    @PostMapping("/back")
    public String back(@ModelAttribute("adminProductCategoryForm") AdminProductCategoryForm adminProductCategoryForm,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("adminProductCategoryForm", adminProductCategoryForm);
        return "redirect:/admin/category/add";
    }

    /**
     * アカウント登録の完了画面を表示
     * 不正遷移防止のためフォームデータの存在チェックを行う
     * 
     * @param adminProductCategoryForm 確認画面から送信されたフォームオブジェクト
     * @param model                    正常アクセスチェック用オブジェクト
     * @param redirectAttributes       不正遷移のエラーメッセージ格納オブジェクト
     * @return 完了画面のテンプレートパス、不正アクセス時は管理者トップ画面へのリダイレクトパス
     */
    @GetMapping("/complete")
    public String showComplete(AdminProductCategoryForm adminProductCategoryForm, RedirectAttributes redirectAttributes,
            Model model) {
        if (model.getAttribute("categoryName") == null) {

            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin";
        }

        return "/admin/category/complete";
    }
}
