package com.example.fullness.stationary.config;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        HttpSession session = request.getSession();
        if (session.getAttribute("LOGIN_ERROR_MSG") == null) {
            String errorMessage = exception.getMessage();
            session.setAttribute("LOGIN_ERROR_MSG", errorMessage);
        }

        setDefaultFailureUrl("/admin/login");

        super.onAuthenticationFailure(request, response, exception);
    }
}
