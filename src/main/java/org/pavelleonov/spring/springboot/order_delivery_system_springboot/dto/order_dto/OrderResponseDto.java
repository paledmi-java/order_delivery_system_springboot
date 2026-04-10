package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.converters.DurationConverter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.ClientAddress;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.OrderItem;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder

public class OrderResponseDto {
    private int price; //
    private boolean isDeliveryFree; //
    private Order.OrderStatus status;
    private String commentary; //
    private LocalDateTime createdAt; //
    private LocalDateTime deliveredAt; //
    private boolean areBonusesUsed; //
    private List<OrderItem> orderItems; //
    private ClientAddress orderAddress; //
}
