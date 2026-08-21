package com.example.fullness.stationary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class UserSecurityConfig {

        @Order(2)
        @Bean
        public SecurityFilterChain frontSecurityFilterChain(HttpSecurity http,
                        UserDetailsService customerUserDetailsServiceImpl) throws Exception {
                http.userDetailsService(customerUserDetailsServiceImpl);
                http.securityMatcher("/**");

                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                                "/",
                                                "/login",
                                                "/account/**",
                                                "/products/**",
                                                "/css/**",
                                                "/js/**",
                                                "/images/**",
                                                "/error")
                                .permitAll()
                                .requestMatchers("/purchase/input", "/purchase/input/**").permitAll()
                                .anyRequest().authenticated());

                http.formLogin(form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .usernameParameter("mailAddress")
                                .passwordParameter("password")
                                .successHandler((request, response, authentication) -> {
                                        // セッション固定攻撃対策
                                        request.changeSessionId();
                                        response.sendRedirect("/");
                                })
                                .failureUrl("/login")
                                .permitAll());

                http.logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(false)
                                .clearAuthentication(true)
                                .permitAll());

                return http.build();
        }
}