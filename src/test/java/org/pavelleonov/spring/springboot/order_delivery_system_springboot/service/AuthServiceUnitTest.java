package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.LoginRequest;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.RefreshTokensRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.AuthResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.InvalidRefreshTokenException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @Test
    void login_ShouldReturnAuthResponse(){

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("mikewazowski");
        loginRequest.setPassword("qwerty");

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUsername()).thenReturn("mikewazowski");
        when(jwtService.generateAccessToken(loginRequest.getLogin()))
                .thenReturn("access.token");
        when(jwtService.generateRefreshToken(loginRequest.getLogin()))
                .thenReturn("refresh.token");

        //when
        AuthResponseDto responseDto = authService.login(loginRequest);

        //then
        assertThat(responseDto.accessToken()).isEqualTo("access.token");
        assertThat(responseDto.refreshToken()).isEqualTo("refresh.token");

    }

    @Test
    void login_ShouldThrowWhenBadCredentials(){

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("mikewazowski");
        loginRequest.setPassword("qwerty");

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);

        when(authenticationManager.authenticate(any())).thenThrow(
                new BadCredentialsException ("Bad credentials")
        );

        //when/then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");

    }

    @Test
    void refreshTokens_ShouldSuccessfullyRefreshTokes(){
        //given
        String oldRefreshToken= "old.refresh.token";

        String newRefreshToken= "new.refresh.token";
        String newAccessToken= "new.accsess.token";

        RefreshTokensRequestDto requestDto = new RefreshTokensRequestDto();
        requestDto.setRefreshToken(oldRefreshToken);

        when(jwtService.extractUsernameIfValid(requestDto.getRefreshToken()))
                .thenReturn("username");
        when(jwtService.generateRefreshToken("username")).thenReturn(newRefreshToken);
        when(jwtService.generateAccessToken("username")).thenReturn(newAccessToken);

        //when
        AuthResponseDto responseDto = authService.refreshTokens(requestDto);

        //then
        verify(jwtService).extractUsernameIfValid(requestDto.getRefreshToken());
        verify(jwtService).generateRefreshToken("username");
        verify(jwtService).generateAccessToken("username");

        assertThat(responseDto.refreshToken()).isEqualTo("new.refresh.token");
        assertThat(responseDto.accessToken()).isEqualTo("new.accsess.token");


    }

    @Test
    void refreshTokens_ShouldThrowWhenTokenInvalid(){
        //given
        String oldRefreshToken= "old.refresh.token";

        String newRefreshToken= "new.refresh.token";
        String newAccessToken= "new.accsess.token";

        RefreshTokensRequestDto requestDto = new RefreshTokensRequestDto();
        requestDto.setRefreshToken(oldRefreshToken);

        when(jwtService.extractUsernameIfValid(requestDto.getRefreshToken()))
                .thenReturn(null);

        //when
       assertThatThrownBy(()->authService.refreshTokens(requestDto))
               .isInstanceOf(InvalidRefreshTokenException.class)
               .hasMessage("Invalid refresh token");

        //then
        verify(jwtService).extractUsernameIfValid(requestDto.getRefreshToken());
        verify(jwtService, never()).generateRefreshToken("username");
        verify(jwtService, never()).generateAccessToken("username");


    }
}