package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findClientByName(String name); // Make Optional return
    Optional<Client> findByCredentialsLogin(String login);
}
