package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientActivateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Role;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.InvalidPasswordException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.RoleNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.WrongCredentialsException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientAddressMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientAddressRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientServiceMockitoTest {

    @Captor
    private ArgumentCaptor<Client> clientCaptor;

    @Captor
    private ArgumentCaptor<String> passwordCaptor;

    @InjectMocks
    private ClientService clientService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ClientAddressRepository clientAddressRepository;

    @Mock
    private ClientDtoMapper clientDtoMapper;
    @Mock
    private ClientAddressMapper clientAddressMapper;
    @Mock
    private PageMapper pageMapper;

    Client client;
    Credentials credentials;

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

    }

    @Test
    public void saveClient_ClientShouldBeSavedSuccessfully(){
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.setEmail("sally@yandex.ru");
        dto.setLogin("sally123");
        dto.setPhoneNumber("+44444444444");
        dto.setPassword("654321");
        dto.setName("Sally Kromsally");

        Role userRole = new Role(1L, "ROLE_USER");

        ClientResponseDto responseDto = new ClientResponseDto(
                dto.getName(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                false,
                false,
                0,
                dto.getLogin()
        );

        when(passwordEncoder.encode(dto.getPassword())).thenReturn("HashedPassword123");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(clientRepository.save(any())).thenAnswer(inv->inv.getArgument(0));
        when(clientDtoMapper.toResponseDto(any(Client.class))).thenReturn(responseDto);

        ClientResponseDto result = clientService.saveClient(dto);

        verify(clientRepository).save(clientCaptor.capture());

        Client capturedClient = clientCaptor.getValue();

        assertThat(capturedClient.getEmail()).isEqualTo(dto.getEmail());
        assertThat(capturedClient.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(capturedClient.getCredentials().getLogin()).isEqualTo(dto.getLogin());
        assertThat(capturedClient.getCredentials().getHashedPassword()).isEqualTo("HashedPassword123");
        assertThat(capturedClient.getName()).isEqualTo(dto.getName());


        assertThat(result.email()).isEqualTo(dto.getEmail());
        assertThat(result.login()).isEqualTo(dto.getLogin());
        assertThat(result.phoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(result.name()).isEqualTo(dto.getName());
        assertThat(result.isOnlineCheckOn()).isFalse();
        assertThat(result.bonusesAmount()).isEqualTo(0);
        assertThat(result.isProfileComplete()).isFalse();


        verify(passwordEncoder, times(1)).encode("654321");
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    public void saveClient_ShouldBeThrownWhenRoleNameIsWrong(){
        ClientCreateDTO dto = new ClientCreateDTO();
        dto.setEmail("sally@yandex.ru");
        dto.setLogin("sally123");
        dto.setPhoneNumber("+44444444444");
        dto.setPassword("654321");
        dto.setName("Sally Kromsally");

        when(passwordEncoder.encode(dto.getPassword())).thenReturn("HashedPassword123");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.saveClient(dto))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessage("Role not found");

        verify(passwordEncoder, times(1)).encode("654321");
        verify(clientRepository, never()).save(any());
    }



    @Test
    public void activateAccount_ShouldThrowWhenWrongLogin(){

        ClientActivateDTO dto = new ClientActivateDTO();
        dto.setLogin("sally");
        dto.setPassword("qwerty");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        assertThatThrownBy(()->clientService.activateAccount(client.getClientId(), dto))
                .isInstanceOf(WrongCredentialsException.class)
                .hasMessage("Wrong credentials");

        verify(clientRepository, never()).save(any());

    }

    @Test
    public void activateAccount_ShouldThrowWhenWrongPassword(){

        ClientActivateDTO dto = new ClientActivateDTO();
        dto.setLogin("mikewazowski");
        dto.setPassword("123456");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(dto.getPassword(),
                client.getCredentials().getHashedPassword()))
                .thenReturn(false);

        assertThatThrownBy(()->clientService.activateAccount(client.getClientId(), dto))
                .isInstanceOf(WrongCredentialsException.class)
                .hasMessage("Wrong credentials");

        verify(clientRepository, never()).save(any());

    }

    @Test
    public void activateAccount_ShouldSuccessfullyActivate(){
        client.setActive(false);
        ClientActivateDTO dto = new ClientActivateDTO();
        dto.setLogin("mikewazowski");
        dto.setPassword("qwerty");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(dto.password, client.getCredentials().getHashedPassword()))
                .thenReturn(true);
        when(clientRepository.save(client)).thenReturn(client);

        clientService.activateAccount(client.getClientId(), dto);

        assertThat(client.isActive()).isTrue();

        verify(clientRepository).save(clientCaptor.capture());

        Client captured = clientCaptor.getValue();

        assertThat(captured.isActive()).isTrue();
    }




    @Test
    public void changePasswordSelf_ShouldThrowWhenOldPasswordIncorrect() {

        ClientPasswordUpdateDTO dto = new ClientPasswordUpdateDTO();
        dto.setOldPassword("123456");
        dto.setNewPassword("654321");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        when(passwordEncoder.matches(dto.getOldPassword(), credentials.getHashedPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> clientService.changePasswordSelf(client.getClientId(), dto))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Old password is incorrect");
    }

    @Test
    public void changePasswordSelf_ShouldThrowWhenNewPasswordSameAsOld() {

        ClientPasswordUpdateDTO dto = new ClientPasswordUpdateDTO();
        dto.setOldPassword("qwerty");
        dto.setNewPassword("qwerty");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        when(passwordEncoder.matches(dto.getNewPassword(), credentials.getHashedPassword()))
                .thenReturn(true);

        assertThatThrownBy(() -> clientService.changePasswordSelf(client.getClientId(), dto))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("New password can't be same as old one");
    }

    @Test
    public void changePasswordSelf_PasswordShouldBeChanged(){
        ClientPasswordUpdateDTO dto = new ClientPasswordUpdateDTO();
        dto.setOldPassword("qwerty");
        dto.setNewPassword("123456");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(dto.getOldPassword(), credentials.getHashedPassword()))
                .thenReturn(true);
        when(passwordEncoder.matches(dto.getNewPassword(), credentials.getHashedPassword()))
                .thenReturn(false);

        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("Encoded123456");

        clientService.changePasswordSelf(client.getClientId(), dto);

        verify(passwordEncoder).encode(passwordCaptor.capture());

        assertThat(passwordCaptor.getValue()).isEqualTo("123456");

        assertThat(client.getCredentials().getHashedPassword())
                .isEqualTo("Encoded123456");
    }



    @Test
    public void findClientById_ShouldFindClientSuccessfully(){

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        Client result = clientService.findClientById(client.getClientId());

        assertThat(result).isEqualTo(client);
        verify(clientRepository).findById(client.getClientId());
    }

    @Test
    public void findClientById_ShouldThrowWhenIdIsIncorrect(){
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.empty());

        assertThatThrownBy(()-> clientService.findClientById(client.getClientId()))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Client not found");
    }
}
