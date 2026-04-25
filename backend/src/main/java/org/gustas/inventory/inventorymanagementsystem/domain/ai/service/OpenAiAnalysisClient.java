package org.gustas.inventory.inventorymanagementsystem.domain.ai.service;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiItemSuggestionDto;
import org.gustas.inventory.inventorymanagementsystem.domain.ai.dto.AiAnalysisDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OpenAiAnalysisClient {

    private final ObjectMapper objectMapper;
    private final AiPromptProvider aiPromptProvider;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${app.openai.responses-url:https://api.openai.com/v1/responses}")
    private String responsesUrl;

    @Value("${app.openai.model:gpt-5-mini}")
    private String model;

    public AiAnalysisDto analyze(String apiKey, String prompt) {
        try {
            String requestBody = objectMapper.writeValueAsString(buildAnalysisRequest(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(responsesUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI analysis request failed");
            }

            JsonNode responseJson = objectMapper.readTree(response.body());
            String outputText = extractOutputText(responseJson);
            if (outputText == null || outputText.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI analysis response was empty");
            }

            return objectMapper.readValue(outputText, AiAnalysisDto.class);
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to generate AI analysis");
        }
    }

    public AiItemSuggestionDto suggestItem(String apiKey, byte[] imageBytes, String contentType) {
        try {
            String requestBody = objectMapper.writeValueAsString(buildItemSuggestionRequest(imageBytes, contentType));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(responsesUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI item suggestion request failed");
            }

            JsonNode responseJson = objectMapper.readTree(response.body());
            String outputText = extractOutputText(responseJson);
            if (outputText == null || outputText.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI item suggestion response was empty");
            }

            return objectMapper.readValue(outputText, AiItemSuggestionDto.class);
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to generate AI item suggestion");
        }
    }

    private ObjectNode buildAnalysisRequest(String prompt) {
        ObjectNode actionSchema = objectMapper.createObjectNode();
        actionSchema.put("type", "array");
        actionSchema.set("items", objectMapper.createObjectNode().put("type", "string"));

        ObjectNode propertiesSchema = objectMapper.createObjectNode();
        propertiesSchema.set("overview", objectMapper.createObjectNode().put("type", "string"));
        propertiesSchema.set("depletionRisks", analysisItemsSchema());
        propertiesSchema.set("oversupplyRisks", analysisItemsSchema());
        propertiesSchema.set("suggestedPurchases", analysisItemsSchema());
        propertiesSchema.set("actions", actionSchema);

        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.set("properties", propertiesSchema);
        schema.set("required", objectMapper.createArrayNode()
                .add("overview")
                .add("depletionRisks")
                .add("oversupplyRisks")
                .add("suggestedPurchases")
                .add("actions"));
        schema.put("additionalProperties", false);

        ObjectNode format = objectMapper.createObjectNode();
        format.put("type", "json_schema");
        format.put("name", "inventory_ai_analysis");
        format.put("strict", true);
        format.set("schema", schema);

        ObjectNode text = objectMapper.createObjectNode();
        text.set("format", format);

        ObjectNode request = objectMapper.createObjectNode();
        request.put("model", model);
        request.put("instructions", aiPromptProvider.getSystemPrompt());
        request.put("input", prompt);
        request.set("text", text);

        return request;
    }

    private ObjectNode buildItemSuggestionRequest(byte[] imageBytes, String contentType) {
        ObjectNode itemProperties = objectMapper.createObjectNode();
        itemProperties.set("scopeLabel", objectMapper.createObjectNode().put("type", "string"));
        itemProperties.set("imageSummary", objectMapper.createObjectNode().put("type", "string"));
        itemProperties.set("item", itemSuggestionSchema());

        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.set("properties", itemProperties);
        schema.set("required", objectMapper.createArrayNode()
                .add("scopeLabel")
                .add("imageSummary")
                .add("item"));
        schema.put("additionalProperties", false);

        ObjectNode format = objectMapper.createObjectNode();
        format.put("type", "json_schema");
        format.put("name", "inventory_ai_item_suggestion");
        format.put("strict", true);
        format.set("schema", schema);

        ObjectNode text = objectMapper.createObjectNode();
        text.set("format", format);

        String imageUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);

        ObjectNode userText = objectMapper.createObjectNode();
        userText.put("type", "input_text");
        userText.put("text", aiPromptProvider.getItemImageUserPrompt());

        ObjectNode imageNode = objectMapper.createObjectNode();
        imageNode.put("type", "input_image");
        imageNode.put("image_url", imageUrl);

        ObjectNode message = objectMapper.createObjectNode();
        message.put("role", "user");
        message.set("content", objectMapper.createArrayNode().add(userText).add(imageNode));

        ObjectNode request = objectMapper.createObjectNode();
        request.put("model", model);
        request.put("instructions", aiPromptProvider.getItemImageSystemPrompt());
        request.set("input", objectMapper.createArrayNode().add(message));
        request.set("text", text);

        return request;
    }

    private ObjectNode itemSuggestionSchema() {
        ObjectNode properties = objectMapper.createObjectNode();
        properties.set("name", objectMapper.createObjectNode().put("type", "string"));
        properties.set("sku", objectMapper.createObjectNode().put("type", "string"));
        properties.set("category", objectMapper.createObjectNode().put("type", "string"));
        properties.set("brand", objectMapper.createObjectNode().put("type", "string"));
        properties.set("manufacturer", objectMapper.createObjectNode().put("type", "string"));
        properties.set("model", objectMapper.createObjectNode().put("type", "string"));
        properties.set("partNumber", objectMapper.createObjectNode().put("type", "string"));
        properties.set("serialNumber", objectMapper.createObjectNode().put("type", "string"));
        properties.set("color", objectMapper.createObjectNode().put("type", "string"));
        properties.set("dimensions", objectMapper.createObjectNode().put("type", "string"));
        properties.set("weight", objectMapper.createObjectNode().put("type", "string"));
        properties.set("unit", objectMapper.createObjectNode().put("type", "string"));
        properties.set("quantity", objectMapper.createObjectNode().put("type", "integer"));
        properties.set("description", objectMapper.createObjectNode().put("type", "string"));
        properties.set("notes", objectMapper.createObjectNode().put("type", "string"));

        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");
        schema.set("properties", properties);
        schema.set("required", objectMapper.createArrayNode()
                .add("name")
                .add("sku")
                .add("category")
                .add("brand")
                .add("manufacturer")
                .add("model")
                .add("partNumber")
                .add("serialNumber")
                .add("color")
                .add("dimensions")
                .add("weight")
                .add("unit")
                .add("quantity")
                .add("description")
                .add("notes"));
        schema.put("additionalProperties", false);
        return schema;
    }

    private ObjectNode analysisItemsSchema() {
        ObjectNode itemProperties = objectMapper.createObjectNode();
        itemProperties.set("itemName", objectMapper.createObjectNode().put("type", "string"));
        itemProperties.set("companyName", objectMapper.createObjectNode().put("type", "string"));
        itemProperties.set("inventoryName", objectMapper.createObjectNode().put("type", "string"));
        itemProperties.set("locationName", objectMapper.createObjectNode().put("type", "string"));
        itemProperties.set("quantity", objectMapper.createObjectNode().put("type", "integer"));
        itemProperties.set("reason", objectMapper.createObjectNode().put("type", "string"));

        ObjectNode itemSchema = objectMapper.createObjectNode();
        itemSchema.put("type", "object");
        itemSchema.set("properties", itemProperties);
        itemSchema.set("required", objectMapper.createArrayNode()
                .add("itemName")
                .add("companyName")
                .add("inventoryName")
                .add("locationName")
                .add("quantity")
                .add("reason"));
        itemSchema.put("additionalProperties", false);

        ObjectNode arraySchema = objectMapper.createObjectNode();
        arraySchema.put("type", "array");
        arraySchema.set("items", itemSchema);

        return arraySchema;
    }

    private String extractOutputText(JsonNode responseJson) {
        JsonNode outputTextNode = responseJson.get("output_text");
        if (outputTextNode != null && !outputTextNode.isNull()) {
            return outputTextNode.asText();
        }

        JsonNode outputNode = responseJson.get("output");
        if (outputNode == null || !outputNode.isArray()) {
            return null;
        }

        for (JsonNode item : outputNode) {
            JsonNode contentNode = item.get("content");
            if (contentNode == null || !contentNode.isArray()) {
                continue;
            }

            for (JsonNode contentItem : contentNode) {
                JsonNode textNode = contentItem.get("text");
                if (textNode != null && !textNode.isNull()) {
                    return textNode.asText();
                }
            }
        }

        return null;
    }

}
