package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;


import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.CreateOrderRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientAddressIsInvalid;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemOrderItemMapper;
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
    private final OrderRepository orderRepository;
    private final BucketItemOrderItemMapper bucketItemOrderItemMapper;


    private final BucketItemDtoMapper bucketItemDtoMapper;

    @Transactional
    // BucketItemDto???
    public List<BucketItemDto> openBucket(Client client) {

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
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        ;

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

    @Transactional
    public void removeItemFromBucket(Client client, int itemId) {
        Bucket bucket = client.getBucket();
        bucket.getBucketItems().removeIf
                (bucketItem -> bucketItem.getItem().getItemId().equals(itemId));
        bucketRepository.save(bucket);
    }


    @Transactional
    public OrderResponseDto makeAnOrder(Client clientDto, CreateOrderRequestDto dto) {

        Client client = clientRepository.findByCredentialsLogin(clientDto.getCredentials().getLogin())
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        Bucket bucket = client.getBucket();
        List<Item> items = bucket.getBucketItems()
                .stream().map(bi -> bi.getItem())
                .toList();

        ClientAddress clientAddress =
                client.getClientAddresses().stream()
                        .filter(ca-> ca.getId() == dto.getClientAddressId())
                        .findFirst().orElseThrow(() ->
                                new ClientAddressIsInvalid("Client address is invalid"));

        // Создание нового адреса

        Order order = new Order();

        List<OrderItem> orderItems = bucket.getBucketItems()
                        .stream().map(bucketItemOrderItemMapper::mapBucketItemtoOrderItem)
                .toList();

        // SET ORDER ITEMS
        orderItems.forEach(order::addOrderItemToOrder);

        // SET ADDRESS ДОБАВИТЬ СОЗДАНИЕ НОВОГО АДРЕСА
        order.setOrderAddress(clientAddress);

        String commentary = dto.getCommentary();
        boolean areBonusesUsed = dto.isAreBonusesUsed();
        if(commentary == null){
            commentary = "";
        }
        // SET COMMENTARY
        order.setCommentary(commentary);

        // SET ARE BONUSES USED
        order.setAreBonusesUsed(areBonusesUsed);

        int price = items.stream()
                .map(item -> item.getPrice())
                .mapToInt(i -> i.intValue())
                .sum();

        // SET IS DELIVERY FREE
        if(price > 2000){
            order.setDeliveryFree(true);
        } else {
            order.setDeliveryFree(false);
            price = price + 300;
        }

        int clientBonuses = client.getBonusesAmount();
        if (areBonusesUsed && price > clientBonuses) {
            price = price - clientBonuses;
        } else if (areBonusesUsed) {
            client.setBonusesAmount(clientBonuses-price);
            price = 0;
        }

        // SET PRICE
        order.setPrice(price);

        // SET CLIENT
        client.addOrderToClient(order);

        orderRepository.save(order);
        client.getBucket().getBucketItems().clear();
        clientRepository.save(client);

        return bucketItemOrderItemMapper.mapOrderToOrderResponseDto(order);
    }
}
