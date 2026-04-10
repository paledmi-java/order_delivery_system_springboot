package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.springframework.stereotype.Component;

@Component
public class BucketItemOrderItemMapper {

    public OrderItem mapBucketItemtoOrderItem(BucketItem bucketItem){
        return OrderItem
                .builder()
                .item(bucketItem.getItem())
                .quantity(bucketItem.getQuantity())
                .priceSnapshot(bucketItem.getItem().getPrice())
                .build();
    }

    public OrderResponseDto mapOrderToOrderResponseDto(Order order){
        return OrderResponseDto
                .builder()
                .areBonusesUsed(order.isAreBonusesUsed())
                .orderAddress(order.getOrderAddress())
                .price(order.getPrice())
                .commentary(order.getCommentary())
                .deliveredAt(order.getDeliveredAt())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .isDeliveryFree(order.isDeliveryFree())
                .orderItems(order.getOrderItems())
                .build();
    }
}
