package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{

    ClientRepository clientRepository;
    PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
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
    public Client findClientByName(String name) {
        return clientRepository.findClientByName(name);
    }

    @Override
    public Optional findByCredentialsLogin(String login) {
        return clientRepository.findByCredentialsLogin(login);
    }

    @Override
    public Client saveClient(ClientDTO clientDTO) {

        String hashedPassword = passwordEncoder.encode(clientDTO.getPassword());

        Credentials credentials = Credentials.builder()
                .login(clientDTO.getLogin())
                .hashedPassword(hashedPassword)
                .build();

        Client client = Client.builder()
                .name(clientDTO.getName())
                .phoneNumber(clientDTO.getPhoneNumber())
                .email(clientDTO.getEmail())
                .login(clientDTO.getLogin())
                .password(clientDTO.getPassword())
                .credentials(credentials)
                .build();

        return clientRepository.save(client);
    }
}
