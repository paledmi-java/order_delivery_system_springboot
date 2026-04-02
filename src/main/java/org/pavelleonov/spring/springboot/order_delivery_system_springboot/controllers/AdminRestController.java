package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientInfoDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminRestController {

    ClientService clientService;
    ClientDtoMapper clientDtoMapper;

    public AdminRestController(ClientService clientService, ClientDtoMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }

    @GetMapping("/api/users/{id}")
    public ClientViewDTO getUser(@PathVariable int id) {
        Client client = clientService.findClient(id);
        return clientDtoMapper.toViewDto(client);
    }

    @PatchMapping("/api/users/{id}")
    public ClientViewDTO updateUser(@PathVariable int id,
                                    @RequestBody ClientUpdateAdminDTO dto) {

        Client client = clientService.updateClientByAdmin(id, dto);
        return clientDtoMapper.toViewDto(client);
    }

    @GetMapping("/api/users")
    public Page<ClientInfoDTO> getUsers(ClientFilter filter, Pageable pageable) {
        return clientService.searchClients(filter, pageable);
    }

    @PatchMapping("/api/users/{id}/deactivate")
    public ClientViewDTO deactivateAccount(@PathVariable int id){
        Client client = clientService.findClient(id);
        return clientDtoMapper.toViewDto(clientService.deactivateAccount(client));
    }

    @PatchMapping("/api/users/{id}/activate")
    public ClientViewDTO activateAccount(@PathVariable int id){
        Client client = clientService.findClient(id);
        return clientDtoMapper.toViewDto(clientService.activateAccount(client));
    }

    @PostMapping("/api/users/{id}/password")
    public ClientViewDTO updateClientPassword(@PathVariable int id, String password){
        Client client = clientService.changePasswordAsAdmin(id, password);
        return clientDtoMapper.toViewDto(client);
    }
}
