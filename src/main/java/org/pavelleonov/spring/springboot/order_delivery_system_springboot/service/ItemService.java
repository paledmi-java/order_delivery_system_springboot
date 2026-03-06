package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;

import java.util.List;

public interface ItemService {
    List<Item> findByIsAvailableTrue();
}
