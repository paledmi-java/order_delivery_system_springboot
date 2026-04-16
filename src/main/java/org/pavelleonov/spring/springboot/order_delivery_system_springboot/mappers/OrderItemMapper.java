package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {

    private final ItemResponseDtoMapper itemResponseDtoMapper;

    public OrderItem mapBucketItemtoOrderItem(BucketItem bucketItem){
        return OrderItem
                .builder()
                .item(bucketItem.getItem())
                .quantity(bucketItem.getQuantity())
                .priceSnapshot(bucketItem.getItem().getPrice())
                .build();
    }

    public OrderItemResponseDto mapOrderItemToResponseDto(OrderItem orderItem){
        return OrderItemResponseDto.builder()
                .priceSnapshot(orderItem.getPriceSnapshot())
                .quantity(orderItem.getQuantity())
                .itemResponseDto(itemResponseDtoMapper.toDto(orderItem.getItem()))
                .build();
    }
}
