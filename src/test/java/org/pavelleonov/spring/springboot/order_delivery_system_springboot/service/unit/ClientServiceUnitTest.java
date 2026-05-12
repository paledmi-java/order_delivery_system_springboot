package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.ClientAddress;
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
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;




@ExtendWith(MockitoExtension.class)
public class ClientServiceUnitTest {

    @Captor
    private ArgumentCaptor<Client> clientCaptor;

    @Captor
    private ArgumentCaptor<String> passwordCaptor;

    @Captor
    private ArgumentCaptor<ClientAddress> addressCaptor;

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
    public void saveClient_ClientShouldBeSavedSuccessfully() {
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
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
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
    public void saveClient_ShouldBeThrownWhenRoleNameIsWrong() {
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
    public void activateAccount_ShouldThrowWhenWrongLogin() {

        ClientActivateDTO dto = new ClientActivateDTO();
        dto.setLogin("sally");
        dto.setPassword("qwerty");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> clientService.activateAccount(client.getClientId(), dto))
                .isInstanceOf(WrongCredentialsException.class)
                .hasMessage("Wrong credentials");

        verify(clientRepository, never()).save(any());

    }

    @Test
    public void activateAccount_ShouldThrowWhenWrongPassword() {

        ClientActivateDTO dto = new ClientActivateDTO();
        dto.setLogin("mikewazowski");
        dto.setPassword("123456");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(dto.getPassword(),
                client.getCredentials().getHashedPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> clientService.activateAccount(client.getClientId(), dto))
                .isInstanceOf(WrongCredentialsException.class)
                .hasMessage("Wrong credentials");

        verify(clientRepository, never()).save(any());

    }

    @Test
    public void activateAccount_ShouldSuccessfullyActivate() {
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
    public void changePasswordSelf_PasswordShouldBeChanged() {
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
    public void findClientById_ShouldFindClientSuccessfully() {

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        Client result = clientService.findClientById(client.getClientId());

        assertThat(result).isEqualTo(client);
        verify(clientRepository).findById(client.getClientId());
    }

    @Test
    public void findClientById_ShouldThrowWhenIdIsIncorrect() {
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findClientById(client.getClientId()))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Client not found");
    }


    @Test
    public void updateBaseFields_ShouldUpdateBaseFields() {
        ClientUpdateSelfDTO dto = new ClientUpdateSelfDTO();
        dto.setName("Sally");
        dto.setEmail("sally@yandex.ru");
        dto.setPhoneNumber("+88888888888");
        dto.setIsAdvertisable(false);
        dto.setIsOnlineCheckOn(true);
        dto.setDateOfBirth(LocalDate.of(1945, 4, 4));

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        Client client1 = clientService.updateBaseFields(client.getClientId(), dto);

        assertThat(client1).isEqualTo(client);

    }

    @Test
    public void getUser_WhenGettingUserShouldReturnRightDto() {
        ClientResponseDto dto = new ClientResponseDto("Sally", "+88888888888",
                "sally@yandex.ru", false,
                false, 0, "sallykromsally");

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientDtoMapper.toResponseDto(client)).thenReturn(dto);

        ClientResponseDto dto1 = clientService.getUser(client.getClientId());

        verify(clientDtoMapper).toResponseDto(client);

        assertThat(dto1).isEqualTo(dto);
    }


    @Test
    public void addNewAddress_ShouldReturnRightDto() {

        ClientAddress existingClientAddress = new ClientAddress(1,
                false, "Moscow", "Pushkina",
                "55", "66", "343423", client);

        client.getClientAddresses().add(existingClientAddress);

        //given
        ClientAddressRequestDto dto = new ClientAddressRequestDto(
                true, "Samara", "Lenina",
                "23", "34", "344556"
        );

        ClientAddressResponseDto dto1 = new ClientAddressResponseDto();
        dto1.setCity(dto.city());
        dto1.setStreet(dto.street());
        dto1.setHouseNumber(dto.houseNumber());
        dto1.setApartment(dto.apartment());
        dto1.setPostal_code(dto.postalCode());
        dto1.setDefault(dto.isDefault());


        //when
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientAddressRepository.save(any(ClientAddress.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(clientAddressMapper.toResponseDto(any(ClientAddress.class))).thenReturn(dto1);

        ClientAddressResponseDto responseDto = clientService.addNewAddress(client.getClientId(), dto);

        //then
        verify(clientAddressRepository).save(addressCaptor.capture());

        ClientAddress clientAddress = addressCaptor.getValue();

        assertThat(client.getClientAddresses().contains(clientAddress)).isTrue();

        assertThat(client.getClientAddresses().stream()
                .anyMatch(ca -> ca.getId() == clientAddress.getId()));

        verify(clientAddressMapper).toResponseDto(clientAddress);


        assertThat(clientAddress)
                .extracting(ClientAddress::getApartment,
                        ClientAddress::getCity,
                        ClientAddress::getStreet,
                        ClientAddress::getPostal_code,
                        ClientAddress::getHouseNumber,
                        ClientAddress::isDefault)
                .containsExactly(dto.apartment(),
                        dto.city(),
                        dto.street(),
                        dto.postalCode(),
                        dto.houseNumber(),
                        dto.isDefault());

        assertThat(responseDto.getApartment()).isEqualTo(dto.apartment());
        assertThat(responseDto.getCity()).isEqualTo(dto.city());
        assertThat(responseDto.getStreet()).isEqualTo(dto.street());
        assertThat(responseDto.getPostal_code()).isEqualTo(dto.postalCode());
        assertThat(responseDto.getHouseNumber()).isEqualTo(dto.houseNumber());
        assertThat(responseDto.isDefault()).isTrue();

    }

    @Test
    public void addNewAddress_DefaultAddressShouldBeReplaced() {

        //given
        ClientAddress existingClientAddress1 = new ClientAddress(1,
                false, "Samara", "Lenina",
                "45", "32", "545332", client);

        ClientAddress existingClientAddress2 = new ClientAddress(1,
                true, "Moscow", "Pushkina",
                "55", "66", "343423", client);

        client.getClientAddresses().add(existingClientAddress1);
        client.getClientAddresses().add(existingClientAddress2);

        ClientAddressRequestDto dto = new ClientAddressRequestDto(
                true, "Kazan", "Stalina",
                "11", "22", "543453"
        );


        //when
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientAddressRepository.save(any(ClientAddress.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        //then
        clientService.addNewAddress(client.getClientId(), dto);
        verify(clientAddressRepository).save(addressCaptor.capture());
        ClientAddress clientAddress1 = addressCaptor.getValue();
        assertThat(clientAddress1.isDefault()).isTrue();

        assertThat(existingClientAddress2.isDefault()).isFalse();
    }

    @Test
    public void addNewAddress_NotDefaultAddressShouldBeNotDefault() {

        //given
        ClientAddressRequestDto dto = new ClientAddressRequestDto(
                false, "Kazan", "Stalina",
                "11", "22", "543453"
        );


        //when
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientAddressRepository.save(any(ClientAddress.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        //then
        clientService.addNewAddress(client.getClientId(), dto);
        verify(clientAddressRepository).save(addressCaptor.capture());
        ClientAddress clientAddress1 = addressCaptor.getValue();
        assertThat(clientAddress1.isDefault()).isFalse();
    }


    @Test
    public void updateClientSelf_ShouldReturnRightDto() {
        //given
        ClientUpdateSelfDTO dto = new ClientUpdateSelfDTO();
        dto.setName("Sally");
        dto.setEmail("sally@yandex.ru");
        dto.setPhoneNumber("+88888888888");
        dto.setIsAdvertisable(false);
        dto.setIsOnlineCheckOn(true);
        dto.setDateOfBirth(LocalDate.of(1945, 4, 4));

        ClientResponseDto expectedResponseDto = new ClientResponseDto(
                dto.getName(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                false,
                dto.getIsOnlineCheckOn(),
                0,
                client.getCredentials().getLogin()
        );

        //when

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv->inv.getArgument(0));
        when(clientDtoMapper.toResponseDto(client)).thenReturn(expectedResponseDto);
        ClientResponseDto actualResponseDto =
                clientService.updateClientSelf(client.getClientId(), dto);

        //then
        verify(clientRepository).save(client);
        verify(clientDtoMapper).toResponseDto(client);

        assertThat(actualResponseDto)
                .extracting(ClientResponseDto::name,
                        ClientResponseDto::email,
                        ClientResponseDto::phoneNumber,
                        ClientResponseDto::login,
                        ClientResponseDto::bonusesAmount,
                        ClientResponseDto::isOnlineCheckOn,
                        ClientResponseDto::isProfileComplete
                )
                .containsExactly(expectedResponseDto.name(),
                        expectedResponseDto.email(),
                        expectedResponseDto.phoneNumber(),
                        client.getCredentials().getLogin(),
                        expectedResponseDto.bonusesAmount(),
                        expectedResponseDto.isOnlineCheckOn(),
                        expectedResponseDto.isProfileComplete());
    }

    @Test
    public void updateClientByAdmin_ShouldBeSuccessFullyUpdated() {

        //given
        ClientUpdateAdminDTO dto = new ClientUpdateAdminDTO();
        dto.setName("Sally");
        dto.setEmail("sally@yandex.ru");
        dto.setPhoneNumber("+88888888888");
        dto.setIsAdvertisable(false);
        dto.setIsOnlineCheckOn(true);
        dto.setDateOfBirth(LocalDate.of(1945, 4, 4));
        dto.setBonusesAmount(100);
        dto.setIsActive(true);
        dto.setIsProfileComplete(true);

        ClientResponseDto expectedResponseDto = new ClientResponseDto(
                dto.getName(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                false,
                dto.getIsOnlineCheckOn(),
                0,
                client.getCredentials().getLogin()
        );

        //when
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv->inv.getArgument(0));
        when(clientDtoMapper.toResponseDto(any(Client.class))).thenReturn(expectedResponseDto);

        ClientResponseDto actualResponseDto =
                clientService.updateClientByAdmin(client.getClientId(), dto);

        //then
        verify(clientRepository).findById(client.getClientId());
        verify(clientRepository).save(clientCaptor.capture());

        Client capturedClient = clientCaptor.getValue();

        verify(clientDtoMapper).toResponseDto(capturedClient);

        assertThat(capturedClient)
                .extracting(Client::getBonusesAmount,
                        Client::isActive,
                        Client::isProfileComplete
                )
                .containsExactly(client.getBonusesAmount(),
                client.isActive(), client.isProfileComplete());

        assertThat(actualResponseDto)
                .extracting(ClientResponseDto::name,
                        ClientResponseDto::email,
                        ClientResponseDto::phoneNumber,
                        ClientResponseDto::login,
                        ClientResponseDto::bonusesAmount,
                        ClientResponseDto::isOnlineCheckOn,
                        ClientResponseDto::isProfileComplete
                )
                .containsExactly(expectedResponseDto.name(),
                        expectedResponseDto.email(),
                        expectedResponseDto.phoneNumber(),
                        client.getCredentials().getLogin(),
                        expectedResponseDto.bonusesAmount(),
                        expectedResponseDto.isOnlineCheckOn(),
                        expectedResponseDto.isProfileComplete());
    }

    @Test
    public void activateClientAccountAsAdmin_ShouldSuccessfullyActivate(){

        //given
        client.setActive(false);

        //when
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv->inv.getArgument(0));

        clientService.activateClientAccountAsAdmin(client.getClientId());

        verify(clientRepository).findById(client.getClientId());
        verify(clientRepository).save(clientCaptor.capture());
        Client clientCaptured = clientCaptor.getValue();

        assertThat(clientCaptured.isActive()).isTrue();
        assertThat(client.isActive()).isTrue();

        //then
    }

    @Test
    public void changeClientPasswordAsAdmin_ShouldSucceed(){

        //given

        ClientAdminPasswordUpdateDTO dto = new ClientAdminPasswordUpdateDTO();
        dto.setNewPassword("123456");

        ClientResponseDto expectedResponseDto = new ClientResponseDto(
                client.getName(),
                client.getPhoneNumber(),
                client.getEmail(),
                false,
                client.isOnlineCheckOn(),
                0,
                client.getCredentials().getLogin()
        );

        //when
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(passwordEncoder.encode(any(String.class))).thenReturn("HashedPassword123");
        when(clientRepository.save(any(Client.class))).thenAnswer(inv->inv.getArgument(0));
        when(clientDtoMapper.toResponseDto(any(Client.class))).thenReturn(expectedResponseDto);

        ClientResponseDto actualResponseDto = clientService
                .changeClientPasswordAsAdmin(client.getClientId(), dto);

        verify(clientRepository).findById(client.getClientId());
        verify(passwordEncoder).encode(passwordCaptor.capture());
        verify(clientRepository).save(clientCaptor.capture());
        verify(clientDtoMapper).toResponseDto(any(Client.class));

        assertThat(passwordCaptor.getValue()).isEqualTo("123456");
        assertThat(clientCaptor.getValue().getClientId()).isEqualTo(client.getClientId());
        assertThat(client.getCredentials().getHashedPassword().equals("HashedPassword123"));

        assertThat(actualResponseDto)
                .extracting(ClientResponseDto::name,
                        ClientResponseDto::email,
                        ClientResponseDto::phoneNumber,
                        ClientResponseDto::login,
                        ClientResponseDto::bonusesAmount,
                        ClientResponseDto::isOnlineCheckOn,
                        ClientResponseDto::isProfileComplete
                )
                .containsExactly(expectedResponseDto.name(),
                        expectedResponseDto.email(),
                        expectedResponseDto.phoneNumber(),
                        client.getCredentials().getLogin(),
                        expectedResponseDto.bonusesAmount(),
                        expectedResponseDto.isOnlineCheckOn(),
                        expectedResponseDto.isProfileComplete());
    }



    @Test
    public void getAddresses_ShouldReturnListOfAddresses(){
        //given
        ClientAddress existingClientAddress1 = new ClientAddress(1,
                false, "Samara", "Lenina",
                "45", "32", "545332", client);

        ClientAddress existingClientAddress2 = new ClientAddress(1,
                true, "Moscow", "Pushkina",
                "55", "66", "343423", client);

        client.getClientAddresses().add(existingClientAddress1);
        client.getClientAddresses().add(existingClientAddress2);

        ClientAddressResponseDto responseDto1 = new ClientAddressResponseDto();
        responseDto1.setCity(existingClientAddress1.getCity());
        responseDto1.setStreet(existingClientAddress1.getStreet());
        responseDto1.setHouseNumber(existingClientAddress1.getHouseNumber());
        responseDto1.setApartment(existingClientAddress1.getApartment());
        responseDto1.setPostal_code(existingClientAddress1.getPostal_code());
        responseDto1.setDefault(existingClientAddress1.isDefault());

        ClientAddressResponseDto responseDto2 = new ClientAddressResponseDto();
        responseDto2.setCity(existingClientAddress2.getCity());
        responseDto2.setStreet(existingClientAddress2.getStreet());
        responseDto2.setHouseNumber(existingClientAddress2.getHouseNumber());
        responseDto2.setApartment(existingClientAddress2.getApartment());
        responseDto2.setPostal_code(existingClientAddress2.getPostal_code());
        responseDto2.setDefault(existingClientAddress2.isDefault());


        //when
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientAddressMapper.toResponseDto(existingClientAddress1)).thenReturn(responseDto1);
        when(clientAddressMapper.toResponseDto(existingClientAddress2)).thenReturn(responseDto2);

        List<ClientAddressResponseDto> addressList
                = clientService.getAddresses(client.getClientId());
        //then

        verify(clientRepository).findById(client.getClientId());
        verify(clientAddressMapper).toResponseDto(existingClientAddress1);
        verify(clientAddressMapper).toResponseDto(existingClientAddress2);

        assertThat(addressList)
                .hasSize(2).containsExactly(responseDto1, responseDto2);
    }

    @Test
    public void getAddresses_ShouldReturnEmptyListOfAddresses(){
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        List<ClientAddressResponseDto> addressList
                = clientService.getAddresses(client.getClientId());

        assertThat(addressList).isEmpty();

        verify(clientAddressMapper, never()).toResponseDto(any());
    }

    @Test
    public void searchClients_ShouldReturnPageOfClientDto(){

        Pageable pageable = PageRequest.of(0,10);

        //given
        ClientFilter clientFilter = new ClientFilter();
        clientFilter.setEmail("sally@yandex.ru");
        clientFilter.setName("Sally");
        clientFilter.setPhone("+88888888888");
        clientFilter.setIsActive(true);

        ClientResponseDto dto = new ClientResponseDto("Sally", "+88888888888",
                "sally@yandex.ru", false,
                false, 0, "sallykromsally");

        Page<Client> clientPage = new PageImpl<>(List.of(client), pageable, 1);

        PagedResponseDto<ClientResponseDto> expected = new PagedResponseDto<>();

        when(clientRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(clientPage);

        when(clientDtoMapper.toResponseDto(client)).thenReturn(dto);
        when(pageMapper.toPagedResponse(any(Page.class))).thenReturn(expected);

        //when
        PagedResponseDto<ClientResponseDto> pagedResponseDto =
                clientService.searchClients(clientFilter, pageable);

        //then
        verify(clientRepository).findAll(any(Specification.class), eq(pageable));
        verify(clientDtoMapper).toResponseDto(client);
        verify(pageMapper).toPagedResponse(any(Page.class));

        assertThat(pagedResponseDto).isEqualTo(expected);
    }
}

