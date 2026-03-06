package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{

    ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
}
