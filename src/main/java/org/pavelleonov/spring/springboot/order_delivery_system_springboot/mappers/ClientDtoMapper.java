package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import org.mapstruct.Mapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.ClientResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Client;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ClientDtoMapper {
    ClientResponseDto toResponseDto(Client client);
}
