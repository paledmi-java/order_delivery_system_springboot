package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Builder

public record ItemResponseDto(
        int itemDtoId,
        String itemName,
        String typeOfItem,
        String ingredients,
        int amountOfPieces,
        int price,
        String description,
        int mass,
        int kcal,
        boolean hasMultiComp,
        boolean isChangeable
) {
}
