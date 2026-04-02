package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.FavouriteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteItemRepository extends JpaRepository<FavouriteItem, Integer> {
}
