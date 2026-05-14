package org.pavelleonov.spring.springboot.order_delivery_system_springboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.PagedResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.item_dto.ItemResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.dto.order_dto.OrderResponseDto;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.entity.Item;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.exception.exceptions.ItemNotFoundException;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.mappers.ItemResponseDtoMapper;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.security.filters.JwtFilter;
import org.pavelleonov.spring.springboot.order_delivery_system_springboot.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItemRestControllerUnitTest {

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private ItemResponseDtoMapper itemResponseDtoMapper;
    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void getItem_ShouldReturn200() throws Exception{

        ItemResponseDto responseDto = ItemResponseDto.builder()
                .itemName("Pizza").build();

        when(itemService.findItemById(eq(1))).thenReturn(responseDto);

        mockMvc.perform(get("/api/public/items/{itemDtoId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Pizza"));

        verify(itemService).findItemById(eq(1));
    }
    @Test
    void getItem_ShouldThrowWhenNotFound() throws Exception{

        when(itemService.findItemById(eq(1)))
                .thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(get("/api/public/items/{itemDtoId}", 1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals
                        ("Item not found", result.getResolvedException().getMessage()))
                .andExpect(result -> assertInstanceOf(ItemNotFoundException.class, result.getResolvedException()));

        verify(itemService).findItemById(eq(1));
    }

    @Test
    void getAllItems_ShouldReturn200() throws Exception{
        ItemResponseDto responseDto = ItemResponseDto.builder()
                .itemName("Pizza").build();

        Pageable pageable = PageRequest.of(0, 10);
        PagedResponseDto<ItemResponseDto> pagedResponseDto = new PagedResponseDto<>();
        pagedResponseDto.setContent(List.of(responseDto));
        pagedResponseDto.setTotalPages(1);
        pagedResponseDto.setSize(10);

        when(itemService.findByIsAvailableTrue(eq(pageable))).thenReturn(pagedResponseDto);


        mockMvc.perform(get("/api/public/items")
                        .param("page", "0")
                        .param("size", "10")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].itemName")
                        .value("Pizza")
                ).andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(itemService).findByIsAvailableTrue(any(Pageable.class));
    }

}