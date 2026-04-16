package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientAddressRepository extends JpaRepository<ClientAddress, Integer> {
    Optional<ClientAddress> findByClient(Client client);

    ClientAddress findByClientAndIsDefault(Client client, boolean isDefault);
}
