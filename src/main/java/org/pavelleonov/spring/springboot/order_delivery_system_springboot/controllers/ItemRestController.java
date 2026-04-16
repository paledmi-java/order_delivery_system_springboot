package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemRestController {

    private final ItemService itemService;
    private final ItemResponseDtoMapper itemResponseDtoMapper;

    @GetMapping("/api/public/items")
    @Operation(summary = "Получить список доступных товаров")
    public PagedResponseDto<ItemResponseDto> getAllItems(@ParameterObject Pageable pageable) {
        return itemService.findByIsAvailableTrue(pageable);
    }

    @GetMapping("/api/public/items/{itemDtoId}")
    @Operation(summary = "Получить товар по id")
    public ItemResponseDto getItem(@PathVariable int itemDtoId){
        return itemService.findItemById(itemDtoId);
    }
}
