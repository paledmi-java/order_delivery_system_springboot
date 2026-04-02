package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;


import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ItemResponseDtoMapper {
    public ItemResponseDto map(Item item){
        return ItemResponseDto.builder()
                .itemName(item.getItemName())
                .typeOfItem(item.getTypeOfItem())
                .description(item.getDescription())
                .ingredients(item.getIngredients())
                .amountOfPieces(item.getAmountOfPieces())
                .price(item.getPrice())
                .mass(item.getMass())
                .kcal(item.getKcal())
                .imageUrl(item.getImageUrl())
                .hasMultiComp(item.isHasMultiComp())
                .isChangeable(item.isChangeable())
                .itemDtoId(item.getItemId())
                .build();
    }
}
