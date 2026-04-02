package org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
