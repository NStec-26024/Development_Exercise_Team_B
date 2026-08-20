package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fullness.stationary.dto.AdminProductSessionData;
import com.example.fullness.stationary.form.AdminProductForm;
import com.example.fullness.stationary.service.SessionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/product/edit/confirm")
public class AdminProductEditConfirmController {

    @Autowired
    private SessionService sessionService;

    @GetMapping
    public String showConfirmPage(HttpSession session, Model model) {

        AdminProductSessionData data = sessionService.get(session);
        if (data == null) {
            return "redirect:/admin/product";
        }

        AdminProductForm form = new AdminProductForm();
        form.setId(data.targetId);
        form.setName(data.name);
        form.setPrice(String.valueOf(data.price));
        form.setStock(String.valueOf(data.stock));
        form.setCategoryId(data.categoryId);
        form.setImagePath(data.existingImageUrl);

        model.addAttribute("form", form);

        return "admin/product/edit_confirm";
    }

    @PostMapping
    public String executeUpdate(HttpSession session) {

        AdminProductSessionData data = sessionService.get(session);
        if (data == null) {
            return "redirect:/admin/product";
        }

        return "redirect:/admin/product/edit/complete";
    }
}
