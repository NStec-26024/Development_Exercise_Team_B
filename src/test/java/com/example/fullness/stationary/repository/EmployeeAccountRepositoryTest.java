package com.example.fullness.stationary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.fullness.stationary.entity.EmployeeAccount;

@SpringBootTest
class EmployeeAccountRepositoryDbTest {

    @Autowired
    private EmployeeAccountRepository repository;

    // ============================================================
    // 正常系：DB から bcrypt パスワードを正しく取得できるか
    // ============================================================
    @Test
    void findByNameTest_case01_Ok() {

        EmployeeAccount result = repository.findByName("yamadatarou1001");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getEmployeeId()).isEqualTo(1001);

        // bcrypt が DB から正しく取得できているか
        assertThat(result.getPassword())
                .isEqualTo("$2a$10$nOMKs31N.scADyHLn1KfyOagrb52vXDEokqGp4MueMbqAam1iaS1e");
    }

    // ============================================================
    // 異常系：存在しないユーザー名の場合 null が返るか
    // ============================================================
    @Test
    void findByNameTest_case02_Ok() {

        EmployeeAccount result = repository.findByName("unknown");

        assertThat(result).isNull();
    }
}
