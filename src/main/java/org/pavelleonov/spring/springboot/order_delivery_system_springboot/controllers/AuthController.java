package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.LoginRequest;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.RefreshTokensRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.AuthResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.AuthService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ClientService clientService;

    @PostMapping("/login")
    @Operation(summary = "Войти в систему")
    public AuthResponseDto login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/register")
    @Operation(summary = "Зарегистрировать нового пользователя")
    public ClientResponseDto register(@Valid @RequestBody ClientCreateDTO dto){
        return clientService.saveClient(dto);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновить токены")
    public AuthResponseDto refreshToken(@RequestBody RefreshTokensRequestDto request){
        return authService.refreshTokens(request);
    }
}
