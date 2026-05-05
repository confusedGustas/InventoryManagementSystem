package org.gustas.inventory.inventorymanagementsystem.domain.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.dto.ItemDto;
import org.gustas.inventory.inventorymanagementsystem.domain.item.service.ItemService;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerIntegrationTest {

    @Mock
    private ItemService itemService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new ItemController(itemService))
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldCreateItem() throws Exception {
        var dto = TestDataFactory.saveItemDto(TestDataFactory.inventory(TestDataFactory.company(), null).getId());
        when(itemService.saveItem(any(), any())).thenReturn(ItemDto.builder().name("Laptop").build());

        mockMvc.perform(post("/items")
                        .principal(new TestingAuthenticationToken("employee", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemService).saveItem(any(), any());
    }

    @Test
    void shouldGetItems() throws Exception {
        var inventoryId = TestDataFactory.inventory(TestDataFactory.company(), null).getId();
        when(itemService.getItems(inventoryId, 2, "employee")).thenReturn(PagedResponseDto.<ItemDto>builder()
                .content(java.util.List.of(ItemDto.builder().name("Laptop").build()))
                .page(2)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build());

        mockMvc.perform(get("/items")
                        .principal(new TestingAuthenticationToken("employee", null))
                        .param("inventoryId", inventoryId.toString())
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));

        verify(itemService).getItems(inventoryId, 2, "employee");
    }

    @Test
    void shouldUpdateItem() throws Exception {
        var itemId = TestDataFactory.item(TestDataFactory.inventory(TestDataFactory.company(), null)).getId();
        var dto = TestDataFactory.saveItemDto(TestDataFactory.inventory(TestDataFactory.company(), null).getId());
        when(itemService.updateItem(eq(itemId), any(), eq("employee"))).thenReturn(ItemDto.builder().name("Laptop").build());

        mockMvc.perform(put("/items/{itemId}", itemId)
                        .principal(new TestingAuthenticationToken("employee", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemService).updateItem(eq(itemId), any(), eq("employee"));
    }

    @Test
    void shouldDeleteItems() throws Exception {
        var itemId = TestDataFactory.item(TestDataFactory.inventory(TestDataFactory.company(), null)).getId();

        mockMvc.perform(delete("/items")
                        .principal(new TestingAuthenticationToken("employee", null))
                        .param("itemIds", itemId.toString()))
                .andExpect(status().isOk());

        verify(itemService).deleteItems(java.util.List.of(itemId), "employee");
    }
}
