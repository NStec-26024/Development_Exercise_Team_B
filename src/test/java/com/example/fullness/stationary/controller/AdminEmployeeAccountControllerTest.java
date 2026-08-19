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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        EmployeeAccount inputAccount;
        Employee inputEmployee;
        AdminEmployeeAccountForm inputForm;

        int newInputEmployeeId = 2; // 社員ID
        String newInputEmployeeName = "川田次郎"; // 社員名
        String newInputAccountName = "kawatajirou1002"; // アカウント名
        String newInputPassword = "password"; // パスワード

        @BeforeEach
        public void setUp() {
                // mockMvc = MockMvcBuilders.standaloneSetup(accountRegistController).build();
                employeeAccount = new EmployeeAccount();
                employeeAccount.setEmployeeId(1);
                employeeAccount.setName("yamadatarou1001");

                // テストで入力する社員情報
                inputEmployee = new Employee();
                inputEmployee.setId(newInputEmployeeId);
                inputEmployee.setName(newInputEmployeeName);

                // テストで入力する社員アカウント情報
                inputAccount = new EmployeeAccount();
                inputAccount.setEmployeeId(newInputEmployeeId);
                inputAccount.setName(newInputAccountName);
                inputAccount.setPassword(newInputPassword);
                inputAccount.setEmployee(inputEmployee);

                inputForm = new AdminEmployeeAccountForm();

        }

        /*
         * serviceからの戻り値である社員情報が１件以上あるとき
         */
        @Test
        public void testShowInputOK_case1() throws Exception {

                when(adminEmployeeAccountService.getEmployeeNameWithEmployeeAccount())
                                .thenReturn(List.of(inputAccount));
                mockMvc.perform(get("/admin/account/form")) // GETリクエストを送信
                                // 画面のURLにアクセスした際のHTTPステータスコードが200か
                                .andExpect(status().isOk())
                                // 画面のURLにアクセスした際にModelに"employeeName"が存在するか
                                .andExpect(model().attributeExists("employeeName"))
                                // 画面のURLにアクセスした際のView名が正しいか
                                .andExpect(view().name("admin/account/form"));

                verify(adminEmployeeAccountService).getEmployeeNameWithEmployeeAccount();// いる？
        }

        /*
         * serviceからの戻り値である社員情報がないとき
         */
        @Test
        public void testShowInputNG_case2() throws Exception {
                when(adminEmployeeAccountService.getEmployeeNameWithEmployeeAccount())
                                .thenReturn(Collections.emptyList());
                mockMvc.perform(get("/admin/account/form"))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("errorMessages"))
                                .andExpect(view().name("admin/account/form"));

                verify(adminEmployeeAccountService).getEmployeeNameWithEmployeeAccount(); // いる？
        }

        /*
         * バリデーションOKのとき
         */
        @Test
        public void testValidateInputOK_case1() throws Exception {

                AdminEmployeeAccountForm inputForm = new AdminEmployeeAccountForm();
                inputForm.setEmployeeId(newInputEmployeeId);
                inputForm.setName(newInputAccountName);
                inputForm.setPassword(newInputPassword);

                // @UniqueAccountNameでアカウント名の重複無しであることにする
                when(adminEmployeeAccountService.getAccountName(newInputAccountName))
                                .thenReturn(true);
                // FormをEmployeeAccountに変換したことにする
                when(employeeAccountHelper.formToEntity(inputForm))
                                .thenReturn(inputAccount);
                // 社員IDからEmployeeAccountを取得したことにする
                when(adminEmployeeAccountService.getEmployeeAccountWithEmployeeId(newInputEmployeeId))
                                .thenReturn(inputAccount);

                // 入力画面からPOST(入力画面から送られてきたデータ)
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(inputAccount.getEmployeeId()))
                                .param("name", inputAccount.getName())
                                .param("password", inputAccount.getPassword()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(model().hasNoErrors())
                                .andExpect(redirectedUrl("/admin/account/confirm"))
                                .andExpect(flash().attributeExists("employee"))
                                .andReturn();
                // Flash Attributeを取得
                FlashMap flashMap = mvcResult.getFlashMap();
                EmployeeAccount resultEmployeeAccount = (EmployeeAccount) flashMap.get("employee");
                // Flash AttributeにEmployeeAccountが入っているか
                assertNotNull(resultEmployeeAccount);
                // employeeの中身が合っているか
                assertEquals(inputAccount.getEmployeeId(), resultEmployeeAccount.getEmployeeId());
                assertEquals(inputAccount.getName(), resultEmployeeAccount.getName());
                assertEquals(inputAccount.getPassword(), resultEmployeeAccount.getPassword());
                // Controllerからserviceにちゃんと値が渡されているか
                verify(adminEmployeeAccountService).getAccountName(newInputAccountName);
                verify(adminEmployeeAccountService).getEmployeeAccountWithEmployeeId(newInputEmployeeId);

                // ControllerからHelperにFormが渡されているか
                verify(employeeAccountHelper).formToEntity(inputForm);

        }

        /*
         * バリデーションエラーのとき
         * 社員名：未選択(=社員ID未選択)
         * アカウント名：正しく入力
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case2() throws Exception {

                MvcResult mvcResult = mockMvc
                                .perform(post("/admin/account/postconfirm")
                                                .param("name", newInputAccountName)
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
        public void testValidateInputNG_case3() throws Exception {

                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
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
        public void testValidateInputNG_case4() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
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
        public void testValidateInputNG_case5() throws Exception {

                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
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
        public void testValidateInputNG_case6() throws Exception {
                when(adminEmployeeAccountService.getAccountName(employeeAccount.getName()))
                                .thenReturn(false);
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

                verify(adminEmployeeAccountService).getAccountName(employeeAccount.getName());
        }

        /*
         * バリデーションエラーのとき
         * 社員名：選択
         * アカウント名：全角カナ
         * パスワード：正しく入力
         */
        @Test
        public void testValidateInputNG_case7() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
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
        public void testValidateInputNG_case8() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
                                .param("name", newInputAccountName))
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
        public void testValidateInputNG_case9() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
                                .param("name", newInputAccountName)
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
        public void testValidateInputNG_case10() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
                                .param("name", newInputAccountName)
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
        public void testValidateInputNG_case11() throws Exception {
                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
                                .param("name", newInputAccountName)
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
        public void testValidateInputNG_case12() throws Exception {

                MvcResult mvcResult = mockMvc.perform(post("/admin/account/postconfirm")
                                .param("employeeId", String.valueOf(newInputEmployeeId))
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
        public void testShowConfirmOK_case1() throws Exception {
                AdminEmployeeAccountForm inputForm = new AdminEmployeeAccountForm();
                inputForm.setEmployeeId(newInputEmployeeId);
                inputForm.setName(newInputAccountName);
                inputForm.setPassword(newInputPassword);

                Employee inputEmployee = new Employee();
                inputEmployee.setName(newInputEmployeeName);

                EmployeeAccount inputEmployeeAccount = new EmployeeAccount();
                inputEmployeeAccount.setEmployee(inputEmployee);

                mockMvc.perform(get("/admin/account/confirm")
                                .flashAttr("adminEmployeeAccountForm", inputForm)
                                .flashAttr("employee", inputEmployeeAccount))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/account/confirm"));

        }

        /*
         * 確認画面表示の異常系テスト
         * employeeId:null
         */
        @Test
        public void testShowConfirmNG_case2() throws Exception {
                AdminEmployeeAccountForm inputForm = new AdminEmployeeAccountForm();
                inputForm.setName(newInputAccountName);
                inputForm.setPassword(newInputPassword);
                mockMvc.perform(get("/admin/account/confirm")
                                .flashAttr("adminEmployeeAccountForm", inputForm))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));
        }

        /*
         * 確認画面表示の異常系テスト
         * name:null
         */
        @Test
        public void testShowConfirmNG_case3() throws Exception {
                AdminEmployeeAccountForm inputForm = new AdminEmployeeAccountForm();
                inputForm.setEmployeeId(newInputEmployeeId);
                inputForm.setPassword(newInputPassword);
                mockMvc.perform(get("/admin/account/confirm")
                                .flashAttr("adminEmployeeAccountForm", inputForm))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * 確認画面表示の異常系テスト
         * name:0文字
         */
        @Test
        public void testShowConfirmNG_case4() throws Exception {
                AdminEmployeeAccountForm inputForm = new AdminEmployeeAccountForm();
                inputForm.setEmployeeId(newInputEmployeeId);
                inputForm.setName("");
                inputForm.setPassword(newInputPassword);
                mockMvc.perform(get("/admin/account/confirm")
                                .flashAttr("adminEmployeeAccountForm", inputForm))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * 確認画面表示の異常系テスト
         * password:null
         */
        @Test
        public void testShowConfirmNG_case5() throws Exception {
                AdminEmployeeAccountForm inputForm = new AdminEmployeeAccountForm();
                inputForm.setEmployeeId(newInputEmployeeId);
                inputForm.setName(newInputAccountName);
                inputForm.setPassword(null);
                mockMvc.perform(get("/admin/account/confirm")
                                .flashAttr("adminEmployeeAccountForm", inputForm))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * 確認画面表示の異常系テスト
         * password:0文字
         */
        @Test
        public void testShowConfirmNG_case6() throws Exception {
                AdminEmployeeAccountForm inputForm = new AdminEmployeeAccountForm();
                inputForm.setEmployeeId(newInputEmployeeId);
                inputForm.setName(newInputAccountName);
                inputForm.setPassword("");

                mockMvc.perform(get("/admin/account/confirm")
                                .flashAttr("adminEmployeeAccountForm", inputForm))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "入力情報が見つかりません。再度入力してください"));

        }

        /*
         * アカウント登録処理の正常テスト
         * アカウント重複なし→DB登録
         */
        @Test
        public void testRegistOK_case1() throws Exception {
                inputForm.setEmployeeId(newInputEmployeeId);
                inputForm.setName(newInputAccountName);
                inputForm.setPassword(newInputPassword);

                when(employeeAccountHelper.formToEntity(inputForm))
                                .thenReturn(inputAccount, inputAccount);
                when(adminEmployeeAccountService.getAccountName(inputAccount.getName()))
                                .thenReturn(true);
                when(adminEmployeeAccountService.getNotHasEmployeeAccount(inputAccount.getEmployeeId()))
                                .thenReturn(true);
                when(adminEmployeeAccountService.addEmployeeAccount(inputAccount))
                                .thenReturn(inputAccount.getEmployeeId());
                when(adminEmployeeAccountService.getEmployeeNameWithEmployeeAccountId(inputAccount.getEmployeeId()))
                                .thenReturn(inputAccount);

                mockMvc.perform(post("/admin/account/postcomplete")
                                .param("name", inputAccount.getName())
                                .param("employeeId", String.valueOf(inputAccount.getEmployeeId()))
                                .param("password", inputAccount.getPassword()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/complete"))
                                .andExpect(flash().attribute("employee", inputAccount))
                                .andExpect(flash().attribute("accountName", inputAccount.getName()));
                // serviceが正しく呼び出されたか
                verify(adminEmployeeAccountService).getAccountName(inputAccount.getName());
                verify(adminEmployeeAccountService).getNotHasEmployeeAccount(inputAccount.getEmployeeId());
                verify(adminEmployeeAccountService).addEmployeeAccount(inputAccount);
                verify(adminEmployeeAccountService).getEmployeeNameWithEmployeeAccountId(inputAccount.getEmployeeId());

                // Helperが正しく呼び出されたか(2回とも)
                verify(employeeAccountHelper, times(2)).formToEntity(inputForm);

        }

        /*
         * アカウント登録異常系テスト
         * アカウント重複あり
         */
        @Test
        public void testRegistNG_case2() throws Exception {

                when(employeeAccountHelper.formToEntity(any(AdminEmployeeAccountForm.class)))
                                .thenReturn(employeeAccount);
                when(adminEmployeeAccountService.getAccountName(employeeAccount.getName()))
                                .thenReturn(false);

                mockMvc.perform(post("/admin/account/postcomplete")
                                .param("name", employeeAccount.getName())
                                .param("employeeId", String.valueOf(inputAccount.getEmployeeId()))
                                .param("password", inputAccount.getPassword()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attribute("errorMessages", "このアカウント名は既に使用されています"))
                                .andExpect(flash().attributeExists("adminEmployeeAccountForm"));

                verify(employeeAccountHelper).formToEntity(any(AdminEmployeeAccountForm.class));
                verify(adminEmployeeAccountService).getAccountName(employeeAccount.getName());

                verify(adminEmployeeAccountService, never()).addEmployeeAccount(inputAccount);
                verify(adminEmployeeAccountService, never())
                                .getEmployeeAccountWithEmployeeId(inputAccount.getEmployeeId());
        }

        /*
         * 戻るボタン正常テスト
         */
        @Test
        public void testBackOK_case1() throws Exception {
                mockMvc.perform(post("/admin/account/back")
                                .param("employeeId", String.valueOf(inputAccount.getEmployeeId()))
                                .param("name", inputAccount.getName())
                                .param("password", inputAccount.getPassword()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/account/form"))
                                .andExpect(flash().attributeExists("adminEmployeeAccountForm"));
        }

        /*
         * 完了画面表示の正常テスト
         */
        @Test
        public void testShowCompleteOK_case1() throws Exception {

                mockMvc.perform(get("/admin/account/complete")
                                .flashAttr("employee", inputAccount))
                                .andExpect(status().isOk())
                                .andExpect(view().name("/admin/account/complete"));
        }

        @Test
        public void testShowCompleteNG_case2() throws Exception {
                mockMvc.perform(get("/admin/account/complete"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin"))
                                .andExpect(flash().attribute("errorMessages", "不正なアクセスです"));

        }

}
