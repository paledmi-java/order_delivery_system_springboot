package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequestDto {

    @Min(1)
    @NotNull
    Integer bucketId;

    @Min(1)
    @NotNull
    Integer clientAddressId;

    @Size(max = 1000)
    String commentary;

    @NotNull
    Boolean areBonusesUsed;
}
