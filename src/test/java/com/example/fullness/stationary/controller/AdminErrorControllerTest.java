package com.example.fullness.stationary.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AdminErrorControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        AdminErrorController controller = new AdminErrorController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void showErrorPageTest_case01_Ok() throws Exception {

        mockMvc.perform(get("/admin/error")
                .sessionAttr("errorMessage", "DB接続エラーが発生しました"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/error"))
                .andExpect(model().attribute("errorMessage", "DB接続エラーが発生しました"));
    }

    @Test
    void showErrorPageTest_case02_NoMessage_Ok() throws Exception {

        mockMvc.perform(get("/admin/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/error"))
                .andExpect(model().attribute("errorMessage", (String) null));
    }
}
