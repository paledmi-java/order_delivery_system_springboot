package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepositoryTest;

    @BeforeEach
    void setUp() {
        clientRepositoryTest.deleteAll();
    }

    @Test
    void itShouldFindByCredentialsLogin() {
        Client client = new Client();
        Credentials credentials = new Credentials();
        credentials.setLogin("mikewazowski");
        credentials.setHashedPassword("qwerty");

        client.setCredentials(credentials);
        client.setEmail("mikewazowski@yandex.ru");
        client.setName("Mike Wazowski");
        client.setDateOfBirth(LocalDate.of(1995, 4, 23));
        client.setPhoneNumber("+89574837483");

        clientRepositoryTest.save(client);

        final Optional<Client> result
                = clientRepositoryTest.findByCredentialsLogin("mikewazowski");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(c->c.getCredentials().getLogin())
                .isEqualTo("mikewazowski");
    }

    @Test
    void itShouldNotFindByCredentialsLogin() {

        final Optional<Client> result
                = clientRepositoryTest.findByCredentialsLogin("mikewazowski");

        assertThat(result)
                .isNotPresent();
    }

    @AfterEach
    void tearDown() {

    }
}