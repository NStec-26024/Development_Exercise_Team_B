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
@SessionAttributes("accountRegistForm")
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
        List<EmployeeAccount> empNameList = adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount();

        // if (!model.containsAttribute("empName")) {
        // model.addAttribute("accountRegistForm", new AccountRegistForm());

        if (empNameList.isEmpty()) {
            model.addAttribute("infoMessage", "アカウント登録可能な社員が存在しません");
        } else {
            model.addAttribute("empName", empNameList);
        }
        // }

        return "admin/employeeAccount/EmployeeAccountInsertInput";
    }

    // 「 社員情報の取得に失敗しました」はエラー画面での表示だと思うので多分消す(exeptionHandlerがやってくれる)
    @PostMapping("/postconfirm")
    public String checkInput(
            @Validated @ModelAttribute("accountRegistForm") AccountRegistForm accountRegistForm, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {

            // try {
            List<EmployeeAccount> empNameList = adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount();
            redirectAttributes.addFlashAttribute("empName", empNameList);

            redirectAttributes.addFlashAttribute("accountRegistForm", accountRegistForm);
            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + "accountRegistForm", result);
            return "redirect:form";

        } else {
            // 追加
            redirectAttributes.addFlashAttribute("accountRegistForm", accountRegistForm);
            // model.addAtributeする必要があるかわからないです
            return "redirect:/admin/account/confirm";
        }
    }

    /*
     * セッションの中身が空っぽか(formオブジェクトがnullかどうか)
     * 空っぽならエラーメッセージ準備→入力画面へリダイレクト
     * 中身あるなら確認画面表示
     */

    @GetMapping("/confirm")
    public String confirm(Model model, @ModelAttribute("accountRegistForm") AccountRegistForm form,
            RedirectAttributes redirectAttributes) {
        /*
         * null:データそのものがない
         * isEmpty:文字数０文字の空っぽ(String)
         */

        if (form.getEmployeeId() == null ||
                form.getName() == null || form.getName().isEmpty() ||
                form.getPassword() == null || form.getPassword().isEmpty()) {

            redirectAttributes.addFlashAttribute("infoMessage", "入力情報が見つかりません。再度入力してください");
            return "redirect:/admin/account/form";
        }

        return "admin/employeeAccount/EmployeeAccountInsertCheck";
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
    public String regist(@SessionAttribute("accountRegistForm") AccountRegistForm form, Model model,
            RedirectAttributes redirectAttributes) {
        // 重複チェック
        if (!adminEmployeeAccountService.selectAccountName(form.getName())) {
            List<EmployeeAccount> empNameList = adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount();
            redirectAttributes.addFlashAttribute("empName", empNameList);
            redirectAttributes.addFlashAttribute("infoMessage", "このアカウント名は既に使用されています");

            return "redirect:/admin/account/form";
        }

        // パスワードのハッシュ化
        String encodePassword = passwordEncoder.encode(form.getPassword());
        // DBへのインサート処理
        int accountId = adminEmployeeAccountService
                .insertEmployeeAccount(formToEntity.formToEntity(form, encodePassword));
        // 完了画面用の社員名を取得
        String name = adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccountId(accountId);
        redirectAttributes.addFlashAttribute("empName", name);

        return "redirect:/admin/account/complete";

    }

    /*
     * 【キャンセル処理】
     * ・セッションの破棄
     * ・メニュー画面へのリダイレクト
     */
    @GetMapping("/cancel")
    public String cancel(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/admin";
    }

    /*
     * セッション削除
     * 完了画面表示
     */
    @GetMapping("/complete")
    public String complete(SessionStatus sessionStatus, Model model, RedirectAttributes redirectAttributes) {
        // if (redirectAttributes.getAttribute())) {

        // model.addAttribute("infoMessage", "不正なアクセスです");
        // return "redirect:/admin/account/form";
        // }

        sessionStatus.setComplete();
        return "admin/employeeAccount/EmployeeAccountComplete";
    }

}
