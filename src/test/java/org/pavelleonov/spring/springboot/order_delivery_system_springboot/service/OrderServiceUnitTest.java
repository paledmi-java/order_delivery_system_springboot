package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.CreateOrderRequestDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.OrderItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.OrderMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientAddressRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.OrderRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private PageMapper pageMapper;
    @Mock
    private ClientAddressRepository clientAddressRepository;

    @Captor
    ArgumentCaptor<Order> orderCaptor;

    Client client;
    Item item;
    Credentials credentials;
    BucketItem bucketItem;
    Bucket bucket;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setClientId(1);
        credentials = new Credentials();
        credentials.setLogin("mikewazowski");
        credentials.setHashedPassword("qwerty");

        client.setCredentials(credentials);
        client.setEmail("mikewazowski@yandex.ru");
        client.setName("Mike Wazowski");
        client.setDateOfBirth(LocalDate.of(1995, 4, 23));
        client.setPhoneNumber("+11111111111");

        item = new Item();
        item.setItemId(1);
        item.setItemName("Cool Pizza");
        item.setTypeOfItem("Pizza");
        item.setIngredients("Dough, Cheese");
        item.setAmountOfPieces(1);
        item.setPrice(1234);
        item.setDescription("Yee pizza");
        item.setMass(500);
        item.setKcal(1000);
        item.setHasMultiComp(false);
        item.setChangeable(true);

        bucket = new Bucket();
        bucket.setId(1);
        client.setBucketAndClientToIt(bucket);

        BucketItemId bucketItemId = new BucketItemId();
        bucketItemId.setBucketId(bucket.getId());
        bucketItemId.setItemId(item.getItemId());

        bucketItem = new BucketItem();
        bucketItem.setId(bucketItemId);
        bucketItem.setQuantity(3);
        bucketItem.setItem(item);
        bucketItem.setBucket(bucket);

        bucket.getBucketItems().add(bucketItem);
    }

    @Test
    void findClientById_ShouldFindClientSuccessfully() {

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        Client result = orderService.findClientById(client.getClientId());

        assertThat(result).isEqualTo(client);
        verify(clientRepository).findById(client.getClientId());
    }

    @Test
    void findClientById_ShouldThrowWhenIdIsIncorrect() {
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findClientById(client.getClientId()))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Client not found");
    }

    @Test
    void makeAnOrder_ShouldSuccessfullyCreateAnOrderWithCommentaryWithFreeDelivery(){

        //given
        client.setBonusesAmount(1000);

        ClientAddress clientAddress1 = new ClientAddress(1,
                false, "Samara", "Lenina",
                "45", "32", "545332", client);


        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setBucketId(bucket.getId());
        requestDto.setCommentary("Commentary");
        requestDto.setAreBonusesUsed(true);
        requestDto.setClientAddressId(clientAddress1.getId());

        client.getClientAddresses().add(clientAddress1);
        clientAddress1.setClient(client);

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(bucketItem.getItem());
        orderItem.setQuantity(bucketItem.getQuantity());
        orderItem.setPriceSnapshot(bucketItem.getItem().getPrice());

        Order order = new Order();
        order.setStatus(Order.OrderStatus.ACTIVE);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        order.setOrderAddress(clientAddress1);
        order.setCommentary(requestDto.getCommentary());
        order.setAreBonusesUsed(requestDto.getAreBonusesUsed());
        order.setPrice(2702);
        order.setDeliveryFree(true);
        order.setClient(client);

        OrderResponseDto dto = OrderResponseDto.builder()
                .price(order.getPrice())
                .commentary(order.getCommentary())
                .status(order.getStatus())
                .isDeliveryFree(order.isDeliveryFree())
                .areBonusesUsed(order.isAreBonusesUsed())
                .build();

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientAddressRepository.findByClientAndIsDefault(client, true))
                .thenReturn(Optional.of(clientAddress1));

        when(orderItemMapper.mapBucketItemtoOrderItem(bucketItem)).thenReturn(orderItem);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.mapOrderToResponseDto(any(Order.class))).thenReturn(dto);

        //when
        OrderResponseDto resultResponseOrderDto
                = orderService.makeAnOrder(client.getClientId(), requestDto);

        //then
        verify(clientRepository).findById(client.getClientId());
        verify(clientAddressRepository).findByClientAndIsDefault(client, true);
        verify(orderItemMapper, times(1)).mapBucketItemtoOrderItem(bucketItem);
        verify(orderRepository).save(orderCaptor.capture());
        verify(orderMapper).mapOrderToResponseDto(any(Order.class));

        Order capturedOrder = orderCaptor.getValue();

        assertThat(capturedOrder.getPrice()).isEqualTo(2702);
        assertThat(capturedOrder.getOrderAddress()).isEqualTo(clientAddress1);
        assertThat(capturedOrder.getStatus()).isEqualTo(Order.OrderStatus.ACTIVE);
        assertThat(capturedOrder.getCommentary()).isEqualTo("Commentary");
        assertThat(capturedOrder.getClient()).isEqualTo(client);
        assertThat(capturedOrder.getOrderItems()).containsExactly(orderItem);
        assertThat(capturedOrder.isDeliveryFree()).isTrue();

        assertThat(capturedOrder.getPrice()).isEqualTo(resultResponseOrderDto.getPrice());
        assertThat(capturedOrder.getStatus()).isEqualTo(resultResponseOrderDto.getStatus());
        assertThat(capturedOrder.getCommentary()).isEqualTo(resultResponseOrderDto.getCommentary());
        assertThat(resultResponseOrderDto.isAreBonusesUsed()).isTrue();
        assertThat(resultResponseOrderDto.isDeliveryFree()).isTrue();

        assertThat(client.getBucket().getBucketItems()).isEmpty();
        assertThat(client.getBonusesAmount()).isEqualTo(0);
    }

}