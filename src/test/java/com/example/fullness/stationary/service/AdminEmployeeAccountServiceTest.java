package com.example.fullness.stationary.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fullness.stationary.repository.AdminEmployeeAccountRepository;

@ExtendWith(MockitoExtension.class)
public class AdminEmployeeAccountServiceTest {

    @Mock
    AdminEmployeeAccountRepository adminEmployeeAccountRepository;

    @InjectMocks
    AdminEmployeeAccountService admineAccountService;

}
