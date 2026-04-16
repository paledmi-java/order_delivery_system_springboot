package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;


import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.RemoveItemToBucketRequestDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.BucketItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientAccountIsInactiveException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor

public class BucketService {

    private final BucketRepository bucketRepository;
    private final ClientRepository clientRepository;
    private final ItemRepository itemRepository;
    private final BucketItemRepository bucketItemRepository;

    private final BucketItemMapper bucketItemDtoMapper;


    private void ensureClientIsActive(Client client) {
        if (!client.isActive()) {
            throw new ClientAccountIsInactiveException("Client account is inactive");
        }
    }

    @Transactional
    public Client findClientById(int clientId) {
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

        ensureClientIsActive(clientDb);

        Item dbItem = itemRepository.findByItemIdAndIsAvailableTrue(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        Bucket bucket = clientDb.getBucket();

        if (bucket == null) {
            bucket = new Bucket();
            clientDb.setBucketAndClientToIt(bucket);
        }

        Bucket finalBucket = bucket;
        System.out.println("Bucket ID = " + finalBucket.getId());

        BucketItem bucketItem = bucketItemRepository
                .findBucketItemByBucketAndItem(finalBucket, dbItem)
                .orElseGet(() ->
                        BucketItem.builder()
                                .id(new BucketItemId())
                                .item(dbItem)
                                .bucket(finalBucket)
                                .quantity(0)
                                .build());

        bucketItem.setQuantity(quantity);

        finalBucket.getBucketItems().add(bucketItem);

        return bucketItemDtoMapper.map(bucketItem);
    }

    @Transactional
    public void removeItemFromBucket(int id, RemoveItemToBucketRequestDTO dto) {

        Client client = findClientById(id);
        Bucket bucket = client.getBucket();

        Item item = itemRepository.findByItemId(dto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        BucketItem bucketItem = bucketItemRepository.findBucketItemByBucketAndItem(bucket, item)
                .orElseThrow(() -> new BucketItemNotFoundException("BucketItem not found"));

        if(dto.getQuantity() >= bucketItem.getQuantity()){
            bucketItemRepository.delete(bucketItem);
        } else {
            bucketItem.setQuantity(bucketItem.getQuantity() - dto.getQuantity());
        }
    }
}
