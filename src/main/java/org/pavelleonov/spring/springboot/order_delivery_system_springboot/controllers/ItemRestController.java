package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.BucketService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ClientService;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemRestController {

    ItemService itemService;

    public ItemRestController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/api/public/items")
    public List<ItemResponseDto> getAllItems(){
        return itemService.findByIsAvailableTrue();
    }

    @GetMapping("/api/public/items/{itemDtoId}")
    public ItemResponseDto getItem(@PathVariable int itemDtoId){
        return itemService
                .findItemDtoByItemId(itemDtoId).orElseThrow(()-> new ItemNotFoundException("Item not found"));
    }
}
