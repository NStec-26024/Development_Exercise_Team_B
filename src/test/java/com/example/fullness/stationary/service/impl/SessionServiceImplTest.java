package com.example.fullness.stationary.service.impl;

import com.example.fullness.stationary.dto.AdminProductSessionData;
import com.example.fullness.stationary.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "session.key.admin-product=testSessionKey"
})
class SessionServiceImplTest {

    @Autowired
    private SessionService sessionService;

    /**
     * save() のテスト：セッションに正しく保存されるか
     */
    @Test
    void save_case01_Ok() {

        MockHttpSession session = new MockHttpSession();

        AdminProductSessionData data = new AdminProductSessionData();
        data.targetId = 1;
        data.name = "商品名";
        data.price = 100;
        data.stock = 10;
        data.categoryId = 2;

        sessionService.save(session, data);

        // セッションから直接取り出して検証
        Object raw = session.getAttribute("testSessionKey");
        assertThat(raw).isInstanceOf(AdminProductSessionData.class);

        AdminProductSessionData result = (AdminProductSessionData) raw;

        assertThat(result.targetId).isEqualTo(1);
        assertThat(result.name).isEqualTo("商品名");
        assertThat(result.price).isEqualTo(100);
        assertThat(result.stock).isEqualTo(10);
        assertThat(result.categoryId).isEqualTo(2);
    }

    /**
     * get() のテスト：保存済みデータが正しく取得できるか
     */
    @Test
    void get_case02_Ok() {

        MockHttpSession session = new MockHttpSession();

        AdminProductSessionData data = new AdminProductSessionData();
        data.targetId = 5;
        data.name = "テスト商品";
        data.price = 500;
        data.stock = 20;
        data.categoryId = 3;

        // まず保存
        session.setAttribute("testSessionKey", data);

        // get() で取得
        AdminProductSessionData result = sessionService.get(session);

        assertThat(result).isNotNull();
        assertThat(result.targetId).isEqualTo(5);
        assertThat(result.name).isEqualTo("テスト商品");
        assertThat(result.price).isEqualTo(500);
        assertThat(result.stock).isEqualTo(20);
        assertThat(result.categoryId).isEqualTo(3);
    }

    /**
     * clear() のテスト：セッションから削除されるか
     */
    @Test
    void clear_case03_Ok() {

        MockHttpSession session = new MockHttpSession();

        AdminProductSessionData data = new AdminProductSessionData();
        data.targetId = 99;

        session.setAttribute("testSessionKey", data);

        // 削除
        sessionService.clear(session);

        // get() の結果が null であることを確認
        AdminProductSessionData result = sessionService.get(session);

        assertThat(result).isNull();
    }
}
