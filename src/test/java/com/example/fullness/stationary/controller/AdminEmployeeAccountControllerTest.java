package com.example.fullness.stationary.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.FlashMap;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AdminEmployeeAccountForm;
import com.example.fullness.stationary.helper.EmployeeAccountHelper;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

import ch.qos.logback.core.model.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class AdminEmployeeAccountControllerTest {

        @Autowired
        AdminEmployeeAccountController accountRegistController;
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
        public void testShowInputOK_case1() throws Exception {
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
        public void testShowInputNG_case2() throws Exception {
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
        public void testValidateInputOK_case3() throws Exception {
                EmployeeAccount inpuAccount = new EmployeeAccount();
                inpuAccount.setEmployeeId(2);
                inpuAccount.setName("kawatajirou1002");
                inpuAccount.setPassword("password");

                // どんな中身のFormでもinputAccountを返す
                when(employeeAccountHelper.formToEntity(any(AdminEmployeeAccountForm.class)))
                                .thenReturn(inpuAccount);
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "2")
                                .param("name", "kawatajirou1002")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/confirm"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                EmployeeAccount result = (EmployeeAccount) flashMap.get("employee");
                assertEquals("kawatajirou1002", result);
                assertEquals(2, result.getEmployeeId());
                assertEquals("password", result.getPassword());
        }

        /*
         * バリデーションエラーのとき
         * 社員名：未選択(=社員ID未選択)
         * アカウント名：正しく入力
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case4() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "kawatajirou1002")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：未選択(=社員ID未選択)
         * アカウント名：未入力
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case5() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：5文字未満
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case6() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：21文字以上
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case7() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：登録済みのアカウント名
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case8() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：全角カナ
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case9() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：正しく入力
         * パスワード：未入力
         */
        @Test
        public void testValidateInputNG_case10() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：正しく入力
         * パスワード：5文字未満
         */
        @Test
        public void testValidateInputNG_case11() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：正しく入力
         * パスワード：21文字以上
         */
        @Test
        public void testValidateInputNG_case12() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：正しく入力
         * パスワード：全角カナ
         */
        @Test
        public void testValidateInputNG_case13() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：全角カナ
         * パスワード：全角カナ
         */
        @Test
        public void testValidateInputNG_case14() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", "")
                                .param("name", "")
                                .param("password", "password"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }
}
