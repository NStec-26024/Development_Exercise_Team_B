package com.example.fullness.stationary.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.fullness.stationary.security.AdminCustomAuthenticationFailureHandler;
import com.example.fullness.stationary.service.AdminLoginAttemptService;

import jakarta.servlet.http.HttpSession;

/**
 * 管理画面（/admin 配下）のセキュリティ設定をまとめたクラス。
 *
 * このクラスは管理画面にアクセスするユーザーが正しくログインしているかを
 * Spring Security を使ってチェックするための設定を担当する。
 *
 * 主に次の処理を設定している：
 * - URL ごとのアクセス制御（認可）
 * - ログイン画面とログイン処理の設定
 * - ログアウト時の動作
 * - セッション切れ時の遷移（timeoutFlag の付与）
 * - ログイン失敗回数によるアカウントロック判定（AdminUserDetailsServiceImpl が
 * accountLocked() を設定し、DaoAuthenticationProvider の標準拡張点
 * isAccountNonLocked() を通じて判定する）
 * - ログイン失敗時のメッセージ設定（CustomAuthenticationFailureHandler）
 * - DB からユーザー情報を読み込む仕組み（AdminUserDetailsServiceImpl）
 */

@Configuration
@EnableWebSecurity
public class AdminSecurityConfig {

        /**
         * 管理画面のセキュリティルールをまとめて構築するメソッド。
         *
         * このメソッドでは次の設定を行う：
         *
         * - URL のアクセス制御
         * /admin/login は誰でもアクセス可能
         * /admin/** は ADMIN 権限を持つユーザーのみアクセス可能
         *
         * - ログイン処理の設定
         * ログイン画面・ログイン処理ともに同一 URL（/admin/login）
         * ロック判定は AdminUserDetailsServiceImpl が設定する accountLocked() を
         * DaoAuthenticationProvider が事前チェックすることで行う（独立フィルターは持たない）
         * 成功時：失敗回数リセット、セッションID再生成、管理画面へ遷移
         * 失敗時：AdminCustomAuthenticationFailureHandler によりメッセージ設定
         *
         * - ログアウト処理の設定
         * セッション破棄、JSESSIONID Cookie 削除、ログアウト後は /admin へ遷移
         *
         * - セッションが無効になったときの処理
         * login 画面以外からのアクセスなら timeoutMessage を付けてログイン画面へ戻す
         *
         * - CSRF 無効化
         * 
         * @param http                HttpSecurity 設定オブジェクト
         * @param failureHandler      認証失敗時のハンドラー
         * @param messageSource       メッセージソース
         * @param loginAttemptService ログイン失敗回数管理サービス
         * @return 管理画面用 SecurityFilterChain
         * @throws Exception セキュリティ設定時の例外
         */

        @Order(1)
        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        AdminCustomAuthenticationFailureHandler failureHandler,
                        MessageSource messageSource,
                        AdminLoginAttemptService loginAttemptServiceImpl) throws Exception {

                // このフィルターチェーンが担当する範囲を /admin 配下に限定する。
                // これにより、顧客向け画面（/ , /login , /account/** 等）は
                // FrontSecurityConfig 側のフィルターチェーンで扱われる。
                http.securityMatcher("/admin/**");

                // URL のアクセス制御
                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                                "/admin",
                                                "/admin/login",
                                                "/admin/error")
                                .permitAll()
                                .anyRequest().hasRole("ADMIN"));

                // ログイン設定
                http.formLogin(form -> form
                                .loginPage("/admin/login") // ログイン画面（GET）
                                .loginProcessingUrl("/admin/login") // 認証処理（POST、画面と同一URL）
                                .usernameParameter("name") // フォームの name と一致
                                .passwordParameter("password") // フォームの password と一致
                                .successHandler((request, response, authentication) -> {
                                        // ログイン成功 → ロック解除
                                        loginAttemptServiceImpl.loginSucceeded(authentication.getName());
                                        // セッション固定攻撃対策
                                        request.changeSessionId();
                                        // 管理画面トップへ遷移
                                        response.sendRedirect("/admin");
                                })
                                .failureHandler(failureHandler)
                                .permitAll());

                // ログアウト設定
                http.logout(logout -> logout
                                .logoutUrl("/admin/logout")
                                .logoutSuccessHandler((request, response, authentication) -> {
                                        response.sendRedirect("/admin");
                                })
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll());

                // セッションが無効になったときの処理
                http.sessionManagement(session -> session
                                .invalidSessionStrategy((request, response) -> {
                                        String path = request.getServletPath();
                                        HttpSession newSession = request.getSession(true);
                                        if (!path.startsWith("/admin/login")) {
                                                String timeoutMessage = messageSource.getMessage(
                                                                "com.example.fullness.stationary.security.session_timeout",
                                                                null,
                                                                Locale.JAPAN);

                                                newSession.setAttribute("timeoutMessage", timeoutMessage);
                                        }
                                        response.sendRedirect("/admin/login");
                                }));

                // CSRF 無効化
                http.csrf(csrf -> csrf.disable());

                return http.build();
        }

        /**
         * パスワードを安全に保存するためのハッシュ化方式（BCrypt）を提供する。
         *
         * @return BCrypt を利用した PasswordEncoder
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}