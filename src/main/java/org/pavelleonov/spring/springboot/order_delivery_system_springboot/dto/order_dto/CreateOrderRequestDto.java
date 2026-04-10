package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto;

import lombok.Getter;

@Getter
public class CreateOrderRequestDto {
    int bucketId;
    int clientAddressId;
    String commentary;
    boolean areBonusesUsed;
}
