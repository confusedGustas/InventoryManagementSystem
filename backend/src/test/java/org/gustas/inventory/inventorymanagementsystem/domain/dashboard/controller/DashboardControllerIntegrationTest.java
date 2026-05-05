package org.gustas.inventory.inventorymanagementsystem.domain.dashboard.controller;

import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.dto.DashboardDto;
import org.gustas.inventory.inventorymanagementsystem.domain.dashboard.service.DashboardService;
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
class DashboardControllerIntegrationTest {

    @Mock
    private DashboardService dashboardService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DashboardController(dashboardService)).build();
    }

    @Test
    void shouldGetDashboard() throws Exception {
        var companyId = TestDataFactory.company().getId();
        when(dashboardService.getDashboard(companyId, "manager")).thenReturn(DashboardDto.builder().build());

        mockMvc.perform(get("/dashboard")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("companyId", companyId.toString()))
                .andExpect(status().isOk());

        verify(dashboardService).getDashboard(companyId, "manager");
    }
}
