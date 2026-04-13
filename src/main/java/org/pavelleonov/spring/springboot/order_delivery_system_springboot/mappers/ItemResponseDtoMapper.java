package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;


import org.mapstruct.Mapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
public interface ItemResponseDtoMapper {
    ItemResponseDto toDto (Item item);
}
