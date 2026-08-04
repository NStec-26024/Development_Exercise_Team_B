// package com.example.fullness.stationary.service;

// import javax.sql.DataSource;

// import org.springframework.context.annotation.Bean;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.provisioning.JdbcUserDetailsManager;

// public class DetailsService {
// @Bean
// public UserDetailsService userDetailsService(DataSource dataSource) {
// JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

// manager.setUsersByUsernameQuery(
// "SELECT name, password, TRUE as enabled FROM employee_account WHERE name =
// ?");

// manager.setAuthoritiesByUsernameQuery(
// "SELECT name, 'ROLE_USER' as authority FROM employee_account WHERE name =
// ?");

// return manager;
// }
// }
