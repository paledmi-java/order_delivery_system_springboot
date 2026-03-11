package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByIsAvailableTrue();
    Optional<Item> findByItemId(int id);
}
