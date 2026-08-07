package com.example.fullness.stationary.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.FlashMap;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.form.AdminEmployeeAccountForm;
import com.example.fullness.stationary.helper.EmployeeAccountHelper;
import com.example.fullness.stationary.service.AdminEmployeeAccountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminEmployeeAccountControllerTest {

        @Autowired
        AdminEmployeeAccountController accountRegistController;

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        AdminEmployeeAccountService adminEmployeeAccountService;

        @MockitoBean
        EmployeeAccountHelper employeeAccountHelper;

        @MockitoBean
        PasswordEncoder passwordEncoder;

        EmployeeAccount employeeAccount;
        int newInputEmployeeId = 2;
        String newInputName = "kawatajirou1002";
        String newInputPassword = "password";

        @BeforeEach
        public void setUp() {
                // mockMvc = MockMvcBuilders.standaloneSetup(accountRegistController).build();
                employeeAccount = new EmployeeAccount();
                employeeAccount.setEmployeeId(1);
                employeeAccount.setName("yamadatarou1001");

        }

        /*
         * employee_Accountテーブルに社員存在しているとき
         */
        @Test
        public void testShowInputOK_case1() throws Exception {
                when(adminEmployeeAccountService.getEmployeeNameWithEmployeeAccount())
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
                when(adminEmployeeAccountService.getEmployeeNameWithEmployeeAccount())
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
                inpuAccount.setEmployeeId(newInputEmployeeId);
                inpuAccount.setName(newInputName);
                inpuAccount.setPassword(newInputPassword);

                // どんな中身のFormでもinputAccountを返す
                when(employeeAccountHelper.formToEntity(any(AdminEmployeeAccountForm.class)))
                                .thenReturn(inpuAccount);
                when(adminEmployeeAccountService.getEmployeeAccountWithEmployeeId(anyInt()))
                                .thenReturn(inpuAccount);
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
                                .param("name", newInputName)
                                .param("password", newInputPassword))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/confirm"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                EmployeeAccount result = (EmployeeAccount) flashMap.get("employee");
                assertNotNull(result);
                assertEquals(newInputName, result.getName());
                assertEquals(newInputEmployeeId, result.getEmployeeId());
                assertEquals(newInputPassword, result.getPassword());
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
                                .param("name", newInputName)
                                .param("password", newInputPassword))
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
                                .param("password", newInputPassword))
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
                                .param("employeeId", "2")
                                .param("name", "acco")
                                .param("password", newInputPassword))
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
                                .param("employeeId", "2")
                                .param("name", "accountaccountaccount")
                                .param("password", newInputPassword))
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
                                .param("employeeId", String.valueOf(newInputEmployeeId))
                                .param("name", employeeAccount.getName())
                                .param("password", newInputPassword))
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
                                .param("employeeId", "2")
                                .param("name", "アカウント")
                                .param("password", newInputPassword))
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
                                .param("employeeId", "2")
                                .param("name", newInputName)
                                .param("password", ""))
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
                                .param("employeeId", "2")
                                .param("name", newInputName)
                                .param("password", "pass"))
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
                                .param("employeeId", "2")
                                .param("name", newInputName)
                                .param("password", "passwordpasswordpassword"))
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
                                .param("employeeId", "2")
                                .param("name", newInputName)
                                .param("password", "パスワード"))
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
                                .param("employeeId", "2")
                                .param("name", "アカウント")
                                .param("password", "パスワード"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andReturn();
                FlashMap flashMap = mvcResult.getFlashMap();
                String errMSG = BindingResult.MODEL_KEY_PREFIX + "adminEmployeeAccountForm";
                assertNotNull(flashMap.get(errMSG));
        }

        /*
         * 確認画面表示の正常系テスト
         */
        @Test
        public void testShowConfirmOK_case15() throws Exception {
                AdminEmployeeAccountForm adminEmployeeAccountForm = new AdminEmployeeAccountForm();
                adminEmployeeAccountForm.setEmployeeId(newInputEmployeeId);
                adminEmployeeAccountForm.setName(newInputName);
                adminEmployeeAccountForm.setPassword(newInputPassword);

                mockMvc.perform(get("/admin/account/confirm")
                                .flashAttr("adminEmployeeAccountForm", adminEmployeeAccountForm))
                                .andExpect(status().isOk())
                                .andExpect(view().name("/admin/account/confirm"));
        }

        /*
         * 確認画面表示の異常系テスト
         * employeeId:null
         */
        @Test
        public void testShowConfirmNG_case16() throws Exception {
                mockMvc.perform(get("/admin/account/confirm")
                                .param("name", newInputName)
                                .param("password", newInputPassword))
                                .andExpect(status().isOk())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));
        }

        /*
         * 確認画面表示の異常系テスト
         * name:null
         */
        @Test
        public void testShowConfirmNG_case17() throws Exception {
                mockMvc.perform(get("/admin/account/confirm")
                                .param("employeeId", "2")
                                .param("password", newInputPassword))
                                .andExpect(status().isOk())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * 確認画面表示の異常系テスト
         * name:0文字
         */
        @Test
        public void testShowConfirmNG_case18() throws Exception {
                mockMvc.perform(get("/admin/account/confirm")
                                .param("employeeId", "2")
                                .param("name", "")
                                .param("password", newInputPassword))
                                .andExpect(status().isOk())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * 確認画面表示の異常系テスト
         * password:null
         */
        @Test
        public void testShowConfirmNG_case19() throws Exception {
                mockMvc.perform(get("/admin/account/confirm")
                                .param("employeeId", "2")
                                .param("name", newInputName))
                                .andExpect(status().isOk())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * 確認画面表示の異常系テスト
         * password:0文字
         */
        @Test
        public void testShowConfirmNG_case20() throws Exception {
                mockMvc.perform(get("/admin/account/confirm")
                                .param("employeeId", "2")
                                .param("name", newInputName)
                                .param("password", ""))
                                .andExpect(status().isOk())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * アカウント登録処理の正常テスト
         * アカウント重複なし→DB登録
         */
        @Test
        public void testRegistOK_case21() throws Exception {
                when(employeeAccountHelper.formToEntity(any(AdminEmployeeAccountForm.class)))
                                .thenReturn(employeeAccount, employeeAccount);
                when(adminEmployeeAccountService.getAccountName("testUSer")).thenReturn(true);
                when(adminEmployeeAccountService.addEmployeeAccount(any(EmployeeAccount.class)))
                                .thenReturn(newInputEmployeeId);
                when(adminEmployeeAccountService.getEmployeeNameWithEmployeeAccountId(anyInt()))
                                .thenReturn(employeeAccount);

                mockMvc.perform(post("/admin/account/postcomplete")
                                .param("name", "testUSer")
                                .param("employeeId", "2")
                                .param("password", newInputPassword))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/complete"))
                                .andExpect(flash().attribute("employee", employeeAccount))
                                .andExpect(flash().attribute("accountName", newInputName));
                // // serviceが正しく呼び出されたか
                // verify(adminEmployeeAccountService).addEmployeeAccount(any(EmployeeAccount.class));
                // verify(adminEmployeeAccountService).getEmployeeNameWithEmployeeAccountId(anyInt());

        }

        /*
         * アカウント登録異常系テスト
         * アカウント重複あり
         */
        @Test
        public void testRegistNG_case22() throws Exception {
                when(employeeAccountHelper.formToEntity(any(AdminEmployeeAccountForm.class)))
                                .thenReturn(employeeAccount);
                when(adminEmployeeAccountService.getAccountName(employeeAccount.getName())).thenReturn(false);

                mockMvc.perform(post("/admin/account/postcomplete")
                                .param("name", employeeAccount.getName())
                                .param("employeeId", "2")
                                .param("password", newInputPassword))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "このアカウント名は既に使用されています"))
                                .andExpect(flash().attributeExists("adminEmployeeAccountForm"));

                verify(adminEmployeeAccountService, never()).addEmployeeAccount(any(EmployeeAccount.class));
        }

        /*
         * 戻るボタン正常テスト
         */
        @Test
        public void testBackOK_case23() throws Exception {
                mockMvc.perform(post("/admin/account/back")
                                .param("employeeId", "2")
                                .param("name", newInputName)
                                .param("password", newInputPassword))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attributeExists("adminEmployeeAccountForm"));
        }

        /*
         * 完了画面表示の正常テスト
         */
        @Test
        public void testShowCompleteOK_case24() throws Exception {
                EmployeeAccount employeeAccount = new EmployeeAccount();

                mockMvc.perform(get("/admin/account/complete")
                                .flashAttr("employee", employeeAccount))
                                .andExpect(status().isOk())
                                .andExpect(view().name("/admin/account/complete"));
        }

        @Test
        public void testShowCompleteNG_case25() throws Exception {
                mockMvc.perform(get("/admin/account/complete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin"))
                                .andExpect(flash().attribute("errorMessages", "不正なアクセスです"));

        }

}
