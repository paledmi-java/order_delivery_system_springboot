package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.ClientAddress;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientAddressMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientAddressRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
class ClientServiceIntegrationTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ClientAddressRepository clientAddressRepository;

    @Autowired
    private ClientDtoMapper clientDtoMapper;
    @Autowired
    private ClientAddressMapper clientAddressMapper;
    @Autowired
    private PageMapper pageMapper;

    Client client;


    @BeforeEach
    void setUp() {

        client = new Client();
        Credentials credentials = new Credentials();
        credentials.setLogin("mikewazowski");
        credentials.setHashedPassword("qwerty");

        client.setCredentials(credentials);
        client.setEmail("mikewazowski@yandex.ru");
        client.setName("Mike Wazowski");
        client.setDateOfBirth(LocalDate.of(1995, 4, 23));
        client.setPhoneNumber("+11111111111");

        clientRepository.save(client);
    }




    @Test
    void addNewAddress_ShouldSaveCorrectAddress() {

        // given
        ClientAddressRequestDto dto = new ClientAddressRequestDto(
                true, "Samara", "Lenina",
                "23", "34", "344556"
        );

        //when
        clientService.addNewAddress(client.getClientId(), dto);

        //then
        Client savedClient = clientRepository.findById(client.getClientId()).orElseThrow();
        ClientAddress clientAddress = savedClient.getClientAddresses().get(0);

        assertThat(savedClient.getClientAddresses()).hasSize(1);

        assertThat(clientAddress.getCity()).isEqualTo(dto.city());
        assertThat(clientAddress.getStreet()).isEqualTo(dto.street());
        assertThat(clientAddress.getHouseNumber()).isEqualTo(dto.houseNumber());
        assertThat(clientAddress.getApartment()).isEqualTo(dto.apartment());
        assertThat(clientAddress.getPostal_code()).isEqualTo(dto.postalCode());

        assertThat(clientAddress.isDefault()).isTrue();

        assertThat(client.getClientAddresses().contains(clientAddress));

        assertThat(clientAddress.getClient().getClientId()).isEqualTo(client.getClientId());
    }

    @Test
    void addNewAddress_ShouldReplaceDefaultAddressCorrectly() {

        //given

        ClientAddressRequestDto dto1 = new ClientAddressRequestDto(
                true, "Samara", "Lenina",
                "23", "34", "344556"
        );

        ClientAddressRequestDto dto2 = new ClientAddressRequestDto(
                false, "Moscow", "Pushkina",
                "55", "66", "886859"
        );

        ClientAddressRequestDto dto3 = new ClientAddressRequestDto(
                true, "Kazan", "Stalina",
                "11", "22", "543453"
        );

        //when
        Client savedClient;

        clientService.addNewAddress(client.getClientId(), dto1);
        savedClient = clientRepository.findById(client.getClientId()).orElseThrow();
        assertThat(savedClient.getClientAddresses().get(0).isDefault()).isTrue();

        clientService.addNewAddress(client.getClientId(), dto2);
        savedClient = clientRepository.findById(client.getClientId()).orElseThrow();
        assertThat(savedClient.getClientAddresses().get(0).isDefault()).isTrue();
        assertThat(savedClient.getClientAddresses().get(1).isDefault()).isFalse();

        clientService.addNewAddress(client.getClientId(), dto3);
        savedClient = clientRepository.findById(client.getClientId()).orElseThrow();
        assertThat(savedClient.getClientAddresses().get(0).isDefault()).isFalse();
        assertThat(savedClient.getClientAddresses().get(1).isDefault()).isFalse();
        assertThat(savedClient.getClientAddresses().get(2).isDefault()).isTrue();

    }

}