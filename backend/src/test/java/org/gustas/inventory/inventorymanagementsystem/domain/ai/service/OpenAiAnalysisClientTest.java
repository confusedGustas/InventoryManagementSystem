package org.gustas.inventory.inventorymanagementsystem.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiItemSuggestionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAiAnalysisClientTest {

    @Mock
    private AiPromptProvider aiPromptProvider;
    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> response;

    private OpenAiAnalysisClient openAiAnalysisClient;

    @BeforeEach
    void setUp() {
        openAiAnalysisClient = new OpenAiAnalysisClient(new ObjectMapper(), aiPromptProvider);
        ReflectionTestUtils.setField(openAiAnalysisClient, "httpClient", httpClient);
        ReflectionTestUtils.setField(openAiAnalysisClient, "responsesUrl", "https://example.test/v1/responses");
        ReflectionTestUtils.setField(openAiAnalysisClient, "model", "gpt-test");
    }

    @Test
    void shouldAnalyzeUsingOutputText() throws Exception {
        when(aiPromptProvider.getSystemPrompt()).thenReturn("system prompt");
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                {"output_text":"{\\"overview\\":\\"overview\\",\\"depletionRisks\\":[],\\"oversupplyRisks\\":[],\\"suggestedPurchases\\":[],\\"actions\\":[\\"Restock\\"]}"}
                """);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        AiAnalysisDto result = openAiAnalysisClient.analyze("secret", "prompt-body");

        assertThat(result.getOverview()).isEqualTo("overview");
        assertThat(result.getActions()).containsExactly("Restock");
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        assertThat(requestCaptor.getValue().uri().toString()).isEqualTo("https://example.test/v1/responses");
        assertThat(requestCaptor.getValue().headers().firstValue("Authorization")).contains("Bearer secret");
    }

    @Test
    void shouldAnalyzeUsingNestedOutputContent() throws Exception {
        when(aiPromptProvider.getSystemPrompt()).thenReturn("system prompt");
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                {"output":[{"content":[{"text":"{\\"overview\\":\\"nested\\",\\"depletionRisks\\":[],\\"oversupplyRisks\\":[],\\"suggestedPurchases\\":[],\\"actions\\":[]}"}]}]}
                """);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        AiAnalysisDto result = openAiAnalysisClient.analyze("secret", "prompt-body");

        assertThat(result.getOverview()).isEqualTo("nested");
    }

    @Test
    void shouldRejectFailedAnalysisResponse() throws Exception {
        when(aiPromptProvider.getSystemPrompt()).thenReturn("system prompt");
        when(response.statusCode()).thenReturn(502);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        assertThatThrownBy(() -> openAiAnalysisClient.analyze("secret", "prompt-body"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("AI analysis request failed");
    }

    @Test
    void shouldSuggestItemFromImageResponse() throws Exception {
        when(aiPromptProvider.getItemImageSystemPrompt()).thenReturn("system image prompt");
        when(aiPromptProvider.getItemImageUserPrompt()).thenReturn("describe image");
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("""
                {"output":[{"content":[{"text":"{\\"scopeLabel\\":\\"Acme\\",\\"imageSummary\\":\\"A laptop on a desk\\",\\"item\\":{\\"name\\":\\"Laptop\\",\\"sku\\":\\"SKU-1\\",\\"category\\":\\"Electronics\\",\\"brand\\":\\"Lenovo\\",\\"manufacturer\\":\\"Lenovo\\",\\"model\\":\\"ThinkPad\\",\\"partNumber\\":\\"PN-1\\",\\"serialNumber\\":\\"SN-1\\",\\"color\\":\\"Black\\",\\"dimensions\\":\\"30x20\\",\\"weight\\":\\"2kg\\",\\"unit\\":\\"pcs\\",\\"quantity\\":1,\\"description\\":\\"Work laptop\\",\\"notes\\":\\"Detected from image\\"}}"}]}]}
                """);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        AiItemSuggestionDto result = openAiAnalysisClient.suggestItem("secret", new byte[]{1, 2, 3}, "image/png");

        assertThat(result.getScopeLabel()).isEqualTo("Acme");
        assertThat(result.getImageSummary()).isEqualTo("A laptop on a desk");
        assertThat(result.getItem().getName()).isEqualTo("Laptop");
        assertThat(result.getItem().getQuantity()).isEqualTo(1);
    }

    @Test
    void shouldRejectEmptyItemSuggestionResponse() throws Exception {
        when(aiPromptProvider.getItemImageSystemPrompt()).thenReturn("system image prompt");
        when(aiPromptProvider.getItemImageUserPrompt()).thenReturn("describe image");
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"output\":[]}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        assertThatThrownBy(() -> openAiAnalysisClient.suggestItem("secret", new byte[]{1}, "image/png"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("AI item suggestion response was empty");
    }
}
