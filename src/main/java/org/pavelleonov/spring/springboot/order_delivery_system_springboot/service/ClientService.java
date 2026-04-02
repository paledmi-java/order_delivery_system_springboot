package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientInfoDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientUpdateSelfDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client findClient(Integer clientId);
    Client saveClient(ClientCreateDTO clientCreateDTO);
    Page<ClientInfoDTO> searchClients(ClientFilter filter, Pageable pageable);
    Client updateClientSelf(CustomUserDetails userDetails, ClientUpdateSelfDTO clientUpdateSelfDTO);
    Client changePasswordSelf(CustomUserDetails userDetails, ClientPasswordUpdateDTO dto);
    Client changePasswordAsAdmin(int id, String password);
    Client deactivateAccount(Client client);
    Client activateAccount(Client client);
    Client updateClientByAdmin(int id, ClientUpdateAdminDTO dto);


//    Optional<Client> findByCredentialsLogin(String login);
//    void addItemToClientBasket(Item item);
//    Optional<Client> findClientByName(String name);
}
