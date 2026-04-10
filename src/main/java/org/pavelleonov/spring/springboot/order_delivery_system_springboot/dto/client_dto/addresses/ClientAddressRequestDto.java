package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses;


public record ClientAddressRequestDto (
        boolean isDefault,
        String city,
        String street,
        String houseNumber,
        String apartment,
        String postalCode
){}
