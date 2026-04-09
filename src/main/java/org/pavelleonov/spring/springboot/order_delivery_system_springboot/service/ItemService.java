package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService{

    private final ItemRepository itemRepository;

    public List<Item> findByIsAvailableTrue() {
        return itemRepository.findByIsAvailableTrue();
    }

    public Item findItemById(int id) {
        return itemRepository.findByItemId(id)
                .orElseThrow(()-> new ItemNotFoundException("Item not found"));
    }
}
