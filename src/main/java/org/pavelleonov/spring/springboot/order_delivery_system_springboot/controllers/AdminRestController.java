package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientAdminPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientInfoDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AdminRestController {

    ClientService clientService;
    ClientDtoMapper clientDtoMapper;

    public AdminRestController(ClientService clientService, ClientDtoMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить клиента")
    public ClientViewDTO getUser(@PathVariable int id) {
        Client client = clientService.findClient(id);
        return clientDtoMapper.toViewDto(client);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Обновить клиента")
    public ClientViewDTO updateUser(@PathVariable int id,
                                    @RequestBody ClientUpdateAdminDTO dto) {

        Client client = clientService.updateClientByAdmin(id, dto);
        return clientDtoMapper.toViewDto(client);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    @Operation(summary = "Получить список клиентов")
    public Page<ClientInfoDTO> getUsers(ClientFilter filter, Pageable pageable) {
        return clientService.searchClients(filter, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Деактивировать аккаунт клиента")
    public ClientViewDTO deactivateAccount(@PathVariable int id){
        Client client = clientService.findClient(id);
        return clientDtoMapper.toViewDto(clientService.deactivateAccount(client));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать аккаунт клиента")
    public ClientViewDTO activateAccount(@PathVariable int id){
        Client client = clientService.findClient(id);
        return clientDtoMapper.toViewDto(clientService.activateAccountAsAdmin(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/password")
    @Operation(summary = "Изменить пароль клиента")
    public ClientViewDTO updateClientPassword(@PathVariable int id,
                                              @RequestBody ClientAdminPasswordUpdateDTO dto){
        Client client = clientService.changePasswordAsAdmin(id, dto);
        return clientDtoMapper.toViewDto(client);
    }
}
