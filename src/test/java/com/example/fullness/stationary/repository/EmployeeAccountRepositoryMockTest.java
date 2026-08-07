package com.example.fullness.stationary.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.fullness.stationary.entity.EmployeeAccount;

public class EmployeeAccountRepositoryMockTest {

    private EmployeeAccountRepository mockRepo;

    @BeforeEach
    void setup() {
        mockRepo = mock(EmployeeAccountRepository.class);

        // 正常系の戻り値
        EmployeeAccount account = new EmployeeAccount();
        account.setId(1);
        account.setEmployeeId(1001);

        when(mockRepo.findByName("yamadatarou1001")).thenReturn(account);

        // 異常系（存在しない場合）
        when(mockRepo.findByName("unknown")).thenReturn(null);
    }

    // 正しい値が返ってきているか
    @Test
    void findByNameTest_case01_Ok() {
        EmployeeAccount result = mockRepo.findByName("yamadatarou1001");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getEmployeeId()).isEqualTo(1001);
    }

    // 存在しない場合 null が返るか
    @Test
    void findByNameTest_case02_Ok() {
        EmployeeAccount result = mockRepo.findByName("unknown");

        assertThat(result).isNull();
    }
}
