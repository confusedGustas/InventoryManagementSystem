package org.gustas.inventory.inventorymanagementsystem.domain.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.LocationOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesAndLocationsDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.dto.InventoryDto;
import org.gustas.inventory.inventorymanagementsystem.domain.inventory.service.InventoryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InventoryControllerIntegrationTest {

    @Mock
    private InventoryService inventoryService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryController(inventoryService))
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldCreateInventory() throws Exception {
        var dto = TestDataFactory.saveInventoryDto(TestDataFactory.company().getId(), TestDataFactory.location(TestDataFactory.company()).getId());
        when(inventoryService.saveInventory(any(), any())).thenReturn(InventoryDto.builder().name("Main Inventory").build());

        mockMvc.perform(post("/inventories")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(inventoryService).saveInventory(any(), any());
    }

    @Test
    void shouldGetInventories() throws Exception {
        when(inventoryService.getInventories(2, "manager")).thenReturn(PagedResponseWithCompaniesAndLocationsDto.<InventoryDto, CompanyOptionDto, LocationOptionDto>builder()
                .content(java.util.List.of(InventoryDto.builder().name("Main Inventory").build()))
                .companies(java.util.List.of())
                .locations(java.util.List.of())
                .page(2)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build());

        mockMvc.perform(get("/inventories")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Main Inventory"));

        verify(inventoryService).getInventories(2, "manager");
    }

    @Test
    void shouldUpdateInventory() throws Exception {
        var inventoryId = TestDataFactory.inventory(TestDataFactory.company(), null).getId();
        var dto = TestDataFactory.saveInventoryDto(TestDataFactory.company().getId(), TestDataFactory.location(TestDataFactory.company()).getId());
        when(inventoryService.updateInventory(eq(inventoryId), any(), eq("manager"))).thenReturn(InventoryDto.builder().name("Main Inventory").build());

        mockMvc.perform(put("/inventories/{inventoryId}", inventoryId)
                        .principal(new TestingAuthenticationToken("manager", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(inventoryService).updateInventory(eq(inventoryId), any(), eq("manager"));
    }

    @Test
    void shouldDisableInventory() throws Exception {
        var inventoryId = TestDataFactory.inventory(TestDataFactory.company(), null).getId();
        when(inventoryService.disableInventory(inventoryId, "manager")).thenReturn(InventoryDto.builder().enabled(false).build());

        mockMvc.perform(patch("/inventories/{inventoryId}/disable", inventoryId)
                        .principal(new TestingAuthenticationToken("manager", null)))
                .andExpect(status().isOk());

        verify(inventoryService).disableInventory(inventoryId, "manager");
    }

    @Test
    void shouldEnableInventory() throws Exception {
        var inventoryId = TestDataFactory.inventory(TestDataFactory.company(), null).getId();
        when(inventoryService.enableInventory(inventoryId, "manager")).thenReturn(InventoryDto.builder().enabled(true).build());

        mockMvc.perform(patch("/inventories/{inventoryId}/enable", inventoryId)
                        .principal(new TestingAuthenticationToken("manager", null)))
                .andExpect(status().isOk());

        verify(inventoryService).enableInventory(inventoryId, "manager");
    }
}
