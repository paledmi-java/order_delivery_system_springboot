package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.client_dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder

public record ClientInfoDTO(
        int id,
        String name,
        boolean isActive,
        LocalDate dateOfBirth,
        String phoneNumber,
        String email,
        boolean isAdvertisable,
        boolean isProfileComplete,
        boolean isOnlineCheckOn,
        int bonusesAmount
) {
}
