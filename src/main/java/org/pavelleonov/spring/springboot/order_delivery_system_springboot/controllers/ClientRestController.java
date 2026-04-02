package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import jakarta.validation.Valid;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientUpdateSelfDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    GET /api/users/me/orders
 */

@RestController
public class ClientRestController {

    ClientService clientService;
    ClientDtoMapper clientDtoMapper;

    public ClientRestController(ClientService clientService,
                                ClientDtoMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }

    @GetMapping("/api/users/me")
    public ClientViewDTO getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Client client = customUserDetails.getClient();
        return clientDtoMapper.toViewDto(client);
    }

    @PostMapping("/api/auth/register")
    public ClientViewDTO register(@Valid @RequestBody ClientCreateDTO dto){
        Client client = clientService.saveClient(dto);
        return clientDtoMapper.toViewDto(client);
    }

    @PatchMapping("/api/users/me/settings")
    public ClientViewDTO updateClientSelf(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @Valid @RequestBody ClientUpdateSelfDTO clientUpdateSelfDTO){

        Client client = clientService.updateClientSelf(customUserDetails, clientUpdateSelfDTO);
        return clientDtoMapper.toViewDto(client);
    }


    @PostMapping("/api/users/me/settings/password")
    public ClientViewDTO updateClientPassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestBody ClientPasswordUpdateDTO dto){
        clientService.changePassword(customUserDetails, dto);
        return clientDtoMapper.toViewDto(customUserDetails.getClient());
    }

    @PatchMapping("/api/users/me/settings/deactivate")
    public ClientViewDTO deactivateAccount(@AuthenticationPrincipal CustomUserDetails userDetails){
        Client client = userDetails.getClient();
       return clientDtoMapper.toViewDto(clientService.deactivateAccount(client));
    }

    @PatchMapping("/api/users/me/activate")
    public ClientViewDTO activateAccount(@AuthenticationPrincipal CustomUserDetails userDetails){
        Client client = userDetails.getClient();
        return clientDtoMapper.toViewDto(clientService.activateAccount(client));
    }

}
