package com.example.fullness.stationary.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AccountRegistForm;
import com.example.fullness.stationary.helper.EmployeeAccountHelper;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

import ch.qos.logback.core.model.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
// 以下の4行を追加
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountRegistControllerTest {

    @Autowired
    AccountRegistController accountRegistController;
    MockMvc mockMvc;

    @MockitoBean
    AdminEmployeeAccountService adminEmployeeAccountService;

    @MockitoBean
    EmployeeAccountHelper employeeAccountHelper;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    EmployeeAccount employeeAccount;

    @BeforeEach
    public void setUp() {
        // mockMvc = MockMvcBuilders.standaloneSetup(accountRegistController).build();
        employeeAccount = new EmployeeAccount();
        employeeAccount.setEmployeeId(1);
        employeeAccount.setName("山田太郎");

    }

    /*
     * employee_Accountテーブルに社員存在しているとき
     */
    @Test
    public void testShowInputOK() throws Exception {
        when(adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount())
                .thenReturn(List.of(employeeAccount));
        mockMvc.perform(get("/admin/account/form")) // GETリクエストを送信
                // 画面のURLにアクセスした際のHTTPステータスコードが200か
                .andExpect(status().isOk())
                // 画面のURLにアクセスした際にModelに"employeeName"が存在するか
                .andExpect(model().attributeExists("employeeName"))
                // 画面のURLにアクセスした際のView名が正しいか
                .andExpect(view().name("admin/account/form"));
    }

    /*
     * employee_Accountテーブルに社員0のとき
     */
    @Test
    public void testShowInputNG() throws Exception {
        when(adminEmployeeAccountService.selectEmployeeNameWithEmployeeAccount())
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/admin/account/form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessages"))
                .andExpect(view().name("admin/account/form"));
    }

    /*
     * バリデーションOKのとき
     * 
     */
    @Test
    public void testCheckInputOK() throws Exception {
        EmployeeAccount inpuAccount = new EmployeeAccount();
        inpuAccount.setEmployeeId(2);
        inpuAccount.setName("kawatajirou1002");
        inpuAccount.setPassword("password");

        // どんな中身のFormでもinputAccountを返す
        when(employeeAccountHelper.formToEntity(any(AccountRegistForm.class)))
                .thenReturn(inpuAccount);
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
