package org.pavelleonov.spring.springboot.order_delivery_system_springboot;

import org.junit.jupiter.api.Test;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OrderDeliverySystemSpringbootApplicationTests {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testPassword() {
        Client client = clientRepository.findById(1).orElseThrow();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches("qwerty", client.getCredentials().getHashedPassword());

        System.out.println("Пароль совпадает? " + matches);
        assertTrue(matches);
    }

    @Test
    void testPasswordHash() {
        Client client = clientRepository.findById(1).orElseThrow();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches("qwerty", client.getCredentials().getHashedPassword());

        System.out.println("Пароль совпадает? " + matches);
        assertTrue(matches);
    }

    @Test
    void testPasswordMatchesHash() {
        String rawPassword = "qwerty"; // пароль, который вводишь в Postman

        // Пример хэша из базы
        String hashedPassword = "$2a$10$I7pPUomucE/NuS1nV1kUAukBZc.e9cczz8pMWPtuZHKROHFBhjgnS";

        PasswordEncoder encoder = new BCryptPasswordEncoder();

        assertTrue(encoder.matches(rawPassword, hashedPassword), "Пароль не совпадает с хэшем!");
    }

    @Test
    void generateHash() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "qwerty"; // твой пароль
        String hash = encoder.encode(rawPassword);

        System.out.println(hash);
    }

    @Test
    void contextLoads() {
    }

}
