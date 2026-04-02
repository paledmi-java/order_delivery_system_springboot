package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService{

    private final ItemRepository itemRepository;
    private final ItemResponseDtoMapper itemResponseDtoMapper;

    public ItemService(ItemRepository itemRepository, ItemResponseDtoMapper itemResponseDtoMapper) {
        this.itemRepository = itemRepository;
        this.itemResponseDtoMapper = itemResponseDtoMapper;
    }

    public List<ItemResponseDto> findByIsAvailableTrue() {
        return itemRepository.findByIsAvailableTrue()
                .stream().map(itemResponseDtoMapper::map)
                .toList();
    }

    public Optional<ItemResponseDto> findItemEntityByItemId(int id){
        return itemRepository.findByItemId(id).map(itemResponseDtoMapper::map);
    }

    public Optional<ItemResponseDto> findItemDtoByItemId(int id) {
        return itemRepository.findByItemId(id).map(itemResponseDtoMapper::map);
    }
}
