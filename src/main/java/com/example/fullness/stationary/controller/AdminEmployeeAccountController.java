package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AdminEmployeeAccountForm;
import com.example.fullness.stationary.helper.EmployeeAccountHelper;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

/**
 * 社員ログインアカウント登録Controller
 * 
 * 担当者がシステムを利用するために必要な情報をシステムに登録
 * 社員アカウントの新規登録処理における画面遷移(入力画面→確認画面→完了画面)
 * セッションデータ管理・バリデーション・DB登録制御
 */
@Controller
@RequestMapping("/admin/account")
// @SessionAttributes("adminEmployeeAccountForm")
public class AdminEmployeeAccountController {
    // service層でハッシュ化させる(DB登録前)
    // アカウントの存在確認もservice層

    @Autowired
    AdminEmployeeAccountService adminEmployeeAccountService;

    @Autowired
    EmployeeAccountHelper employeeAccountHelper;

    /**
     * リクエスト毎にアカウント登録Formオブジェクトを初期化
     * 
     * @return 初期化されたadminEmployeeAccountFormオブジェクト
     */
    @ModelAttribute
    public AdminEmployeeAccountForm setUpForm() {
        return new AdminEmployeeAccountForm();
    }

    /**
     * アカウント登録の入力画面を表示
     * データベースから登録可能な社員一覧を取得し、セレクトボックス用に画面に引き渡す
     * 
     * @param model 画面へデータを引き渡すためのModelオブジェクト
     * @return 入力画面のテンプレートパス ("admin/account/form")
     */
    @GetMapping("/form")
    public String showInput(Model model) {
        List<EmployeeAccount> employeeNameList = adminEmployeeAccountService.getEmployeeNameWithEmployeeAccount();

        if (employeeNameList.isEmpty()) {
            model.addAttribute("errorMessages", "アカウント登録可能な社員が存在しません");
        } else {
            model.addAttribute("employeeName", employeeNameList);
        }

        return "admin/account/form";
    }

    /**
     * 入力画面からの送信内容をバリデーションチェックし、問題がなければ確認画面へリダイレクト
     * エラーがある場合は入力フォームへ値を保持したまま差し戻し
     * 
     * @param adminEmployeeAccountForm 入力画面から送信されたフォームオブジェクト
     * @param result                   バリデーションの検証結果
     * @param redirectAttributes       リダイレクト先へデータを引き継ぐためのフラッシュ属性設定オブジェクト
     * @return 成功時は確認画面、失敗時は入力画面へのリダイレクトパス
     */
    @PostMapping("/postconfirm")
    public String validateInput(
            @Validated @ModelAttribute("adminEmployeeAccountForm") AdminEmployeeAccountForm adminEmployeeAccountForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("adminEmployeeAccountForm", adminEmployeeAccountForm);
        if (result.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm", result);
            return "redirect:/admin/account/form";

        } else {

            EmployeeAccount employeeAccount = employeeAccountHelper.formToEntity(adminEmployeeAccountForm);
            employeeAccount = adminEmployeeAccountService
                    .getEmployeeAccountWithEmployeeId(employeeAccount.getEmployeeId());
            redirectAttributes.addFlashAttribute("employee", employeeAccount);

            return "redirect:/admin/account/confirm";
        }
    }

    /**
     * アカウント登録の確認画面を表示
     * 不正遷移防止のためフォームデータの存在チェックを行う
     * 
     * @param adminEmployeeAccountForm /postconfirmから送信されたフォームオブジェクト
     * @param redirectAttributes       不正アクセス時のエラーメッセージ格納オブジェクト
     * @return 確認画面のテンプレートパス、不正遷移時は入力画面へのリダイレクトパス
     */
    @GetMapping("/confirm")
    public String showConfirm(
            @ModelAttribute("adminEmployeeAccountForm") AdminEmployeeAccountForm adminEmployeeAccountForm,
            RedirectAttributes redirectAttributes) {
        /*
         * null:データそのものがない
         * isEmpty:文字数０文字の空っぽ(String)
         */

        if (adminEmployeeAccountForm.getEmployeeId() == null ||
                adminEmployeeAccountForm.getName() == null || adminEmployeeAccountForm.getName().isEmpty() ||
                adminEmployeeAccountForm.getPassword() == null || adminEmployeeAccountForm.getPassword().isEmpty()) {

            redirectAttributes.addFlashAttribute("errorMessages", "入力情報が見つかりません。再度入力してください");
            return "redirect:/admin/account/form";
        }

        return "admin/account/confirm";
    }

    /**
     * アカウント名の重複チェックを行い、パスワードをハッシュ化した上でデータベースへ登録
     * 成功時は完了画面へリダイレクト、重複エラー時は入力画面へリダイレクト
     * 
     * @param adminEmployeeAccountForm 確認画面から送信されたフォームオブジェクト
     * @param redirectAttributes       完了画面へ引き渡すデータ格納オブジェクト
     * @return 完了画面へのリダイレクトパス、重複エラー時は入力画面へのリダイレクトパス
     */
    @PostMapping("/postcomplete")
    public String regist(AdminEmployeeAccountForm adminEmployeeAccountForm,
            RedirectAttributes redirectAttributes) {
        // 重複チェック
        EmployeeAccount employeeAccount = employeeAccountHelper.formToEntity(adminEmployeeAccountForm);
        if (!adminEmployeeAccountService.getAccountName(employeeAccount.getName())) {

            redirectAttributes.addFlashAttribute("errorMessages", "このアカウント名は既に使用されています");
            redirectAttributes.addFlashAttribute("adminEmployeeAccountForm", adminEmployeeAccountForm);
            return "redirect:/admin/account/form";
        }

        // DBへのインサート処理
        int accountId = adminEmployeeAccountService
                .addEmployeeAccount(employeeAccountHelper.formToEntity(adminEmployeeAccountForm));
        employeeAccount = adminEmployeeAccountService
                .getEmployeeNameWithEmployeeAccountId(accountId);
        redirectAttributes.addFlashAttribute("employee", employeeAccount);

        redirectAttributes.addFlashAttribute("accountName", adminEmployeeAccountForm.getName());
        return "redirect:/admin/account/complete";

    }

    /**
     * 確認画面から入力画面へ戻る処理を制御
     * ユーザーがこれまで入力していた内容とともに入力画面へリダイレクト
     * 
     * @param adminEmployeeAccountForm 入力画面から送信されたフォームオブジェクト
     * @param redirectAttributes       入力画面へ引き渡すデータ格納オブジェクト
     * @return 入力画面へのリダイレクトパス
     */
    @PostMapping("/back")
    public String back(@ModelAttribute("adminEmployeeAccountForm") AdminEmployeeAccountForm adminEmployeeAccountForm,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("adminEmployeeAccountForm", adminEmployeeAccountForm);
        return "redirect:/admin/account/form";
    }

    /**
     * アカウント登録の完了画面を表示
     * 不正遷移防止のためフォームデータの存在チェックを行う
     * 
     * @param adminEmployeeAccountForm 確認画面から送信されたフォームオブジェクト
     * @param model                    正常アクセスチェック用オブジェクト
     * @param redirectAttributes       不正遷移のエラーメッセージ格納オブジェクト
     * @return 完了画面のテンプレートパス、不正アクセス時は管理者トップ画面へのリダイレクトパス
     */
    @GetMapping("/complete")
    public String showComplete(AdminEmployeeAccountForm adminEmployeeAccountForm, RedirectAttributes redirectAttributes,
            Model model) {
        if (model.getAttribute("employee") == null) {

            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin";
        }

        return "/admin/account/complete";
    }
}
