package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;


import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.BucketItemRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.BucketRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class BucketService {

    private final BucketRepository bucketRepository;
    private final BucketItemDtoMapper bucketItemDtoMapper;
    private final ClientRepository clientRepository;
    private final ItemRepository itemRepository;
    private final BucketItemRepository bucketItemRepository;

    @Transactional
    // BucketItemDto???
    public List<BucketItemDto> openBucket(Client client){

        Client clientDb = clientRepository.findByCredentialsLogin(client.getCredentials().getLogin())
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        Bucket bucket = clientDb.getBucket();
        if (bucket == null || bucket.getBucketItems().isEmpty()) {
            return List.of();
        }

        return bucket.getBucketItems()
                .stream()
                .map(bucketItemDtoMapper::map)
                .toList();
    }


    @Transactional
    public BucketItem addItemToBucket(Client client, Item item, int quantity) {

        Client dbClient = clientRepository.findByCredentialsLogin(client.getCredentials().getLogin())
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        Item dbItem = itemRepository.findByItemId(item.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));;

        Bucket bucket = dbClient.getBucket();

        if (bucket == null) {
            bucket = new Bucket();
            dbClient.setBucketAndClientToIt(bucket);
        }

        BucketItemId bucketItemId = new BucketItemId();
        bucketItemId.setBucketId(bucket.getId());
        bucketItemId.setItemId(dbItem.getItemId());

        BucketItem bucketItem = BucketItem.builder()
                .id(bucketItemId)
                .item(dbItem)
                .bucket(bucket)
                .quantity(quantity)
                .build();

        bucketItemRepository.save(bucketItem);

        bucket.getBucketItems().add(bucketItem);

        bucketRepository.save(bucket);

        return bucketItem;
    }

    public BucketItem removeItemFromBucket(Client client, Item item, int quantity){
        return new BucketItem();
    }


}
