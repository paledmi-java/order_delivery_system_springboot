package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> findByIsAvailableTrue();
    Optional<ItemResponseDto> findByItemId(int id);
}
