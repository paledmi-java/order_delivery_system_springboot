package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.AddItemToBucketRequestDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.RemoveItemToBucketRequestDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientActivateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientUpdateSelfDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.CreateOrderRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetailService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.BucketService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.JwtService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ClientRestController.class)
@WithMockUser
class ClientRestControllerUnitTest {

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private ClientService clientService;
    @MockitoBean
    private BucketService bucketService;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

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

        customUserDetails = new CustomUserDetails(client);

    }

    @Test
    void updateClientSelf_ShouldReturnUpdatedClient() throws Exception {
        //given
        ClientUpdateSelfDTO clientUpdateSelfDTO = new ClientUpdateSelfDTO();
        clientUpdateSelfDTO.setName("New Name");

        ClientResponseDto clientResponseDto = ClientResponseDto.builder()
                .name("New Name")
                .build();

        when(clientService.updateClientSelf(eq(1), any(ClientUpdateSelfDTO.class)))
                .thenReturn(clientResponseDto);

        //when/then
        mockMvc.perform(
                        patch("/api/users/me/settings")
                                .with(user(customUserDetails))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clientUpdateSelfDTO))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));

        verify(clientService).updateClientSelf(eq(1), any(ClientUpdateSelfDTO.class));
    }

    @Test
    void updateClientSelf_ShouldReturn400WhenNameIsBlank() throws Exception {
        //given
        ClientUpdateSelfDTO clientUpdateSelfDTO = new ClientUpdateSelfDTO();
        clientUpdateSelfDTO.setName("");

        ClientResponseDto clientResponseDto = ClientResponseDto.builder()
                .name("")
                .build();

        when(clientService.updateClientSelf(eq(1), any(ClientUpdateSelfDTO.class)))
                .thenReturn(clientResponseDto);

        //when/then
        mockMvc.perform(
                patch("/api/users/me/settings")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateSelfDTO))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateClientSelf_ShouldReturn401WhenUnauthorised() throws Exception {
        //given
        ClientUpdateSelfDTO clientUpdateSelfDTO = new ClientUpdateSelfDTO();
        clientUpdateSelfDTO.setName("");

        ClientResponseDto clientResponseDto = ClientResponseDto.builder()
                .name("")
                .build();

        when(clientService.updateClientSelf(eq(1), any(ClientUpdateSelfDTO.class)))
                .thenReturn(clientResponseDto);

        //when/then
        mockMvc.perform(
                patch("/api/users/me/settings")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateSelfDTO))
        ).andExpect(status().isBadRequest());
    }


    @Test
    void getCurrentUser_ShouldReturnCurrentUser() throws Exception {
        //given

        ClientResponseDto clientResponseDto = ClientResponseDto.builder()
                .name("New Name")
                .phoneNumber("+123456")
                .email("qwerty@yandex.ru")
                .build();

        when(clientService.getUser(eq(1))).thenReturn(clientResponseDto);

        //when/then

        mockMvc.perform(
                        get("/api/users/me")
                                .with(user(customUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.phoneNumber").value("+123456"))
                .andExpect(jsonPath("$.email").value("qwerty@yandex.ru"));


        verify(clientService).getUser(eq(1));
    }


    @Test
    void updateClientPassword_ShouldSuccessfullyUpdatePassword() throws Exception {
        //given
        ClientPasswordUpdateDTO dto = new ClientPasswordUpdateDTO();
        dto.setNewPassword("qwerty123456");
        dto.setOldPassword("123456qwerty");

        //when/then
        mockMvc.perform(post("/api/users/me/settings/password")
                .with(user(customUserDetails))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isNoContent());

        verify(clientService).changePasswordSelf(eq(1), any(ClientPasswordUpdateDTO.class));

    }

    @Test
    void updateClientPassword_ShouldReturn400WhenPasswordIsIncorrect() throws Exception {
        //given
        ClientPasswordUpdateDTO dto = new ClientPasswordUpdateDTO();
        dto.setOldPassword("123456qwerty");
        dto.setNewPassword("123456");

        //when/then
        mockMvc.perform(post("/api/users/me/settings/password")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deactivateAccount_ShouldReturn204() throws Exception {

        mockMvc.perform(patch("/api/users/me/settings/deactivate")
                        .with(user(customUserDetails))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(clientService).deactivateAccount(customUserDetails.getId());
    }


    @Test
    void activateAccount_ShouldReturn204() throws Exception {

        ClientActivateDTO dto = new ClientActivateDTO();
        dto.setLogin("paledmi");
        dto.setPassword("123456");

        mockMvc.perform(patch("/api/users/me/settings/activate")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<ClientActivateDTO> captor = ArgumentCaptor.forClass(ClientActivateDTO.class);

        verify(clientService).activateAccount(eq(1), captor.capture());

        assertThat(captor.getValue().getLogin()).isEqualTo(dto.login);
        assertThat(captor.getValue().getPassword()).isEqualTo(dto.password);
    }

    @Test
    void openBucket_ShouldReturnList() throws Exception {
        //given
        List<BucketItemDto> dtos = new ArrayList<>();
        BucketItemDto dto1 = BucketItemDto.builder().quantity(213).build();
        BucketItemDto dto2 = BucketItemDto.builder().quantity(234).build();
        dtos.add(dto1);
        dtos.add(dto2);

        when(bucketService.openBucket(eq(1))).thenReturn(dtos);

        //when/then

        mockMvc.perform(get("/api/users/me/bucket")
                        .with(user(customUserDetails))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].quantity").value(213))
                .andExpect(jsonPath("$[1].quantity").value(234));

        verify(bucketService).openBucket(eq(1));
    }

    @Test
    void openBucket_ShouldReturnEmptyList() throws Exception {
        //given
        List<BucketItemDto> dtos = new ArrayList<>();

        when(bucketService.openBucket(eq(1))).thenReturn(dtos);

        //when/then

        mockMvc.perform(get("/api/users/me/bucket")
                        .with(user(customUserDetails))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bucketService).openBucket(eq(1));
    }


    @Test
    void addBucketItem_ShouldReturn200() throws Exception {
        //given
        AddItemToBucketRequestDTO dto = new AddItemToBucketRequestDTO();
        dto.setItemId(1);
        dto.setQuantity(123);

        BucketItemDto bucketItemDto = BucketItemDto.builder().quantity(123).build();

        when(bucketService.addItemToBucket(eq(1), eq(1), eq(123)))
                .thenReturn(bucketItemDto);

        //when/then
        mockMvc.perform(post("/api/users/me/bucket/add")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(123));

        verify(bucketService).addItemToBucket(eq(1), eq(1), eq(123));
    }

    @Test
    void addBucketItem_ShouldReturn400WhenRequestIsInvalid() throws Exception {
        //given
        AddItemToBucketRequestDTO dto = new AddItemToBucketRequestDTO();
        dto.setItemId(1);
        dto.setQuantity(1001);

        //when/then
        mockMvc.perform(post("/api/users/me/bucket/add")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(bucketService, never()).addItemToBucket(eq(1), eq(1), eq(123));
    }


    @Test
    void removeBucketItem_ShouldReturn200() throws Exception {
        //given
        RemoveItemToBucketRequestDTO requestDTO = new RemoveItemToBucketRequestDTO();
        requestDTO.setItemId(1);
        requestDTO.setQuantity(123);

        mockMvc.perform(patch("/api/users/me/bucket/remove/")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<RemoveItemToBucketRequestDTO> captor = ArgumentCaptor.forClass(RemoveItemToBucketRequestDTO.class);
        verify(bucketService).removeItemFromBucket(eq(1), captor.capture());

        assertThat(captor.getValue().getItemId()).isEqualTo(requestDTO.getItemId());
        assertThat(captor.getValue().getQuantity()).isEqualTo(requestDTO.getQuantity());

    }

    @Test
    void removeBucketItem_ShouldReturn400WhenRequestIsInvalid() throws Exception {
        //given
        RemoveItemToBucketRequestDTO requestDTO = new RemoveItemToBucketRequestDTO();
        requestDTO.setItemId(0);
        requestDTO.setQuantity(123);

        mockMvc.perform(patch("/api/users/me/bucket/remove/")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(bucketService, never()).removeItemFromBucket(eq(1), any(RemoveItemToBucketRequestDTO.class));

    }


    @Test
    void makeAnOrder_ShouldReturn200() throws Exception {
        //given
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setCommentary("Commentary");
        requestDto.setAreBonusesUsed(true);
        requestDto.setBucketId(1);
        requestDto.setClientAddressId(1);

        OrderResponseDto responseDto = OrderResponseDto
                .builder()
                .commentary("Commentary")
                .areBonusesUsed(true)
                .build();

        when(orderService.makeAnOrder(eq(1), any(CreateOrderRequestDto.class)))
                .thenReturn(responseDto);

        //when/then
        mockMvc.perform(post("/api/users/me/bucket/order")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentary").value("Commentary"))
                .andExpect(jsonPath("$.areBonusesUsed").value(true));

        ArgumentCaptor<CreateOrderRequestDto> captor = ArgumentCaptor
                .forClass(CreateOrderRequestDto.class);

        verify(orderService).makeAnOrder(eq(1), captor.capture());
        assertThat(captor.getValue().getCommentary()).isEqualTo(requestDto.getCommentary());
        assertThat(captor.getValue().getAreBonusesUsed()).isTrue();
    }

    @Test
    void makeAnOrder_ShouldReturn400WhenInputIsInvalid() throws Exception {
        //given
        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setCommentary("Commentary");
        requestDto.setAreBonusesUsed(true);

        //when/then
        mockMvc.perform(post("/api/users/me/bucket/order")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).makeAnOrder(eq(1), any(CreateOrderRequestDto.class));
    }


    @Test
    void addNewAddress_ShouldReturn200() throws Exception {
        //given
        ClientAddressRequestDto requestDto = ClientAddressRequestDto
                .builder()
                .city("Samara")
                .postalCode("456342")
                .street("Samarskaya")
                .apartment("45")
                .isDefault(true)
                .houseNumber("123")
                .build();


        ClientAddressResponseDto responseDto = ClientAddressResponseDto
                .builder()
                .city("Samara")
                .houseNumber("123")
                .build();

        when(clientService.addNewAddress(eq(1), any(ClientAddressRequestDto.class)))
                .thenReturn(responseDto);

        //when/then
        mockMvc.perform(post("/api/users/me/settings/addresses")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Samara"))
                .andExpect(jsonPath("$.houseNumber").value("123"));

        ArgumentCaptor<ClientAddressRequestDto> captor = ArgumentCaptor
                .forClass(ClientAddressRequestDto.class);

        verify(clientService).addNewAddress(eq(1), captor.capture());
        assertThat(captor.getValue().city()).isEqualTo(requestDto.city());
        assertThat(captor.getValue().houseNumber()).isEqualTo(requestDto.houseNumber());
    }

    @Test
    void addNewAddress_ShouldReturn400WhenInputIsInvalid() throws Exception {
        //given
        ClientAddressRequestDto requestDto = ClientAddressRequestDto
                .builder()
                .city("Samara")
                .postalCode("456")
                .street("Samarskaya")
                .apartment("45")
                .isDefault(true)
                .houseNumber("123")
                .build();

        //when/then
        mockMvc.perform(post("/api/users/me/settings/addresses")
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).addNewAddress(eq(1), any(ClientAddressRequestDto.class));

    }


    @Test
    void showAllOrders_ShouldReturnListOfOrders() throws Exception{

        OrderResponseDto responseDto1 = OrderResponseDto
                .builder()
                .price(1234).build();

        OrderResponseDto responseDto2 = OrderResponseDto
                .builder()
                .price(2345).build();

        List<OrderResponseDto> list = new ArrayList<>();
        list.add(responseDto1);
        list.add(responseDto2);

        when(orderService.getOrders(eq(1))).thenReturn(list);

        mockMvc.perform(get("/api/users/me/orders")
                .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].price").value(1234))
                .andExpect(jsonPath("$.[1].price").value(2345));

        verify(orderService).getOrders(eq(1));

    }

    @Test
    void showAllOrders_ShouldReturnEmptyListOfOrders() throws Exception{

        List<OrderResponseDto> list = new ArrayList<>();

        when(orderService.getOrders(eq(1))).thenReturn(list);

        mockMvc.perform(get("/api/users/me/orders")
                        .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(orderService).getOrders(eq(1));

    }


    @Test
    void getAddresses_ShouldReturnListOfAddresses() throws Exception{

        ClientAddressResponseDto dto1 = new ClientAddressResponseDto();
        dto1.setCity("Samara");

        ClientAddressResponseDto dto2 = new ClientAddressResponseDto();
        dto2.setCity("Moscow");

        List<ClientAddressResponseDto> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);

        when(clientService.getAddresses(eq(1))).thenReturn(list);

        mockMvc.perform(get("/api/users/me/adresses")
                .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].city").value("Samara"))
                .andExpect(jsonPath("$.[1].city").value("Moscow"));

        verify(clientService).getAddresses(eq(1));
    }

    @Test
    void getAddresses_ShouldReturnEmptyListOfAddresses() throws Exception{

        List<ClientAddressResponseDto> list = new ArrayList<>();

        when(clientService.getAddresses(eq(1))).thenReturn(list);

        mockMvc.perform(get("/api/users/me/adresses")
                        .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(clientService).getAddresses(eq(1));
    }
}
