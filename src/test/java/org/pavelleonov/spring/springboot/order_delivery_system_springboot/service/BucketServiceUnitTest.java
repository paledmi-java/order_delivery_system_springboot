package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.BucketItemDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.bucket_item_dto.RemoveItemToBucketRequestDTO;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.*;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.BucketItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientAccountIsInactiveException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ClientNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.BucketItemMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.BucketItemRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ClientRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BucketServiceUnitTest {

    @InjectMocks
    private BucketService bucketService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BucketItemRepository bucketItemRepository;

    @Mock
    private BucketItemMapper bucketItemDtoMapper;

    @Mock
    private ItemResponseDtoMapper itemResponseDtoMapper;

    @Captor
    private ArgumentCaptor<BucketItem> bucketItemCaptor;

    @Captor
    private ArgumentCaptor<Client> clientCaptor;

    Client client;
    Credentials credentials;
    Item item;

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

    }


    @Test
    public void findClientById_ShouldFindClientSuccessfully() {

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        Client result = bucketService.findClientById(client.getClientId());

        assertThat(result).isEqualTo(client);
        verify(clientRepository).findById(client.getClientId());
    }

    @Test
    public void findClientById_ShouldThrowWhenIdIsIncorrect() {
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bucketService.findClientById(client.getClientId()))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Client not found");
    }


    @Test
    void openBucket_ShouldReturnListOfBucketItems(){

        Bucket bucket = new Bucket();
        bucket.setClient(client);

        BucketItem bucketItem1 = new BucketItem();
        BucketItem bucketItem2 = new BucketItem();
        bucket.setBucketItems(List.of(bucketItem1, bucketItem2));
        client.setBucket(bucket);

        BucketItemDto bucketItemDto1 = mock(BucketItemDto.class);
        BucketItemDto bucketItemDto2 = mock(BucketItemDto.class);

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(bucketItemDtoMapper.map(bucketItem1)).thenReturn(bucketItemDto1);
        when(bucketItemDtoMapper.map(bucketItem2)).thenReturn(bucketItemDto2);

        //when
        List<BucketItemDto> result = bucketService.openBucket(client.getClientId());

        assertThat(result).hasSize(2).containsExactly(bucketItemDto1, bucketItemDto2);

        verify(bucketItemDtoMapper).map(bucketItem1);
        verify(bucketItemDtoMapper).map(bucketItem2);
    }

    @Test
    void openBucket_ShouldReturnEmptyList_WhenBucketIsNull() {
        //given
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        //when
        List<BucketItemDto> result = bucketService.openBucket(client.getClientId());
        //then
        assertThat(client.getBucket()).isNull();
        assertThat(result).isEmpty();

    }

    @Test
    void openBucket_ShouldReturnEmptyList_WhenBucketIsEmpty() {
        //given
        Bucket bucket = new Bucket();
        bucket.setClient(client);
        client.setBucket(bucket);
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        //when
        List<BucketItemDto> result = bucketService.openBucket(client.getClientId());

        //then
        assertThat(client.getBucket()).isNotNull();
        assertThat(result).isEmpty();
    }




    @Test
    void addItemToBucket_ShouldThrowExWhenClientInactive(){
        client.setActive(false);
        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> bucketService
                .addItemToBucket(client.getClientId(), item.getItemId(), 4))
                .isInstanceOf(ClientAccountIsInactiveException.class)
                .hasMessage("Client account is inactive");
    }

    @Test
    void addItemToBucket_ShouldSuccessfullyAddItemToBucket(){

        //given

        Bucket bucket = new Bucket();
        client.setBucket(bucket);

        when(clientRepository.findById(client.getClientId()))
                .thenReturn(Optional.of(client));

        when(itemRepository.findByItemIdAndIsAvailableTrue(item.getItemId()))
                .thenReturn(Optional.of(item));

        BucketItemId bucketItemId = new BucketItemId();
        bucketItemId.setBucketId(client.getBucket().getId());
        bucketItemId.setItemId(item.getItemId());

        BucketItem bucketItem = new BucketItem();
        bucketItem.setItem(item);
        bucketItem.setQuantity(5);
        bucketItem.setBucket(client.getBucket());
        bucketItem.setId(bucketItemId);

        client.getBucket().getBucketItems().add(bucketItem);

        when(bucketItemRepository.findBucketItemByBucketAndItem(bucket, item))
                .thenReturn(Optional.of(bucketItem));

        ItemResponseDto itemResponseDto = new ItemResponseDto(item.getItemId(),
                item.getItemName(), "ert", "rty", 34,
                4567, "dfg", 456, 789, true, true);

        BucketItemDto bucketItemDto = new BucketItemDto(itemResponseDto, 17);

        when(bucketItemDtoMapper.map(bucketItem)).thenReturn(bucketItemDto);


        //when
        BucketItemDto result = bucketService.addItemToBucket
                (client.getClientId(), item.getItemId(), 12);

        //then
        verify(clientRepository).findById(client.getClientId());
        verify(itemRepository).findByItemIdAndIsAvailableTrue(item.getItemId());
        verify(bucketItemRepository)
                .findBucketItemByBucketAndItem(client.getBucket(), item);

        verify(bucketItemDtoMapper).map(bucketItemCaptor.capture());

        BucketItem captured = bucketItemCaptor.getValue();

        assertThat(captured.getQuantity()).isEqualTo(17);
        assertThat(captured.getItem().getItemId()).isEqualTo(item.getItemId());
        assertThat(captured.getId()).isEqualTo(bucketItem.getId());
        assertThat(captured.getBucket().getId()).isEqualTo(client.getBucket().getId());

        assertThat(result.quantity()).isEqualTo(17);
        assertThat(result.itemResponseDto().itemDtoId()).isEqualTo(item.getItemId());
        assertThat(result.itemResponseDto().itemName()).isEqualTo(item.getItemName());
        assertThat(client.getBucket().getBucketItems()).contains(bucketItem);
    }

    @Test
    void addItemToBucket_ShouldThrowWhenItemIsNotAvailable(){
        //given
        when(clientRepository.findById(client.getClientId()))
                .thenReturn(Optional.of(client));

        when(itemRepository.findByItemIdAndIsAvailableTrue(item.getItemId()))
                .thenReturn(Optional.empty());

        //when/then

        assertThatThrownBy(() -> bucketService
                .addItemToBucket(client.getClientId(), item.getItemId(), 23))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("Item not found");
    }

    @Test
    void addItemToBucket_ShouldSuccessfullyAddNewItemToBucketWhenBucketIsNull(){

        //given

        when(clientRepository.findById(client.getClientId()))
                .thenReturn(Optional.of(client));

        when(itemRepository.findByItemIdAndIsAvailableTrue(item.getItemId()))
                .thenReturn(Optional.of(item));
        when(bucketItemRepository.findBucketItemByBucketAndItem(any(Bucket.class), eq(item)))
                .thenReturn(Optional.empty());

        ItemResponseDto itemResponseDto = new ItemResponseDto(item.getItemId(),
                item.getItemName(), "ert", "rty", 34,
                4567, "dfg", 456, 789, true, true);

        BucketItemDto bucketItemDto = new BucketItemDto(itemResponseDto, 17);

        when(bucketItemDtoMapper.map(any(BucketItem.class))).thenReturn(bucketItemDto);


        //when
        BucketItemDto result = bucketService.addItemToBucket
                (client.getClientId(), item.getItemId(), 17);

        //then
        verify(clientRepository).findById(client.getClientId());
        verify(itemRepository).findByItemIdAndIsAvailableTrue(item.getItemId());
        verify(bucketItemRepository)
                .findBucketItemByBucketAndItem(any(Bucket.class), eq(item));

        verify(bucketItemDtoMapper).map(bucketItemCaptor.capture());

        BucketItem captured = bucketItemCaptor.getValue();

        assertThat(captured.getQuantity()).isEqualTo(17);
        assertThat(captured.getItem().getItemId()).isEqualTo(item.getItemId());
        assertThat(captured.getBucket()).isNotNull();

        assertThat(result.quantity()).isEqualTo(17);
        assertThat(result.itemResponseDto().itemDtoId()).isEqualTo(item.getItemId());
        assertThat(result.itemResponseDto().itemName()).isEqualTo(item.getItemName());
        assertThat(client.getBucket().getBucketItems()).contains(captured);
    }



    @Test
    void removeItemFromBucket_ShouldSuccessfullyRemoveBucketItem(){
        //given
        Bucket bucket = new Bucket();
        client.setBucketAndClientToIt(bucket);

        BucketItemId bucketItemId = new BucketItemId();
        bucketItemId.setBucketId(client.getBucket().getId());
        bucketItemId.setItemId(item.getItemId());

        BucketItem bucketItem = new BucketItem();
        bucketItem.setItem(item);
        bucketItem.setQuantity(5);
        bucketItem.setBucket(client.getBucket());
        bucketItem.setId(bucketItemId);

        client.getBucket().getBucketItems().add(bucketItem);

        RemoveItemToBucketRequestDTO dto = new RemoveItemToBucketRequestDTO();
        dto.setItemId(item.getItemId());
        dto.setQuantity(6);

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.of(item));
        when(bucketItemRepository.findBucketItemByBucketAndItem(client.getBucket(), item))
                .thenReturn(Optional.of(bucketItem));

        //when
        bucketService.removeItemFromBucket(client.getClientId(), dto);

        verify(clientRepository).findById(client.getClientId());
        verify(itemRepository).findByItemId(item.getItemId());
        verify(bucketItemRepository).findBucketItemByBucketAndItem(client.getBucket(), item);
        verify(bucketItemRepository).delete(bucketItem);

        assertThat(bucketItem.getQuantity()).isEqualTo(5);
    }

    @Test
    void removeItemFromBucket_ShouldSuccessfullyReduceAmountOfBucketItem(){
        //given
        Bucket bucket = new Bucket();
        client.setBucketAndClientToIt(bucket);

        BucketItemId bucketItemId = new BucketItemId();
        bucketItemId.setBucketId(client.getBucket().getId());
        bucketItemId.setItemId(item.getItemId());

        BucketItem bucketItem = new BucketItem();
        bucketItem.setItem(item);
        bucketItem.setQuantity(5);
        bucketItem.setBucket(client.getBucket());
        bucketItem.setId(bucketItemId);

        client.getBucket().getBucketItems().add(bucketItem);

        RemoveItemToBucketRequestDTO dto = new RemoveItemToBucketRequestDTO();
        dto.setItemId(item.getItemId());
        dto.setQuantity(3);

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.of(item));
        when(bucketItemRepository.findBucketItemByBucketAndItem(client.getBucket(), item))
                .thenReturn(Optional.of(bucketItem));

        //when
        bucketService.removeItemFromBucket(client.getClientId(), dto);

        verify(clientRepository).findById(client.getClientId());
        verify(itemRepository).findByItemId(item.getItemId());
        verify(bucketItemRepository).findBucketItemByBucketAndItem(client.getBucket(), item);
        verify(bucketItemRepository, never()).delete(bucketItem);

        assertThat(bucketItem.getQuantity()).isEqualTo(2);
    }

    @Test
    void removeItemFromBucket_ShouldThrowItemNotFoundException(){
        //given

        Bucket bucket = new Bucket();
        client.setBucketAndClientToIt(bucket);

        RemoveItemToBucketRequestDTO dto = new RemoveItemToBucketRequestDTO();
        dto.setItemId(item.getItemId());
        dto.setQuantity(3);

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.empty());

        //when/then
        assertThatThrownBy(() -> bucketService.removeItemFromBucket(client.getClientId(), dto))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("Item not found");

        verify(clientRepository).findById(client.getClientId());
        verify(itemRepository).findByItemId(item.getItemId());
        verify(bucketItemRepository, never()).findBucketItemByBucketAndItem(any(), any());
        verify(bucketItemRepository, never()).delete(any());

    }

    @Test
    void removeItemFromBucket_ShouldThrowBucketItemNotFoundException(){
        //given

        Bucket bucket = new Bucket();
        client.setBucketAndClientToIt(bucket);

        RemoveItemToBucketRequestDTO dto = new RemoveItemToBucketRequestDTO();
        dto.setItemId(item.getItemId());
        dto.setQuantity(3);

        when(clientRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.of(item));
        when(bucketItemRepository.findBucketItemByBucketAndItem(bucket, item))
                .thenReturn(Optional.empty());

        //when/then
        assertThatThrownBy(() -> bucketService.removeItemFromBucket(client.getClientId(), dto))
                .isInstanceOf(BucketItemNotFoundException.class)
                .hasMessage("BucketItem not found");

        verify(clientRepository).findById(client.getClientId());
        verify(itemRepository).findByItemId(item.getItemId());
        verify(bucketItemRepository, never()).delete(any());

    }
}