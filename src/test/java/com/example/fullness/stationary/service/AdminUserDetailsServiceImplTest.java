package com.example.fullness.stationary.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.fullness.stationary.entity.EmployeeAccount;
import com.example.fullness.stationary.repository.EmployeeAccountRepository;
import com.example.fullness.stationary.service.impl.AdminUserDetailsServiceImpl;

class AdminUserDetailsServiceImplTest {

    private EmployeeAccountRepository mockRepository;
    private AdminUserDetailsServiceImpl service;

    @BeforeEach
    void setup() {
        mockRepository = mock(EmployeeAccountRepository.class);
        service = new AdminUserDetailsServiceImpl(mockRepository);
    }

    // ============================================================
    // 正常系：ユーザーが存在 → UserDetails を返すルート
    // ============================================================
    @Test
    void loadUserByUsernameTest_case01_Ok() {

        // 入力
        EmployeeAccount account = new EmployeeAccount();
        account.setName("yamadatarou1001");
        account.setPassword("$2a$10$5W3fG3.GKrBOTbzlwY.kWeJRv8.RMJTnhUpu5M.5XUEODK3.jrsNO");

        when(mockRepository.findByName("yamadatarou1001"))
                .thenReturn(account);

        // 処理
        UserDetails userDetails = service.loadUserByUsername("yamadatarou1001");

        // 出力
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

        // 入力
        when(mockRepository.findByName("unknown"))
                .thenReturn(null);

        // 出力（例外）
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("unknown");
        });
    }
}
