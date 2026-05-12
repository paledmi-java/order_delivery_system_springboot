package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Order;

@Getter
@Setter
public class ChangeOrderStatusRequestDto {
    @NotNull
    private Order.OrderStatus status;
}
