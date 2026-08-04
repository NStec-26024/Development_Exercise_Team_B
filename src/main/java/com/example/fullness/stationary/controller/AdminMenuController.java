package com.example.fullness.stationary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminMenuController {

    @GetMapping
    public String adminRoot() {
        return "admin/menu";
    }
}
