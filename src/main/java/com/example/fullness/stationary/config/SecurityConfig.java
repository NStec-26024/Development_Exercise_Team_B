package com.example.fullness.stationary.config;

import javax.sql.DataSource;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.fullness.stationary.security.CustomAuthenticationFailureHandler;
import com.example.fullness.stationary.security.SessionLockFilter;
import com.example.fullness.stationary.service.LoginAttemptService;

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
 * - ログイン失敗回数によるアカウントロック判定（SessionLockFilter）
 * - ログイン失敗時のメッセージ設定（CustomAuthenticationFailureHandler）
 * - DB からユーザー情報を読み込む仕組み（JdbcUserDetailsManager）
 *
 * 管理画面は社内管理者専用のため、
 * ロック判定やセッションタイムアウトなど、より厳しいセキュリティを適用している。
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final LoginAttemptService loginAttemptService;

        /**
         * ログイン失敗回数を管理するサービスを受け取る。
         * ログイン成功時の失敗回数リセットやロック判定に使用する。
         */

        public SecurityConfig(LoginAttemptService loginAttemptService) {
                this.loginAttemptService = loginAttemptService;
        }

        @Bean
        public SessionLockFilter sessionLockFilter(LoginAttemptService loginAttemptService,
                        MessageSource messageSource) {
                return new SessionLockFilter(loginAttemptService, messageSource);
        }

        /**
         * 管理画面のセキュリティルールをまとめて構築するメソッド。
         *
         * このメソッドでは次の設定を行う：
         *
         * - URL のアクセス制御
         * /admin/login などは誰でもアクセス可能
         * /admin/** は ADMIN 権限を持つユーザーのみアクセス可能
         *
         * - ロック判定フィルターの追加
         * ログイン前に SessionLockFilter を実行し、アカウントがロック中なら認証処理を止める
         *
         * - ログイン処理の設定
         * ログイン画面の URL（/admin/login）
         * ログイン処理の URL（/admin/login-auth）
         * ログイン成功時：失敗回数リセット、セッションID再生成、管理画面へ遷移
         * ログイン失敗時：CustomAuthenticationFailureHandler によりメッセージ設定
         *
         * - ログアウト処理の設定
         * セッション破棄
         * JSESSIONID Cookie 削除
         * ログアウト後は /admin へ遷移
         *
         * - セッションが無効になったときの処理
         * セッションが切れた場合、login 画面以外からのアクセスなら timeoutFlag を付けてログイン画面へ戻す
         *
         * - CSRF 無効化
         * 管理画面の要件に合わせて CSRF を無効化する
         *
         * @return 管理画面用の SecurityFilterChain
         */

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        CustomAuthenticationFailureHandler failureHandler,
                        MessageSource messageSource,
                        SessionLockFilter sessionLockFilter) throws Exception {

                // URL のアクセス制御
                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                                "/admin",
                                                "/admin/login",
                                                "/admin/error",
                                                "/css/**",
                                                "/js/**",
                                                "/",
                                                "/images/**")
                                .permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated());

                // ロック判定フィルターを認証前に追加
                http.addFilterBefore(sessionLockFilter, UsernamePasswordAuthenticationFilter.class);

                // ログイン設定
                http.formLogin(form -> form
                                .loginPage("/admin/login") // ログイン画面（GET）
                                .loginProcessingUrl("/admin/login-auth") // 認証処理（POST）
                                .usernameParameter("name") // フォームの name と一致
                                .passwordParameter("password") // フォームの password と一致
                                .successHandler((request, response, authentication) -> {
                                        // ログイン成功 → ロック解除
                                        loginAttemptService.loginSucceeded(authentication.getName());
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
                                        // login 画面以外からのアクセス時のみ timeoutFlag を付与
                                        if (!path.startsWith("/admin/login")) {
                                                request.getSession(true).setAttribute("timeoutFlag", true);
                                        }
                                        response.sendRedirect("/admin/login");
                                }));

                // CSRF 無効化
                http.csrf(csrf -> csrf.disable());

                return http.build();
        }

        /**
         * DB（employee_account テーブル）からユーザー情報を読み込む仕組みを提供する。
         *
         * ユーザー名・パスワード・権限を SQL で取得し、Spring Security が認証に利用する。
         * 
         */
        @Bean
        public UserDetailsService userDetailsService(DataSource dataSource) {
                JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

                manager.setUsersByUsernameQuery(
                                "SELECT name, password, TRUE as enabled FROM employee_account WHERE name = ?");

                manager.setAuthoritiesByUsernameQuery(
                                "SELECT name, 'ROLE_ADMIN' as authority FROM employee_account WHERE name = ?");

                return manager;
        }

        /**
         * パスワードを安全に保存するためのハッシュ化方式（BCrypt）を提供する。
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
