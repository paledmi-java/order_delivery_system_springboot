package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import lombok.*;

import java.time.LocalDate;

@Builder

public record ClientResponseDto(
         String name,
         String phoneNumber,
         String email,
         boolean isProfileComplete,
         boolean isOnlineCheckOn,
         int bonusesAmount,
         String login
) {
}
