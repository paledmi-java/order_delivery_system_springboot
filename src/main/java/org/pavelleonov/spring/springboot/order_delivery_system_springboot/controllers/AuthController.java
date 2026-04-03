package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.LoginRequest;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientActivateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ClientDtoMapper clientDtoMapper;
    private final ClientService clientService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword())
                );

        // если сюда дошли — значит логин успешный
        return "OK"; // позже заменим на JWT
    }

    @PostMapping("/register")
    public ClientViewDTO register(@Valid @RequestBody ClientCreateDTO dto){
        Client client = clientService.saveClient(dto);
        return clientDtoMapper.toViewDto(client);
    }

    @PatchMapping("/activate")
    public ClientViewDTO activateAccount(@RequestBody ClientActivateDTO dto){
        Client client = clientService.activateAccount(dto);
        return clientDtoMapper.toViewDto(client);
    }
}
