package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;

@Builder
public record BucketItemDto(ItemResponseDto itemResponseDto, int quantity) {
}
