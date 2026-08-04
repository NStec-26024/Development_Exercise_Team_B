package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fullness.stationary.form.LoginForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) { // 💡 error パラメータを削除

        HttpSession session = request.getSession();
        String errorMsg = (String) session.getAttribute("LOGIN_ERROR_MSG");

        if (errorMsg != null) {
            model.addAttribute("securityErrorMessage", errorMsg);
            session.removeAttribute("LOGIN_ERROR_MSG");
        }

        model.addAttribute("loginForm", new LoginForm());
        return "admin/login";
    }

    @PostMapping("/login")
    public String loginProcess(@Validated @ModelAttribute LoginForm loginForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("loginForm", loginForm);
            return "admin/login";
        }
        return "forward:/admin/login-auth";
    }
}
