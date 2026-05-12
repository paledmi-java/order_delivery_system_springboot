package org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        if(exception instanceof BadCredentialsException){
            response.sendRedirect("/login?error=bad_credentials");
        } else {
            response.sendRedirect("/login?error");
        }

    }
}

//else if(exception instanceof LockedException){
//        response.sendRedirect("/login?error=locked");
//        }
