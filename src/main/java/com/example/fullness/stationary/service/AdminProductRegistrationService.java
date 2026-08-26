package com.example.fullness.stationary.service;

import org.springframework.stereotype.Service;
import com.example.fullness.stationary.form.AdminProductRegistrationForm;

@Service
public interface AdminProductRegistrationService {

    public void addProduct(AdminProductRegistrationForm adminProductRegistrationForm);

}
