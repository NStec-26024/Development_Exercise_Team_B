package com.example.fullness.stationary.config;

import java.io.IOException;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final LoginAttemptService loginAttemptService;

        public SecurityConfig(LoginAttemptService loginAttemptService) {
                this.loginAttemptService = loginAttemptService;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        CustomAuthenticationFailureHandler failureHandler,
                        MessageSource messageSource) throws Exception {

                // 認可設定
                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                                "/admin",
                                                "/admin/login",
                                                "/admin/login-auth",
                                                "/admin/error",
                                                "/css/**",
                                                "/js/**",
                                                "/",
                                                "/images/**")
                                .permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated());

                // ロック判定フィルターを認証前に追加
                http.addFilterBefore(
                                new SessionLockFilter(loginAttemptService, messageSource),
                                UsernamePasswordAuthenticationFilter.class);

                // ログイン設定
                http.formLogin(form -> form
                                .loginPage("/admin/login")
                                .loginProcessingUrl("/admin/login-auth")
                                .usernameParameter("name")
                                .passwordParameter("password")
                                .successHandler((request, response, authentication) -> {
                                        loginAttemptService.loginSucceeded(authentication.getName());
                                        request.changeSessionId();
                                        response.sendRedirect("/admin");
                                })
                                .failureHandler(failureHandler)
                                .permitAll());

                // ログアウト設定
                http.logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessHandler((request, response, authentication) -> {
                                        response.sendRedirect("/admin");
                                })
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll());

                // セッション管理
                http.sessionManagement(session -> session
                                .invalidSessionStrategy((request, response) -> {
                                        String path = request.getServletPath();
                                        if (!path.startsWith("/admin/login")) {
                                                request.getSession(true).setAttribute("timeoutFlag", true);
                                        }

                                        response.sendRedirect("/admin/login");
                                })
                                .sessionConcurrency(concurrency -> concurrency
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false)
                                                .expiredSessionStrategy(event -> {
                                                        String path = event.getRequest().getServletPath();

                                                        if (!path.startsWith("/admin/login")) {
                                                                event.getRequest().getSession(true)
                                                                                .setAttribute("timeoutFlag", true);
                                                        }

                                                        try {
                                                                event.getResponse().sendRedirect("/admin/login");
                                                        } catch (IOException e) {
                                                                throw new RuntimeException(e);
                                                        }
                                                })));

                // CSRF 無効化
                http.csrf(csrf -> csrf.disable());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService(DataSource dataSource) {
                JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

                manager.setUsersByUsernameQuery(
                                "SELECT name, password, TRUE as enabled FROM employee_account WHERE name = ?");

                manager.setAuthoritiesByUsernameQuery(
                                "SELECT name, 'ROLE_ADMIN' as authority FROM employee_account WHERE name = ?");

                return manager;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
