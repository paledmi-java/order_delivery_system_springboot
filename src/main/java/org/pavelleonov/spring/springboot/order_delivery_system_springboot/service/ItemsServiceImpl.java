package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemsServiceImpl implements ItemService{

    ItemRepository itemRepository;

    public ItemsServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> findByIsAvailableTrue() {
        return itemRepository.findByIsAvailableTrue();
    }

    @Override
    public Optional<ItemResponseDto> findByItemId(int id) {
        Optional<Item> optionalItem = itemRepository.findByItemId(id);

        if(optionalItem.isEmpty()){
            return Optional.empty();
        }

        Item item = optionalItem.get();

        ItemResponseDto itemDto = ItemResponseDto.builder()
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
                .itemId(item.getItemId())
                .build();

        return Optional.of(itemDto);
    }
}
