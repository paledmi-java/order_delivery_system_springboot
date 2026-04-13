package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;


import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.OrderItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class BucketService {

    private final BucketRepository bucketRepository;
    private final ClientRepository clientRepository;
    private final ItemRepository itemRepository;
    private final BucketItemRepository bucketItemRepository;

    private final BucketItemMapper bucketItemDtoMapper;

    @Transactional
    public Client findClientById(int clientId){
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }

    @Transactional
    public List<BucketItemDto> openBucket(int id) {

        Client clientDb = findClientById(id);

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
    public BucketItemDto addItemToBucket(int clientId, int itemId, int quantity) {

        Client clientDb = findClientById(clientId);

        Item dbItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        Bucket bucket = clientDb.getBucket();

        if (bucket == null) {
            bucket = new Bucket();
            clientDb.setBucketAndClientToIt(bucket);
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

        return bucketItemDtoMapper.map(bucketItem);
    }

    @Transactional
    public void removeItemFromBucket(int id, int itemId) {
        Client client = findClientById(id);
        Bucket bucket = client.getBucket();
        bucket.getBucketItems().removeIf
                (bucketItem -> bucketItem.getItem().getItemId().equals(itemId));
        bucketRepository.save(bucket);
    }
}
