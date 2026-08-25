package com.example.fullness.stationary.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;

@Service
public interface AdminProductRegistrationService {

    @Transactional
    public void addProduct(AdminProductRegistrationForm adminProductRegistrationForm);

}
