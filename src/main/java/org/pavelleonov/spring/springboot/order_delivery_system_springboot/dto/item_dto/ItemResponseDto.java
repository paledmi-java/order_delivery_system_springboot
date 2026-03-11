package org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class ItemResponseDto {

    private int itemId;
    private String itemName;
    private String typeOfItem;
    private String ingredients;
    private int amountOfPieces;
    private int price;
    private String description;
    private int mass;
    private int kcal;
    private String imageUrl;
    private boolean hasMultiComp;
    private boolean isChangeable;
}
