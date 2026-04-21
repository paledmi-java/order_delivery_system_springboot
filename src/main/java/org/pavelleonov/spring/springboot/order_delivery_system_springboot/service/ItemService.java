package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService{

    private final ItemRepository itemRepository;

    private final ItemResponseDtoMapper itemResponseDtoMapper;
    private final PageMapper pageMapper;

    @Transactional
    public PagedResponseDto<ItemResponseDto> findByIsAvailableTrue(Pageable pageable) {

        var page = itemRepository.findPageByIsAvailableTrue(pageable);

        log.info("Fetching available items. page = {}, size = {}",
                pageable.getPageNumber(), pageable.getPageSize());

        log.info("Fetched {} items", page.getNumberOfElements());

        return pageMapper.toPagedResponse(
                page.map(itemResponseDtoMapper::toDto));
    }

    @Transactional
    public ItemResponseDto findItemById(int id) {

        Item item = itemRepository.findByItemId(id)
                .orElseThrow(()-> {
                    log.warn("Item not found with id {}", id);
                    return new ItemNotFoundException("Item not found");
                });

        return itemResponseDtoMapper.toDto(item);
    }
}
