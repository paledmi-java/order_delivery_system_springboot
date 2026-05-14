package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.LoginRequest;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.RefreshTokensRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.AuthResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Role;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.JwtFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.AuthService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerUnitTest {

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private ClientService clientService;
    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    private CustomUserDetails customUserDetails;
    private Client client;
    private Credentials credentials;

    @BeforeEach
    void setUp() {

        client = new Client();
        client.setClientId(1);
        credentials = new Credentials();
        credentials.setLogin("mikewazowski");
        credentials.setHashedPassword("qwerty");

        client.setCredentials(credentials);
        client.setEmail("mikewazowski@yandex.ru");
        client.setName("Mike Wazowski");
        client.setDateOfBirth(LocalDate.of(1995, 4, 23));
        client.setPhoneNumber("+11111111111");

        Role role = new Role(1L, "ADMIN");
        client.getRoles().add(role);

        customUserDetails = new CustomUserDetails(client);
    }

    @Test
    void login_ShouldReturn200() throws Exception{

        LoginRequest request = new LoginRequest();
        request.setLogin("paledmi");
        request.setPassword("123456");

        AuthResponseDto responseDto = new AuthResponseDto("123", "456");

        when(authService.login(any(LoginRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/login")
                .with(user(customUserDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("123"))
                .andExpect(jsonPath("$.refreshToken").value("456"));

        verify(authService).login(any(LoginRequest.class));
    }


    @Test
    void register_ShouldReturn200() throws Exception{

        ClientCreateDTO requestDto = ClientCreateDTO
                .builder()
                .name("Pablo")
                .phoneNumber("+78473847234")
                .email("pablodiablo@mail.ru")
                .password("qwerty1234")
                .login("paledmi")
                .build();

        ClientResponseDto responseDto = ClientResponseDto
                .builder()
                .name("Pablo")
                .phoneNumber("+78473847234")
                .email("pablodiablo@mail.ru")
                .login("paledmi")
                .build();

        when(clientService.saveClient(any(ClientCreateDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                .with(user(customUserDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pablo"));

        verify(clientService).saveClient(any(ClientCreateDTO.class));
    }
    @Test
    void register_ShouldReturn400WhenInputInvalid() throws Exception{

        ClientCreateDTO requestDto = ClientCreateDTO
                .builder()
                .name("Pablo")
                .phoneNumber("+784")
                .email("pablodiablo.ru")
                .password("qwer")
                .login("paledmi")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).saveClient(any(ClientCreateDTO.class));
    }

    @Test
    void refreshToken_ShouldReturn200() throws Exception{

        RefreshTokensRequestDto request = new RefreshTokensRequestDto();
        request.setRefreshToken("12345");

        AuthResponseDto responseDto = new AuthResponseDto("123", "456");

        when(authService.refreshTokens(any(RefreshTokensRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/refresh")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("123"))
                .andExpect(jsonPath("$.refreshToken").value("456"));

        verify(authService).refreshTokens(any(RefreshTokensRequestDto.class));
    }
}