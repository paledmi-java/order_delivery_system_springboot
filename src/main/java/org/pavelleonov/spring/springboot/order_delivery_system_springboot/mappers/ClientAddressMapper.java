package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import org.mapstruct.Mapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.ClientAddress;

@Mapper(componentModel = "spring")
public interface ClientAddressMapper {
    ClientAddressResponseDto toResponseDto(ClientAddress clientAddress);
}
