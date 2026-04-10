package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses.ClientAddressResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.ClientAddress;
import org.springframework.stereotype.Component;

@Component
public class ClientAddressDtoMapper {

    public ClientAddressResponseDto mapClientAddressToResponse(ClientAddress clientAddress){
        return ClientAddressResponseDto.builder()
                .city(clientAddress.getCity())
                .apartment(clientAddress.getApartment())
                .houseNumber(clientAddress.getHouseNumber())
                .isDefault(clientAddress.isDefault())
                .postal_code(clientAddress.getPostal_code())
                .street(clientAddress.getStreet())
                .build();
    }
}
