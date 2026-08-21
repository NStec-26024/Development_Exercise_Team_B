package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.service.AdminProductRegistrationService;

@Controller
@RequestMapping("/admin/product")
public class AdminProductAddComplete {

    @Autowired
    AdminProductRegistrationService adminProductRegistrationService;

    @ModelAttribute
    public AdminProductRegistrationForm setUpForm() {
        return new AdminProductRegistrationForm();
    }

}
