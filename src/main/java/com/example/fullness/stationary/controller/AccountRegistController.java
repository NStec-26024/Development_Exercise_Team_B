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

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AccountRegistFrom;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

@Controller
@RequestMapping("/admin/account")
@SessionAttributes("accountRegistForm")
public class AccountRegistController {

    @Autowired
    AdminEmployeeAccountService adminEmployeeAccountService;

    @ModelAttribute
    public AccountRegistFrom setUpFrom() {
        return new AccountRegistFrom();
    }

    @GetMapping("/form")
    public String showInput(Model model) {
        if (!model.containsAttribute("accountRegistForm")) {
            model.addAttribute("accountRegistForm", new AccountRegistFrom());
        }

        try {
            List<Employee> employee = adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount();

            if (employee.isEmpty()) {
                model.addAttribute("infoMessage", "アカウント登録可能な社員が存在しません");
            } else {
                model.addAttribute("employee", employee);
            }
        } catch (Exception e) {
            model.addAttribute("errMessage", "社員情報の取得に失敗しました");
        }
        return "admin/employeeAccount/EmployeeAccountInsertInput";
    }

    @PostMapping("/postconfirm")
    public String checkInput(
            @Validated @ModelAttribute("accountRegistForm") AccountRegistFrom form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            try {
                List<Employee> employee = adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount();
                model.addAttribute("employee", employee);

            } catch (Exception e) {
                model.addAttribute("errMessage", "社員情報の取得に失敗しました");
            }

            return "admin/employeeAccount/EmployeeAccountInsertInput";
        } else {
            return "redirect;admin/employeeAccount/confirm";
        }
    }

    /*
     * セッションの中身が空っぽか(formオブジェクトがnullかどうか)
     * 空っぽならエラーメッセージ準備→入力画面へリダイレクト
     * 中身あるなら確認画面表示
     */

    @GetMapping("/getconfirm")
    public String confirm(Model model, @ModelAttribute("accountRegistForm") AccountRegistFrom form) {
        /*
         * null:データそのものがない
         * isEmpty:文字数０文字の空っぽ(String)
         */

        if (form.getEmployeeId() == null ||
                form.getName() == null || form.getName().isEmpty() ||
                form.getPassword() == null || form.getPassword().isEmpty()) {

            model.addAttribute("message", "入力情報が見つかりません。再度入力してください");
            return "redirect:redirect:/admin/form";
        }

        return "admin/employeeAccount/confituhrm";
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
    @PostMapping("/confirm")
    public String regist(@ModelAttribute("accountRegistForm") Model model, AccountRegistFrom form,
            RedirectAttributes redirectAttributes) {
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*
     * 【キャンセル処理】
     * ・セッションの破棄
     * ・メニュー画面へのリダイレクト
     */

    @GetMapping("/")
    /*
     * セッション削除
     * 完了画面表示
     */
    @GetMapping("/complete")
    public String complete(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "admin/employeeAccount/complete";
    }

}
