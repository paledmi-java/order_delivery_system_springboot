package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.LoginRequest;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.AuthResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientActivateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ClientDtoMapper clientDtoMapper;
    private final ClientService clientService;

    @PostMapping("/login")
    @Operation(summary = "Войти в систему")
    public AuthResponseDto login(@RequestBody LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword())
                );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

        return new AuthResponseDto(accessToken, refreshToken);
    }

    @PostMapping("/register")
    @Operation(summary = "Зарегистрировать нового пользователя")
    public ClientViewDTO register(@Valid @RequestBody ClientCreateDTO dto){
        Client client = clientService.saveClient(dto);
        return clientDtoMapper.toViewDto(client);
    }

    @PatchMapping("/activate")
    @Operation(summary = "Активировать аккаунт деактивированного пользователя")
    public ClientViewDTO activateAccount(@RequestBody ClientActivateDTO dto){
        Client client = clientService.activateAccount(dto);
        return clientDtoMapper.toViewDto(client);
    }

    @GetMapping("/refresh")
    @Operation(summary = "Обновить токены")
    public AuthResponseDto refreshToken(@RequestParam String refreshToken){
        String username = jwtService.extractUsernameIfValid(refreshToken);
        if(username == null){
            throw new ClientNotFoundException("Invalid refresh token");
        }

        String newRefreshToken = jwtService.generateRefreshToken(username);
        String newAccessToken = jwtService.generateAccessToken(username);
        return new AuthResponseDto(newAccessToken, newRefreshToken);
    }
}
