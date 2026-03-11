package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialsRepository extends JpaRepository<Credentials, String> {
    Optional<Credentials> findByLogin(String login);
}
