package com.example.fullness.stationary.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.fullness.stationary.repository.EmployeeAccountRepository;

@ExtendWith(MockitoExtension.class)
public class AdminEmployeeAccountServiceTest {

    @Mock
    EmployeeAccountRepository adminEmployeeAccountRepository;

    @InjectMocks
    AdminEmployeeAccountService admineAccountService;

}
