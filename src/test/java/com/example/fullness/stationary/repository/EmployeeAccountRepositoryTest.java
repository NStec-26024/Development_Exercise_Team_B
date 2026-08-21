package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import com.example.fullness.stationary.entity.EmployeeAccount;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = { "/sql/schema.sql", "/sql/data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmployeeAccountRepositoryTest {

    @Autowired
    EmployeeAccountRepository adminEmployeeAccountRepository;

    @Test
    public void InsertEmployeeAccountTest_case1() {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        employeeAccount.setEmployeeId(2);
        employeeAccount.setName("kawatajirou1002");
        employeeAccount.setPassword("password");
        adminEmployeeAccountRepository.insertEmployeeAccount(employeeAccount);
        int actual = employeeAccount.getId();
        assertEquals(2, actual);
    }

    @Test
    public void selectEmployeeNameWithEmployeeAccountTest_OK_case2() {
        List<EmployeeAccount> employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
        assertEquals("川田次郎", employeeAccount.get(0).getEmployee().getName());
        assertEquals(2, employeeAccount.get(0).getEmployee().getId());
        assertEquals("海田三郎", employeeAccount.get(1).getEmployee().getName());
        assertEquals(3, employeeAccount.get(1).getEmployee().getId());

    }

    @Test
    @Sql(scripts = { "/sql/schema.sql" })
    public void selectEmployeeNameWithEmployeeAccountTest_null_case3() {
        List<EmployeeAccount> employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
        assertTrue(employeeAccount.isEmpty());

    }

    @Test
    public void selectNotHasEmployeeAccountTest_OK_case4() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectNotHasEmployeeAccount(2);
        assertEquals("川田次郎", employeeAccount.getEmployee().getName());
        assertEquals(2, employeeAccount.getEmployee().getId());

    }

    @Test
    public void selectNotHasEmployeeAccountTest_Ok_case5() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectNotHasEmployeeAccount(3);
        assertEquals("海田三郎", employeeAccount.getEmployee().getName());
        assertEquals(3, employeeAccount.getEmployee().getId());

    }

    @Test
    public void selectNotHasEmployeeAccountTest_null_case6() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectNotHasEmployeeAccount(1);
        assertEquals(null, employeeAccount);

    }

    @Test
    public void selectAccountNameTest_OK_case7() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectAccountName("yamadatarou1001");
        assertEquals(1, employeeAccount.getId());
        assertEquals("yamadatarou1001", employeeAccount.getName());
        assertEquals("$2a$10$nOMKs31N.scADyHLn1KfyOagrb52vXDEokqGp4MueMbqAam1iaS1e", employeeAccount.getPassword());
        assertEquals(1, employeeAccount.getEmployeeId());

    }

    @Test
    public void selectAccountNameTest_null_case8() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectAccountName("a");
        assertEquals(null, employeeAccount);

    }

    @Test
    public void selectEmployeeNameWithEmployeeAccountIdTest_OK_case9() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(1);
        assertEquals("yamadatarou1001", employeeAccount.getName());
        assertEquals("山田太郎", employeeAccount.getEmployee().getName());

    }

    @Test
    public void selectEmployeeNameWithEmployeeAccountIdTest_null_case10() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(2);
        assertEquals(null, employeeAccount);

    }

    // ============================================================
    // 正常系：指定したユーザー名で DB から正しい情報を取得できること
    // ============================================================
    @Test
    void findByNameTest_case01_Ok() {

        // 実行
        EmployeeAccount result = adminEmployeeAccountRepository.findByName("yamadatarou1001");

        // 検証：null ではない
        assertThat(result).isNotNull();

        // 検証：id / employeeId が正しい
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getEmployeeId()).isEqualTo(1);

        // 検証：bcrypt パスワードが正しく取得できている
        assertThat(result.getPassword())
                .isEqualTo("$2a$10$nOMKs31N.scADyHLn1KfyOagrb52vXDEokqGp4MueMbqAam1iaS1e");
    }

    // ============================================================
    // 異常系：存在しないユーザー名の場合 null が返ること
    // ============================================================
    @Test
    void findByNameTest_case02_Ok() {

        // 実行
        EmployeeAccount result = adminEmployeeAccountRepository.findByName("unknown");

        // 検証：null が返る
        assertThat(result).isNull();

    }

}
