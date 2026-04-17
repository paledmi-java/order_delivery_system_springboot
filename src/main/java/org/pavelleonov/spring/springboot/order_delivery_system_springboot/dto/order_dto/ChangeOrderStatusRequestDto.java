package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;

@Getter
public class ChangeOrderStatusRequestDto {

    @NotNull
    private Order.OrderStatus status;
}
