package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword())
                );

        // если сюда дошли — значит логин успешный
        return "OK"; // позже заменим на JWT
    }
}
