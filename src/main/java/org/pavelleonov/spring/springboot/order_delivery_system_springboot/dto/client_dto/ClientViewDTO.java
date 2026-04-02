package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import lombok.*;

@Builder

public record ClientViewDTO(
         String name,
         String login
) {
}
