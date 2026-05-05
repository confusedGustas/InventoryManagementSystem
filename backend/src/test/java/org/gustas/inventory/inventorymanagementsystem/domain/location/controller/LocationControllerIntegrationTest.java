package org.gustas.inventory.inventorymanagementsystem.domain.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.dto.LocationDto;
import org.gustas.inventory.inventorymanagementsystem.domain.location.service.LocationService;
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
class LocationControllerIntegrationTest {

    @Mock
    private LocationService locationService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new LocationController(locationService))
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldCreateLocation() throws Exception {
        var dto = TestDataFactory.saveLocationDto(TestDataFactory.company().getId());
        when(locationService.saveLocation(any(), any())).thenReturn(LocationDto.builder().name("Warehouse").build());

        mockMvc.perform(post("/locations")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(locationService).saveLocation(any(), any());
    }

    @Test
    void shouldGetLocations() throws Exception {
        when(locationService.getLocations(2, "manager")).thenReturn(PagedResponseWithCompaniesDto.<LocationDto, CompanyOptionDto>builder()
                .content(java.util.List.of(LocationDto.builder().name("Warehouse").build()))
                .companies(java.util.List.of())
                .page(2)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build());

        mockMvc.perform(get("/locations")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Warehouse"));

        verify(locationService).getLocations(2, "manager");
    }

    @Test
    void shouldUpdateLocation() throws Exception {
        var locationId = TestDataFactory.location(TestDataFactory.company()).getId();
        var dto = TestDataFactory.saveLocationDto(TestDataFactory.company().getId());
        when(locationService.updateLocation(eq(locationId), any(), eq("manager"))).thenReturn(LocationDto.builder().name("Warehouse").build());

        mockMvc.perform(put("/locations/{locationId}", locationId)
                        .principal(new TestingAuthenticationToken("manager", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(locationService).updateLocation(eq(locationId), any(), eq("manager"));
    }

    @Test
    void shouldDisableLocation() throws Exception {
        var locationId = TestDataFactory.location(TestDataFactory.company()).getId();
        when(locationService.disableLocation(locationId, "manager")).thenReturn(LocationDto.builder().enabled(false).build());

        mockMvc.perform(patch("/locations/{locationId}/disable", locationId)
                        .principal(new TestingAuthenticationToken("manager", null)))
                .andExpect(status().isOk());

        verify(locationService).disableLocation(locationId, "manager");
    }

    @Test
    void shouldEnableLocation() throws Exception {
        var locationId = TestDataFactory.location(TestDataFactory.company()).getId();
        when(locationService.enableLocation(locationId, "manager")).thenReturn(LocationDto.builder().enabled(true).build());

        mockMvc.perform(patch("/locations/{locationId}/enable", locationId)
                        .principal(new TestingAuthenticationToken("manager", null)))
                .andExpect(status().isOk());

        verify(locationService).enableLocation(locationId, "manager");
    }
}
