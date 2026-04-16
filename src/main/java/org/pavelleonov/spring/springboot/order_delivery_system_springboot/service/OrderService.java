package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import lombok.RequiredArgsConstructor;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.ChangeOrderStatusRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.CreateOrderRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exceptions.OrderNotFoundException;
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
    public Client findClientById(int id){
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }


    @Transactional
    public OrderResponseDto makeAnOrder(int id, CreateOrderRequestDto dto) {

        Client client = findClientById(id);
        Bucket bucket = client.getBucket();

        // решить проблемы N+1
        List<BucketItem> bucketItems = bucket.getBucketItems();

        // Создание нового адреса
        ClientAddress clientAddress =
                clientAddressRepository.findByClientAndIsDefault(client, true);

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
        if(commentary == null){
            commentary = "";
        }
        // SET COMMENTARY
        order.setCommentary(commentary);

        // SET ARE BONUSES USED
        order.setAreBonusesUsed(areBonusesUsed);

        int price = bucketItems.stream()
                .map(item -> item.getItem().getPrice())
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
            client.setBonusesAmount(0);
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

        return orderMapper.mapOrderToResponseDto(order);
    }




    @Transactional
    public List<OrderResponseDto> getOrders(int id){
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));

        return client.getCompleteOrders()
                .stream().map(orderMapper::mapOrderToResponseDto)
                .toList();
    }

    @Transactional
    public PagedResponseDto<OrderResponseDto> getAllOrders(Pageable pageable){
        Page<OrderResponseDto> dtos = orderRepository.findAll(pageable)
                .map(orderMapper::mapOrderToResponseDto);

        return pageMapper.toPagedResponse(dtos);
    }

    @Transactional
    public OrderResponseDto changeOrderStatus(int id, ChangeOrderStatusRequestDto dto){
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new OrderNotFoundException("Order not found"));
        order.setStatus(dto.getStatus());
        orderRepository.save(order);
        return orderMapper.mapOrderToResponseDto(order);
    }


}
