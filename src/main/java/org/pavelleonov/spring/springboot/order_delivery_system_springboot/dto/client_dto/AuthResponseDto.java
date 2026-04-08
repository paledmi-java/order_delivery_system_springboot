package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;


public record AuthResponseDto(
        String accessToken,
        String refreshToken
) {
}
