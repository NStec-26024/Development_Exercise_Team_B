package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/product/edit/complete")
public class AdminProductEditCompleteController {

    @GetMapping
    public String showCompletePage() {
        return "admin/product/edit_complete";
    }
}
