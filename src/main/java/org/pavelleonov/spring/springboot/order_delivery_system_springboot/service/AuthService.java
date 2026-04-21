package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.LoginRequest;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.RefreshTokensRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.AuthResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.InvalidRefreshTokenException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthResponseDto login(LoginRequest request){

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

            log.info("User logged in successfully, login = {}", request.getLogin());
            return new AuthResponseDto(accessToken, refreshToken);
        } catch (BadCredentialsException ex) {
            log.warn("Failed logging attempt for login = {}", request.getLogin());
            throw ex;
        }
    }

    public AuthResponseDto refreshTokens(RefreshTokensRequestDto request){

        String username = jwtService.extractUsernameIfValid(request.getRefreshToken());

        if(username == null){
            log.warn("Failed refresh token attempt");
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String newRefreshToken = jwtService.generateRefreshToken(username);
        String newAccessToken = jwtService.generateAccessToken(username);
        log.info("Tokens refreshed for user = {}", username);
        return new AuthResponseDto(newAccessToken, newRefreshToken);
    }
}
