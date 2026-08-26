package com.example.fullness.stationary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.entity.ProductCategory;
import com.example.fullness.stationary.exception.AdminBusinessException;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;
import com.example.fullness.stationary.service.AdminProductService;

@Controller
@RequestMapping("admin/product")
public class AdminProductAddFormController {

    @Autowired
    AdminProductService adminProductService;

    @ModelAttribute("form")
    public AdminProductRegistrationForm setUpForm() {
        return new AdminProductRegistrationForm();
    }

    @GetMapping("/add")

    public String showInput(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<ProductCategory> productCategoryList = adminProductService.getAllCategories();
            if (productCategoryList.isEmpty()) {

                model.addAttribute("errorMessages", "カテゴリ情報の取得に失敗しました");
            } else {
                model.addAttribute("categories", productCategoryList);
            }
            return "admin/product/add_form";

        } catch (Exception e) {
            throw new AdminBusinessException("カテゴリ情報の取得に失敗しました");
        }
    }
}
