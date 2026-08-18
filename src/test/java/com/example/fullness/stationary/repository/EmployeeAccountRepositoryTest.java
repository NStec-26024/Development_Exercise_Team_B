package com.example.fullness.stationary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.example.fullness.stationary.entity.EmployeeAccount;

@SpringBootTest
@Transactional
@Sql(scripts = {
        "classpath:sql/clear.sql",
        "classpath:sql/data.sql"
}, config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
class EmployeeAccountRepositoryTest {

    @Autowired
    private EmployeeAccountRepository repository;

    // ============================================================
    // 正常系：指定したユーザー名で DB から正しい情報を取得できること
    // ============================================================
    @Test
    void findByNameTest_case01_Ok() {

        // 実行
        EmployeeAccount result = repository.findByName("yamadatarou1001");

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
        EmployeeAccount result = repository.findByName("unknown");

        // 検証：null が返る
        assertThat(result).isNull();
    }
}
