package com.example.fullness.stationary.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import com.example.fullness.stationary.entity.Employee;
import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import org.springframework.dao.DataRetrievalFailureException;

@ExtendWith(MockitoExtension.class)
public class AdminEmployeeAccountServiceTest {

    @Mock
    EmployeeAccountRepository employeeAccountRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AdminEmployeeAccountServiceImpl adminEmployeeAccountService;

    private EmployeeAccount employeeAccount;

    @BeforeEach
    void setUp() {
        employeeAccount = new EmployeeAccount();
        employeeAccount.setName("yamada");
        employeeAccount.setPassword("password");
        employeeAccount.setId(1);
        employeeAccount.setEmployeeId(101);
        Employee employee = new Employee();
        employee.setName("山田太郎");
        employee.setId(101);
        employeeAccount.setEmployee(employee);

    }

    @Test
    public void addEmployeeAccountTest_OK_case1() {

        when(passwordEncoder.encode(employeeAccount.getPassword())).thenReturn("paswordencode");

        when(employeeAccountRepository.insertEmployeeAccount(any(EmployeeAccount.class)))
                .thenAnswer(invocation -> {
                    EmployeeAccount arg = invocation.getArgument(0);
                    arg.setId(123);
                    return 1;
                });

        int resultId = adminEmployeeAccountService.addEmployeeAccount(employeeAccount);

        assertEquals(123, resultId);
        verify(passwordEncoder, times(1)).encode("password");
        assertEquals("paswordencode", employeeAccount.getPassword());
    }

    @Test
    public void addEmployeeAccountTest_NG_case2() {
        EmployeeAccount employeeAccount = new EmployeeAccount();
        when(employeeAccountRepository.insertEmployeeAccount(employeeAccount))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminEmployeeAccountService.addEmployeeAccount(employeeAccount);
        });

    }

    @Test
    public void getEmployeeNameWithEmployeeAccountTest_OK_case3() {
        List<EmployeeAccount> employeeAccountList = new ArrayList<EmployeeAccount>();
        employeeAccountList.add(employeeAccount);
        when(employeeAccountRepository.selectEmployeeNameWithEmployeeAccount()).thenReturn(employeeAccountList);

        List<EmployeeAccount> actualEmployeeAccountList = adminEmployeeAccountService
                .getEmployeeNameWithEmployeeAccount();

        assertEquals(employeeAccountList, actualEmployeeAccountList);

    }

    @Test
    public void getEmployeeNameWithEmployeeAccountTest_NG_case4() {
        when(employeeAccountRepository.selectEmployeeNameWithEmployeeAccount())
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminEmployeeAccountService.getEmployeeNameWithEmployeeAccount();
        });

    }

    @Test
    public void getEmployeeAccountWithEmployeeIdTest_OK_case5() {

        when(employeeAccountRepository.selectNotHasEmployeeAccount(employeeAccount.getEmployeeId()))
                .thenReturn(employeeAccount);

        EmployeeAccount actualEmployeeAccount = adminEmployeeAccountService
                .getEmployeeAccountWithEmployeeId(employeeAccount.getEmployeeId());

        assertEquals(employeeAccount, actualEmployeeAccount);

    }

    @Test
    public void getEmployeeAccountWithEmployeeIdTest_NG_case6() {
        when(employeeAccountRepository.selectNotHasEmployeeAccount(1))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminEmployeeAccountService.getEmployeeAccountWithEmployeeId(1);
        });

    }

    @Test
    public void getAccountNameTest_OK_case07() {

        when(employeeAccountRepository.selectAccountName(employeeAccount.getName()))
                .thenReturn(employeeAccount);

        boolean actual = adminEmployeeAccountService.getAccountName(employeeAccount.getName());

        assertEquals(false, actual);

    }

    @Test
    public void getAccountNameTest_OK_case08() {

        when(employeeAccountRepository.selectAccountName(employeeAccount.getName()))
                .thenReturn(null);

        boolean actual = adminEmployeeAccountService.getAccountName(employeeAccount.getName());

        assertEquals(true, actual);

    }

    @Test
    public void getAccountNameTest_NG_case09() {
        when(employeeAccountRepository.selectAccountName("a"))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminEmployeeAccountService.getAccountName("a");
        });

    }

    @Test
    public void getEmployeeNameWithEmployeeAccountIdTest_OK_case10() {

        when(employeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(employeeAccount.getId()))
                .thenReturn(employeeAccount);

        EmployeeAccount actualEmployeeAccount = adminEmployeeAccountService
                .getEmployeeNameWithEmployeeAccountId(employeeAccount.getId());

        assertEquals(employeeAccount, actualEmployeeAccount);
    }

    @Test
    public void getEmployeeNameWithEmployeeAccountIdTest_NG_case11() {
        when(employeeAccountRepository.selectEmployeeNameWithEmployeeAccountId(1))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminEmployeeAccountService.getEmployeeNameWithEmployeeAccountId(1);
        });

    }

    @Test
    public void getNotHasEmployeeAccountTest_OK_case12() {
        when(employeeAccountRepository.selectNotHasEmployeeAccount(employeeAccount.getEmployeeId()))
                .thenReturn(employeeAccount);

        boolean actual = adminEmployeeAccountService
                .getNotHasEmployeeAccount(employeeAccount.getEmployeeId());

        assertEquals(true, actual);
    }

    @Test
    public void getNotHasEmployeeAccountTest_OK_case13() {
        when(employeeAccountRepository.selectNotHasEmployeeAccount(2))
                .thenReturn(null);

        boolean actual = adminEmployeeAccountService
                .getNotHasEmployeeAccount(2);

        assertEquals(false, actual);
    }

    @Test
    public void getNotHasEmployeeAccountTest_NG_case14() {
        when(employeeAccountRepository.selectNotHasEmployeeAccount(1))
                .thenThrow(new DataRetrievalFailureException("e"));
        assertThrows(DataRetrievalFailureException.class, () -> {
            adminEmployeeAccountService.getNotHasEmployeeAccount(1);
        });

    }

}
