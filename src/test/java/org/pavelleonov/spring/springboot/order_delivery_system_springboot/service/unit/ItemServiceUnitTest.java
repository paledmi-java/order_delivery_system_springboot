package org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.PageMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.repository.ItemRepository;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemResponseDtoMapper itemResponseDtoMapper;
    @Mock
    private PageMapper pageMapper;

    private Item item;

    @BeforeEach
    void setUp() {
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
    void findByIsAvailableTrue_shouldReturnPagedItems() {

        Pageable pageable = PageRequest.of(0, 5);

        Item item1 = new Item();
        item1.setItemId(1);
        Item item2 = new Item();
        item1.setItemId(2);

        List<Item> items = List.of(item1, item2);
        Page<Item> page = new PageImpl<>(items, pageable, items.size());

        ItemResponseDto responseDto1 = ItemResponseDto.builder().build();
        ItemResponseDto responseDto2 = ItemResponseDto.builder().build();

        PagedResponseDto<ItemResponseDto> expectedResponseDto = new PagedResponseDto<>();

        when(itemRepository.findPageByIsAvailableTrue(pageable))
                .thenReturn(page);
        when(itemResponseDtoMapper.toDto(item1)).thenReturn(responseDto1);
        when(itemResponseDtoMapper.toDto(item2)).thenReturn(responseDto2);
        when(pageMapper.toPagedResponse(any(Page.class))).thenReturn(expectedResponseDto);

        //when
        PagedResponseDto<ItemResponseDto> result
                = itemService.findByIsAvailableTrue(pageable);

        //then
        verify(itemRepository).findPageByIsAvailableTrue(pageable);
        verify(itemResponseDtoMapper).toDto(item1);
        verify(itemResponseDtoMapper).toDto(item2);
        verify(pageMapper).toPagedResponse(any(Page.class));

        assertThat(result).isEqualTo(expectedResponseDto);

    }

    @Test
    void findItemById_ShouldFindItem() {

        //given
        ItemResponseDto expectedDto = ItemResponseDto.builder().build();

        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.of(item));
        when(itemResponseDtoMapper.toDto(item)).thenReturn(expectedDto);

        //when
        ItemResponseDto resultDto = itemService.findItemById(item.getItemId());

        //then
        verify(itemRepository).findByItemId(item.getItemId());
        verify(itemResponseDtoMapper).toDto(item);

        assertThat(resultDto).isEqualTo(expectedDto);
    }

    @Test
    void findItemById_ShouldThrowWhenItemNotFound() {

        //given

        when(itemRepository.findByItemId(item.getItemId())).thenReturn(Optional.empty());

        //when/then
        assertThatThrownBy(() -> itemService.findItemById(item.getItemId()))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("Item not found");

        verify(itemRepository).findByItemId(item.getItemId());
        verify(itemResponseDtoMapper, never()).toDto(item);
    }
}