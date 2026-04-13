package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderResponseDto mapOrderToResponseDto(Order order){

        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(orderItemMapper::mapOrderItemToResponseDto)
                .toList();

        return OrderResponseDto
                .builder()
                .areBonusesUsed(order.isAreBonusesUsed())
                .price(order.getPrice())
                .commentary(order.getCommentary())
                .deliveredAt(order.getDeliveredAt())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .isDeliveryFree(order.isDeliveryFree())
                .orderItems(items)
                .build();
    }

}
