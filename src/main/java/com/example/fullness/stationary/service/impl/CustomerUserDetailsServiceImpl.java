package com.example.fullness.stationary.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fullness.stationary.entity.Customer;
import com.example.fullness.stationary.repository.CustomerRepository;

@Service
public class CustomerUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String mailAddress) {

        Customer account = customerRepository.findByMailAddress(mailAddress);

        if (account == null) {
            throw new UsernameNotFoundException("Customer not found: " + mailAddress);
        }

        return User.withUsername(account.getMailAddress())
                .password(account.getPassword())
                .roles("USER")
                .build();
    }
}
