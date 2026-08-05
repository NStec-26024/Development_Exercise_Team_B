package com.example.fullness.stationary.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.example.fullness.stationary.security.CustomAuthenticationFailureHandler;
import com.example.fullness.stationary.service.LoginAttemptService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final LoginAttemptService loginAttemptService;

        public SecurityConfig(LoginAttemptService loginAttemptService) {
                this.loginAttemptService = loginAttemptService;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        CustomAuthenticationFailureHandler failureHandler) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/admin", "/admin/login", "/css/**", "/js/**", "/",
                                                                "/images/**",
                                                                "/error")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/admin/login")
                                                .loginProcessingUrl("/admin/login-auth")
                                                .usernameParameter("name")
                                                .passwordParameter("password")
                                                .successHandler((request, response, authentication) -> {
                                                        request.changeSessionId();
                                                        String username = authentication.getName();
                                                        loginAttemptService.loginSucceeded(username);
                                                        response.sendRedirect("/admin");
                                                })
                                                .failureHandler(failureHandler)
                                                .permitAll())
                                // ★修正①：ログアウト成功時にリダイレクトではなく、コントローラーへ「フォワード」する
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        // 新しい空のセッションを作成してメッセージを入れる
                                                        request.getSession(true).setAttribute("logoutFlag", true);
                                                        request.getRequestDispatcher("/admin/login").forward(request,
                                                                        response);
                                                })
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .csrf(csrf -> csrf.disable())
                                // ★修正②：セッション切れの際もURLを変えず、ハンドラーを使って「フォワード」する
                                .sessionManagement(session -> session
                                                .invalidSessionStrategy((request, response) -> {
                                                        request.getSession(true).setAttribute("timeoutFlag", true);
                                                        request.getRequestDispatcher("/admin/login").forward(request,
                                                                        response);
                                                })
                                                .sessionConcurrency(concurrency -> concurrency
                                                                .maximumSessions(1)
                                                                .maxSessionsPreventsLogin(false)
                                                                .expiredSessionStrategy(event -> {
                                                                        event.getRequest().getSession(true)
                                                                                        .setAttribute("timeoutFlag",
                                                                                                        true);
                                                                        event.getRequest()
                                                                                        .getRequestDispatcher(
                                                                                                        "/admin/login")
                                                                                        .forward(event.getRequest(),
                                                                                                        event.getResponse());
                                                                })));

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService(DataSource dataSource) {
                JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource) {
                        @Override
                        public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(
                                        String username) {
                                if (loginAttemptService.isBlocked(username)) {
                                        throw new LockedException("Account is locked");
                                }
                                return super.loadUserByUsername(username);
                        }
                };

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
