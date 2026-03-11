package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;

import java.util.Optional;

public interface ClientService {
    Client findClient(Integer clientId);
    Client findClientByName(String name);
    Client saveClient(ClientDTO clientDTO);
    Optional<Client> findByCredentialsLogin(String login);
}
