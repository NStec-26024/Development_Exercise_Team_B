package com.example.fullness.stationary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fullness.stationary.dto.AdminProductSessionData;
import com.example.fullness.stationary.entity.Product;
import com.example.fullness.stationary.form.AdminProductForm;
import com.example.fullness.stationary.service.AdminProductQueryService;
import com.example.fullness.stationary.service.SessionService;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping("/admin/product/edit")
public class AdminProductEditFormController {

    @Autowired
    private AdminProductQueryService productQueryService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SessionService sessionService;

    @GetMapping("/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Product product = productQueryService.getProductById(id);
        if (product == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("com.example.fullness.stationary.product.not_found", null, Locale.JAPAN));
            return "redirect:/admin/product";
        }

        AdminProductForm form = new AdminProductForm();
        form.setId(id);

        AdminProductSessionData data = sessionService.get(session);
        if (data != null && id.equals(data.targetId)) {
            form.setName(data.name);
            form.setPrice(String.valueOf(data.price));
            form.setStock(String.valueOf(data.stock));
            form.setCategoryId(data.categoryId);
            form.setImagePath(data.existingImageUrl);
        } else {
            form.setName(product.getName());
            form.setPrice(String.valueOf(product.getPrice()));
            int stock = product.getProductStock() != null ? product.getProductStock().getQuantity() : 0;
            form.setStock(String.valueOf(stock));
            form.setCategoryId(product.getCategoryId());
            form.setImagePath(product.getImageUrl());
        }

        model.addAttribute("form", form);
        model.addAttribute("categories", productQueryService.getAllCategories());
        model.addAttribute("productId", id);
        model.addAttribute("currentImageUrl", product.getImageUrl());

        return "admin/product/edit_form";
    }

    @PostMapping("/{id}")
    public String submitEditForm(
            @PathVariable Integer id,
            @ModelAttribute("form") AdminProductForm form,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Product current = productQueryService.getProductById(id);

        if (current == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    messageSource.getMessage("com.example.fullness.stationary.product.not_found", null, Locale.JAPAN));
            return "redirect:/admin/product";
        }
        System.out.println("DEBUG existingImageUrl = " + current.getImageUrl());

        int price = 0;
        int stock = 0;

        try {
            if (form.getPrice() != null && !form.getPrice().isBlank()) {
                price = Integer.parseInt(form.getPrice());
            }
        } catch (Exception ignored) {
        }

        try {
            if (form.getStock() != null && !form.getStock().isBlank()) {
                stock = Integer.parseInt(form.getStock());
            }
        } catch (Exception ignored) {
        }

        AdminProductSessionData data = new AdminProductSessionData();
        data.targetId = id;
        data.name = form.getName(); // 空でも OK
        data.price = price; // 空なら 0
        data.stock = stock; // 空なら 0
        data.categoryId = form.getCategoryId(); // null でも OK（後続で使うなら注意）

        // 画像処理（空なら何もしない）
        if (form.getImage() != null && !form.getImage().isEmpty()) {
            try {
                data.imageBytes = form.getImage().getBytes();
                data.imageFileName = form.getImage().getOriginalFilename();
            } catch (IOException e) {
                // 画像読み込み失敗しても遷移は続行
            }
        }

        data.existingImageUrl = current.getImageUrl();

        sessionService.save(session, data);

        return "redirect:/admin/product/edit/confirm";
    }

    // @PostMapping("/{id}")
    // public String submitEditForm(
    // @PathVariable Integer id,
    // @Valid @ModelAttribute("form") AdminProductForm form,
    // BindingResult bindingResult,
    // HttpSession session,
    // Model model,
    // RedirectAttributes redirectAttributes) {

    // Product current = productQueryService.getProductById(id);
    // if (current == null) {
    // redirectAttributes.addFlashAttribute("errorMessage",
    // messageSource.getMessage("com.example.fullness.stationary.product.not_found",
    // null, Locale.JAPAN));
    // return "redirect:/admin/product";
    // }

    // if (bindingResult.hasErrors()) {
    // model.addAttribute("categories", productQueryService.getAllCategories());
    // model.addAttribute("productId", id);
    // model.addAttribute("currentImageUrl", current.getImageUrl());
    // return "admin/product/edit_form";
    // }

    // AdminProductSessionData data = new AdminProductSessionData();
    // data.targetId = id;
    // data.name = form.getName();
    // data.price = Integer.parseInt(form.getPrice());
    // data.stock = Integer.parseInt(form.getStock());
    // data.categoryId = form.getCategoryId();

    // if (form.getImage() != null && !form.getImage().isEmpty()) {
    // try {
    // data.imageBytes = form.getImage().getBytes();
    // } catch (IOException e) {
    // throw new AdminBusinessException("画像の読み込みに失敗しました");
    // }
    // data.imageFileName = form.getImage().getOriginalFilename();
    // }

    // data.existingImageUrl = current.getImageUrl();

    // sessionService.save(session, data);

    // return "redirect:/admin/product/edit/confirm";
    // }
}
