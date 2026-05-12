package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void makeAnOrder_ShouldSuccessfullyCreateAnOrderWithoutCommentaryWithPaidDeliveryNoBonuses(){

        //given
        client.setBonusesAmount(1600);
        bucketItem.setQuantity(1);

        ClientAddress clientAddress1 = new ClientAddress(1,
                false, "Samara", "Lenina",
                "45", "32", "545332", client);


        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setBucketId(bucket.getId());
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
        order.setPrice(0);
        order.setDeliveryFree(false);
        order.setClient(client);

        OrderResponseDto dto = OrderResponseDto.builder()
                .price(order.getPrice())
                .commentary("")
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

        assertThat(capturedOrder.getPrice()).isEqualTo(0);
        assertThat(capturedOrder.getOrderAddress()).isEqualTo(clientAddress1);
        assertThat(capturedOrder.getStatus()).isEqualTo(Order.OrderStatus.ACTIVE);
        assertThat(capturedOrder.getCommentary()).isEqualTo("");
        assertThat(capturedOrder.getClient()).isEqualTo(client);
        assertThat(capturedOrder.getOrderItems()).containsExactly(orderItem);
        assertThat(capturedOrder.isDeliveryFree()).isFalse();

        assertThat(capturedOrder.getPrice()).isEqualTo(resultResponseOrderDto.getPrice());
        assertThat(capturedOrder.getStatus()).isEqualTo(resultResponseOrderDto.getStatus());
        assertThat(capturedOrder.getCommentary()).isEqualTo(resultResponseOrderDto.getCommentary());
        assertThat(resultResponseOrderDto.isAreBonusesUsed()).isTrue();
        assertThat(resultResponseOrderDto.isDeliveryFree()).isFalse();

        assertThat(client.getBucket().getBucketItems()).isEmpty();
        assertThat(client.getBonusesAmount()).isEqualTo(66);
    }

    @Test
    void makeAnOrder_ShouldThrowWhenAddressNotFound(){

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


        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(clientAddressRepository.findByClientAndIsDefault(client, true))
                .thenReturn(Optional.empty());

        //when/then
        assertThatThrownBy(()->orderService.makeAnOrder(client.getClientId(), requestDto))
                .isInstanceOf(ClientAddressNotFoundException.class)
                .hasMessage("Client address not found");

        verify(clientRepository).findById(client.getClientId());
        verify(clientAddressRepository).findByClientAndIsDefault(client, true);
        verify(orderItemMapper, never()).mapBucketItemtoOrderItem(bucketItem);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderMapper, never()).mapOrderToResponseDto(any(Order.class));
    }



    @Test
    void getOrders_ShouldGetListOfOrders(){

        //given
        Order order = new Order();
        order.setStatus(Order.OrderStatus.ACTIVE);
        List<OrderItem> orderItems = new ArrayList<>();
        order.setOrderItems(orderItems);
        order.setClient(client);

        client.getCompleteOrders().add(order);

        OrderResponseDto orderResponseDto = OrderResponseDto
                .builder()
                .commentary("Commentary")
                .build();

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(orderMapper.mapOrderToResponseDto(order)).thenReturn(orderResponseDto);

        //when
        List<OrderResponseDto> orderList = orderService.getOrders(client.getClientId());

        //then
        verify(clientRepository).findById(client.getClientId());
        verify(orderMapper, times(1)).mapOrderToResponseDto(order);

        assertThat(orderList).containsExactly(orderResponseDto);
        assertThat(orderList).hasSize(1);
    }

    @Test
    void getOrders_ShouldGetEmptyListOfOrders(){

        //given

        OrderResponseDto orderResponseDto = OrderResponseDto
                .builder()
                .commentary("Commentary")
                .build();

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        //when
        List<OrderResponseDto> orderList = orderService.getOrders(client.getClientId());

        //then
        verify(clientRepository).findById(client.getClientId());
        verify(orderMapper, never()).mapOrderToResponseDto(any(Order.class));

        assertThat(orderList).isEmpty();
    }


    @Test
    void changeOrderStatus_ShouldSuccessfullyChange(){
        //given
        Order order = new Order();
        order.setOrderId(1);
        order.setStatus(Order.OrderStatus.ACTIVE);
        List<OrderItem> orderItems = new ArrayList<>();
        order.setOrderItems(orderItems);
        order.setClient(client);
        client.getCompleteOrders().add(order);

        ChangeOrderStatusRequestDto requestDto = new ChangeOrderStatusRequestDto();
        requestDto.setStatus(Order.OrderStatus.DECLINED);

        OrderResponseDto responseDto = OrderResponseDto.builder()
                .status(Order.OrderStatus.DECLINED).build();

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenAnswer(inv->inv.getArgument(0));
        when(orderMapper.mapOrderToResponseDto(order)).thenReturn(responseDto);

        //when
        OrderResponseDto resultResponseDto =
                orderService.changeOrderStatus(order.getOrderId(), requestDto);

        //then
        verify(orderRepository).findById(order.getOrderId());
        verify(orderRepository).save(order);
        verify(orderMapper).mapOrderToResponseDto(order);

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.DECLINED);
        assertThat(resultResponseDto.getStatus()).isEqualTo(Order.OrderStatus.DECLINED);
    }

    @Test
    void changeOrderStatus_ShouldThrowWhenOrderNotFound(){
        //given
        Order order = new Order();
        order.setOrderId(1);
        order.setStatus(Order.OrderStatus.ACTIVE);
        List<OrderItem> orderItems = new ArrayList<>();
        order.setOrderItems(orderItems);
        order.setClient(client);
        client.getCompleteOrders().add(order);

        ChangeOrderStatusRequestDto requestDto = new ChangeOrderStatusRequestDto();
        requestDto.setStatus(Order.OrderStatus.DECLINED);

        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.empty());

        //when/then
        assertThatThrownBy(()->orderService.changeOrderStatus(order.getOrderId(), requestDto))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found");

        verify(orderRepository).findById(order.getOrderId());

        verify(orderRepository, never()).save(any(Order.class));
        verify(orderMapper, never()).mapOrderToResponseDto(any(Order.class));
    }


    // косячный ложный тест (исправить и дописать еще для метода)
    @Test
    void getAllOrders_ShouldReturnAllOrders(){
        //given

        Order order1 = new Order();
        Order order2 = new Order();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orderPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        OrderResponseDto responseDto1 = OrderResponseDto.builder().build();
        OrderResponseDto responseDto2 = OrderResponseDto.builder().build();

        PagedResponseDto<OrderResponseDto> expectedResponseDto = new PagedResponseDto<>();

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.mapOrderToResponseDto(order1)).thenReturn(responseDto1);
        when(orderMapper.mapOrderToResponseDto(order2)).thenReturn(responseDto2);
        when(pageMapper.toPagedResponse(any(Page.class))).thenReturn(expectedResponseDto);

        //when
        PagedResponseDto<OrderResponseDto> resultDto = orderService.getAllOrders(pageable);

        //then
        verify(orderRepository).findAll(pageable);
        verify(orderMapper).mapOrderToResponseDto(order1);
        verify(orderMapper).mapOrderToResponseDto(order2);
        verify(pageMapper).toPagedResponse(any(Page.class));

        assertThat(resultDto).isEqualTo(expectedResponseDto);
    }




}