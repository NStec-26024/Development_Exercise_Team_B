package com.example.fullness.stationary.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class PasswordEncoderTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void checkHash() {
        String rawPassword = "yamadapassword1001";

        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("元のパスワード: " + rawPassword);
        System.out.println("ハッシュ化後: " + encodedPassword);
    }
}