package com.example.fullness.stationary.controlller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountRegistInput {

    @GetMapping("/form")
    public String accountForm() {
        return "/form";
    }

}
