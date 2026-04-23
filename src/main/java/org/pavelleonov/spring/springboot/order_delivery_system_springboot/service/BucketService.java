package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.RemoveItemToBucketRequestDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.BucketItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientAccountIsInactiveException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
            log.warn("Client is inactive id = {}", client.getClientId());
            throw new ClientAccountIsInactiveException("Client account is inactive");
        }
    }


    @Transactional
    public Client findClientById(int id) {

        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client with id = {} not found", id);
                    return new ClientNotFoundException("Client not found");
                });
    }

    @Transactional
    public List<BucketItemDto> openBucket(int id) {

        Client clientDb = findClientById(id);

        Bucket bucket = clientDb.getBucket();
        if (bucket == null || bucket.getBucketItems().isEmpty()) {
            log.debug("Bucket is empty , client id = {}", id);
            return List.of();
        }

        log.info("Bucket opened for client id={}, items count = {}",
                id, bucket.getBucketItems().size());

        return bucket.getBucketItems()
                .stream()
                .map(bucketItemDtoMapper::map)
                .toList();
    }


    @Transactional
    public BucketItemDto addItemToBucket(int clientId, int itemId, int quantity) {

        log.info("Adding item to bucket: client id = {}, itemId = {}, quantity = {}"
                , clientId, itemId, quantity);

        Client clientDb = findClientById(clientId);

        ensureClientIsActive(clientDb);

        Item dbItem = itemRepository.findByItemIdAndIsAvailableTrue(itemId)
                .orElseThrow(() -> {
                    log.warn("Item with id = {} is not found or not available", itemId);
                    return new ItemNotFoundException("Item not found");
                });

        Bucket bucket = clientDb.getBucket();

        int idDbClient = clientDb.getClientId();

        if (bucket == null) {
            log.debug("Creating new bucket for client id = {}", idDbClient);
            bucket = new Bucket();
            clientDb.setBucketAndClientToIt(bucket);
        }

        Bucket finalBucket = bucket;

        BucketItem bucketItem = bucketItemRepository
                .findBucketItemByBucketAndItem(finalBucket, dbItem)
                .orElseGet(() ->
                        BucketItem.builder()
                                .id(new BucketItemId())
                                .item(dbItem)
                                .bucket(finalBucket)
                                .build());

        bucketItem.setQuantity(bucketItem.getQuantity() + quantity);

        finalBucket.getBucketItems().add(bucketItem);

        log.info("Item added to bucket: itemId = {}, client id = {} , quantity = {}",
                itemId, idDbClient, quantity);
        return bucketItemDtoMapper.map(bucketItem);
    }


    @Transactional
    public void removeItemFromBucket(int id, RemoveItemToBucketRequestDTO dto) {
        Client client = findClientById(id);
        Bucket bucket = client.getBucket();

        log.info("Removing item from bucket: clientId = {}, bucketId = {}, quantity = {}",
                id, bucket.getId(), dto.getQuantity());

        Item item = itemRepository.findByItemId(dto.getItemId())
                .orElseThrow(() -> {
                    log.warn("Item not found for removal id = {}", dto.getItemId());
                    return new ItemNotFoundException("Item not found");
                });

        BucketItem bucketItem = bucketItemRepository.findBucketItemByBucketAndItem(bucket, item)
                .orElseThrow(() -> {
                    log.warn("Bucket item not found: clientId = {}, itemId = {}",
                           id, dto.getItemId());
                    return new BucketItemNotFoundException("BucketItem not found");
                });

        if (dto.getQuantity() >= bucketItem.getQuantity()) {
            bucketItemRepository.delete(bucketItem);

            log.info("Bucket item removed completely: itemId = {}, bucketId = {}, quantity = {}"
                    ,dto.getItemId(), bucket.getId(), dto.getQuantity());

        } else {
           bucketItem.setQuantity(bucketItem.getQuantity() - dto.getQuantity());

            log.info("Bucket item quantity decreased: itemId = {}, bucketId = {}, quantity = {}"
                    ,dto.getItemId(), bucket.getId(), bucketItem.getQuantity() - dto.getQuantity());
        }

    }
}
