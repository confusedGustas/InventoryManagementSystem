package org.gustas.inventory.inventorymanagementsystem.domain.analytics.controller;

import org.gustas.inventory.inventorymanagementsystem.domain.analytics.dto.AnalyticsDto;
import org.gustas.inventory.inventorymanagementsystem.domain.analytics.service.AnalyticsService;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerIntegrationTest {

    @Mock
    private AnalyticsService analyticsService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AnalyticsController(analyticsService)).build();
    }

    @Test
    void shouldGetAnalytics() throws Exception {
        var companyId = TestDataFactory.company().getId();
        when(analyticsService.getAnalytics(companyId, "manager")).thenReturn(AnalyticsDto.builder().build());

        mockMvc.perform(get("/analytics")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("companyId", companyId.toString()))
                .andExpect(status().isOk());

        verify(analyticsService).getAnalytics(companyId, "manager");
    }
}
