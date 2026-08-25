package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.service.AdminProductCategoryService;

@Controller
@RequestMapping("admin/product/add")
public class AdminProductAddConfirmController {

    @Autowired
    AdminProductCategoryService adminProductCategoryService;

    @ModelAttribute("form")
    public AdminProductRegistrationForm setUpForm() {
        return new AdminProductRegistrationForm();
    }

    @PostMapping("/postconfirm")
    public String validateInput(
            @Validated @ModelAttribute("form") AdminProductRegistrationForm adminProductRegistrationForm,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("form", adminProductRegistrationForm);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX + "form", result);
            return "redirect:/admin/product/add";

        } else {
            ProductCategory productCategory = adminProductCategoryService
                    .getById(adminProductRegistrationForm.getCategoryId());
            adminProductRegistrationForm.setCategoryName(productCategory.getName());
            redirectAttributes.addFlashAttribute("form", adminProductRegistrationForm);

            return "redirect:/admin/product/add/confirm";
        }

    }

    @GetMapping("/confirm")
    public String showConfirm(
            @ModelAttribute("form") AdminProductRegistrationForm adminProductRegistrationForm,
            RedirectAttributes redirectAttributes) {

        if (adminProductRegistrationForm.getName() == null
                || adminProductRegistrationForm.getName().isEmpty() ||
                adminProductRegistrationForm.getPrice() == null ||
                adminProductRegistrationForm.getStock() == null ||
                adminProductRegistrationForm.getCategoryId() == null) {

            redirectAttributes.addFlashAttribute("errorMessages", "不正なアクセスです");
            return "redirect:/admin/product/add/";
        }

        return "/admin/product/add_confirm";
    }

    @PostMapping("/back")
    public String back(
            AdminProductRegistrationForm adminProductRegistrationForm,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("form", adminProductRegistrationForm);
        return "redirect:/admin/product/add";

    }
}
