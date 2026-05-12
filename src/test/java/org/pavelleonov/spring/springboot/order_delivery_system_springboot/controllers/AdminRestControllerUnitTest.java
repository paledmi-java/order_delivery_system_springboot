package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientAdminPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.ChangeOrderStatusRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Role;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.JwtFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminRestController.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false)
class AdminRestControllerUnitTest {

    @MockitoBean
    private ClientService clientService;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    private Client client;
    private Credentials credentials;
    private CustomUserDetails customUserDetails;

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
    void getUser_ShouldReturnUser() throws Exception {

        ClientResponseDto dto = ClientResponseDto.builder().name(client.getName()).build();

        when(clientService.getUser(1)).thenReturn(dto);

        mockMvc.perform(get("/api/users/{id}", 1)
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mike Wazowski"));

        verify(clientService).getUser(1);
    }

    @Test
    void getUser_ShouldReturn400() throws Exception {

        ClientResponseDto dto = ClientResponseDto.builder().name(client.getName()).build();

        when(clientService.getUser(2)).thenReturn(dto);

        mockMvc.perform(get("/api/users/abc", 1)
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(clientService, never()).getUser(2);
    }

    @Test
    void updateUser_ShouldSuccessfullyUpdate() throws Exception {
        ClientUpdateAdminDTO requestDto = new ClientUpdateAdminDTO();
        requestDto.setName("Pablo");

        ClientResponseDto responseDto = ClientResponseDto.builder()
                .name("Pablo").build();

        when(clientService.updateClientByAdmin(eq(1), any(ClientUpdateAdminDTO.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/users/{id}", 1)
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pablo"));

        verify(clientService).updateClientByAdmin(eq(1), any(ClientUpdateAdminDTO.class));
    }

    @Test
    void updateUser_ShouldThrowWhenInputIsInvalid() throws Exception {
        ClientUpdateAdminDTO requestDto = new ClientUpdateAdminDTO();
        requestDto.setName("P");

        ClientResponseDto responseDto = ClientResponseDto.builder()
                .name("P").build();

        when(clientService.updateClientByAdmin(eq(1), any(ClientUpdateAdminDTO.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/users/{id}", 1)
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(clientService, never())
                .updateClientByAdmin(eq(1), any(ClientUpdateAdminDTO.class));
    }


    @Test
    void findUsers_ShouldReturnPageOfUsers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        ClientResponseDto dto = ClientResponseDto.builder().name("Pablo").build();
        PagedResponseDto<ClientResponseDto> pagedResponseDto = new PagedResponseDto<>();
        List<ClientResponseDto> dtoList = new ArrayList<>();
        dtoList.add(dto);
        pagedResponseDto.setContent(dtoList);
        pagedResponseDto.setPage(0);
        pagedResponseDto.setSize(10);

        when(clientService.searchClients(any(ClientFilter.class), eq(pageable))).thenReturn(pagedResponseDto);

        mockMvc.perform(get("/api/users/")
                        .with(user(customUserDetails))
                        .param("page", "0")
                        .param("size", "10"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content[0].name").value("Pablo"));

        verify(clientService).searchClients(any(ClientFilter.class), eq(pageable));
    }

    @Test
    void deactivateAccount_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(patch("/api/users/{id}/deactivate", 1))
                .andExpect(status().isNoContent());

        verify(clientService).deactivateAccount(1);
    }

    @Test
    void activateAccount_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(patch("/api/users/{id}/activate", 1))
                .andExpect(status().isNoContent());

        verify(clientService).activateClientAccountAsAdmin(1);
    }

    @Test
    void updateClientPassword_ShouldReturn200() throws Exception {

        ClientAdminPasswordUpdateDTO requestDto = new ClientAdminPasswordUpdateDTO();
        requestDto.setNewPassword("123456Qwerty");

        ClientResponseDto responseDto = ClientResponseDto.builder()
                .name("Pablo").build();

        when(clientService.changeClientPasswordAsAdmin(eq(1), any(ClientAdminPasswordUpdateDTO.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/users/{id}/password", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pablo"));

        verify(clientService).changeClientPasswordAsAdmin
                (eq(1), any(ClientAdminPasswordUpdateDTO.class));
    }

    @Test
    void updateClientPassword_ShouldReturn400WhenInputInvalid() throws Exception {

        ClientAdminPasswordUpdateDTO requestDto = new ClientAdminPasswordUpdateDTO();
        requestDto.setNewPassword("123456");

        mockMvc.perform(post("/api/users/{id}/password", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).changeClientPasswordAsAdmin
                (eq(1), any(ClientAdminPasswordUpdateDTO.class));
    }

    @Test
    void changeOrderStatus_ShouldReturn200() throws Exception {

        ChangeOrderStatusRequestDto requestDto = new ChangeOrderStatusRequestDto();
        requestDto.setStatus(Order.OrderStatus.UNPAID);

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .commentary("Commentary")
                .build();

        when(orderService
                .changeOrderStatus(eq(1), any(ChangeOrderStatusRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/api/users/orders/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentary").value("Commentary"));

        verify(orderService).changeOrderStatus(eq(1), any(ChangeOrderStatusRequestDto.class));

    }

    @Test
    void changeOrderStatus_ShouldReturn400WhenInputInvalid() throws Exception {

        ChangeOrderStatusRequestDto requestDto = new ChangeOrderStatusRequestDto();

        mockMvc.perform(patch("/api/users/orders/{id}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).changeOrderStatus(eq(1), any(ChangeOrderStatusRequestDto.class));

    }
}