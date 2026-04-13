package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientAdminPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AdminRestController {

    private final ClientService clientService;
    private final OrderService orderService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить клиента")
    public ClientResponseDto getUser(@PathVariable int id) {
        return clientService.getUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Обновить клиента")
    public ClientResponseDto updateUser(@PathVariable int id,
                                        @RequestBody ClientUpdateAdminDTO dto) {

        return clientService.updateClientByAdmin(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    @Operation(summary = "Получить список клиентов")
    public Page<ClientResponseDto> findUsers(ClientFilter filter, Pageable pageable) {
        return clientService.searchClients(filter, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Деактивировать аккаунт клиента")
    public ResponseEntity<Void> deactivateAccount(@PathVariable int id){
        clientService.deactivateAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать аккаунт клиента")
    public ClientResponseDto activateAccount(@PathVariable int id){
        return clientService.activateClientAccountAsAdmin(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/password")
    @Operation(summary = "Изменить пароль клиента")
    public ClientResponseDto updateClientPassword(@PathVariable int id,
                                                  @RequestBody ClientAdminPasswordUpdateDTO dto){
        return clientService.changeClientPasswordAsAdmin(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    @Operation(summary = "Посмотреть заказы")
    public Page<OrderResponseDto> getOrders(@RequestParam int page,
                                            @RequestParam int size){
        return orderService.getAllOrders(page, size);
    }


}
