package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.InvalidPasswordException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.specifications.ClientSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{

    ClientRepository clientRepository;
    PasswordEncoder passwordEncoder;
    ClientDtoMapper clientDtoMapper;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder, ClientDtoMapper clientDtoMapper) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientDtoMapper = clientDtoMapper;
    }

    @Override
    public Client findClient(Integer clientId) {
        Client client = null;
        Optional<Client> optional = clientRepository.findById(clientId);
        if(optional.isPresent()){
            client = optional.get();
        }
        return client;
    }

    @Override
    public Client deactivateAccount(Client client) {
        client.setActive(false);
        clientRepository.save(client);
        return client;
    }

    @Override
    public Client activateAccount(Client client) {
        client.setActive(true);
        clientRepository.save(client);
        return client;
    }

    public Client updateBaseFields(Client client, BasicClientUpdateDTO dto){
        if(dto.getName() != null) client.setName(dto.getName());
        if(dto.getEmail() != null) client.setEmail(dto.getEmail());
        if(dto.getDateOfBirth() != null) client.setDateOfBirth(dto.getDateOfBirth());
        if(dto.getPhoneNumber() != null) client.setPhoneNumber(dto.getPhoneNumber());
        if(dto.getIsAdvertisable() != null) client.setAdvertisable(dto.getIsAdvertisable());
        if(dto.getIsOnlineCheckOn() !=null) client.setOnlineCheckOn(dto.getIsOnlineCheckOn());
        return client;
    }


    @Override
    public Client updateClientSelf(CustomUserDetails userDetails, ClientUpdateSelfDTO dto) {
        Client client = userDetails.getClient();
        updateBaseFields(client, dto);
        clientRepository.save(client);
        return client;
    }

    @Override
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

    @Override
    public Client changePasswordSelf(CustomUserDetails userDetails, ClientPasswordUpdateDTO dto) {
        Client client = userDetails.getClient();
        Credentials credentials = client.getCredentials();

        if(!passwordEncoder.matches(dto.getOldPassword(), credentials.getHashedPassword())){
            throw new InvalidPasswordException("Old password is incorrect");
        }

        credentials.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
        client.setCredentials(credentials);
        return clientRepository.save(client);
    }

    @Override
    public Client changePasswordAsAdmin(int id, String password) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
        Credentials credentials = client.getCredentials();

        credentials.setHashedPassword(passwordEncoder.encode(password));
        client.setCredentials(credentials);
        return clientRepository.save(client);
    }

    @Override
    public Client saveClient(ClientCreateDTO clientCreateDTO) {

        String hashedPassword = passwordEncoder.encode(clientCreateDTO.getPassword());

        Credentials credentials = Credentials.builder()
                .login(clientCreateDTO.getLogin())
                .hashedPassword(hashedPassword)
                .build();

        Client client = Client.builder()
                .name(clientCreateDTO.getName())
                .phoneNumber(clientCreateDTO.getPhoneNumber())
                .email(clientCreateDTO.getEmail())
                .credentials(credentials)
                .build();

        return clientRepository.save(client);
    }

    @Override
    public Page<ClientInfoDTO> searchClients(ClientFilter clientFilter, Pageable pageable) {
        Specification<Client> specification = null;
        specification = Specification.where(specification);

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
}
