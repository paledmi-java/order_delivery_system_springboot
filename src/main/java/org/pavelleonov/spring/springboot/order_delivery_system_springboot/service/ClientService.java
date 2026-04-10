package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.InvalidPasswordException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.RoleNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientAddressRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.RoleRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.specifications.ClientSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientDtoMapper clientDtoMapper;
    private final RoleRepository roleRepository;
    private final ClientAddressRepository clientAddressRepository;

    @Transactional
    public Client findClient(Integer clientId) {
        Client client = null;
        Optional<Client> optional = clientRepository.findById(clientId);
        if(optional.isPresent()){
            client = optional.get();
        }
        return client;
    }

    @Transactional
    public Client deactivateAccount(Client client) {
        client.setActive(false);
        clientRepository.save(client);
        return client;
    }

    @Transactional
    public Client activateAccount(ClientActivateDTO dto) {
        Client client = clientRepository.findByCredentialsLogin(dto.getLogin())
                .orElseThrow(()-> new ClientNotFoundException("Client not found"));
        client.setActive(true);
        clientRepository.save(client);
        return client;
    }

    @Transactional
    public Client activateAccountAsAdmin(int id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(()-> new ClientNotFoundException("Client not found"));
        client.setActive(true);
        clientRepository.save(client);
        return client;
    }


    @Transactional
    public Client updateBaseFields(Client client, BasicClientUpdateDTO dto){
        if(dto.getName() != null) client.setName(dto.getName());
        if(dto.getEmail() != null) client.setEmail(dto.getEmail());
        if(dto.getDateOfBirth() != null) client.setDateOfBirth(dto.getDateOfBirth());
        if(dto.getPhoneNumber() != null) client.setPhoneNumber(dto.getPhoneNumber());
        if(dto.getIsAdvertisable() != null) client.setAdvertisable(dto.getIsAdvertisable());
        if(dto.getIsOnlineCheckOn() !=null) client.setOnlineCheckOn(dto.getIsOnlineCheckOn());
        return client;
    }


    @Transactional
    public Client updateClientSelf(CustomUserDetails userDetails, ClientUpdateSelfDTO dto) {
        Client client = userDetails.getClient();
        updateBaseFields(client, dto);
        clientRepository.save(client);
        return client;
    }

    @Transactional
    public Client updateClientByAdmin(int id, ClientUpdateAdminDTO dto){
        Client client = clientRepository.findById(id)
                .orElseThrow(()-> new ClientNotFoundException("Client not found"));
        updateBaseFields(client, dto);

        if(dto.getIsActive() != null) client.setActive(dto.getIsActive());
        if(dto.getIsProfileComplete() != null) client.setProfileComplete(dto.getIsProfileComplete());
        if(dto.getBonusesAmount() != null) client.setBonusesAmount(dto.getBonusesAmount());
        clientRepository.save(client);
        return client;
    }

    @Transactional
    public void changePasswordSelf(CustomUserDetails userDetails, ClientPasswordUpdateDTO dto) {
        Client client = userDetails.getClient();
        Credentials credentials = client.getCredentials();

        if(!passwordEncoder.matches(dto.getOldPassword(), credentials.getHashedPassword())){
            throw new InvalidPasswordException("Old password is incorrect");
        }

        credentials.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
        client.setCredentials(credentials);
        clientRepository.save(client);
    }

    @Transactional
    public Client changePasswordAsAdmin(int id, ClientAdminPasswordUpdateDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
        Credentials credentials = client.getCredentials();

        credentials.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
        client.setCredentials(credentials);
        return clientRepository.save(client);
    }

    @Transactional
    public Client saveClient(ClientCreateDTO clientCreateDTO) {

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
                .credentials(credentials)
                .isActive(true)
                .build();

        Bucket bucket = new Bucket();
        client.setBucketAndClientToIt(bucket);

        if(client.getRoles() == null){
            client.setRoles(new HashSet<>());
        }
        client.getRoles().add(userRole);

        return clientRepository.save(client);
    }

    @Transactional
    public Page<ClientInfoDTO> searchClients(ClientFilter clientFilter, Pageable pageable) {
        Specification<Client> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if(clientFilter.getName() != null){
            specification = specification.and(ClientSpecification.hasName(clientFilter.getName()));
        }

        if(clientFilter.getEmail() != null){
            specification = specification.and(ClientSpecification.hasEmail(clientFilter.getEmail()));
        }

        if(clientFilter.getPhone() != null){
            specification = specification.and(ClientSpecification.hasPhone(clientFilter.getPhone()));
        }

        if (clientFilter.getIsActive() != null){
            specification = specification.and(ClientSpecification.hasIsActive(clientFilter.getIsActive()));
        }

        return clientRepository.findAll(specification, pageable)
                .map(c->clientDtoMapper.toInfoDto(c));
    }

    @Transactional
    public Client getClientByUsername(String username){
        return clientRepository.findByCredentialsLogin(username)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }

    @Transactional
    public ClientAddress addNewAddress(Client clientDto, ClientAddressRequestDto dto){
        Client client = clientRepository.findByCredentialsLogin(clientDto.getCredentials().getLogin())
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

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
        clientRepository.save(client);

        return clientAddress;
    }
}
