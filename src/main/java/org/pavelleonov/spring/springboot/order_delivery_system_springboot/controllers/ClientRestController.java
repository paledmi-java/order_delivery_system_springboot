package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientCreateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientPasswordUpdateDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientUpdateSelfDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientViewDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Bucket;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.BucketItem;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ClientDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.CustomUserDetails;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.BucketService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    GET /api/users/me/orders
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ClientRestController {

    private final ClientService clientService;
    private final ClientDtoMapper clientDtoMapper;
    private final BucketService bucketService;
    private final ItemService itemService;
    private final BucketItemDtoMapper bucketItemDtoMapper;

    @GetMapping("/me")
    @Operation(summary = "Домашняя страницу пользователя")
    public ClientViewDTO getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Client client = customUserDetails.getClient();
        return clientDtoMapper.toViewDto(client);
    }

    @PatchMapping("/me/settings")
    @Operation(summary = "Обновить свой аккаунт")
    public ClientViewDTO updateClientSelf(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @Valid @RequestBody ClientUpdateSelfDTO clientUpdateSelfDTO){

        Client client = clientService.updateClientSelf(customUserDetails, clientUpdateSelfDTO);
        return clientDtoMapper.toViewDto(client);
    }


    @PostMapping("/me/settings/password")
    @Operation(summary = "Изменить свой пароль")
    public ClientViewDTO updateClientPassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestBody ClientPasswordUpdateDTO dto){
        clientService.changePasswordSelf(customUserDetails, dto);
        return clientDtoMapper.toViewDto(customUserDetails.getClient());
    }

    @PatchMapping("/me/settings/deactivate")
    @Operation(summary = "Деактивировать свой аккаунт")
    public ClientViewDTO deactivateAccount(@AuthenticationPrincipal CustomUserDetails userDetails){
        Client client = userDetails.getClient();
        return clientDtoMapper.toViewDto(clientService.deactivateAccount(client));
    }

    @GetMapping("/me/bucket")
    @Operation(summary = "Открыть свою корзину")
    public List<BucketItemDto> openBucket(@AuthenticationPrincipal CustomUserDetails userDetails){

        return bucketService.openBucket(userDetails.getClient());
    }

    @PostMapping("/me/bucket/add")
    @Operation(summary = "Добавить товар в корзину")
    public BucketItemDto addBucketItem(@RequestParam int itemId,
                                       @RequestParam int quantity,
                                       @AuthenticationPrincipal
                                                   CustomUserDetails userDetails){
        return bucketItemDtoMapper.map(bucketService.addItemToBucket
                (userDetails.getClient(), itemService.findItemById(itemId), quantity));
    }

    @PostMapping("/me/bucket/remove")
    @Operation(summary = "Удалить товар из корзины")
    public BucketItemDto removeBucketItem(@RequestParam int itemId,
                                       @RequestParam int quantity,
                                       @AuthenticationPrincipal
                                       CustomUserDetails userDetails){
        return bucketItemDtoMapper.map(bucketService.removeItemFromBucket
                (userDetails.getClient(), itemService.findItemById(itemId), quantity));
    }

}
