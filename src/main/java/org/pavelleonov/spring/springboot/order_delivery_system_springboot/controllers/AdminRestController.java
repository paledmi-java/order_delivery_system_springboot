package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientAdminPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.admin.ClientUpdateAdminDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.ChangeOrderStatusRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.ClientFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.OrderService;
import org.springdoc.core.annotations.ParameterObject;
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
    public ClientResponseDto getUser
            (@PathVariable int id) {
        return clientService.getUser(id); //tested
    } //tested

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Обновить клиента")
    public ClientResponseDto updateUser
            (@PathVariable int id,
             @Valid @RequestBody ClientUpdateAdminDTO dto) {

        return clientService.updateClientByAdmin(id, dto);
    } //tested

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    @Operation(summary = "Получить список клиентов")
    public PagedResponseDto<ClientResponseDto> findUsers
            (ClientFilter filter,
             @ParameterObject Pageable pageable) {
        return clientService.searchClients(filter, pageable);
    } //tested

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Деактивировать аккаунт клиента")
    public ResponseEntity<Void> deactivateAccount(@PathVariable int id) {
        clientService.deactivateAccount(id);
        return ResponseEntity.noContent().build();
    } //tested

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать аккаунт клиента")
    public ResponseEntity<Void> activateAccount(@PathVariable int id) {
        clientService.activateClientAccountAsAdmin(id);
        return ResponseEntity.noContent().build();
    } //tested

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/password")
    @Operation(summary = "Изменить пароль клиента")
    public ClientResponseDto updateClientPassword
            (@PathVariable int id,
             @Valid @RequestBody ClientAdminPasswordUpdateDTO dto) {
        return clientService.changeClientPasswordAsAdmin(id, dto);
    } //tested

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    @Operation(summary = "Посмотреть заказы")
    public PagedResponseDto<OrderResponseDto> getOrders
            (@ParameterObject Pageable pageable) {
        return orderService.getAllOrders(pageable);
    } //tested


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/orders/{id}/status")
    @Operation(summary = "Изменить статус заказа")
    public OrderResponseDto changeOrderStatus
            (@PathVariable int id,
             @Valid @RequestBody ChangeOrderStatusRequestDto dto) {
        return orderService.changeOrderStatus(id, dto);
    } //tested

}
