package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService{

    private final ItemRepository itemRepository;
    private final ItemResponseDtoMapper itemResponseDtoMapper;

    @Transactional
    public Page<ItemResponseDto> findByIsAvailableTrue(int page, int size) {
        return itemRepository.findByIsAvailableTrue(PageRequest.of(page,size))
                .map(itemResponseDtoMapper::toDto);
    }

    @Transactional
    public ItemResponseDto findItemById(int id) {
        Item item = itemRepository.findByItemId(id)
                .orElseThrow(()-> new ItemNotFoundException("Item not found"));
        return itemResponseDtoMapper.toDto(item);
    }
}
