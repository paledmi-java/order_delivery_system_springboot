package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemRestController {

    private final ItemService itemService;
    private final ItemResponseDtoMapper itemResponseDtoMapper;

    @GetMapping("/api/public/items")
    @Operation(summary = "Получить список доступных товаров") // Сделать пагинацию
    public List<ItemResponseDto> getAllItems(){
        return itemService.findByIsAvailableTrue().stream().map(itemResponseDtoMapper::map)
                .toList();
    }

    @GetMapping("/api/public/items/{itemDtoId}")
    @Operation(summary = "Получить товар по id")
    public ItemResponseDto getItem(@PathVariable int itemDtoId){
        return itemResponseDtoMapper.map(itemService
                .findItemById(itemDtoId));
    }
}
