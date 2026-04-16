package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.ChangeOrderStatusRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientAccountIsInactiveException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.InvalidPasswordException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.RoleNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientAddressMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
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

@Service
@RequiredArgsConstructor
public class ClientService {


    private final PasswordEncoder passwordEncoder;

    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;

    private final ClientDtoMapper clientDtoMapper;
    private final ClientAddressMapper clientAddressMapper;
    private final PageMapper pageMapper;


    private void ensureClientIsActive(Client client){
        if (!client.isActive()){
            throw new ClientAccountIsInactiveException("Client account is inactive");
        }
    }

    @Transactional
    public Client findClientById(int id){
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }

    @Transactional
    public ClientResponseDto getUser(int id){
        Client client = findClientById(id);
        return clientDtoMapper.toResponseDto(client);
    }

    @Transactional
    public void deactivateAccount(int id) {
        Client client = findClientById(id);
        client.setActive(false);
    }


    // Поменять на mapStruct
    @Transactional
    public Client updateBaseFields(int id, BasicClientUpdateDTO dto){

        Client client = findClientById(id);

        if(dto.getName() != null) client.setName(dto.getName());
        if(dto.getEmail() != null) client.setEmail(dto.getEmail());
        if(dto.getDateOfBirth() != null) client.setDateOfBirth(dto.getDateOfBirth());
        if(dto.getPhoneNumber() != null) client.setPhoneNumber(dto.getPhoneNumber());
        if(dto.getIsAdvertisable() != null) client.setAdvertisable(dto.getIsAdvertisable());
        if(dto.getIsOnlineCheckOn() !=null) client.setOnlineCheckOn(dto.getIsOnlineCheckOn());

        return client;
    }


    // CLIENT
    @Transactional
    public ClientResponseDto activateAccount(ClientActivateDTO dto) {
        Client client = clientRepository.findByCredentialsLogin(dto.getLogin())
                .orElseThrow(()-> new ClientNotFoundException("Client not found"));

        client.setActive(true);
        return clientDtoMapper.toResponseDto(clientRepository.save(client));
    }

    @Transactional
    public ClientResponseDto updateClientSelf(int id, ClientUpdateSelfDTO dto) {
        Client client = updateBaseFields(id, dto);
        clientRepository.save(client);
        return clientDtoMapper.toResponseDto(client);
    }

    @Transactional
    public void changePasswordSelf(int id, ClientPasswordUpdateDTO dto) {
        Client client = findClientById(id);
        Credentials credentials = client.getCredentials();

        if(!passwordEncoder.matches(dto.getOldPassword(), credentials.getHashedPassword())){
            throw new InvalidPasswordException("Old password is incorrect");
        }

        if(passwordEncoder.matches(dto.getNewPassword(), credentials.getHashedPassword())){
            throw new InvalidPasswordException("New password cant be same as new one");
        }

//        client.addCredentialsToClient(credentials);
        credentials.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    @Transactional
    public ClientResponseDto saveClient(ClientCreateDTO clientCreateDTO) {

        String hashedPassword = passwordEncoder.encode(clientCreateDTO.getPassword());

        Credentials credentials = Credentials.builder()
                .login(clientCreateDTO.getLogin())
                .hashedPassword(hashedPassword)
                .build();

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new RoleNotFoundException("Role not found"));

        Client client = Client.builder()
                .name(clientCreateDTO.getName())
                .phoneNumber(clientCreateDTO.getPhoneNumber())
                .email(clientCreateDTO.getEmail())
                .isActive(true)
                .build();

        client.addCredentialsToClient(credentials);

        Bucket bucket = new Bucket();
        client.setBucketAndClientToIt(bucket);

        if(client.getRoles() == null){
            client.setRoles(new HashSet<>());
        }
        client.getRoles().add(userRole);

        return clientDtoMapper.toResponseDto(clientRepository.save(client));
    }

    @Transactional
    public PagedResponseDto<ClientResponseDto> searchClients(ClientFilter clientFilter, Pageable pageable) {
        Specification<Client> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if(clientFilter.getName() != null)
            specification = specification.and(ClientSpecification.hasName(clientFilter.getName()));

        if(clientFilter.getEmail() != null)
            specification = specification.and(ClientSpecification.hasEmail(clientFilter.getEmail()));

        if(clientFilter.getPhone() != null)
            specification = specification.and(ClientSpecification.hasPhone(clientFilter.getPhone()));

        if (clientFilter.getIsActive() != null)
            specification = specification.and(ClientSpecification.hasIsActive(clientFilter.getIsActive()));

        Page<ClientResponseDto> dtos =  clientRepository.findAll(specification, pageable)
                .map(clientDtoMapper::toResponseDto);

        return pageMapper.toPagedResponse(dtos);
    }

    @Transactional
    public ClientAddressResponseDto addNewAddress(int id, ClientAddressRequestDto dto){
        Client client = findClientById(id);

        ClientAddress clientAddress = ClientAddress
                .builder()
                .client(client)
                .city(dto.city())
                .apartment(dto.apartment())
                .houseNumber(dto.houseNumber())
                .street(dto.street())
                .isDefault(dto.isDefault())
                .postal_code(dto.postalCode())
                .build();

        client.getClientAddresses().add(clientAddress);

        return clientAddressMapper.toResponseDto(clientAddress);
    }


    @Transactional
    public List<ClientAddressResponseDto> getAddresses(int id){
        Client client = findClientById(id);

        return client.getClientAddresses().stream()
                .map(clientAddressMapper::toResponseDto)
                .toList();
    }


    //ADMIN

    @Transactional
    public ClientResponseDto updateClientByAdmin(int id, ClientUpdateAdminDTO dto){

        Client client = updateBaseFields(id, dto);

        if(dto.getIsActive() != null) client.setActive(dto.getIsActive());
        if(dto.getIsProfileComplete() != null) client.setProfileComplete(dto.getIsProfileComplete());
        if(dto.getBonusesAmount() != null) client.setBonusesAmount(dto.getBonusesAmount());
        clientRepository.save(client);
        return clientDtoMapper.toResponseDto(client);
    }

    @Transactional
    public ClientResponseDto activateClientAccountAsAdmin(int id) {
        Client client = findClientById(id);
        client.setActive(true);
        return clientDtoMapper.toResponseDto(clientRepository.save(client));
    }

    @Transactional
    public ClientResponseDto changeClientPasswordAsAdmin(int id, ClientAdminPasswordUpdateDTO dto) {
        Client client = findClientById(id);
        Credentials credentials = client.getCredentials();

        credentials.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
        client.addCredentialsToClient(credentials);
        return clientDtoMapper.toResponseDto(clientRepository.save(client));
    }
}
