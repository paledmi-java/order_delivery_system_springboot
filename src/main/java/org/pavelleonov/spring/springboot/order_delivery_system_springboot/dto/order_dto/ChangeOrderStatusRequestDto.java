package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto;


import lombok.Getter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;

@Getter
public class ChangeOrderStatusRequestDto {
    private Order.OrderStatus status;
}
