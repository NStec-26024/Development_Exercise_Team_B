package com.example.fullness.stationary.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import com.example.fullness.stationary.service.AdminLoginAttemptService;

class AdminUserDetailsServiceImplTest {

    private EmployeeAccountRepository mockRepository;
    private AdminLoginAttemptService mockLoginAttemptService;
    private AdminUserDetailsServiceImpl service;

    @BeforeEach
    void setup() {
        mockRepository = mock(EmployeeAccountRepository.class);
        mockLoginAttemptService = mock(AdminLoginAttemptService.class);

        service = new AdminUserDetailsServiceImpl();

        ReflectionTestUtils.setField(service, "employeeAccountRepository", mockRepository);
        ReflectionTestUtils.setField(service, "adminLoginAttemptServiceImpl", mockLoginAttemptService);
    }

    // ============================================================
    // 正常系：ユーザーが存在 → UserDetails を返すルート
    // ============================================================
    @Test
    void loadUserByUsernameTest_case01_Ok() {

        EmployeeAccount account = new EmployeeAccount();
        account.setName("yamadatarou1001");
        account.setPassword("$2a$10$nOMKs31N.scADyHLn1KfyOagrb52vXDEokqGp4MueMbqAam1iaS1e");

        when(mockRepository.findByName("yamadatarou1001"))
                .thenReturn(account);

        when(mockLoginAttemptService.isBlocked("yamadatarou1001"))
                .thenReturn(false);

        UserDetails userDetails = service.loadUserByUsername("yamadatarou1001");

        assertEquals("yamadatarou1001", userDetails.getUsername());
        assertEquals(account.getPassword(), userDetails.getPassword());
        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // ============================================================
    // 異常系：ユーザーが存在しない → 例外ルート
    // ============================================================
    @Test
    void loadUserByUsernameTest_case02_Ok() {

        when(mockRepository.findByName("unknown"))
                .thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("unknown");
        });
    }
}
