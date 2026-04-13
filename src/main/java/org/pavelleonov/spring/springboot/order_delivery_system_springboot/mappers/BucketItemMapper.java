package org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.BucketItem;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BucketItemMapper {

    private final ItemResponseDtoMapper itemResponseDtoMapper;

    public BucketItemDto map(BucketItem bucketItem){
        if(bucketItem == null || bucketItem.getItem() == null){
            throw new RuntimeException("BucketItem is null");
        }

        return new BucketItemDto(itemResponseDtoMapper.map(bucketItem.getItem()),
                bucketItem.getQuantity());
    }
}
