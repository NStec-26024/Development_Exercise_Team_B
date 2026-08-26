package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.service.AdminProductRegistrationService;

@Controller
@RequestMapping("/admin/product/add")
public class AdminProductAddCompleteController {

    @Autowired
    AdminProductRegistrationService adminProductRegistrationService;

    @ModelAttribute("form")
    public AdminProductRegistrationForm setUpForm() {
        return new AdminProductRegistrationForm();
    }

    @PostMapping("/postcomplete")
    public String regist(@RequestParam("action") String action,
            AdminProductRegistrationForm adminProductRegistrationForm,
            RedirectAttributes redirectAttributes) {
        if (adminProductRegistrationForm.getName() == null
                || adminProductRegistrationForm.getName().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin/product/add";
        }

        // --- 戻るボタン ---
        if ("back".equals(action)) {
            redirectAttributes.addFlashAttribute("form", adminProductRegistrationForm);
            return "redirect:/admin/product/add";
        }

        adminProductRegistrationService.addProduct(adminProductRegistrationForm);
        redirectAttributes.addFlashAttribute("form", adminProductRegistrationForm);
        return "redirect:/admin/product/add/complete";

    }

    @GetMapping("complete")
    public String showComplete(@ModelAttribute("form") AdminProductRegistrationForm form,
            RedirectAttributes redirectAttributes) {

        if (form == null || form.getName() == null) {
            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin";
        }

        return "/admin/product/add_complete";

    }

}
