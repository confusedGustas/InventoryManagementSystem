package org.gustas.inventory.inventorymanagementsystem.domain.ai.controller;

import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiReportDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiScopeDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.service.AiService;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiControllerIntegrationTest {

    @Mock
    private AiService aiService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AiController(aiService)).build();
    }

    @Test
    void shouldGetScope() throws Exception {
        var companyId = TestDataFactory.company().getId();
        when(aiService.getScope(companyId, "manager")).thenReturn(AiScopeDto.builder().build());

        mockMvc.perform(get("/ai/scope")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("companyId", companyId.toString()))
                .andExpect(status().isOk());

        verify(aiService).getScope(companyId, "manager");
    }

    @Test
    void shouldGetAnalysis() throws Exception {
        var companyId = TestDataFactory.company().getId();
        when(aiService.analyze(companyId, "manager")).thenReturn(AiAnalysisDto.builder().build());

        mockMvc.perform(get("/ai/analysis")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("companyId", companyId.toString()))
                .andExpect(status().isOk());

        verify(aiService).analyze(companyId, "manager");
    }

    @Test
    void shouldSuggestItem() throws Exception {
        var companyId = TestDataFactory.company().getId();
        var file = new MockMultipartFile("image", "item.png", "image/png", new byte[]{1, 2, 3});
        when(aiService.suggestItem(eq(companyId), any(), eq("manager")))
                .thenReturn(org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiItemSuggestionDto.builder().scopeLabel("Acme").build());

        mockMvc.perform(multipart("/ai/item-suggestion")
                        .file(file)
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("companyId", companyId.toString()))
                .andExpect(status().isOk());

        verify(aiService).suggestItem(eq(companyId), any(), eq("manager"));
    }

    @Test
    void shouldGetReports() throws Exception {
        var companyId = TestDataFactory.company().getId();
        when(aiService.getReports(companyId, "manager")).thenReturn(List.of(AiReportDto.builder().overview("Overview").build()));

        mockMvc.perform(get("/ai/reports")
                        .principal(new TestingAuthenticationToken("manager", null))
                        .param("companyId", companyId.toString()))
                .andExpect(status().isOk());

        verify(aiService).getReports(companyId, "manager");
    }

    @Test
    void shouldDownloadPdf() throws Exception {
        var reportId = java.util.UUID.randomUUID();
        when(aiService.downloadReportPdf(reportId, "manager")).thenReturn(new byte[]{1, 2});

        mockMvc.perform(get("/ai/reports/{reportId}/pdf", reportId)
                        .principal(new TestingAuthenticationToken("manager", null)))
                .andExpect(status().isOk());

        verify(aiService).downloadReportPdf(reportId, "manager");
    }
}
