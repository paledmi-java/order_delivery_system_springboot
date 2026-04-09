package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto;

import lombok.AllArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;

public record BucketItemDto(ItemResponseDto itemResponseDto, int quantity) {
}
