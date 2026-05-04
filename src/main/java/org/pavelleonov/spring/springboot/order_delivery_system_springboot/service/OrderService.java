package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.ChangeOrderStatusRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.CreateOrderRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientAddressNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.OrderNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.OrderItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.OrderMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientAddressRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;
    private final PageMapper pageMapper;
    private final ClientAddressRepository clientAddressRepository;

    @Transactional
    public Client findClientById(int id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client with id = {} not found", id);
                    return new ClientNotFoundException("Client not found");
                });
    }


    @Transactional
    public OrderResponseDto makeAnOrder(int id, CreateOrderRequestDto dto) {

        log.info("Creating new order: clientId = {}, areBonusesUsed = {}", id, dto.getAreBonusesUsed());
        Client client = findClientById(id);
        Bucket bucket = client.getBucket();

        List<BucketItem> bucketItems = bucket.getBucketItems();

        ClientAddress clientAddress =
                clientAddressRepository.findByClientAndIsDefault(client, true)
                        .orElseThrow(() -> new ClientAddressNotFoundException
                                ("Client address not found"));

        Order order = new Order();

        List<OrderItem> orderItems = bucketItems
                .stream().map(orderItemMapper::mapBucketItemtoOrderItem)
                .toList();

        // SET ORDER ITEMS
        orderItems.forEach(order::addOrderItemToOrder);

        // SET ADDRESS
        order.setOrderAddress(clientAddress);

        String commentary = dto.getCommentary();
        boolean areBonusesUsed = dto.getAreBonusesUsed();
        if (commentary == null) {
            commentary = "";
        }
        // SET COMMENTARY
        order.setCommentary(commentary);

        // SET ARE BONUSES USED
        order.setAreBonusesUsed(areBonusesUsed);

        int price = bucketItems.stream()
                .map(item -> item.getItem().getPrice() * item.getQuantity())
                .mapToInt(i -> i)
                .sum();

        // SET IS DELIVERY FREE
        if (price > 2000) {
            order.setDeliveryFree(true);
        } else {
            order.setDeliveryFree(false);
            price = price + 300;
        }

        int clientBonuses = client.getBonusesAmount();
        if (areBonusesUsed && price > clientBonuses) {
            price = price - clientBonuses;
            client.setBonusesAmount(0);
        } else if (areBonusesUsed) {
            client.setBonusesAmount(clientBonuses - price);
            price = 0;
        }

        // SET PRICE
        order.setPrice(price);

        // SET CLIENT
        client.addOrderToClient(order);

        Order savedOrder = orderRepository.save(order);
        client.getBucket().getBucketItems().clear();

        log.info("Bucket is cleared after order creation: clientId = {}", id);
        log.info("Created new order: clientId = {}, orderId = {}, price = {}, isDeliveryFree = {}"
                , id, savedOrder.getOrderId(), price, savedOrder.isDeliveryFree());

        return orderMapper.mapOrderToResponseDto(savedOrder);
    }

    @Transactional
    public List<OrderResponseDto> getOrders(int id) {
        log.info("Getting client orders: clientId = {}", id);

        Client client = findClientById(id);

        List<OrderResponseDto> orderList =  client.getCompleteOrders()
                .stream().map(orderMapper::mapOrderToResponseDto)
                .toList();
        log.info("Fetched {} orders for client id = {}", orderList.size(), id);

        return orderList;
    }

    @Transactional
    public PagedResponseDto<OrderResponseDto> getAllOrders(Pageable pageable) {
        log.info("Fetching all orders: page = {}, size = {}"
                , pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderResponseDto> dtos = orderRepository.findAll(pageable)
                .map(orderMapper::mapOrderToResponseDto);

        log.info("Fetched {} orders", dtos.getNumberOfElements());

        return pageMapper.toPagedResponse(dtos);
    }

    @Transactional
    public OrderResponseDto changeOrderStatus(int id, ChangeOrderStatusRequestDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found: orderId = {}", id);
                    return new OrderNotFoundException("Order not found");
                });

        order.setStatus(dto.getStatus());
        Order orderSaved = orderRepository.save(order);
        log.info("Order status changed: orderId = {}, status = {}"
                , orderSaved.getOrderId(), orderSaved.getStatus());
        return orderMapper.mapOrderToResponseDto(orderSaved);
    }


}
