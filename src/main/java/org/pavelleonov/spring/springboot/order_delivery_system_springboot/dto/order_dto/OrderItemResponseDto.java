package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto;

import lombok.Builder;
import lombok.Getter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;

@Getter
@Builder
public class OrderItemResponseDto {
    private Integer priceSnapshot;
    private Integer quantity;
    private ItemResponseDto itemResponseDto;
}
