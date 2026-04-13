package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.CreateOrderRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientAddressIsInvalid;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.OrderItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.OrderMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;


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
                .stream().map(orderItemMapper::mapBucketItemtoOrderItem)
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

        return orderMapper.mapOrderToResponseDto(order);
    }

    @Transactional
    public List<OrderResponseDto> getOrders(Client clientDto){
        Client client = clientRepository.findById(clientDto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        return client.getCompleteOrders()
                .stream().map(orderMapper::mapOrderToResponseDto)
                .toList();
    }

    @Transactional
    public Page<OrderResponseDto> getAllOrders(int page, int size){
        return orderRepository.findAll(PageRequest.of(page, size))
                .map(orderMapper::mapOrderToResponseDto);
    }
}
