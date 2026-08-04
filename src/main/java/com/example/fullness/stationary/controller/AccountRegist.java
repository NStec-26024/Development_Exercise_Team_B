package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AccountRegistFrom;

@Controller
@RequestMapping("/account")
@SessionAttributes("accountRegistForm")
public class AccountRegist {

    private final PasswordEncoder passwordEncoder;

    public AccountRegist(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @ModelAttribute
    public AccountRegistFrom setUpFrom() {
        return new AccountRegistFrom();
    }

    @GetMapping("/form")
    public String showInput(Model model) {
        if (!model.containsAttribute("accountRegistForm")) {
            model.addAttribute("accountRegistForm", new AccountRegistFrom());
        }
        return "admin/form";
    }

    @PostMapping("/form")
    public String checkInput(
            @Validated @ModelAttribute("accountRegistForm") AccountRegistFrom form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/form";
        } else {

        }

    }

}
