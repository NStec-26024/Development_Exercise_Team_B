package com.example.fullness.stationary.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdminMenuControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminMenuController())
                .build();
    }

    // 入力
    @Test
    void adminRootTest_case01_Ok() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    // 処理
    @Test
    void adminRootTest_case02_Ok() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(view().name("admin/menu"));
    }

    // 出力
    @Test
    void adminRootTest_case03_Ok() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/menu"))
                .andExpect(model().size(0));
    }
}
