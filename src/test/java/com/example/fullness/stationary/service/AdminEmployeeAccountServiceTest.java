package com.example.fullness.stationary.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.example.fullness.stationary.repository.AdminEmployeeAccountRepository;

public class AdminEmployeeAccountServiceTest {

    @Mock
    AdminEmployeeAccountRepository adminEmployeeAccountRepository;

    @InjectMocks
    AdminEmployeeAccountService admineAccountService;

}
