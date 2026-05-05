package org.gustas.inventory.inventorymanagementsystem.domain.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.dto.CompanyDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.service.CompanyService;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
class CompanyControllerIntegrationTest {

    @Mock
    private CompanyService companyService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new CompanyController(companyService))
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldCreateCompany() throws Exception {
        var dto = TestDataFactory.saveCompanyDto();
        dto.setContactEmail("contact@acme.test");
        when(companyService.saveCompany(any())).thenReturn(CompanyDto.builder().name("Acme").build());

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(companyService).saveCompany(any());
    }

    @Test
    void shouldGetCompanies() throws Exception {
        when(companyService.getCompanies(2)).thenReturn(PagedResponseDto.<CompanyDto>builder()
                .content(java.util.List.of(CompanyDto.builder().name("Acme").build()))
                .page(2)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build());

        mockMvc.perform(get("/companies").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Acme"));

        verify(companyService).getCompanies(2);
    }

    @Test
    void shouldUpdateCompany() throws Exception {
        var dto = TestDataFactory.saveCompanyDto();
        dto.setContactEmail("contact@acme.test");
        var companyId = TestDataFactory.company().getId();
        when(companyService.updateCompany(eq(companyId), any())).thenReturn(CompanyDto.builder().name("Acme").build());

        mockMvc.perform(put("/companies/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(companyService).updateCompany(eq(companyId), any());
    }

    @Test
    void shouldDisableCompany() throws Exception {
        var companyId = TestDataFactory.company().getId();
        when(companyService.disableCompany(companyId)).thenReturn(CompanyDto.builder().status("SUSPENDED").build());

        mockMvc.perform(patch("/companies/{companyId}/disable", companyId))
                .andExpect(status().isOk());

        verify(companyService).disableCompany(companyId);
    }

    @Test
    void shouldEnableCompany() throws Exception {
        var companyId = TestDataFactory.company().getId();
        when(companyService.enableCompany(companyId)).thenReturn(CompanyDto.builder().status("ACTIVE").build());

        mockMvc.perform(patch("/companies/{companyId}/enable", companyId))
                .andExpect(status().isOk());

        verify(companyService).enableCompany(companyId);
    }
}
