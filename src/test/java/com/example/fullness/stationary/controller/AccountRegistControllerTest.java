package com.example.fullness.stationary.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

import ch.qos.logback.core.model.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
// 以下の4行を追加
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.api.Assertions;

@SpringBootTest
public class AccountRegistControllerTest {

    @Autowired
    AccountRegistController accountRegistController;
    MockMvc mockMvc;

    @Mock
    private AdminEmployeeAccountService adminEmployeeAccountService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountRegistController).build();
        EmployeeAccount employeeAccount = new EmployeeAccount();
    }

    @Test
    public void testShowInputOK() throws Exception {
        mockMvc.perform(get("/admin/account/form")) // GETリクエストを送信
                // 画面のURLにアクセスした際のHTTPステータスコードが200か
                .andExpect(status().isOk())
                // 画面のURLにアクセスした際のView名が正しいか
                .andExpect(view().name("admin/account/form"))
                // 画面のURLにアクセスした際にModelに"employeeName"が存在するか
                .andExpect(model().attributeExists("employeeName"));
    }

    @Test
    public void testCheckInputOK() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                .param("employeeId", "2")
                .param("name", "kawatajirou1002")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("redirect:/admin/account/confirm"))
                .andReturn();
        // ModelMap modelMap = mvcResult.getFlashMap();
        // EmployeeAccount result = (Employee)getFlashMap
        // Assertions.assertEquals(result, )

    }

}
