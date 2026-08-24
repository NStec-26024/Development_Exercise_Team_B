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
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.service.AdminProductRegistrationService;

@Controller
@RequestMapping("/admin/product")
public class AdminProductAddConfirmController {

    @Autowired
    AdminProductRegistrationService adminProductRegistrationService;

    @ModelAttribute
    public AdminProductRegistrationForm setUpForm() {
        return new AdminProductRegistrationForm();
    }

    @PostMapping("/postconfirm")
    public String validateInput(
            @Validated @ModelAttribute("AdminProductRegistrationForm") AdminProductRegistrationForm adminProductRegistrationForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("adminProductRegistrationForm", adminProductRegistrationForm);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + "adminProductRegistrationForm", result);
            return "redirect:/admin/product/add";

        } else {
            ProductCategory productCategory = adminProductRegistrationService
                    .getCategoryNameById(adminProductRegistrationForm.getProductCategoryId());
            redirectAttributes.addFlashAttribute("productCategory", productCategory);

            return "redirect:/admin/product/add/confirm";
        }

    }

    @GetMapping("/confirm")
    public String showConfirm(
            @ModelAttribute("adminProductRegistrationForm") AdminProductRegistrationForm adminProductRegistrationForm,
            RedirectAttributes redirectAttributes) {

        if (adminProductRegistrationForm.getProductName() == null
                || adminProductRegistrationForm.getProductName().isEmpty() ||
                adminProductRegistrationForm.getPrice() == null ||
                adminProductRegistrationForm.getQuantity() == null ||
                adminProductRegistrationForm.getProductCategoryId() == null) {

            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin/product/add/form";
        }

        return "/admin/product/add/confirm";
    }

    @PostMapping("/back")
    public String back(
            @ModelAttribute("adminProductRegistrationForm") AdminProductRegistrationForm adminProductRegistrationForm,
            RedirectAttributes redirectAttributes, Model model) {
        redirectAttributes.addFlashAttribute("adminProductRegistrationForm", adminProductRegistrationForm);
        return "/admin/product/add/form";

    }
}
