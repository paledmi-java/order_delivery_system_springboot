package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientUpdateSelfDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/*
    GET /api/users/me/orders
 */

@RestController
@RequestMapping("/api/users")
public class ClientRestController {

    ClientService clientService;
    ClientDtoMapper clientDtoMapper;

    public ClientRestController(ClientService clientService,
                                ClientDtoMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }

    @GetMapping("/me")
    @Operation(summary = "Получить текущего пользователя")
    public ClientViewDTO getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Client client = customUserDetails.getClient();
        return clientDtoMapper.toViewDto(client);
    }

    @PatchMapping("/me/settings")
    @Operation(summary = "Обновить текущего пользователя")
    public ClientViewDTO updateClientSelf(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @Valid @RequestBody ClientUpdateSelfDTO clientUpdateSelfDTO){

        Client client = clientService.updateClientSelf(customUserDetails, clientUpdateSelfDTO);
        return clientDtoMapper.toViewDto(client);
    }


    @PostMapping("/me/settings/password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ClientViewDTO updateClientPassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestBody ClientPasswordUpdateDTO dto){
        clientService.changePasswordSelf(customUserDetails, dto);
        return clientDtoMapper.toViewDto(customUserDetails.getClient());
    }

    @PatchMapping("/me/settings/deactivate")
    @Operation(summary = "Деактивировать текущего пользователя")
    public ClientViewDTO deactivateAccount(@AuthenticationPrincipal CustomUserDetails userDetails){
        Client client = userDetails.getClient();
        return clientDtoMapper.toViewDto(clientService.deactivateAccount(client));
    }


}
