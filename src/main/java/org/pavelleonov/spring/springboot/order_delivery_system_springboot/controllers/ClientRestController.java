package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.AddItemToBucketRequestDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.RemoveItemToBucketRequestDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientActivateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientUpdateSelfDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.CreateOrderRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.BucketService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.OrderService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ClientRestController {

    private final ClientService clientService;
    private final BucketService bucketService;
    private final OrderService orderService;

    @GetMapping("/me")
    @Operation(summary = "Домашняя страницу пользователя")
    public ClientResponseDto getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return clientService.getUser(userDetails.getId());
    } //tested

    @PatchMapping("/me/settings")
    @Operation(summary = "Обновить свой аккаунт")
    public ClientResponseDto updateClientSelf
            (@AuthenticationPrincipal CustomUserDetails userDetails,
             @Valid @RequestBody ClientUpdateSelfDTO clientUpdateSelfDTO) {

        return clientService.updateClientSelf(userDetails.getId(), clientUpdateSelfDTO);
    } //tested


    @PostMapping("/me/settings/password")
    @Operation(summary = "Изменить свой пароль")
    public ResponseEntity<Void> updateClientPassword
            (@AuthenticationPrincipal CustomUserDetails userDetails,
             @Valid @RequestBody ClientPasswordUpdateDTO dto) {
        clientService.changePasswordSelf(userDetails.getId(), dto);
        return ResponseEntity.noContent().build();
    } //tested

    @PatchMapping("/me/settings/deactivate")
    @Operation(summary = "Деактивировать свой аккаунт")
    public ResponseEntity<Void> deactivateAccount
            (@AuthenticationPrincipal CustomUserDetails userDetails) {
        clientService.deactivateAccount(userDetails.getId());
        return ResponseEntity.noContent().build();
    } //tested

    @PatchMapping("/me/settings/activate")
    @Operation(summary = "Активировать свой аккаунт")
    public ResponseEntity<Void> activateAccount
            (@AuthenticationPrincipal CustomUserDetails userDetails,
             @RequestBody ClientActivateDTO dto) {
        clientService.activateAccount(userDetails.getId(), dto);
        return ResponseEntity.noContent().build();
    } //tested

    @GetMapping("/me/bucket")
    @Operation(summary = "Открыть свою корзину")
    public List<BucketItemDto> openBucket
            (@AuthenticationPrincipal CustomUserDetails userDetails) {
        return bucketService.openBucket(userDetails.getId());
    } //tested

    @PostMapping("/me/bucket/add")
    @Operation(summary = "Добавить товар в корзину")
    public BucketItemDto addBucketItem(@Valid @RequestBody AddItemToBucketRequestDTO dto,
                                       @AuthenticationPrincipal
                                       CustomUserDetails userDetails) {
        return bucketService.addItemToBucket
                (userDetails.getId(),
                        dto.getItemId(), dto.getQuantity());
    } //tested

    @PatchMapping("/me/bucket/remove/")
    @Operation(summary = "Удалить товар из корзины")
    public ResponseEntity<Void> removeBucketItem
            (@Valid @RequestBody RemoveItemToBucketRequestDTO dto,
             @AuthenticationPrincipal
             CustomUserDetails userDetails) {
        bucketService.removeItemFromBucket
                (userDetails.getId(), dto);
        return ResponseEntity.noContent().build();
    } //tested


    // ИСПРАВИТЬ РЕКУРСИЮ
    @PostMapping("/me/bucket/order")
    @Operation(summary = "Сделать заказ")
    public OrderResponseDto makeAnOrder
    (@AuthenticationPrincipal
     CustomUserDetails userDetails,
     @Valid @RequestBody CreateOrderRequestDto dto) {
        return orderService.makeAnOrder(userDetails.getId(), dto);
    }//tested

    @PostMapping("/me/settings/addresses")
    @Operation(summary = "Добавить новый адрес доставки")
    public ClientAddressResponseDto addNewAddress
            (@AuthenticationPrincipal
             CustomUserDetails userDetails,
             @Valid @RequestBody ClientAddressRequestDto dto) {
        return clientService.addNewAddress(userDetails.getId(), dto);
    } //tested

    @GetMapping("/me/orders")
    @Operation(summary = "Посмотреть свои заказы")
    public List<OrderResponseDto> showAllOrders
            (@AuthenticationPrincipal
             CustomUserDetails userDetails) {
        return orderService.getOrders(userDetails.getId());
    }

    @GetMapping("me/adresses")
    @Operation(summary = "Посмотреть свои адреса")
    public List<ClientAddressResponseDto> getAddresses
            (@AuthenticationPrincipal
             CustomUserDetails userDetails) {
        return clientService.getAddresses(userDetails.getId());
    }
}
