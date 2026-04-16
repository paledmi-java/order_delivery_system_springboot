package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AddItemToBucketRequestDTO {

    @Min(1)
    @NotNull
    Integer itemId;

    @Min(1)
    @Max(1000)
    @NotNull
    Integer quantity;
}
