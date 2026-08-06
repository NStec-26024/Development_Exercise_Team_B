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
import com.example.fullness.stationary.form.AccountRegistForm;
import com.example.fullness.stationary.helper.FormToEntity;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

@Controller
@RequestMapping("/admin/account")
// @SessionAttributes("accountRegistForm")
public class AccountRegistController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminEmployeeAccountService adminEmployeeAccountService;

    @Autowired
    FormToEntity formToEntity;

    @ModelAttribute
    public AccountRegistForm setUpForm() {
        return new AccountRegistForm();
    }

    @GetMapping("/form")
    public String showInput(Model model) {
        List<EmployeeAccount> employeeNameList = adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount();

        if (employeeNameList.isEmpty()) {
            model.addAttribute("errorMessages", "アカウント登録可能な社員が存在しません");
        } else {
            model.addAttribute("employeeName", employeeNameList);
        }

        return "admin/account/form";
    }

    // 「 社員情報の取得に失敗しました」はエラー画面での表示だと思うので多分消す(exeptionHandlerがやってくれる)
    @PostMapping("/postconfirm")
    public String checkInput(
            @Validated @ModelAttribute("accountRegistForm") AccountRegistForm accountRegistForm, BindingResult result,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("accountRegistForm", accountRegistForm);
        if (result.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + "accountRegistForm", result);
            return "redirect:/admin/account/form";

        } else {

            EmployeeAccount employeeAccount = formToEntity.formToEntity(accountRegistForm);
            employeeAccount = adminEmployeeAccountService
                    .selectNotHasEmployeeAccount(employeeAccount.getEmployeeId());
            redirectAttributes.addFlashAttribute("employee", employeeAccount);

            return "redirect:/admin/account/confirm";
        }
    }

    /*
     * セッションの中身が空っぽか(formオブジェクトがnullかどうか)
     * 空っぽならエラーメッセージ準備→入力画面へリダイレクト
     * 中身あるなら確認画面表示
     */

    @GetMapping("/confirm")
    public String confirm(@ModelAttribute("accountRegistForm") AccountRegistForm accountRegistForm,
            RedirectAttributes redirectAttributes) {
        /*
         * null:データそのものがない
         * isEmpty:文字数０文字の空っぽ(String)
         */

        if (accountRegistForm.getEmployeeId() == null ||
                accountRegistForm.getName() == null || accountRegistForm.getName().isEmpty() ||
                accountRegistForm.getPassword() == null || accountRegistForm.getPassword().isEmpty()) {

            redirectAttributes.addFlashAttribute("errorMessages", "入力情報が見つかりません。再度入力してください");
            return "redirect:/admin/account/form";
        }

        return "admin/account/confirm";
    }

    /*
     * ・アカウント名が重複しているか(DBのemployee_accountテーブルにおなじアカウント名があるか問合せ)
     * serviceクラスにメソッドがあるか
     * DBに存在(重複)：エラーメッセージ準備→入力画面にリダイレクト
     * ・パスワードのハッシュ化
     * ハッシュ化→formオブジェクトのパスワード欄に上書き
     * ・DBへの書き込み
     * xmlのinsert文：登録OK→完了画面へ
     * ・登録出来たら、完了画面にリダイレクト
     * 完了画面での二重登録(browserの更新ボタンによる)バグを防ぐために
     * ・登録失敗/DB登録エラー
     * 例外処理でメッセージ設定、Javaのlogにエラーを記録
     */

    // 登録処理失敗はたぶん消す(ExeptionHandlerがやってくれる)
    @PostMapping("/postcomplete")
    public String regist(AccountRegistForm accountRegistForm,
            RedirectAttributes redirectAttributes) {
        // 重複チェック
        EmployeeAccount employeeAccount = formToEntity.formToEntity(accountRegistForm);
        if (!adminEmployeeAccountService.selectAccountName(employeeAccount.getName())) {

            redirectAttributes.addFlashAttribute("errorMessages", "このアカウント名は既に使用されています");
            redirectAttributes.addFlashAttribute("accountRegistForm", accountRegistForm);
            return "redirect:/admin/account/form";
        }

        // パスワードのハッシュ化
        String encodePassword = passwordEncoder.encode(accountRegistForm.getPassword());
        // DBへのインサート処理
        int accountId = adminEmployeeAccountService
                .insertEmployeeAccount(formToEntity.formToEntity(accountRegistForm, encodePassword));
        employeeAccount = adminEmployeeAccountService
                .selectEmployeeNameWithEmployeeAccountId(accountId);
        redirectAttributes.addFlashAttribute("employee", employeeAccount);

        redirectAttributes.addFlashAttribute("accountName", accountRegistForm.getName());
        return "redirect:/admin/account/complete";

    }

    /*
     * 【キャンセル処理】
     * ・セッションの破棄
     * ・メニュー画面へのリダイレクト
     */
    @PostMapping("/back")
    public String back(@ModelAttribute("accountRegistForm") AccountRegistForm accountRegistForm,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("accountRegistForm", accountRegistForm);
        return "redirect:/admin/account/form";
    }

    /*
     * セッション削除
     * 完了画面表示
     */
    @GetMapping("/complete")
    public String complete(AccountRegistForm accountRegistForm, RedirectAttributes redirectAttributes, Model model) {
        if (model.getAttribute("employee") == null) {

            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin";
        }

        return "admin/account/complete";
    }
}
