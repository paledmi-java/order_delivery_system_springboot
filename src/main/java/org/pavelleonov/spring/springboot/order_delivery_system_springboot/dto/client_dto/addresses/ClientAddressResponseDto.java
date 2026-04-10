package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto.addresses;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientAddressResponseDto {
    private boolean isDefault;
    private String city;
    private String street;
    private String houseNumber;
    private String apartment;
    private String postal_code;
}
