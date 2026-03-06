package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;

public interface ClientService {
    Client findClient(Integer clientId);
    Client findClientByName(String name);
}
