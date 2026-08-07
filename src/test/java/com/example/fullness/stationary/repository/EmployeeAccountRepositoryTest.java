package com.example.fullness.stationary.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        employeeAccount.setName("asdfgh");
        employeeAccount.setPassword("sadfghgf");
        adminEmployeeAccountRepository.insertEmployeeAccount(employeeAccount);
        int expected = employeeAccount.getId();
        assertEquals(expected, 2);
    }

    @Test
    public void selectEmployeeNameWithEmployeeAccountTest_OK_case2() {
        List<EmployeeAccount> employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
        assertEquals(employeeAccount.get(0).getEmployee().getName(), "川田次郎");
        assertEquals(employeeAccount.get(0).getEmployee().getId(), 2);
        assertEquals(employeeAccount.get(1).getEmployee().getName(), "海田三郎");
        assertEquals(employeeAccount.get(1).getEmployee().getId(), 3);

    }

    @Test
    @Sql(scripts = { "/sql/schema.sql" })
    public void selectEmployeeNameWithEmployeeAccountTest_null_case3() {
        List<EmployeeAccount> employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccount();
        assertEquals(employeeAccount, null);

    }

    @Test
    public void selectNotHasEmployeeAccountTest_OK_case4() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectNotHasEmployeeAccount(2);
        assertEquals(employeeAccount.getEmployee().getName(), "川田次郎");
        assertEquals(employeeAccount.getEmployee().getId(), 2);

    }

    @Test
    public void selectNotHasEmployeeAccountTest_Ok_case5() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectNotHasEmployeeAccount(3);
        assertEquals(employeeAccount.getEmployee().getName(), "海田三郎");
        assertEquals(employeeAccount.getEmployee().getId(), 3);

    }

    @Test
    public void selectNotHasEmployeeAccountTest_null_case6() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectNotHasEmployeeAccount(1);
        assertEquals(employeeAccount, null);

    }

    @Test
    public void selectAccountNameTest_OK_case7() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectAccountName("yamadatarou1001");
        assertEquals(employeeAccount.getId(), 1);
        assertEquals(employeeAccount.getName(), "yamadatarou1001");
        assertEquals(employeeAccount.getPassword(), "yamadapassword1001");
        assertEquals(employeeAccount.getEmployeeId(), 1);

    }

    @Test
    public void selectAccountNameTest_null_case8() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectAccountName("a");
        assertEquals(employeeAccount, null);

    }

    @Test
    public void selectEmployeeNameWithEmployeeAccountIdTest_OK_case9() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(1);
        assertEquals(employeeAccount.getName(), "yamadatarou1001");
        assertEquals(employeeAccount.getEmployee().getName(), "山田太郎");

    }

    @Test
    public void selectEmployeeNameWithEmployeeAccountIdTest_null_case10() {
        EmployeeAccount employeeAccount = adminEmployeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(2);
        assertEquals(employeeAccount, null);

    }

}
