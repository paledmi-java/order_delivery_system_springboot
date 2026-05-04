package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientAddressMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientAddressRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.RoleRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.specifications.ClientSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final PasswordEncoder passwordEncoder;

    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final ClientAddressRepository clientAddressRepository;

    private final ClientDtoMapper clientDtoMapper;
    private final ClientAddressMapper clientAddressMapper;
    private final PageMapper pageMapper;


    @Transactional
    public Client findClientById(int id) {

        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client with id = {} not found", id);
                    return new ClientNotFoundException("Client not found");
                });
    } //tested


    @Transactional
    public ClientResponseDto getUser(int id) {
        Client client = findClientById(id);
        return clientDtoMapper.toResponseDto(client);
    } //tested

    @Transactional
    public void deactivateAccount(int id) {
        Client client = findClientById(id);
        log.info("Deactivating client with id={}", id);
        client.setActive(false);
    }  //tested


    // Поменять на mapStruct
    @Transactional
    public Client updateBaseFields(int id, BasicClientUpdateDTO dto) {

        Client client = findClientById(id);
        StringBuilder changes = new StringBuilder();

        if (dto.getName() != null) {
            client.setName(dto.getName());
            changes.append("name, ");
        }

        if (dto.getEmail() != null) {
            client.setEmail(dto.getEmail());
            changes.append("email, ");
        }

        if (dto.getPhoneNumber() != null) {
            client.setPhoneNumber(dto.getPhoneNumber());
            changes.append("phone, ");
        }

        if (dto.getDateOfBirth() != null) {
            client.setDateOfBirth(dto.getDateOfBirth());
            changes.append("dateOfBirth, ");
        }

        if (dto.getIsAdvertisable() != null) {
            client.setAdvertisable(dto.getIsAdvertisable());
            changes.append("IsAdvertisable, ");
        }

        if (dto.getIsOnlineCheckOn() != null) {
            client.setOnlineCheckOn(dto.getIsOnlineCheckOn());
            changes.append("IsOnlineCheckOn, ");
        }

        if (!changes.isEmpty()) {
            log.info("Updated client id = {}, fields = [{}]", id, changes);
        }
        return client;
    } //tested


    // CLIENT
    @Transactional
    public void activateAccount(int id, ClientActivateDTO dto) {
        log.debug("Attempt to activate client with id = {}", id);
        Client client = findClientById(id);
        String clientLogin = client.getCredentials().getLogin();
        String clientPassword = client.getCredentials().getHashedPassword();

        if (!dto.login.equals(clientLogin)
                || !passwordEncoder.matches(dto.password, clientPassword)) {
            log.warn("Failed activation attempt for client with id = {}", id);
            throw new WrongCredentialsException("Wrong credentials");
        }
        log.info("Setting active client with id = {}", id);
        client.setActive(true);
        clientRepository.save(client);
    } //tested

    @Transactional
    public ClientResponseDto updateClientSelf(int id, ClientUpdateSelfDTO dto) {
        Client client = updateBaseFields(id, dto);
        clientRepository.save(client);
        return clientDtoMapper.toResponseDto(client);
    } //tested

    @Transactional
    public void changePasswordSelf(int id, ClientPasswordUpdateDTO dto)  {
        Client client = findClientById(id);
        Credentials credentials = client.getCredentials();
        if (!passwordEncoder.matches(dto.getOldPassword(), credentials.getHashedPassword())) {
            log.warn("Failed password change attempt for client id = {}", id);
            throw new InvalidPasswordException("Old password is incorrect");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), credentials.getHashedPassword())) {
            log.warn("Failed password change attempt for client id = {}", id);
            throw new InvalidPasswordException("New password can't be same as old one");
        }

        log.info("Password changed for client id = {}", id);
        credentials.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
    } //tested

    @Transactional
    public ClientResponseDto saveClient(ClientCreateDTO clientCreateDTO) {

        log.info("Creating new client with email = {}", clientCreateDTO.getEmail());
        String hashedPassword = passwordEncoder.encode(clientCreateDTO.getPassword());

        Credentials credentials = Credentials.builder()
                .login(clientCreateDTO.getLogin())
                .hashedPassword(hashedPassword)
                .build();

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> {
                    log.warn("Required role 'ROLE_USER' not found in database");
                    return new RoleNotFoundException("Role not found");
                });

        Client client = Client.builder()
                .name(clientCreateDTO.getName())
                .phoneNumber(clientCreateDTO.getPhoneNumber())
                .email(clientCreateDTO.getEmail())
                .isActive(true)
                .build();

        client.addCredentialsToClient(credentials);

        Bucket bucket = new Bucket();
        client.setBucketAndClientToIt(bucket);

        client.getRoles().add(userRole);

        Client savedClient = clientRepository.save(client);
        log.info("Created new client id = {}", savedClient.getClientId());
        return clientDtoMapper.toResponseDto(savedClient);
    } //tested


    @Transactional
    public PagedResponseDto<ClientResponseDto> searchClients
            (ClientFilter clientFilter, Pageable pageable) {

        log.info("Searching clients: name = {}, email = {}, " +
                        "phone = {}, isActive = {}, page = {}, size = {}",
                clientFilter.getName(),
                clientFilter.getEmail(),
                clientFilter.getPhone(),
                clientFilter.getIsActive(),
                pageable.getPageNumber(),
                pageable.getPageSize());

        Specification<Client> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (clientFilter.getName() != null)
            specification = specification.and(ClientSpecification.hasName(clientFilter.getName()));

        if (clientFilter.getEmail() != null)
            specification = specification.and(ClientSpecification.hasEmail(clientFilter.getEmail()));

        if (clientFilter.getPhone() != null)
            specification = specification.and(ClientSpecification.hasPhone(clientFilter.getPhone()));

        if (clientFilter.getIsActive() != null)
            specification = specification.and(ClientSpecification.hasIsActive(clientFilter.getIsActive()));

        Page<ClientResponseDto> dtos = clientRepository.findAll(specification, pageable)
                .map(clientDtoMapper::toResponseDto);

        log.info("Found {} clients", dtos.getNumberOfElements());
        return pageMapper.toPagedResponse(dtos);
    } //tested

    @Transactional
    public ClientAddressResponseDto addNewAddress
            (int id, ClientAddressRequestDto dto) {

        log.info("Creating new address for client id = {}", id);
        Client client = findClientById(id);

        ClientAddress clientAddress = ClientAddress
                .builder()
                .client(client)
                .city(dto.city())
                .apartment(dto.apartment())
                .houseNumber(dto.houseNumber())
                .street(dto.street())
                .postal_code(dto.postalCode())
                .build();

        if (dto.isDefault()) {
            if (client.getClientAddresses().stream().anyMatch(ClientAddress::isDefault)) {
                client.getClientAddresses().stream().filter(ClientAddress::isDefault)
                        .findAny().ifPresent(ca -> ca.setDefault(false));
                clientAddress.setDefault(true);
            } else clientAddress.setDefault(true);
        } else {
            clientAddress.setDefault(false);
        }

        ClientAddress savedClientAddress = clientAddressRepository.save(clientAddress);
        log.info("Created new address id = {} for client id = {}", savedClientAddress.getId(), id);
        client.getClientAddresses().add(savedClientAddress);
        return clientAddressMapper.toResponseDto(savedClientAddress);
    } //tested


    @Transactional
    public List<ClientAddressResponseDto> getAddresses(int id){
        Client client = findClientById(id);
        log.info("Getting addresses for client id = {}", id);
        return client.getClientAddresses().stream()
                .map(clientAddressMapper::toResponseDto)
                .toList();
    } //tested


    //ADMIN

    @Transactional
    public ClientResponseDto updateClientByAdmin(int id, ClientUpdateAdminDTO dto) {

        log.info("Updating client id = {}", id);
        StringBuilder stringBuilder = new StringBuilder();

        Client client = updateBaseFields(id, dto);

        if (dto.getIsActive() != null) {
            client.setActive(dto.getIsActive());
            stringBuilder.append("isActive, ");
        }
        if (dto.getIsProfileComplete() != null) {
            client.setProfileComplete(dto.getIsProfileComplete());
            stringBuilder.append("isProfileComplete, ");
        }
        if (dto.getBonusesAmount() != null) {
            client.setBonusesAmount(dto.getBonusesAmount());
            stringBuilder.append("bonusesAmount, ");
        }

        Client savedClient = clientRepository.save(client);
        if (!stringBuilder.isEmpty()) {
            log.info("Updated client id = {} fields = [{}] (admin fields)", id, stringBuilder);
        }

        return clientDtoMapper.toResponseDto(savedClient);
    } //tested

    @Transactional
    public void activateClientAccountAsAdmin(int id) {
        Client client = findClientById(id);
        client.setActive(true);
        log.info("Set client active id = {} as admin", id);
        clientRepository.save(client);
    } //tested

    @Transactional
    public ClientResponseDto changeClientPasswordAsAdmin
            (int id, ClientAdminPasswordUpdateDTO dto) {
        Client client = findClientById(id);
        Credentials credentials = client.getCredentials();
        credentials.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
        client.addCredentialsToClient(credentials);
        log.info("Changed client password id = {} as admin", id);
        return clientDtoMapper.toResponseDto(clientRepository.save(client));
    } //tested
}
